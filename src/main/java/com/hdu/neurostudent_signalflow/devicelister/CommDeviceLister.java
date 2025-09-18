package com.hdu.neurostudent_signalflow.devicelister;

import com.hdu.neurostudent_signalflow.datalister.SerialPortLister;
import com.hdu.neurostudent_signalflow.extractor.CommDataExtractor;
import com.hdu.neurostudent_signalflow.utils.SerialPortUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/*
*   串口监听程序，用于监听是否有新串口插入
* */
@Component
public class CommDeviceLister extends DeviceLister{
    private static final Logger logger = LoggerFactory.getLogger(CommDeviceLister.class);

    // 串口数据处理类
    @Resource
    CommDataExtractor commDataExtractor;

    // 正在监听中的串口列表
    public static List<String> serialList = new ArrayList<>();

    @Override
    @Scheduled(fixedRate = 30 * 1000)
    public void Lister() {
        logger.info("串口监听程序正常启动");
        super.Lister();
        //获取可用的串口名称列表
        List<String> portList = SerialPortUtil.getSerialPortList();

        for (String serialPortName : portList) {
            if (!serialList.contains(serialPortName))  {
                SerialPortUtil serialPortUtil = new SerialPortUtil();
                SerialPortLister serialPortLister = new SerialPortLister(commDataExtractor);
                // 未监听过的串口
                //TODO 通知后端此设备已上线，此功能暂时未实现
                serialList.add(serialPortName);
                // 打开串口，并进行监听
                serialPortUtil.setSerialPortLister(serialPortLister);
                serialPortUtil.connectSerialPort(serialPortName);
                logger.info(serialPortName + "串口已连接");
            }
        }
    }
}
