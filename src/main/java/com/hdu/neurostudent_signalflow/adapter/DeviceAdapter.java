package com.hdu.neurostudent_signalflow.adapter;

// 设备适配器接口
public interface DeviceAdapter {

    //数据添加函数
    void addData(Object data);

    // 适配器转换过程（主要是数据格式转换）
    void processDevice();
}
