package com.hdu.neurostudent_signalflow.entity;


import lombok.Data;

import java.util.List;

/*
*   脑电信号
* */


public class UnifyData extends Signal{

    //数据
    public List<Double> data;

    public UnifyData(String deviceId, String protocolV, String smapeRate, String channels, String type) {
        super(deviceId, protocolV, smapeRate, channels, type);
    }
}
