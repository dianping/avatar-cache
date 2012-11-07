package com.dianping.cache.queue;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * <p>
 * 文件队列实现
 * </p>
 * <p>
 * 保证线程安全，并且提供缓存功能。
 * </p>
 * 
 * @author Leo Liang
 */
public class DefaultFileQueueImpl<T> implements FileQueue<T> {
	private static Logger									log						= Logger.getLogger(DefaultFileQueueImpl.class);
	private final DefaultFileQueueMeta						meta;
	private final String									configFile;
	private final String									storageName;

	private final DefaultFileQueueDataFile					dataFile;
	private ObjectOutputStream								dataFileOutputStream;

	private long											memCacheLoadPos;
	private String											memCacheFileReading;

	private final CountDownLatch							latch					= new CountDownLatch(1);
	private final LinkedBlockingQueue<MemCachedMessage<T>>	memcacheQueue;
	private Thread											readerTask;
	private AtomicBoolean									stopped					= new AtomicBoolean(false);
	private final ReentrantLock								addLock					= new ReentrantLock();
	private final ReentrantLock								getLock					= new ReentrantLock();
	private AtomicReference<MemCachedMessage<T>>			lastMsg;
	private final ConcurrentMap<String, AtomicLong>			fileMap					= new ConcurrentHashMap<String, AtomicLong>();
	private int												objectOutputstreamTimes	= 0;
	private Random											delayRandom				= new Random();

	/**
	 * 内存缓存中的消息包装
	 * 
	 * @author Leo Liang
	 */
	private static class MemCachedMessage<T> {
		private T		data;
		private String	file;
		private boolean	isFirstMessageInFile;
		private long	pos;

		public MemCachedMessage(T data, String file, boolean isFirstMessageInFile, long pos) {
			this.data = data;
			this.file = file;
			this.isFirstMessageInFile = isFirstMessageInFile;
			this.pos = pos;
		}

		/**
		 * @return the pos
		 */
		public long getPos() {
			return pos;
		}

		/**
		 * @return the data
		 */
		public T getData() {
			return data;
		}

		/**
		 * @return the file
		 */
		public String getFile() {
			return file;
		}

		/**
		 * @return the isFirstMessageInFile
		 */
		public boolean isFirstMessageInFile() {
			return isFirstMessageInFile;
		}

	}

	public DefaultFileQueueImpl(final String configFile, final String storageName) {
		this.meta = new DefaultFileQueueMeta(storageName, configFile);
		this.dataFile = new DefaultFileQueueDataFile(storageName, configFile);
		this.memcacheQueue = new LinkedBlockingQueue<MemCachedMessage<T>>(DefaultFileQueueConfig
				.getInstance(configFile).getMemCacheSize());
		loadMeta();
		this.storageName = storageName;
		this.configFile = configFile;
		this.lastMsg = new AtomicReference<MemCachedMessage<T>>(null);
		this.dataFileOutputStream = null;
		// 启动后台批量填充内存缓存的任务
		start();
		log.info("Inited SeqFileStorage: " + storageName + " readingFile: " + memCacheFileReading + " pos: "
				+ memCacheLoadPos);
	}

	private void loadMeta() {
		memCacheLoadPos = meta.getReadPos();
		memCacheFileReading = meta.getFileReading();
		if ("null".equalsIgnoreCase(memCacheFileReading) || StringUtils.isBlank(memCacheFileReading)) {
			memCacheFileReading = null;
		}
		String readFileFromDiskScan = dataFile.getStorageFileName();
		if (readFileFromDiskScan == null) {
			if (memCacheFileReading != null) {
				log.error("meta file's fileReading: " + memCacheFileReading + " not equal to data file remaining: "
						+ null);
			}
			memCacheFileReading = null;
			memCacheLoadPos = 0;
			return;
		}
		if (memCacheFileReading == null) {
			memCacheFileReading = readFileFromDiskScan;
			memCacheLoadPos = 0;
		} else if (!memCacheFileReading.equals(readFileFromDiskScan)) {
			log.error("meta file fileReading: " + memCacheFileReading + " not equal to " + readFileFromDiskScan);
			File f = new File(memCacheFileReading);
			if (f.exists()) {
				dataFile.adjust(memCacheFileReading);
				return;
			}
			memCacheFileReading = readFileFromDiskScan;
			memCacheLoadPos = 0;
		}
		return;
	}

	/**
	 * 启动内存缓存的填充任务
	 */
	private void start() {
		readerTask = new Thread(new Runnable() {
			public void run() {
				try {
					loadMessages();
				} catch (Throwable t) {
					log.error("Exception occur.", t);
				}
			}
		}, "seqfile_cache_reader_thread_" + this.storageName);
		readerTask.start();
	}

