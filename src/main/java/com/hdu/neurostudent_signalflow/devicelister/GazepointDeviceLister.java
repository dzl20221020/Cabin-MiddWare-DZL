package com.hdu.neurostudent_signalflow.devicelister;


import com.hdu.neurostudent_signalflow.adapter.GazePointDeviceAdapter;
import com.hdu.neurostudent_signalflow.config.GazepointProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;



/*
*   眼动仪监听程序
*
* */
@Component
public class GazepointDeviceLister extends DeviceLister{
    private static final Logger logger = LoggerFactory.getLogger(GazepointDeviceLister.class);

    // 眼动仪设备适配器
    @Resource
    GazePointDeviceAdapter gazePointDeviceAdapter;
    @Resource
    GazepointProperties gazepointProperties;

    //接收数据的队列
    Queue<String> recData = new ConcurrentLinkedQueue<>();

    // 获取数据函数
    private void getData(){
        try {
            logger.info("[3-2-1]:数据读取程序启动...");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gazepointProperties.getSocket().getInputStream()));
            String line;
            gazePointDeviceAdapter.addData(recData);
            gazePointDeviceAdapter.processDevice();
            while ((line = bufferedReader.readLine()) != null){
                //适配器进行解析
                //在这里加时间戳
                line += "&";
                line += System.currentTimeMillis();
                recData.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("[3-2-2]:数据读取出错...");
        }

    }

    // 监听函数
    @Override
    public void Lister() {
        super.Lister();
        //实现眼动仪的监听程序
        logger.info("[3]:眼动仪监听程序启动...");
        //监听应用程序端口
        while (true){
            //阻塞while循环
            while (gazepointProperties.getSocket() != null);
            try {
                logger.info("[3-1-1]:尝试监听gazepoint control端口...");
                // 创建连接
                Socket socket = new Socket(gazepointProperties.getHost(),gazepointProperties.getPort());
                gazepointProperties.setSocket(socket);
            } catch (IOException e) {
                logger.error("[3-1-2]:gazepoint control未连接...");
                //等待一段时间再次监听
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    e.printStackTrace();
                    logger.error("[3-1-3]:gazepoint尝试监听出错...");
                }
            }
            if (gazepointProperties.getSocket() != null){
                // 发送指令
                try {
                    OutputStream outputStream = gazepointProperties.getSocket().getOutputStream();
                    outputStream.write("<SET ID=\"ENABLE_SEND_COUNTER\" STATE=\"1\" />\r\n".getBytes());
                    outputStream.write("<SET ID=\"ENABLE_SEND_POG_FIX\" STATE=\"1\" />\r\n".getBytes());
                    outputStream.write("<SET ID=\"ENABLE_SEND_POG_LEFT\" STATE=\"1\" />\r\n".getBytes());
                    outputStream.write("<SET ID=\"ENABLE_SEND_POG_RIGHT\" STATE=\"1\" />\r\n".getBytes());
                    outputStream.write("<SET ID=\"ENABLE_SEND_DATA\" STATE=\"1\" />\r\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("[3-1-4]:gazepoint监听器发送命令出错...");
                }
                //进行数据收取并解析
                getData();
            }
        }

    }
}
