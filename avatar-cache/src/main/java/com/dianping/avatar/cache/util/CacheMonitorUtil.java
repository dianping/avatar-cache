/**
 * Project: avatar-cache
 * 
 * File Created at 2011-9-13
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.avatar.cache.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dianping.hawk.Hawk;

/**
 *
 * @author danson.liu
 */
public class CacheMonitorUtil {
    
    private static final Log logger = LogFactory.getLog(CacheMonitorUtil.class);
    
    private static Class<?> hawkClass;
    
    private static ConcurrentMap<String, Integer> logFactorMap = new ConcurrentHashMap<String, Integer>();
            
    private static long logFactor1;
    private static long logFactor2;
    private static long logFactor3;
    
    private static final String KEY_CACHE = "cache";
    
    private static final long timeout = 10000;
    
    static {
        try {
            hawkClass = Class.forName("com.dianping.hawk.Hawk");
        } catch (ClassNotFoundException e) {
            logger.warn("[com.dianping.hawk.Hawk] not found in classpath, "
                    + "so partial monitor func is unavailable.");
        }
    }
    
    /**
     * 用于cache监控打点
     */
    public static void log(String key1, String key2, String key3, String key4, double value) {
        try {
            if (hawkClass != null) {
                Hawk.log(key1, key2, key3, key4, value, timeout);
            }
        } catch (Throwable throwable) {
        	if (logFactor2++ % 300 == 0) {
        		logger.error("Do cache monitor log error.", throwable);
        	}
        }
    }
    
    /**
     * 记录指定category未配置缓存项配置
     * @param category 
     */
    public static void logConfigNotFound(String category) {
        try {
        	int logFactorNew = getNewLogFactor(category);
        	if (logFactorNew % 300 == 0) {
	            String errorMsg = "Cache item config[category=" + category + "] not found.";
	            if (hawkClass != null) {
	                Hawk.log(KEY_CACHE, "item-config-not-found", category, null, "com.dianping.avatar.cache.ConfigNotFoundError", errorMsg, "");
	            } else {
	                logger.error(errorMsg);
	            }
        	}
        } catch (Throwable throwable) {
            if (logFactor1++ % 300 == 0) {
                logger.error("Log config-not-found failed.", throwable);
            }
        }
    }
    
    /**
     * 记录缓存访问错误日志，大部分报到本地日志文件，小部分手机到hawk
     * @param errorMsg
     * @param throwable
     */
    public static void logCacheError(String errorMsg, Throwable throwable) {
    	try {
    		String factorName = StringUtils.substringBefore(errorMsg, "[");
    		int logFactorNew = getNewLogFactor(factorName);
    		if (logFactorNew % 100 == 0) {
    			logger.error("Operate cache error: " + errorMsg, throwable);
    		}
	    	if (hawkClass != null) {
	    		if (logFactorNew % 300 == 0) {
	    			Hawk.log(KEY_CACHE, "cache-operate-error", null, null, throwable.getClass().getName(), errorMsg, getErrorString(throwable));
	    		}
	    	}
    	} catch (Throwable t) {
    		if (logFactor3++ % 300 == 0) {
                logger.error("Log cache-error failed.", throwable);
            }
    	}
    }
    
    private static int getNewLogFactor(String factorName) {
    	Integer newFactor = null;
    	if (logFactorMap.containsKey(factorName)) {
    		Integer oldFactor = logFactorMap.get(factorName);
    		newFactor = oldFactor + 1;
    		logFactorMap.put(factorName, newFactor);
    	} else {
    		logFactorMap.put(factorName, 0);
    		newFactor = 0;
    	}
    	return newFactor;
    }
    
    private static String getErrorString(Throwable throwable) {
		OutputStream out = null;
		PrintWriter writer = null;
		try {
			out = new ByteArrayOutputStream(3000);
			writer = new PrintWriter(out);
			throwable.printStackTrace(writer);
			writer.flush();
			return out.toString();
		} catch (Exception e2) {
			return throwable.getMessage();
		} finally {
			if (writer != null) {
				writer.close();
			}
			out = null;
			writer = null;
		}
	}
    
}
