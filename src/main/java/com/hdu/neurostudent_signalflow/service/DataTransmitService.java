package com.hdu.neurostudent_signalflow.service;

import com.hdu.neurostudent_signalflow.utils.mqtt.MqttSendClient;
import com.hdu.neurostudent_signalflow.utils.websocket.MindtoothWebSocketClient;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * 数据转发服务
 * 功能：将数据转发到目标地址，可扩展写
 */
@Component
@Scope("prototype")
public class DataTransmitService implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DataTransmitService.class);

    @Resource
    MqttSendClient mqttSendClient;

    @Resource
    private MindtoothWebSocketClient mindtoothWebSocketClient;

    // 发送队列
    @Setter
    private BlockingQueue<String> sendQueue;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 启动定时任务
    public void start() {
        if (sendQueue.isEmpty()) {
            return;
        }

        while (!sendQueue.isEmpty()) {
            try {
                DataTran(sendQueue.take());
            } catch (InterruptedException e) {
                logger.error("发送队列取出出现问题", e);
                Thread.currentThread().interrupt(); // 重置中断状态
                return;
            }
        }
    }

    // 停止定时任务
    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // 数据转发函数
    public void DataTran(String data) {
        mqttSendClient.publish(false, "test", data);
    }

    @Override
    public void run() {
        logger.info("开启定时任务");
        scheduler.scheduleAtFixedRate(this::start, 0, 100, TimeUnit.MILLISECONDS);
    }
}
