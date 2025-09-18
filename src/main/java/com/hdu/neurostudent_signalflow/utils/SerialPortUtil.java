package com.hdu.neurostudent_signalflow.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.hdu.neurostudent_signalflow.adapter.ComDeviceAdapter;
import com.hdu.neurostudent_signalflow.config.CommProperties;
import com.hdu.neurostudent_signalflow.datalister.SerialPortLister;
import com.hdu.neurostudent_signalflow.extractor.CommDataExtractor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/*
*   串口处理工具类
* */

public class SerialPortUtil {


    public static volatile boolean SERIAL_PORT_STATE = false;

    public static volatile SerialPort SERIAL_PORT_OBJECT = null;

    static SerialPortLister serialPortLister;

    public  void setSerialPortLister(SerialPortLister s){
        serialPortLister = s;
    }

    //查找所有可用端口
    public static List<String> getSerialPortList() {
        // 获得当前所有可用串口
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        List<String> portNameList = new ArrayList<String>();
        // 将可用串口名添加到List并返回该List
        for(SerialPort serialPort:serialPorts) {
            System.out.println(serialPort.getSystemPortName());
            portNameList.add(serialPort.getSystemPortName());
        }
        //去重
        portNameList = portNameList.stream().distinct().collect(Collectors.toList());
        return portNameList;
    }

    //  连接串口
    public void connectSerialPort(String portName){
        try {
            SerialPort serialPort = openSerialPort(portName, CommProperties.SERIAL_BAUD_RATE);
            TimeUnit.MILLISECONDS.sleep(2000);
            //给当前串口对象设置监听器
            serialPort.addDataListener(serialPortLister);
            if(serialPort.isOpen()) {
                SERIAL_PORT_OBJECT = serialPort;
                SERIAL_PORT_STATE = true;
                System.out.println(portName+"-- start success");
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


    //  打开串口
    public SerialPort openSerialPort(String portName, Integer baudRate) {
        SerialPort serialPort = SerialPort.getCommPort(portName);
        if (baudRate != null) {
            serialPort.setBaudRate(baudRate);
        }
        if (!serialPort.isOpen()) {  //开启串口
            serialPort.openPort(1000);
        }else{
            return serialPort;
        }
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        serialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
        return serialPort;
    }

    //  关闭串口
    public  void closeSerialPort() {
        if (SERIAL_PORT_OBJECT != null && SERIAL_PORT_OBJECT.isOpen()){
            SERIAL_PORT_OBJECT.closePort();
            SERIAL_PORT_STATE = false;
            SERIAL_PORT_OBJECT = null;
        }
    }

    //  发送字节数组
    public  void sendSerialPortData(byte[] content) {
        if (SERIAL_PORT_OBJECT != null && SERIAL_PORT_OBJECT.isOpen()){
            SERIAL_PORT_OBJECT.writeBytes(content, content.length);
        }
    }

    //  读取字节数组
    public static byte[] readSerialPortData() {
        if (SERIAL_PORT_OBJECT != null && SERIAL_PORT_OBJECT.isOpen()){
            byte[] reslutData = null;
            try {
                if (!SERIAL_PORT_OBJECT.isOpen()){return null;};
                int i=0;
//                while (SERIAL_PORT_OBJECT.bytesAvailable() > 0 && i++ < 5) Thread.sleep(20);
                byte[] readBuffer = new byte[SERIAL_PORT_OBJECT.bytesAvailable()];
                int numRead = SERIAL_PORT_OBJECT.readBytes(readBuffer, readBuffer.length);
                if (numRead > 0) {
                    reslutData = readBuffer;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reslutData;
        }
        return null;
    }

}
