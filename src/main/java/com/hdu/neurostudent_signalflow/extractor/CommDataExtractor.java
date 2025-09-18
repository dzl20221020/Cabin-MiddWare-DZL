package com.hdu.neurostudent_signalflow.extractor;

import com.hdu.neurostudent_signalflow.adapter.ComDeviceAdapter;
import com.hdu.neurostudent_signalflow.utils.ConvertHexStrAndStrUtils;
import com.hdu.neurostudent_signalflow.utils.SerialPortUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
*   串口外部数据处理程序
*   功能：对监听器接收到的串口数据进行处理
*   完成时间：
*
* */
@Component
public class CommDataExtractor {
    private static final Logger logger = LoggerFactory.getLogger(CommDataExtractor.class);

    //串口适配器
    @Resource
    private ComDeviceAdapter comDeviceAdapter;

    public void dataAvailable() {
        try {
            logger.info("进行串口数据处理");
            //当前监听器监听到的串口返回数据 back
            byte[] back = SerialPortUtil.readSerialPortData();
            String str = new String(back, StandardCharsets.UTF_8);
            System.out.println("back-"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))+"--"+str);  //TODO 输出语句

            // 将数据发给适配器
            comDeviceAdapter.addData(back);
            comDeviceAdapter.processDevice();

            //throw new Exception();
        } catch (Exception e) {
            logger.error("串口数据处理程序出错");
        }
    }
}
