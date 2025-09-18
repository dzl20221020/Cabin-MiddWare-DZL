package com.hdu.neurostudent_signalflow.utils.mqtt;

import com.hdu.neurostudent_signalflow.config.MqttProperties;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


//@Component
public class MqttAcceptCallback implements MqttCallbackExtended {

    private static final Logger logger = LoggerFactory.getLogger(MqttAcceptCallback.class);

//    @Autowired
    @Lazy
    private MqttAcceptClient mqttAcceptClient;

//    @Autowired
    private MqttProperties mqttProperties;

    @Override
    public void connectionLost(Throwable throwable) {
        logger.info("连接断开，可以重连", throwable);
        if (MqttAcceptClient.getClient() == null || !MqttAcceptClient.getClient().isConnected()) {
            logger.info("【EMQX重新连接】....................................................");
            mqttAcceptClient.reconnection();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        logger.info("【接收消息主题】: {}", topic);
        logger.info("【接收消息Qos】: {}", mqttMessage.getQos());
        logger.info("【接收消息内容】: {}", new String(mqttMessage.getPayload()));
        // BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\Desktop\\Cabin\\test.txt",true));
        // writer.write(new String(mqttMessage.getPayload()));
        // writer.newLine();
        // writer.close();
        // int i = 1 / 0;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            String[] topics = token.getTopics();
            for (String topic : topics) {
                logger.info("向主题【{}】发送消息成功！", topic);
            }
            MqttMessage message = token.getMessage();
            if (message != null) {
                logger.info("【消息内容】: {}", new String(message.getPayload(), "UTF-8"));
            }
        } catch (Exception e) {
            logger.error("MqttAcceptCallback deliveryComplete error, message: {}", e.getMessage(), e);
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        logger.info("============================= 客户端【{}】连接成功！=============================", MqttAcceptClient.getClient().getClientId());
        if (MqttAcceptClient.getClient().isConnected()) {
//            mqttAcceptClient.subscribe(mqttProperties.getDefaultTopic(), 0);
        } else {
            logger.warn("订阅主题时，客户端未连接！");
        }
    }
}
