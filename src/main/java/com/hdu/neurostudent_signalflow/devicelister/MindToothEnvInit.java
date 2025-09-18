package com.hdu.neurostudent_signalflow.devicelister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
*   功能：监听程序开始前的SDK及LSL-python流初始化
* */
@Component
public class MindToothEnvInit implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(MindToothEnvInit.class);

    private List<Process> processes = new ArrayList<>();


    @Override
    public void run() {
        logger.info("[2-1]:mindtooth硬件环境初始化...");
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("JVM 关闭，正在关闭所有相关的外部进程...");
            for (Process process : processes) {
                process.destroy();
            }
        }));
        //启动SDK
//        startSDK();
        //进行数据流初始化
//        initStream();
        // 启动线程分别执行 initStream() 和 startSDK()
        Thread initStreamThread = new Thread(this::initStream);
        Thread startSDKThread = new Thread(this::startSDK);

        // 启动线程
        initStreamThread.start();
        startSDKThread.start();

        // 等待线程完成
        try {
            initStreamThread.join();
            startSDKThread.join();
        } catch (InterruptedException e) {
            logger.error("线程等待失败", e);
        }


    }

    // 启动mindtooth的python操作程序
    public void initStream(){
        logger.info("[2-1-1]:mindtooth-python操作程序启动...");
        try {
            // 指定可执行文件的路径
            // 获取资源文件的路径
//            ClassPathResource resource = new ClassPathResource("SDK/EegAmpApp/startPython.bat");
//            Path exePath = Paths.get(resource.getURI());

            // 创建 ProcessBuilder 对象
//            // 未打包阶段
//            ProcessBuilder processBuilder = new ProcessBuilder(exePath.toString());
            // 开发阶段
            ProcessBuilder processBuilder = new ProcessBuilder("H:\\doing\\NeuroStudent_SignalFlow\\NeuroStudent_SignalFlow\\src\\main\\resources\\SDK\\EegAmpApp\\startPython.bat");
            //测试阶段
//            ProcessBuilder processBuilder = new ProcessBuilder("H:\\environment\\SDK\\EegAmpApp\\startPython.bat");

            // 将标准错误流合并到标准输出流
            processBuilder.redirectErrorStream(true);

            // 启动外部应用程序
            Process process = processBuilder.start();
            processes.add(process);

            // 读取外部应用程序的输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("External application output: " + line);
            }

            // 等待外部应用程序退出
            int exitCode = process.waitFor();
            System.out.println("External application exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            logger.error("[2-1-1]:mindtooth-python操作程序启动失败...");
            e.printStackTrace();
            initStream();
        }
    }

    // 启动mindtooth的SDK
    public void startSDK(){
        logger.info("[2-1-2]:mindtooth-SDK启动...");
        while (true){
            try {
                // 指定可执行文件的路径
                // 获取资源文件的路径
//            ClassPathResource resource = new ClassPathResource("SDK/EegAmpApp/startEegAmpApp.bat");
//            Path exePath = Paths.get(resource.getURI());
//
//            // 创建 ProcessBuilder 对象
//            // 未打包阶段
//            ProcessBuilder processBuilder = new ProcessBuilder(exePath.toString());
                // 开发阶段
                ProcessBuilder processBuilder = new ProcessBuilder("H:\\doing\\NeuroStudent_SignalFlow\\NeuroStudent_SignalFlow\\src\\main\\resources\\SDK\\EegAmpApp\\startEegAmpApp.bat");
                // 测试阶段
//            ProcessBuilder processBuilder = new ProcessBuilder("H:\\environment\\SDK\\EegAmpApp\\startEegAmpApp.bat");
                // 设置工作目录，如果可执行文件依赖于特定目录中的其他文件
                // processBuilder.directory(new File("path/to/working/directory"));

                // 启动外部应用程序
                Process process = processBuilder.start();
                processes.add(process);
//            initStream();

                // 读取外部应用程序的输出流
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("SDK External application output: " + line);
                }
                // 等待外部应用程序退出
                int exitCode = process.waitFor();
                System.out.println("SDK External application exited with code: " + exitCode);

            } catch (IOException | InterruptedException e) {
                logger.error("[2-1-2]:mindtooth-SDK启动失败...");
                e.printStackTrace();
                startSDK();
            }
        }
    }

    public void shutdown() {
        logger.info("[2-1-3]:mindtooth-SDK及Python操作程序关闭...");

        // 关闭 Python 操作程序
        for (Process process : processes) {
            try {
                // 发送退出命令或者强制杀掉 Python 进程
                process.destroyForcibly();
            } catch (Exception e) {
                logger.error("[2-1-3]:mindtooth-Python操作程序关闭失败...", e);
            }
        }

        // 关闭 SDK
        try {
            // 使用 taskkill 命令强制关闭 SDK 进程
            ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/f", "/im", "eegAmpController.exe");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            logger.info("SDK application exited with code: " + exitCode);
        } catch (Exception e) {
            logger.error("[2-1-3]:mindtooth-SDK关闭失败...", e);
        } finally {
            // 清空进程列表
            processes.clear();
        }
    }

}