	public String getStorageName() {
		return storageName;
	}

	/**
	 * 切换内存缓存正在读取的文件
	 * 
	 * @param oi
	 */
	private void switchReadFile(ObjectInputStream oi) {
		if (!dataFile.isEmpty()) {
			if (oi != null) {
				try {
					oi.close();
				} catch (IOException e) {
					log.error("close file failed when switching file", e);
				}
			}
			memCacheFileReading = dataFile.getStorageFileName();
			memCacheLoadPos = 0;
		}
	}

	/**
	 * 填充内存缓存队列
	 */
	private void loadMessages() {
		// 刚启动的时候，可能还没有数据文件，当第一次调用add方法时会导致填充任务真正开始
		if (StringUtils.isBlank(memCacheFileReading)) {
			try {
				latch.await();
			} catch (InterruptedException e) {
				log.error("lacth interruptted", e);
				return;
			}
			switchReadFile(null);
		}

		// 每次循环加载一个文件
		while (!stopped.get()) {

			ObjectInputStream oi = openInputStream();
			seek(oi, memCacheLoadPos);
			// 响应线程池的shutdown或者cancel
			if (Thread.interrupted() || stopped.get()) {
				return;
			}
			loadFromFile(oi);
			// 响应线程池的shutdown或者cancel
			if (Thread.interrupted() || stopped.get()) {
				return;
			}
			switchReadFile(oi);
		}

	}

	private boolean seek(ObjectInputStream oi, long pos) {
		for (long i = 0; i < pos; i++) {
			try {
				oi.readObject();
			} catch (Exception e) {
				log.error("Seek objectInputStream failed.", e);
				return false;
			}
		}
		return true;
	}

	private void addToMemCache(T data, boolean isFirstMessageInFile, long pos) {
		try {
			memcacheQueue.put(new MemCachedMessage<T>(data, memCacheFileReading, isFirstMessageInFile, pos));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			log.error("Exception occured.", e);
		}
	}

