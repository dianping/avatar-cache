/**
 * Project: cache-server
 * 
 * File Created at 2010-10-15
 * $Id$
 * 
 * Copyright 2010 dianping.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.cache.remote.jms;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.dianping.cache.queue.DefaultFileQueueImpl;
import com.dianping.cache.queue.FileQueue;
import com.dianping.cache.queue.FileQueueClosedException;
import com.dianping.lion.client.ConfigCache;
import com.dianping.mailremote.remote.MailService;
import com.dianping.modules.spring.flex.SecurityHelper;
import com.mysql.jdbc.util.LRUCache;


/**
 * CacheMessageProducer is used to produce jms message.
 * 
 * @author pengshan.zhang
 * 
 */
public class CacheMessageProducerAMQ implements Serializable,  MQSender{

	/**
     * Serial Version UID
     */
    private static final long serialVersionUID = 2472485481241075834L;
    
    private static Logger logger = LoggerFactory.getLogger(CacheMessageProducerAMQ.class);
    
    private static final String CONFIG_KEY_ACTIVEMQ_ENABLED = "avatar-cache.activemq.enabled";
    private static final String CONFIG_KEY_ACTIVEMQ_SEND_ENABLED = "avatar-cache.activemq.send.enabled";
    private static final String CONFIG_KEY_COST_WARN_THRESHOLD = "avatar-cache.activemq.warn.costthreshold";
	private static final String CONFIG_KEY_ERROR_COUNT_WARN_THRESHOLD = "avatar-cache.activemq.warn.countthreshold";
	private static final String CONFIG_KEY_ACTIVEMQ_SEND_ERRMAIL = "avatar-cache.activemq.warn.sendmail";
	private static final String CONFIG_KEY_ACTIVEMQ_ERRMAIL_INTERVAL = "avatar-cache.activemq.warn.mailinterval";
	private static final String CONFIG_KEY_ACTIVEMQ_ERRMAIL_RECEIVERS = "avatar-cache.activemq.warn.mailreceivers";

	private static final long DEFAULT_MAIL_SEND_INTERVAL = 5 * 60 * 1000L;
	
	private FileQueue<Object> messageQueue = new DefaultFileQueueImpl<Object>("config/file-queue.properties", "cache-message");
    
    /**
     * Spring JmsTemplate used to send message
     */
    private JmsTemplate jmsTemplate;
    /**
     * Topic used for Cache jms message.
     */
    private Destination notifyTopic;
    
    /**
     * Queueu used for Cache jms message
     */
    private Destination notifyQueue;
    
    private MailService mailService;
    private ExecutorService executorService = Executors.newFixedThreadPool(3);
    
    @SuppressWarnings("unchecked")
	private Map<String, Integer> errorCountMap = Collections.synchronizedMap(new LRUCache(100));
    private long lastMailSendTime = System.currentTimeMillis();
    private Object mailSendMonitor = new Object();
    
    /**
     * @return the jmsTemplate
     */
    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    /**
     * @param jmsTemplate
     *            the jmsTemplate to set
     */
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * @return the notifyTopic
     */
    public Destination getNotifyTopic() {
        return notifyTopic;
    }

    /**
     * @param notifyTopic
     *            the notifyTopic to set
     */
    public void setNotifyTopic(Destination notifyTopic) {
        this.notifyTopic = notifyTopic;
    }

    /**
     * Send message to topic.
     * 
     * @param msg
     *            message used to send to topic
     */
    public void sendMessageToTopic(final Object msg) {
    	if(isSendAMQRequired()) {
    		if (isOperateFromUI()) {
    			jmsTemplate.convertAndSend(notifyTopic, msg);
    		} else {
    			try {
					messageQueue.add(msg);
				} catch (FileQueueClosedException e) {
					logger.error("Send message[" + msg + "] to activemq failed.", e);
				}
    		}
    	}
    }

	private boolean isOperateFromUI() {
		return SecurityHelper.getPrincipal() != null;
	}

