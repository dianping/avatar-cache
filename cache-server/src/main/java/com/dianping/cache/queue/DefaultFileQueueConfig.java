package com.dianping.cache.queue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Leo Liang
 */
public class DefaultFileQueueConfig {
	private static final Logger							log						= Logger.getLogger(DefaultFileQueueConfig.class);

	private String										metaFilePath;
	private String										dataFilePath;
	private int											memCacheSize;
	private long										maxDataFileSize;
	private String										dataBakPath;
	private boolean										dataBakOn;

	private static Map<String, DefaultFileQueueConfig>	instanceMap				= new HashMap<String, DefaultFileQueueConfig>();

	private static final String							DEFAULT_METAFILEPATH	= "/home/work/relayseqfilestorage/meta";
	private static final String							DEFAULT_DATAFILEPATH	= "/home/work/relayseqfilestorage/data";
	private static final String							DEFAULT_DATABAKPATH		= "/home/work/relayseqfilestorage/bak";
	private static final int							DEFAULT_MAXDATAFILESIZE	= 50 * 1024 * 1024;
	private static final int							DEFAULT_MEMCACHESIZE	= 10000;

	private DefaultFileQueueConfig(String configFile) {
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = DefaultFileQueueConfig.class.getClassLoader().getResourceAsStream(configFile);
			prop.load(in);
		} catch (Exception e) {
			log.error("Load SeqFileStorageConfig properties file failed.", e);
			throw new RuntimeException("Load SeqFileStorageConfig properties file failed.", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error("Cloase SeqFileStorageConfig properties file failed.", e);
				}
			}
		}
		this.metaFilePath = prop.getProperty("metapath", DEFAULT_METAFILEPATH);
		this.dataFilePath = prop.getProperty("datapath", DEFAULT_DATAFILEPATH);
		this.dataBakPath = prop.getProperty("databakpath", DEFAULT_DATABAKPATH);
		try {
			String bakSwitchStr = prop.getProperty("databakon", Boolean.TRUE.toString());
			this.dataBakOn = Boolean.valueOf(bakSwitchStr);
			String memCacheSizeStr = prop.getProperty("memcachesize");
			if (StringUtils.isNotBlank(memCacheSizeStr) && StringUtils.isNumeric(memCacheSizeStr)) {
				this.memCacheSize = Integer.parseInt(memCacheSizeStr);
			} else {
				this.memCacheSize = DEFAULT_MEMCACHESIZE;
			}

			String maxDataFileSizeStr = prop.getProperty("maxDataFileSize");
			if (StringUtils.isNotBlank(maxDataFileSizeStr) && StringUtils.isNumeric(maxDataFileSizeStr)) {
				this.maxDataFileSize = Long.parseLong(maxDataFileSizeStr);
			} else {
				this.maxDataFileSize = DEFAULT_MAXDATAFILESIZE;
			}
		} catch (Exception e) {
			log.error("Parse property failed.", e);
			throw new RuntimeException("Parse property failed.", e);
		}
	}

	public synchronized static DefaultFileQueueConfig getInstance(String configFile) {
		if (instanceMap.get(configFile) == null) {
			instanceMap.put(configFile, new DefaultFileQueueConfig(configFile));
		}
		return instanceMap.get(configFile);
	}

	/**
	 * @return the dataFilePath
	 */
	public String getDataFilePath() {
		return dataFilePath;
	}

	/**
	 * @return the metaFilePath
	 */
	public String getMetaFilePath() {
		return metaFilePath;
	}

	/**
	 * @return the memCacheSize
	 */
	public int getMemCacheSize() {
		return memCacheSize;
	}

	/**
	 * @return the maxDataFileSize
	 */
	public long getMaxDataFileSize() {
		return maxDataFileSize;
	}

	/**
	 * @return the dataBakPath
	 */
	public String getDataBakPath() {
		return dataBakPath;
	}

	/**
	 * @return the dataBakOn
	 */
	public boolean isDataBakOn() {
		return dataBakOn;
	}

}
