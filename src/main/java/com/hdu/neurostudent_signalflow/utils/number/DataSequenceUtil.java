package com.hdu.neurostudent_signalflow.utils.number;

import java.util.concurrent.atomic.AtomicLong;

public class DataSequenceUtil {

    private static final AtomicLong eegSequenceNumber = new AtomicLong(0);  // EEG数据的递增编号
    private static final AtomicLong labelSequenceNumber = new AtomicLong(0);  // Label数据的递增编号
    private static final Object lock = new Object();  // 同步锁

    // 为EEG数据生成一个递增编号
    public static long generateEegSequenceNumber() {
        synchronized (lock) {
            return eegSequenceNumber.getAndIncrement();
        }
    }

    // 为Label数据生成一个递增编号
    public static long generateLabelSequenceNumber() {
        synchronized (lock) {
            return labelSequenceNumber.getAndIncrement();
        }
    }

    // 重置EEG数据的编号
    public static void resetEegSequenceNumber(long newValue) {
        synchronized (lock) {
            eegSequenceNumber.set(newValue);
        }
    }

    // 重置Label数据的编号
    public static void resetLabelSequenceNumber(long newValue) {
        synchronized (lock) {
            labelSequenceNumber.set(newValue);
        }
    }

    // 获取当前的EEG编号
    public static long getCurrentEegSequenceNumber() {
        synchronized (lock) {
            return eegSequenceNumber.get();
        }
    }

    // 获取当前的Label编号
    public static long getCurrentLabelSequenceNumber() {
        synchronized (lock) {
            return labelSequenceNumber.get();
        }
    }
}
