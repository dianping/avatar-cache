package com.dianping.cache.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;

import org.springframework.jms.core.JmsTemplate;

public class NotifyMessageProducer {

    private JmsTemplate jmsTemplate;
    private Destination notifyQueue;
    private Destination notifyTopic;

    private void sendMessage(String username, String email, Destination destination) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userName", username);
        map.put("email", email);

        jmsTemplate.convertAndSend(destination, map);
    }

    public void sendQueue(String username, String email) {
        sendMessage(username, email,notifyQueue);
    }
    
    
    public void sendTopic(String username, String email) {
        sendMessage(username, email, notifyTopic);
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setNotifyQueue(Destination notifyQueue) {
        this.notifyQueue = notifyQueue;
    }

    public void setNotifyTopic(Destination nodifyTopic) {
        this.notifyTopic = nodifyTopic;
    }

    /**
     * @return the jmsTemplate
     */
    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    /**
     * @return the notifyQueue
     */
    public Destination getNotifyQueue() {
        return notifyQueue;
    }

    /**
     * @return the notifyTopic
     */
    public Destination getNotifyTopic() {
        return notifyTopic;
    }
    
}
