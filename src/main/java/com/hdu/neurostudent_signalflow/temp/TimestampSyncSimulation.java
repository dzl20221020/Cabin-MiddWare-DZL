package com.hdu.neurostudent_signalflow.temp;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import java.util.concurrent.atomic.AtomicLong;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimestampSyncSimulation {
    // 基准时间戳，初始为当前系统时间（毫秒级）
    private final AtomicLong currentTimestamp = new AtomicLong(System.currentTimeMillis());
    private final Object lock = new Object();  // 用于同步锁

    // EEG数据时间戳递增，保证8毫秒步进
    public long generateTimestamp() {
        synchronized (lock) {
            return currentTimestamp.getAndAdd(8);  // 每次增加8毫秒
        }
    }

    // 获取当前的时间戳（不递增时间戳，只读）
    public long getCurrentTimestamp() {
        return currentTimestamp.get();
    }

    // 模拟EEG数据线程
    public void eegDataThread() {
        Random random = new Random();
        try {
            while (true) {
                // 模拟不定时接收EEG数据，可能比8毫秒多或者少
                int delay = 6 + random.nextInt(5);  // 随机6到10毫秒的延迟
                Thread.sleep(delay);

                // 生成并递增时间戳
                long timestamp = generateTimestamp();
                System.out.println("EEG Data Received - Timestamp: " + timestamp);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 模拟Label数据线程
    public void labelDataThread() {
        Random random = new Random();
        try {
            while (true) {
                // 模拟随机事件，按键触发标签生成
                int delay = 500 + random.nextInt(1000);  // 每隔0.5到1.5秒随机生成label
                Thread.sleep(delay);

                // 读取当前的时间戳，不影响时间戳的步进
                long timestamp = getCurrentTimestamp();
                System.out.println("Label Event Triggered - Timestamp: " + timestamp);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 主函数，启动两个线程
    public static void main(String[] args) {
        TimestampSyncSimulation simulation = new TimestampSyncSimulation();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 启动EEG数据线程
        executor.execute(simulation::eegDataThread);

        // 启动Label数据线程
        executor.execute(simulation::labelDataThread);

        // 主线程等待执行完成
        executor.shutdown();
    }
}
