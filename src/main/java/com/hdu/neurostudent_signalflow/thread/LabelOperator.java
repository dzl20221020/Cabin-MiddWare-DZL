package com.hdu.neurostudent_signalflow.thread;

import com.alibaba.fastjson.JSON;
import com.hdu.neurostudent_signalflow.entity.UnifyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public class LabelOperator implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(DataOperator.class);

    // 接受队列
    private BlockingQueue<double[]> revData;
    // 发送队列
    private BlockingQueue<String> sendQueue;

    public LabelOperator(BlockingQueue<double[]> data, BlockingQueue<String> sendQueue) {
        this.revData = data;
        this.sendQueue = sendQueue;
    }

    @Override
    public void run() {
        LabelData2UnifyData();
    }

    public void LabelData2UnifyData(){
        while (true) {
            //判读队列中是否有数据
            if (revData.isEmpty()) {
                //队列为空，则暂停50ms在进行检测
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException (e);
                }
            } else {
                //把数据提取出来，然后封装成对象
                double[] dataArr = revData.poll();
                if (dataArr == null && dataArr.length != 2) {
                    continue;
                }
                //把时间戳取出来
                UnifyData unifyData = new UnifyData("paradigm", "v1.0", "20", "1", "label");
                unifyData.setTimeStamp(String.format("%.0f", dataArr[0]));

                //把数据存储进去
                List<Double> data = new ArrayList<>();
                data.add(dataArr[1]);
                unifyData.data = data;

                //转换成json格式
                String labelJson = JSON.toJSONString(unifyData);
                //加入到发送队列
                try {
                    sendQueue.put(labelJson);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
