package com.hdu.neurostudent_signalflow.service;



import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public interface UnifyDataService {
    // 处理消息头
    void operatorM(Map<String, Object> map);
    // 处理数据体
    void operatorD(Map<String, Object> map,String timestamp);

    void handle(Queue<String> sendQueue);
}

