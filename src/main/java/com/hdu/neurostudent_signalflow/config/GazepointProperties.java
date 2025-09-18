package com.hdu.neurostudent_signalflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component
@Data
@ConfigurationProperties("gazepoint")
public class GazepointProperties {
    private Socket socket;   // 与gazepoint control的套接字对象

    private String host;  //gazepoint control所在主机地址
    private int port;  //gazepoint control服务的端口
}
