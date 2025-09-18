package com.hdu.neurostudent_signalflow.adapter;


import com.hdu.neurostudent_signalflow.thread.DataOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// 串口设备适配器
@Component
public class ComDeviceAdapter extends Device implements DeviceAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ComDeviceAdapter.class);

    //接收数据队列
    private BlockingQueue<String> recQueue = new LinkedBlockingQueue<>();

    // 防止线程被多次启动
    private volatile boolean isOperatorDataRunning = false;

    @Resource
    private DataOperator dataOperator;

    private String serialPortName;  //当前处理的串口信息

    public void setSerialPort(String serialPortName){

    }

    //数据初始化
    @Override
    public void addData(Object data) {
        this.data = data;
    }

    // 数据转换函数（将串口数据转换给统一格式的数据）
    public void processDevice() {
        //将字节数据转换为字符串数据
        String strData = new String((byte[] )this.data, StandardCharsets.UTF_8);
        //屁股后面加个时间戳
        long timestamp = System.currentTimeMillis();
        strData += "+" + timestamp;
        //数据存入队列
        try {
            recQueue.put(strData);
        } catch (InterruptedException e) {
            logger.error("串口接收数据队列插入出现问题");
        }
        if (!isOperatorDataRunning) {
            dataOperator.setQueue(recQueue);
            dataOperator.operatopr();
            isOperatorDataRunning = true;
        }
    }




}
