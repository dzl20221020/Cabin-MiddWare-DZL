package com.hdu.neurostudent_signalflow.datalister;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.hdu.neurostudent_signalflow.adapter.ComDeviceAdapter;
import com.hdu.neurostudent_signalflow.extractor.CommDataExtractor;
import org.springframework.stereotype.Component;


public class SerialPortLister implements SerialPortDataListener {
    private final CommDataExtractor commDataExtractor;



    public SerialPortLister(CommDataExtractor commDataExtractor) {
        this.commDataExtractor = commDataExtractor;
    }

    @Override
    public int getListeningEvents() { //必须是return这个才会开启串口工具的监听
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (commDataExtractor != null) {
            commDataExtractor.dataAvailable();
        }
    }

}
