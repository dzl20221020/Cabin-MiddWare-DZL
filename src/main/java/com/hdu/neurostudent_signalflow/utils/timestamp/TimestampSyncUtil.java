package com.hdu.neurostudent_signalflow.utils.timestamp;


import java.util.concurrent.atomic.AtomicLong;

public class TimestampSyncUtil {

    private static AtomicLong currentTimestamp;  // 基准时间戳
    private static final Object lock = new Object();  // 用于同步锁
    private static final long stepInMillis = 8;  // 每次时间戳递增的步进

    // 静态代码块初始化
    static {
        resetTimestamp(System.currentTimeMillis());  // 初始化基准时间戳为当前系统时间
    }

    // 重置基准时间戳，使用新的初始值
    public static void resetTimestamp(long newTimestamp) {
        synchronized (lock) {
            currentTimestamp = new AtomicLong(newTimestamp);
        }
    }

    // 生成时间戳，严格按照步进增加
    public static long generateTimestamp() {
        synchronized (lock) {
            return currentTimestamp.getAndAdd(stepInMillis);
        }
    }

    // 获取当前时间戳（只读，不递增）
    public static long getCurrentTimestamp() {
        synchronized (lock) {
            return currentTimestamp.get();
        }
    }
}
