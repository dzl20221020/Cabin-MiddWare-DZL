package com.hdu.neurostudent_signalflow.utils.mqtt;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class MqttSendCallBack implements MqttCallbackExtended {

    @Autowired
    @Lazy
    private MqttSendClient mqttSendClient;

    private static final Logger logger = LoggerFactory.getLogger(MqttSendCallBack.class);

    @Override
    public void connectionLost(Throwable cause) {
        logger.error("连接断开，正在尝试重连", cause);
        mqttSendClient.reconnection(); // 断开连接后，手动触发重连
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.info("【接收消息主题】: {}", topic);
        logger.info("【接收消息Qos】: {}", message.getQos());
        logger.info("【接收消息内容】: {}", new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 不再需要额外处理
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect) {
            logger.info("重新连接成功: {}", serverURI);
        } else {
            logger.info("首次连接成功: {}", serverURI);
        }
    }
}
