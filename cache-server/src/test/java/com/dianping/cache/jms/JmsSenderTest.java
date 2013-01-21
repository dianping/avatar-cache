package com.dianping.cache.jms;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/config/spring/applicationContext-jms-sender-test.xml")
@Ignore
public class JmsSenderTest {

	@Autowired
	private NotifyMessageProducer notifyMessageProducer;

	@Test
	public void queueMessage() {
		notifyMessageProducer.sendQueue("zhangsan", "zhangsan@dianping.com");
	}

	@Test
	public void topicMessage() {
	    notifyMessageProducer.sendTopic("lisi", "lisi@dianping.com");
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/config/spring/applicationContext-jms-sender-test.xml");
		NotifyMessageProducer producer = (NotifyMessageProducer) context.getBean("notifyMessageProducer");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				System.out.print(">");
				String line = reader.readLine().toLowerCase();
				if (line.startsWith("quit") || line.startsWith("exit")) {
					break;
				}
				try {
					producer.sendTopic("lisi", line);
				} catch (Exception e) {
					System.err.println("Error happend: " + e.getMessage());
				}
				System.out.println();
				System.out.println("A message has been sent.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
