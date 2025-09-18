package com.hdu.neurostudent_signalflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/websocket/w") // 允许跨域访问的路径
                .allowedOrigins("*")        // 允许所有域进行跨域访问，也可以指定特定域
                .allowedMethods("*");       // 允许所有方法进行跨域访问，也可以指定特定方法
    }
    //开启WebSocket的支持，并把该类注入到spring容器中
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
//    private final String wsServerUrl = "ws://127.0.0.1:8888/websocket/display";
//
//    @Bean
//    public WebSocketClient webSocketClient() {
//        try {
//            MyWebSocketClient webSocketClient =
//                    new MyWebSocketClient(new URI(wsServerUrl));
//            webSocketClient.connect();
//            return webSocketClient;
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
