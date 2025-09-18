package com.hdu.neurostudent_signalflow.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/*
*   实体父类：定义实体一些公共操作
* */

@Data
@RequiredArgsConstructor
public class Signal implements Serializable {
    //固定成员变量
    // 信息结构
    private final String deviceId;    //设备号
    private final String protocolV;   //协议版本
    private final String smapeRate;   //采样率
    private final String channels;    //通道数
    private final String type;    //信息类别
    //共有时间戳标志
    private String timeStamp;   //时间戳（毫秒级别）

}
