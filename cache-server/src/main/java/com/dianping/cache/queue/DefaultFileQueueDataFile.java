package com.dianping.cache.queue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author Leo Liang
 */
public class DefaultFileQueueDataFile {
	private final static Logger		log		= Logger.getLogger(DefaultFileQueueDataFile.class);
	private final String			path;
	private final String			prefix	= "seqfilestorage";
	private final TreeSet<String>	files;
	private final TreeSet<String>	oldFiles;
	private final String			storageName;
	private final String			configFile;

	public DefaultFileQueueDataFile(String storageName, String configFile) {
		this.path = DefaultFileQueueConfig.getInstance(configFile).getDataFilePath() + File.separator + storageName
				+ File.separator;
		this.files = new TreeSet<String>();
		this.oldFiles = new TreeSet<String>();
		this.storageName = storageName;
		this.configFile = configFile;
		scan();
	}

	private void scan() {
		File dir = new File(this.path);
		if (!dir.exists()) {
			return;
		}
		String[] nameList = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(prefix);
			}
		});
		for (String fileName : nameList) {
			String fullPath = this.path + fileName;
			files.add(fullPath);
		}
	}

	/**
	 * <pre>
	 * 创建一个新的数据文件。
	 * 
	 * 文件名是：seqfilestorage.[19位自增序列]
	 * </pre>
	 * 
	 * @return 新增的数据文件名
	 */
	public synchronized String createStorageFile() {
		long count = 0;
		if (!files.isEmpty()) {
			String lastFileName = files.last();
			count = Long.valueOf(lastFileName.substring(lastFileName.lastIndexOf(".") + 1)).longValue() + 1;
		} else if (!oldFiles.isEmpty()) {
			String lastFileName = oldFiles.last();
			count = Long.valueOf(lastFileName.substring(lastFileName.lastIndexOf(".") + 1)).longValue() + 1;
		}
		ensureDir(this.path);
		String newFileName = this.path + this.prefix + "." + String.format("%019d", count);
		files.add(newFileName);
		return newFileName;
	}

	private void ensureDir(String path) {
		File baseFile = new File(path);
		if (!baseFile.exists()) {
			if (!baseFile.mkdirs()) {
				throw new RuntimeException("Can not create dir: " + path);
			}
		}
	}

	/**
	 * 获得下一个数据文件
	 * 
	 * @return
	 */
	public synchronized String getStorageFileName() {
		if (files.size() > 0) {
			String fullname = files.first();
			files.remove(fullname);
			if (fullname != null) {
				oldFiles.add(fullname);
			}
			return fullname;
		} else {
			return null;
		}
	}

	/**
	 * 启动的时候，如果上一次没有正确删除老文件，需要调整DataFile内部的数据
	 * 
	 * @param fileReading
	 */
	public synchronized void adjust(String fileReading) {
		String[] filesArray = files.toArray(new String[0]);
		for (String file : filesArray) {
			if (Long.valueOf(fileReading.substring(fileReading.lastIndexOf(".") + 1)) >= Long.valueOf(file
					.substring(file.lastIndexOf(".") + 1))) {
				files.remove(file);
				oldFiles.add(file);
			}
		}
	}

	/**
	 * 备份并清除老的数据文件
	 * 
	 * @param filePath
	 */
	public synchronized void archiveAndRemoveOldFile(String filePath) {
		if (oldFiles.size() == 0)
			return;
		String first = oldFiles.first();
		if (first.equals(filePath))
			return;
		Set<String> subSet = oldFiles.subSet(first, filePath);
		String[] subSetArray = subSet.toArray(new String[0]);
		for (int i = 0; i < subSetArray.length; i++) {
			String toDeleteFileName = subSetArray[i];
			if (toDeleteFileName.equals(filePath)) {
				continue;
			}

			File toDelete = new File(toDeleteFileName);

			try {
				if (DefaultFileQueueConfig.getInstance(configFile).isDataBakOn()) {
					SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

					String bakPath = DefaultFileQueueConfig.getInstance(configFile).getDataBakPath() + File.separator
							+ storageName + File.separator + sf.format(new Date()) + File.separator;
					ensureDir(bakPath);
					FileUtils.copyFileToDirectory(toDelete, new File(bakPath));
				}

				if (toDelete.delete() == false) {
					log.error(toDeleteFileName + " delete failed.");
				} else {
					oldFiles.remove(toDeleteFileName);
				}
			} catch (IOException e) {
				log.error("Backup data file failed.", e);
			}

		}
	}

	/**
	 * 是否还有数据文件
	 * 
	 * @return
	 */
	public synchronized boolean isEmpty() {
		return files.isEmpty();
	}

}
