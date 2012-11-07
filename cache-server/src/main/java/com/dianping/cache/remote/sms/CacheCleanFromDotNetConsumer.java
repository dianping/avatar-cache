/**
 * 
 */
package com.dianping.cache.remote.sms;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.dianping.cache.entity.CacheKeyConfiguration;
import com.dianping.cache.service.CacheConfigurationService;
import com.dianping.cache.service.CacheKeyConfigurationService;
import com.dianping.queue.listener.MessageListener;
import com.dianping.queue.message.Message;
import com.dianping.queue.message.TextMessage;

/**
 * @author jian.liu
 *
 */
public class CacheCleanFromDotNetConsumer implements MessageListener {
	
	private CacheKeyConfigurationService cacheKeyConfigurationService;
	
	private CacheConfigurationService cacheConfigurationService;

	@Override
	public void onMessage(Message message) {
		System.out.println("Receive message[" + message + "].");
		TextMessage textMsg = (TextMessage) message;
		String content = textMsg.getContent();
		if (content.startsWith("*")) {
			//clear by category
			String category = content.substring(1);
			cacheConfigurationService.clearByCategory(category);
		} else {
			//clear specified cache item
			String[] splits = StringUtils.split(content, '|');
			String category = splits[0];
			CacheKeyConfiguration configuration = cacheKeyConfigurationService.find(category);
			String[] params = (String[]) ArrayUtils.subarray(splits, 1, splits.length);
			cacheConfigurationService.clearByKey(configuration.getCacheType(), generateKey(configuration, params));
		}
	}

	private String generateKey(CacheKeyConfiguration configuration, String[] params) {
		String accessKey = configuration.getCategory() + "." + configuration.getIndexTemplate() 
				+ "_" + configuration.getVersion();
		for (int i = 0; i < params.length; i++) {
			accessKey = accessKey.replace("{" + i + "}", params[i].toString());
		}
		return accessKey;
	}

	public void setCacheConfigurationService(CacheConfigurationService cacheConfigurationService) {
		this.cacheConfigurationService = cacheConfigurationService;
	}

	public void setCacheKeyConfigurationService(CacheKeyConfigurationService cacheKeyConfigurationService) {
		this.cacheKeyConfigurationService = cacheKeyConfigurationService;
	}

}
