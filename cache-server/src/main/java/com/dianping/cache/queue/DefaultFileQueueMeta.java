package com.dianping.cache.queue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import org.apache.log4j.Logger;

/**
 * 
 * @author Leo Liang
 */
public class DefaultFileQueueMeta {

	private static final String	STRING_NULL	= "null";

	public static class Meta {
		private volatile String	fileWriting;
		private volatile String	fileReading;
		private volatile long	readPos;

		public Meta(long readPos) {
			super();
			this.readPos = readPos;
			fileWriting = STRING_NULL;
			fileReading = STRING_NULL;
		}

		public Meta(long readPos, String fileReading) {
			super();
			this.readPos = readPos;
			this.fileReading = fileReading;
		}

		public Meta(long readPos, String fileReading, String fileWriting) {
			super();
			this.readPos = readPos;
			this.fileReading = fileReading;
			this.fileWriting = fileWriting;
		}

		public Meta() {
			super();
			this.readPos = 0L;
			fileReading = STRING_NULL;
			fileWriting = STRING_NULL;
		}

		public long getReadPos() {
			return readPos;
		}

		public String getFileWriting() {
			return fileWriting;
		}

		public void setFileWriting(String fileWriting) {
			this.fileWriting = fileWriting;
		}

		public String getFileReading() {
			return fileReading;
		}

		public void set(long readPos, String fileReading) {
			this.readPos = readPos;
			this.fileReading = fileReading;
		}

		public void set(long readPos, String fileReading, String fileWriting) {
			this.readPos = readPos;
			this.fileWriting = fileWriting;
			this.fileReading = fileReading;
		}

		public void set(long readPos) {
			this.readPos = readPos;
		}
	}

	private static Logger			log			= Logger.getLogger(DefaultFileQueueMeta.class);
	private final Meta				meta;
	private final String			name		= "meta";
	private MappedByteBuffer		mbb;
	private final int				maxFileLen	= 2048;
	private final RandomAccessFile	metaFile;

	public DefaultFileQueueMeta(String storageName, String configFile) {
		super();
		this.meta = new Meta();
		String metaPath = DefaultFileQueueConfig.getInstance(configFile).getMetaFilePath() + File.separator + storageName
				+ File.separator;
		ensureFile(metaPath + this.name);
		try {
			metaFile = new RandomAccessFile(metaPath + this.name, "rwd");
			this.mbb = metaFile.getChannel().map(MapMode.READ_WRITE, 0, maxFileLen);
		} catch (Exception e) {
			throw new RuntimeException("Construct " + DefaultFileQueueMeta.class.getCanonicalName() + " failed.", e);
		}
		loadFromDisk();
	}

	private void ensureFile(String fileName) {
		File file = new File(fileName);
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				throw new RuntimeException("can not create dir: " + file.getParent());
			}
		}
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new RuntimeException("can not create file: " + file);
				}
			} catch (IOException e) {
				throw new RuntimeException("can not create file: " + file);
			}
		}

	}

	private void loadFromDisk() {
		mbb.position(0);
		int readingFileNameLen = mbb.getInt();
		long pos = mbb.getLong();
		byte[] readingFileNameByteArray = new byte[readingFileNameLen];
		try {
			mbb.get(readingFileNameByteArray);
		} catch (BufferUnderflowException e) {
			log.error("Meta file broken. Set position to 0 and Readingfile to null");
			meta.set(0, STRING_NULL);
			return;
		}

		int writingFileNameLen = mbb.getInt();
		byte[] writingFileNameByteArray = new byte[writingFileNameLen];
		try {
			mbb.get(writingFileNameByteArray);
		} catch (BufferUnderflowException e) {
			log.error("Meta file broken. Set Writingfile to null");
			meta.setFileWriting(STRING_NULL);
			return;
		}
		String readingFile = null;
		String writingFile = null;

		try {
			readingFile = new String(readingFileNameByteArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Processing readingFile name encoding failed.", e);
		}

		try {
			writingFile = new String(writingFileNameByteArray, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			log.error("Processing writingFile name encoding failed.", e);
		}
		if (readingFile == null) {
			readingFile = STRING_NULL;
		}
		if (writingFile == null) {
			writingFile = STRING_NULL;
		}

		meta.set(pos, readingFile, writingFile);
	}

	public synchronized void updateRead(long pos, String fileReading) {
		try {
			meta.set(pos, fileReading);
			mbb.position(0);
			byte[] bytes = fileReading.getBytes("UTF-8");
			mbb.putInt(bytes.length);
			mbb.putLong(meta.getReadPos());
			mbb.put(bytes);
			bytes = meta.getFileWriting().getBytes("UTF-8");
			mbb.putInt(bytes.length);
			mbb.put(bytes);
		} catch (UnsupportedEncodingException e) {
			log.error("meta file UpdateRead failed.", e);
		}

	}

	public synchronized void updateWrite(String fileWriting) {
		try {
			meta.setFileWriting(fileWriting);
			mbb.position(4 + 8 + meta.getFileReading().getBytes().length);
			byte[] bytes = fileWriting.getBytes("UTF-8");
			mbb.putInt(bytes.length);
			mbb.put(bytes);
		} catch (UnsupportedEncodingException e) {
			log.error("meta file updateWrite failed.", e);
		}
	}

	public long getReadPos() {
		return meta.getReadPos();
	}

	public String getFileReading() {
		return meta.getFileReading();
	}

	public String getFileWriting() {
		return meta.getFileWriting();
	}

	public synchronized void close() {
		mbb.force();
		mbb = null;
		try {
			metaFile.close();
		} catch (IOException e) {
			log.error("Close metaFile failed.", e);
		}
	}

}
