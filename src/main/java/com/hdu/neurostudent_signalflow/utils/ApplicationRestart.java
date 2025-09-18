package com.hdu.neurostudent_signalflow.utils;

import com.hdu.neurostudent_signalflow.NeuroStudentSignalFlowApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;


public class ApplicationRestart {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationRestart.class);

    // 开发阶段时候的逻辑
    public static void restart(String[] args) {
        // 通过运行时重启
        try {
            String java = System.getProperty("java.home") + "/bin/java";
            String classpath = System.getProperty("java.class.path");
            String mainClass = "com.hdu.neurostudent_signalflow.NeuroStudentSignalFlowApplication"; // 替换为你的主类

            ProcessBuilder processBuilder = new ProcessBuilder(java, "-cp", classpath, mainClass);
            processBuilder.start();
        } catch (IOException e) {
            logger.error("Failed to restart application", e);
        }
        // 退出当前进程
        System.exit(1);
    }

    // 打包后的逻辑代码
//    public static void restart(String[] args) {
//        try {
//            // 获取 JAR 文件路径
////            URL jarUrl = ApplicationRestart.class.getProtectionDomain().getCodeSource().getLocation();
////            File jarFile = new File(jarUrl.toURI());
//            String jarFile = "H:\\doing\\NeuroStudent_SignalFlow\\NeuroStudent_SignalFlow\\target\\NeuroStudent_SignalFlow-0.0.1-SNAPSHOT.jar";
//            System.out.println(jarFile);
//            // 构建重启命令
//            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarFile);
//
//            // 如果有需要传递的命令行参数
//            if (args != null) {
//                processBuilder.command().addAll(List.of(args));
//            }
//
//            // 启动新进程
//            Process process = processBuilder.start();
//            logger.info("Application restart initiated.");
//
//            // 可选：等待新进程启动完成
////            process.waitFor();
//        } catch (IOException e) {
//            logger.error("Failed to restart application", e);
//        }
//
//        // 退出当前进程
//        System.exit(1);
//    }


}