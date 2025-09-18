package com.hdu.neurostudent_signalflow.thread;

import com.alibaba.fastjson.JSON;
import com.hdu.neurostudent_signalflow.config.DataTypeProperties;
import com.hdu.neurostudent_signalflow.config.MindToothProperties;
import com.hdu.neurostudent_signalflow.entity.UnifyData;
import com.hdu.neurostudent_signalflow.service.DataTransmitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static cn.hutool.core.convert.Convert.toDoubleArray;


public class MindToothOperator {
    private static final Logger logger = LoggerFactory.getLogger(MindToothOperator.class);

    private BlockingQueue<String> sendQueue;
    private MindToothProperties mindToothProperties;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private DataTransmitService dataTransmitService;

    int num = 0;

    public MindToothOperator(BlockingQueue<String> sendQueue,MindToothProperties mindToothProperties,DataTransmitService dataTransmitService) {
        this.sendQueue = sendQueue;
        this.mindToothProperties = mindToothProperties;
        this.dataTransmitService = dataTransmitService;
        logger.info("mindtooth处理启动");
    }



    public void EEGData2UnifyData(double[] sig_data) {
        try {
            //                        logger.info("[当前处理线程：]"+Thread.currentThread());

//            logger.info("[当前处理线程：]"+ num++ + "   "+Thread.currentThread()+"处理的数据序号为："+ sig_data[sig_data.length-1]);


            // 处理数据逻辑
            int size = sig_data.length;
            double type = sig_data[size - 2];
            String deviceId = type == mindToothProperties.getImpedance_id() ? mindToothProperties.getImpedance_name() : mindToothProperties.getData_name();
            String channels = type == mindToothProperties.getImpedance_id() ? mindToothProperties.getImpChannels() : mindToothProperties.getDataChannels();
            UnifyData mindTooth = new UnifyData(deviceId, "1.0", mindToothProperties.getSampeRate(), channels, DataTypeProperties.EEG_TYPE);

            double[] realData = Arrays.stream(sig_data, 0, size - 1).toArray();
            List<Double> dataList = new ArrayList<>(Arrays.asList(toDoubleArray(realData)));

            mindTooth.data = dataList.subList(0, size - 2);
            String timestamp_temp = String.format("%.0f", sig_data[size - 1]);
//                        System.out.println(Arrays.toString(sig_data));

            // 转换时间戳
            mindTooth.setTimeStamp(timestamp_temp);

            // 将数据追加写入文件
//            try (FileWriter writer = new FileWriter("eeg_data_comparison2.txt", true)) { // "true" 表示追加模式
//                writer.write(Arrays.toString(sig_data)+ "       " +timestamp_temp + "     " + timestamp_temp + System.lineSeparator()); // 将 EEG 数据转换为字符串并写入文件
//            } catch (IOException e) {
//                e.printStackTrace(); // 处理文件写入异常
//            }

            // 转换成JSON格式
            String mintoothJson = JSON.toJSONString(mindTooth);
            // 加入到发送队列中
            dataTransmitService.DataTran(mintoothJson);
        } catch (Exception e) {
            // 处理线程中断异常
            Thread.currentThread().interrupt();
            logger.error("处理线程被中断", e);
        }
    }

}