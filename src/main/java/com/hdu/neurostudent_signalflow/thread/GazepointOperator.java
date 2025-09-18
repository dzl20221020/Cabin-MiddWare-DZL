package com.hdu.neurostudent_signalflow.thread;

import com.alibaba.fastjson.JSON;
import com.hdu.neurostudent_signalflow.config.DataTypeProperties;
import com.hdu.neurostudent_signalflow.entity.GazepointData;
import com.hdu.neurostudent_signalflow.utils.gazepoint.Xmlutil;
import org.w3c.dom.Element;

import java.util.Queue;

/*
*   功能：眼动仪数据处理类，用于处理眼动仪数据转换
* */
public class GazepointOperator implements Runnable{
    // 接受队列
    private Queue<String> data;
    // 发送队列
    private Queue<String> sendQueue;

    public GazepointOperator(Queue<String> data,Queue<String> sendQueue) {
        this.data = data;
        this.sendQueue = sendQueue;
    }

    @Override
    public void run() {
        GazePointData2UnifyData();
    }

    public void GazePointData2UnifyData() {
        //进行数据转换
        Queue<String> revData = this.data;
        while (true) {
            //判读队列中是否有数据
            if (revData.isEmpty()) {
                //队列为空，则暂停50ms在进行检测
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                //把数据提取出来，然后封装成对象
                String dataStr = revData.poll();
                if (data == null){
                    continue;
                }
                //把时间戳取出来
                if (dataStr == null){
                    continue;
                }
                String[] dataArray = dataStr.split("&");
                String dataStr2 = dataArray[0];
                String timestamp = dataArray[1];
                Element root = Xmlutil.parseXml(dataStr2);
                if (root.getTagName().equalsIgnoreCase("REC")){
                    GazepointData gazepointData = new GazepointData("gazepoint","1.0","60","-1", DataTypeProperties.EOG_TYPE);

                    //提取属性值
                    gazepointData.cnt = root.getAttribute("CNT");
                    gazepointData.fpogx = root.getAttribute("FPOGX");
                    gazepointData.fpogy = root.getAttribute("FPOGY");
                    gazepointData.fpogs = root.getAttribute("FPOGS");
                    gazepointData.fpogd = root.getAttribute("FPOGD");
                    gazepointData.fpogid = root.getAttribute("FPOGID");
                    gazepointData.fpogv = root.getAttribute("FPOGV");
                    gazepointData.lpogx = root.getAttribute("LPOGX");
                    gazepointData.lpogy = root.getAttribute("LPOGY");
                    gazepointData.lpogv = root.getAttribute("LPOGV");
                    gazepointData.rpogx = root.getAttribute("RPOGX");
                    gazepointData.rpogy = root.getAttribute("RPOGY");
                    gazepointData.rpogv = root.getAttribute("RPOGV");

                    gazepointData.setTimeStamp(timestamp);
                    //转换成JSON格式
                    String gazepointDataStr = JSON.toJSONString(gazepointData);
                    //加入到发送队列中
                    sendQueue.offer(gazepointDataStr);
                }
            }

        }
    }
}
