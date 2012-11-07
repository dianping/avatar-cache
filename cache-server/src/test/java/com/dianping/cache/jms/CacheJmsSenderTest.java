package com.dianping.cache.jms;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dianping.cache.remote.jms.CacheMessageProducer;
import com.dianping.remote.cache.dto.CacheKeyTypeVersionUpdateDTO;
import com.dianping.remote.cache.dto.CacheMessageDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/config/spring/applicationContext-cache-jms-sender-test.xml")
public class CacheJmsSenderTest {

	@Autowired
	private CacheMessageProducer cacheMessageProducer;

//	@Test
//	public void queueMessage() {
//	}

	@Test
	public void topicMessage() {
	    CacheMessageDTO msg = buildMessage();
	    cacheMessageProducer.sendMessageToTopic(msg);
	}

    /**
     * @return
     */
    private CacheMessageDTO buildMessage() {
        CacheKeyTypeVersionUpdateDTO msg = new CacheKeyTypeVersionUpdateDTO();
        msg.setAddTime(System.currentTimeMillis());
        msg.setMsgValue("Test");
        msg.setVersion("10");
        return msg;
    }
}
