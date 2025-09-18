package com.hdu.neurostudent_signalflow.thread;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hdu.neurostudent_signalflow.service.Impl.UnifyDataServiceImpl;
import com.hdu.neurostudent_signalflow.service.UnifyDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JsonProcessor implements Runnable{
    //待发送的队列
    private BlockingQueue<String> sendQueue;
    //待处理数据的队列
    private BlockingQueue<String> dataQueue;
    //不完整的json
    private StringBuilder incompleteData = new StringBuilder();
    private UnifyDataService unifyDataService = new UnifyDataServiceImpl();

    private static final Logger logger = LoggerFactory.getLogger(JsonProcessor.class);


    //读取队列的锁
    private Lock lock = new ReentrantLock();

    public JsonProcessor(BlockingQueue<String> sendQueue, BlockingQueue<String> dataQueue) {
        this.sendQueue = sendQueue;
        this.dataQueue = dataQueue;
    }

    @Override
    public void run() {
        //进行数据处理
        while (true) {
            try {
                lock.lock();
                String strData = dataQueue.take();
                // 取出时间戳
                String[] dataTemp = strData.split("\\+");
                List<String> dataList = new ArrayList<>();
                List<String> timeList = new ArrayList<>();
                dataList.add(dataTemp[0]);
                timeList.add(dataTemp[1]);

                //一个线程一次性最少取出10个数据进行处理
                for (int i = 0;i < 10 && i < dataQueue.size();i++){
                    strData = dataQueue.poll();

                    // 取出时间戳
                    dataTemp = strData.split("\\+");
                    dataList.add(dataTemp[0]);
                    timeList.add(dataTemp[1]);
                }

                while (!dataList.get(dataList.size()-1).endsWith("\n")){
                    if (dataQueue.isEmpty()){
                        lock.unlock();
                    }
                    lock.lock();
                    //不是完整的json格式，则继续读取
                    strData = dataQueue.take();

                    // 取出时间戳
                    dataTemp = strData.split("\\+");
                    dataList.add(dataTemp[0]);
                    timeList.add(dataTemp[1]);
                }
                //直到取出完整的JSON字符串
                //解开锁
                lock.unlock();

                //进行json处理
                if (dataList.size() == 0) {
                    Thread.sleep(10); // 如果队列中没有数据，暂停一段时间后继续循环
                    continue;
                }
                for (int i = 0;i < dataList.size();i++){
                    String timestamp = null;
                    //进行遍历，对每个数据进行处理
                    if (incompleteData.length() == 0){
                        timestamp = timeList.get(i);
                    }
                    incompleteData.append(dataList.get(i));
                    String str_buffer = incompleteData.toString();

                    boolean changeTime = false;
                    while (str_buffer.contains("\n")) {
                        int endIndex = str_buffer.lastIndexOf("\n") + 1;  // 包含换行符
                        String completeJson = str_buffer.substring(0, endIndex);
                        String[] arr = completeJson.replaceAll("\n+", "\n").split("\n");
                        for (String str1 : arr) {
                            if (changeTime){
                                timestamp = timeList.get(i);
                            }
                            JSONObject jsonObject = null;
                            try{
                                jsonObject = JSON.parseObject(str1);
                            }catch (JSONException e){
                                logger.error("解析串口JSON数据出现错误");
                            }
                            if (null == jsonObject || str1.trim().length() == 0) {
                                break;
                            }
                            Map<String, Object> map = jsonObject.getInnerMap();
                            Set<String> keys = map.keySet();
                            if (keys.contains("data") && keys.contains("header")) {
                                // 头信息处理
                                unifyDataService.operatorM(map);
                                // 处理消息头
                                unifyDataService.operatorD(map,timestamp);
                                // 后续处理
                                unifyDataService.handle(sendQueue);
                            } else {
                                // 异常报文
                                logger.error("串口JSON出现错误:" + str_buffer);
                            }
                            changeTime = true;
                        }
                        // 重新计算缓冲区剩余的字符串（不包括已处理的完整 JSON 数据）
                        str_buffer = str_buffer.substring(endIndex);
                        //把剩余字符串存回缓存字符串中
                        incompleteData.setLength(0);
                        incompleteData.append(str_buffer);
                    }
                }

            } catch (InterruptedException e) {
                logger.error("串口数据解析出现错误");
            }
        }
    }
}