	private void delayLoad(int readLen) {
		if (readLen == 0 && dataFile.isEmpty()) {
			delay(0);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadFromFile(ObjectInputStream oi) {
		Throwable t = null;
		boolean firstMessageInFile = memCacheLoadPos == 0 ? true : false;
		try {
			while (t == null || dataFile.isEmpty()) {
				// 响应线程池的shutdown或者cancel
				if (Thread.interrupted() || stopped.get()) {
					Thread.currentThread().interrupt();
					return;
				}

				// 当正在读的数据已经追上正在写入的数据时，为了避免写入数据的flush没有完全完成而导致
				// 读到不完整的数据，delay一下
				AtomicLong writePos = fileMap.get(memCacheFileReading);
				if (writePos != null && memCacheLoadPos >= writePos.get() - 1) {
					delay(1000);
				}

				T obj = null;
				try {
					obj = (T) oi.readObject();

				} catch (EOFException e) {
					t = e;
				}

				if (obj != null) {
					memCacheLoadPos++;
					addToMemCache(obj, firstMessageInFile, memCacheLoadPos);
					firstMessageInFile = false;
					delayRandom.setSeed(System.currentTimeMillis());
					delayLoad(delayRandom.nextInt(10));
				} else {
					delay(50);
				}
				
			}

			t = null;
			// 当文件到结尾但是后来文件，并且这个时候新增了一个数据文件时，
			// 会导致上面的循环退出。但是如果这个时候老文件又flush了一点内容，
			// 我们需要多读一次避免这个时间窗口的出现。
			do {
				// 响应线程池的shutdown或者cancel
				if (Thread.interrupted() || stopped.get()) {
					Thread.currentThread().interrupt();
					return;
				}

				T obj = null;
				try {
					obj = (T) oi.readObject();
				} catch (EOFException e) {
					t = e;
				}

				if (obj != null) {
					memCacheLoadPos++;
					addToMemCache(obj, firstMessageInFile, memCacheLoadPos);
					Random random = new Random(System.currentTimeMillis());
					delayLoad(random.nextInt(10));
				}
			} while (t == null);
		} catch (Exception e) {
			log.error("Load file failed. File: " + memCacheFileReading, e);
		}
	}

	private ObjectInputStream openInputStream() {
		ObjectInputStream oi = null;
		Throwable t = null;
		for (int i = 0; i < 3; i++) {
			try {
				oi = new ObjectInputStream(new FileInputStream(new File(memCacheFileReading)));
				t = null;
				break;
			} catch (Exception e) {
				t = e;
				delay((i + 1) * 15);
			}
		}
		if (t != null) {
			String errMsg = "Open InputStream for background thread failed. file: " + memCacheFileReading;
			log.error(errMsg, t);
			throw new RuntimeException(errMsg, t);
		} else {
			return oi;
		}
	}

	private void delay(int i) {
		try {
			Thread.sleep(5 * (i + 1));
		} catch (InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * @see FileQueue#get()
	 */
	public T get() {
		MemCachedMessage<T> msg = null;
		getLock.lock();
		try {
			msg = memcacheQueue.take();
			lastMsg.set(msg);
			commitMessage();
		} catch (InterruptedException e) {
			log.warn("interrupted when taking messsage from SeqFileStorage");
			Thread.currentThread().interrupt();
		} finally {
			getLock.unlock();
		}
		return msg == null ? null : msg.getData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dp.hippo.queue.FileQueue#get(long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public T get(long timeout, TimeUnit timeUnit) {
		MemCachedMessage<T> msg = null;
		getLock.lock();
		try {
			msg = memcacheQueue.poll(timeout, timeUnit);
			if (msg == null) {
				return null;
			}
			lastMsg.set(msg);
			commitMessage();
		} catch (InterruptedException e) {
			log.warn("interrupted when taking messsage from SeqFileStorage");
			Thread.currentThread().interrupt();
		} finally {
			getLock.unlock();
		}
		return msg == null ? null : msg.getData();
	}

	private void commitMessage() {
		MemCachedMessage<T> msg = null;
		if ((msg = lastMsg.getAndSet(null)) != null) {
			meta.updateRead(msg.getPos(), msg.getFile());
			// 如果是文件的第一个消息，触发备份和删除老文件事件
			if (msg.isFirstMessageInFile()) {
				dataFile.archiveAndRemoveOldFile(msg.getFile());
			}
		}
	}

	/**
	 * @see FileQueue#add(Serializable)
	 */
	public void add(T m) throws FileQueueClosedException {
		if (stopped.get()) {
			throw new FileQueueClosedException("SeqFileStorage has been closed. Storage name: " + storageName);
		}
		addLock.lock();
		try {
			checkDataFileOutputStream();
			write(m);
		} finally {
			addLock.unlock();
		}
		latch.countDown();
	}

	public void close() {
		stopped.set(true);
		if (readerTask != null) {
			readerTask.interrupt();
		}
		meta.close();
	}

	private void checkDataFileOutputStream() {
		FileInputStream input = null;
		try {
			// 启动的时候，dataFileOutputStream为null，不管上次启动时候写入的文件满了没，
			// 为了简单起见，还是新建一个文件
			if (dataFileOutputStream == null) {
				String newDataFileName = dataFile.createStorageFile();
				dataFileOutputStream = new ObjectOutputStream(new FileOutputStream(newDataFileName));
				meta.updateWrite(newDataFileName);
				// 更新写入的偏移量
				fileMap.put(newDataFileName, new AtomicLong(0));
				objectOutputstreamTimes = 0;
			}
			input = new FileInputStream(meta.getFileWriting());
			// 如果达到配置文件指定的大小，切换文件
			if (input.available() >= DefaultFileQueueConfig.getInstance(configFile).getMaxDataFileSize()) {
				dataFileOutputStream.close();
				String newDataFileName = dataFile.createStorageFile();
				dataFileOutputStream = new ObjectOutputStream(new FileOutputStream(newDataFileName));
				// 清除切换前老文件的记录
				fileMap.remove(meta.getFileWriting());
				meta.updateWrite(newDataFileName);
				// 更新写入的偏移量
				fileMap.put(newDataFileName, new AtomicLong(0));
				objectOutputstreamTimes = 0;
			}
		} catch (Exception e) {
			log.error("Create data file failed.", e);
			throw new RuntimeException("create data file failed.", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error("Close temp inputstream for size cal failed.", e);
				}
			}
		}
	}

	private void write(T m) {
		try {
			if (m == null) {
				return;
			}
			objectOutputstreamTimes++;
			if (objectOutputstreamTimes == 2000) {
				dataFileOutputStream.reset();
				objectOutputstreamTimes = 1;
			}
			dataFileOutputStream.writeObject(m);
			dataFileOutputStream.flush();
			// 更新写入的偏移量
			fileMap.get(meta.getFileWriting()).incrementAndGet();
		} catch (Exception e) {
			log.error("write message failed", e);
			throw new RuntimeException("write message failed", e);
		}
	}

}
