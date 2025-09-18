package com.hdu.neurostudent_signalflow.adapter;

import com.hdu.neurostudent_signalflow.config.MindToothProperties;
import com.hdu.neurostudent_signalflow.service.DataTransmitService;
import com.hdu.neurostudent_signalflow.thread.MindToothOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/*
*   功能：mindtooth适配器类，用于消除mindtooth与统一数据标准之间的不同
* */
@Component
public class MindToothDeviceAdatper extends Device implements DeviceAdapter{
    private static final Logger logger = LoggerFactory.getLogger(MindToothDeviceAdatper.class);

    @Resource
    MindToothProperties mindToothProperties;    //mindtooth配置类
    @Resource
    DataTransmitService dataTransmitService;    //统一数据转发类

    private BlockingQueue<String> sendQuue = new LinkedBlockingQueue<>();   //发送队列

    ExecutorService mindtoothOperatorexecutorService = Executors.newFixedThreadPool(10);

    MindToothOperator mindToothOperator = null;




    // 创建5个数据处理队列，用于每个线程单独进行处理
    List<BlockingDeque<double[]>> processQueue = new ArrayList<>();

    public MindToothDeviceAdatper() {

        //初始化五个队列
        for (int i = 0;i < 5;i++){
            BlockingDeque<double[]> queue = new LinkedBlockingDeque<>();
            this.processQueue.add(queue);
        }
    }



    @Override
    public void addData(Object data) {
        this.data = data;
    }   //设置接收队列

    @Override
    public void processDevice() {

    }

    //我现在有这么一个场景，数据采集程序会从数据源采集数据并压入一个队列中，然后另一个数据处理程序会从
    // 处理队列数据
//    @Async("mindtoothTaskExecutor")
//    public void distributeQueue() {
//            try {
//                double[] data = dataQueue.takeFirst();
//                processData(data);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//    }

    // mindtooth数据处理

    public void processData(double[] data) {
        if (mindToothOperator == null){
            mindToothOperator = new MindToothOperator(sendQuue,mindToothProperties,dataTransmitService);
        }
        mindToothOperator.EEGData2UnifyData(data);
    }

}
