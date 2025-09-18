package com.hdu.neurostudent_signalflow.devicelister;

import com.hdu.neurostudent_signalflow.adapter.MindToothDeviceAdatper;
import com.hdu.neurostudent_signalflow.utils.websocket.MindtoothWebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/*
*   用于监听mindtooth的数据与阻抗值
*
* */
@Component
public class MindToothDeviceLister extends DeviceLister{
    private static final Logger logger = LoggerFactory.getLogger(MindToothDeviceLister.class);

    @Resource
    MindToothDeviceAdatper mindToothDeviceAdatper;  //mindtooth适配器类
    @Resource
    MindToothDataLister mindToothDataLister;    //mindtooth数据监听类
    @Resource
    MindToothImpLister mindToothImpLister;  //mindtooth阻抗值监听类
    @Resource
    MindToothEnvInit mindToothEnvInit;  //mindtooth环境初始化类
    @Resource
    MindtoothWebSocketClient mindtoothWebSocketClient;  //mindtooth命令转发websocket客户端
    //创建一个队列，用于存储获取的数据，
    BlockingDeque<double[]> recvData = new LinkedBlockingDeque<>(); //TODO 需要查看一下是否可以将队列数减小一些


    @Override
    public void Lister() {
        super.Lister();
        logger.info("[2]:mindtooth初始化程序启动...");
        //启动适配器
        mindToothDeviceAdatper.addData(recvData);
        //启动适配器处理过程
        mindToothDeviceAdatper.processDevice();
        //启动适配器监听过程
//        mindToothDeviceAdatper.distributeQueue();
        //另起一个线程启动环境
        Thread envThread = new Thread(mindToothEnvInit);
        envThread.start();
        //发送读取信息
//        mindtoothWebSocketClient.send("acquisition");   //开始自动开始数据读取
        mindToothDataLister.setRecQueue(recvData);
        mindToothDataLister.setMindToothDeviceAdatper(mindToothDeviceAdatper);
        mindToothImpLister.setRecQueue(recvData);
        //启动数据监听程序
        Thread dataThread = new Thread(mindToothDataLister);
        dataThread.start();
        //启动阻抗值监听程序
        Thread impThread = new Thread(mindToothImpLister);
        impThread.start();
    }





}
