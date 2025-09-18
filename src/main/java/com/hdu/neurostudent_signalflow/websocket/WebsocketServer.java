package com.hdu.neurostudent_signalflow.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 *   这个websocket服务器专用于
 *
 * */

@Component
@ServerEndpoint("/websocket/w")
public class WebsocketServer {
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebsocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    // 存储客户端ID和对应的WebSocket对象
    private static ConcurrentHashMap<String, WebsocketServer> clientMap = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 客户端ID
    private String clientId;

    public WebsocketServer() {
        System.out.println("启动websocket服务器");
    }

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        String clientId = UUID.randomUUID().toString(); // 使用随机生成的 UUID 作为客户端 ID
        String queryString = session.getQueryString();

        Map<String, String> params = new ConcurrentHashMap<>();
        if (queryString != null) {
            params = Stream.of(queryString.split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(param -> param[0], param -> param[1]));
        }

        String username = params.get("username");
        if (username == null) {
            username = "UnknownUser"; // 如果没有提供用户名，可以设置一个默认值
        }

        System.out.println("用户 " + username + " 连接成功！");
        this.clientId = clientId;
        webSocketSet.add(this); // 加入set中
        clientMap.put(clientId, this); // 加入clientMap中
        addOnlineCount(); // 在线数加1
        System.out.println("[数据传输服务器]:有新连接加入！当前在线人数为" + getOnlineCount());
        System.out.println("[数据传输服务器]:新连接的客户端ID：" + clientId);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); // 从set中删除
        clientMap.remove(this.clientId); // 从clientMap中删除
        subOnlineCount(); // 在线数减1
        System.out.println("[数据传输服务器]:有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        String clientId = this.clientId;
        System.out.println("[数据传输服务器]:来自客户端用户" + clientId + "的消息:" + message);
        // 群发消息
        for (WebsocketServer item : webSocketSet) {
            try {
                System.out.println("[数据传输服务器]:向客户端用户" + clientId + "发送消息:" + message);
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("[数据传输服务器]:发生错误");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebsocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebsocketServer.onlineCount--;
    }

    public static CopyOnWriteArraySet<WebsocketServer> getWebSocketSet() {
        return webSocketSet;
    }

    public static void setWebSocketSet(
            CopyOnWriteArraySet<WebsocketServer> webSocketSet) {
        WebsocketServer.webSocketSet = webSocketSet;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