	private void sendErrorMail(final String mailContent) {
		if (isErrorMailSendRequired()) {
			synchronized (mailSendMonitor) {
				if (isErrorMailSendRequired()) {
					lastMailSendTime = System.currentTimeMillis();
					List<String> mailReceivers = getMailReceivers();
					for (final String mailReceiver : mailReceivers) {
						executorService.execute(new Runnable() {
							@Override
							public void run() {
								try {
									mailService.send(15, mailReceiver, "[Caution-ActiveMQ]****************From Cache-Service", mailContent);
								} catch (Throwable e) {
									logger.error("Send activemq warning mail to [" + mailReceiver + "] failed.", e);
								}
							}
						});
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> getMailReceivers() {
		String receivers = null;
		try {
			receivers = ConfigCache.getInstance().getProperty(CONFIG_KEY_ACTIVEMQ_ERRMAIL_RECEIVERS);
		} catch (Throwable e) {
			logger.warn("Get config[" + CONFIG_KEY_ACTIVEMQ_ERRMAIL_RECEIVERS + "] from lion failed.", e);
		}
		return StringUtils.isNotBlank(receivers) ? Arrays.asList(StringUtils.split(receivers, ",，")): Collections.EMPTY_LIST;
	}

	private boolean isErrorMailSendRequired() {
		return isSendAMQErrorMail() && (System.currentTimeMillis() - lastMailSendTime >= getMailSendInterval());
	}
	
	private long getMailSendInterval() {
		Long mailSendInterval = null;
    	try {
    		mailSendInterval = ConfigCache.getInstance().getLongProperty(CONFIG_KEY_ACTIVEMQ_ERRMAIL_INTERVAL);
		} catch (Throwable e) {
			logger.warn("Get config[" + CONFIG_KEY_ACTIVEMQ_ERRMAIL_INTERVAL + "] from lion failed.", e);
		}
		return mailSendInterval != null ? mailSendInterval : DEFAULT_MAIL_SEND_INTERVAL;
	}

	private boolean isSendAMQErrorMail() {
    	Boolean sendErrMail = null;
    	try {
    		sendErrMail = ConfigCache.getInstance().getBooleanProperty(CONFIG_KEY_ACTIVEMQ_SEND_ERRMAIL);
		} catch (Throwable e) {
			logger.warn("Get config[" + CONFIG_KEY_ACTIVEMQ_SEND_ERRMAIL + "] from lion failed.", e);
		}
		return sendErrMail != null ? sendErrMail : true;
	}

	private synchronized Integer incrAndReturnErrorCount(String minuteLabel) {
		Integer oldErrorCount = errorCountMap.get(minuteLabel);
		if (oldErrorCount == null) {
			errorCountMap.put(minuteLabel, 1);
			return 1;
		}
		Integer newErrorCount = oldErrorCount + 1;
		errorCountMap.put(minuteLabel, newErrorCount);
		return newErrorCount;
	}

	private Integer getSendAMQErrorCountWarnThreshold() {
		Integer errorCountThreshold = null;
    	try {
    		errorCountThreshold = ConfigCache.getInstance().getIntProperty(CONFIG_KEY_ERROR_COUNT_WARN_THRESHOLD);
		} catch (Throwable e) {
			logger.warn("Get config[" + CONFIG_KEY_ERROR_COUNT_WARN_THRESHOLD + "] from lion failed.", e);
		}
		return errorCountThreshold != null ? errorCountThreshold : 20;
	}

	private String getMinuteLabel(Calendar now) {
		return now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH) + "-" + now.get(Calendar.DATE)
			+ "-" + now.get(Calendar.HOUR_OF_DAY) + "-" + now.get(Calendar.MINUTE);
	}

	private boolean isSendAMQRequired() {
    	Boolean amqEnabled = null;
    	try {
			amqEnabled = ConfigCache.getInstance().getBooleanProperty(CONFIG_KEY_ACTIVEMQ_ENABLED);
		} catch (Throwable e) {
			logger.warn("Get config[" + CONFIG_KEY_ACTIVEMQ_ENABLED + "] from lion failed.", e);
		}
		return amqEnabled != null ? amqEnabled : true;
	}

    private long getSendAMQCostWarnThreshold() {
    	Long warnThreshold = null;
    	try {
    		warnThreshold = ConfigCache.getInstance().getLongProperty(CONFIG_KEY_COST_WARN_THRESHOLD);
		} catch (Throwable e) {
			logger.warn("Get config[" + CONFIG_KEY_COST_WARN_THRESHOLD + "] from lion failed.", e);
		}
		return warnThreshold != null ? warnThreshold : 5000;
	}

	/**
     * @param notifyQueue the notifyQueue to set
     */
    public void setNotifyQueue(Destination notifyQueue) {
        this.notifyQueue = notifyQueue;
    }

    /**
     * @return the notifyQueue
     */
    public Destination getNotifyQueue() {
        return notifyQueue;
    }

    /**
     * Send message to queue.
     * 
     * @param msg
     *            message used to send to queue
     */
    public void sendMessageToQueue(final Object msg) {
        jmsTemplate.convertAndSend(notifyQueue, msg);
    }

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
	
	public void init() {
		new MessageSender().start();
	}
	
	class MessageSender extends Thread {
		public MessageSender() {
			setDaemon(true);
			setName("AMQ-Message-Sender-Thread");
		}
		
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				Object message = null;
				try {
					if (!isAMQSendEnabled()) {
						continue;
					}
					message = messageQueue.get(3, TimeUnit.SECONDS);
					if (message != null) {
						Calendar now = Calendar.getInstance();
						long begin = now.getTimeInMillis();
						int errorCount = 0;
						String minuteLabel = getMinuteLabel(now);
						try {
							jmsTemplate.convertAndSend(notifyTopic, message);
							if (System.currentTimeMillis() - begin >= getSendAMQCostWarnThreshold()) {
								errorCount = incrAndReturnErrorCount(minuteLabel);
							}
						} catch (Throwable e) {
							errorCount = incrAndReturnErrorCount(minuteLabel);
						}
						Integer warnThreshold = getSendAMQErrorCountWarnThreshold();
						if (errorCount >= warnThreshold) {
			    			errorCountMap.remove(minuteLabel);
			    			sendErrorMail("ActiveMQ一分钟内错误(超时---阀值" + getSendAMQCostWarnThreshold() + "ms/异常)次数超过了预设阀值[" + warnThreshold + "]，请查看.");
						}
					}
				} catch (Throwable e) {
					logger.error("Send activemq message" + (message != null ? "[" + message + "]" : "") + " failed.", e);
				} finally {
					try {
						if (message == null) {
							Thread.sleep(1000);
						}
					} catch (InterruptedException e) {
					}
				}
			}
			logger.warn("Thread[" + getName() + "] terminate unexpected.");
		}

		private boolean isAMQSendEnabled() {
			Boolean isAMQSendEnabled = null;
			try {
				isAMQSendEnabled = ConfigCache.getInstance().getBooleanProperty(CONFIG_KEY_ACTIVEMQ_SEND_ENABLED);
			} catch (Throwable e) {
				logger.warn("Get config[" + CONFIG_KEY_ACTIVEMQ_SEND_ENABLED + "] from lion failed.", e);
			}
			return isAMQSendEnabled != null ? isAMQSendEnabled : true;
		}
	}
    
}
