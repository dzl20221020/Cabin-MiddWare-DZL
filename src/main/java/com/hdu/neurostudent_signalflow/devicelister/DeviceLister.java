package com.hdu.neurostudent_signalflow.devicelister;


/*
*   设备监听器的基类，其实现Runnable接口
* */
public class DeviceLister implements Runnable{

    @Override
    public void run() {
        Lister();
    }

    public void Lister() {
        // 实现特定设备的监听程序代码
    }
}
