package com.hdu.neurostudent_signalflow.utils.mqtt;


import com.hdu.neurostudent_signalflow.config.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//@Component
public class MqttAcceptClient {
    private static final Logger logger = LoggerFactory.getLogger(MqttAcceptClient.class);

//    @Autowired
    private MqttAcceptCallback mqttAcceptCallback;

//    @Autowired
    private MqttProperties mqttProperties;

    private static volatile MqttClient client;

    static synchronized MqttClient getClient() {
        return client;
    }

    private static synchronized void setClient(MqttClient client) {
        MqttAcceptClient.client = client;
    }

    public synchronized void connect() {
        try {
            MqttClient client = new MqttClient(mqttProperties.getHostUrl(), mqttProperties.getClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttProperties.getUsername());
            options.setPassword(mqttProperties.getPassword().toCharArray());
            options.setConnectionTimeout(mqttProperties.getTimeout());
            options.setKeepAliveInterval(mqttProperties.getKeepAlive());
            options.setAutomaticReconnect(mqttProperties.getReconnect());
            options.setCleanSession(mqttProperties.getCleanSession());
            setClient(client);
            client.setCallback(mqttAcceptCallback);
            client.connect(options);
        } catch (Exception e) {
            logger.error("MqttAcceptClient connect error, message: {}", e.getMessage(), e);
        }
    }

    public void reconnection() {
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                Thread.sleep(1000 * (retryCount + 1));  // 指数退避
                connect();
                if (getClient().isConnected()) {
                    break;
                }
            } catch (InterruptedException e) {
                logger.error("Reconnection interrupted", e);
                Thread.currentThread().interrupt();
            }
            retryCount++;
        }
    }

    public void subscribe(String topic, int qos) {
        logger.info("========================【开始订阅主题: {}】========================", topic);
        try {
            synchronized (this) {
                if (client != null && client.isConnected()) {
                    client.subscribe(topic, qos);
                } else {
                    logger.warn("客户端未连接，无法订阅主题: {}", topic);
                }
            }
        } catch (MqttException e) {
            logger.error("MqttAcceptClient subscribe error, message: {}", e.getMessage(), e);
        }
    }

    public void unsubscribe(String topic) {
        logger.info("========================【取消订阅主题: {}】========================", topic);
        try {
            synchronized (this) {
                if (client != null && client.isConnected()) {
                    client.unsubscribe(topic);
                } else {
                    logger.warn("客户端未连接，无法取消订阅主题: {}", topic);
                }
            }
        } catch (MqttException e) {
            logger.error("MqttAcceptClient unsubscribe error, message: {}", e.getMessage(), e);
        }
    }
}