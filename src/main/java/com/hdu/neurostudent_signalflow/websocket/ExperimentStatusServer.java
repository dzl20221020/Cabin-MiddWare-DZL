package com.hdu.neurostudent_signalflow.websocket;

import com.hdu.neurostudent_signalflow.utils.ExperimentStatus;
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
@ServerEndpoint("/websocket/experimentStatusServer")
public class ExperimentStatusServer {
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<ExperimentStatusServer> webSocketSet = new CopyOnWriteArraySet<>();

    // 存储客户端ID和对应的WebSocket对象
    private static ConcurrentHashMap<String, ExperimentStatusServer> clientMap = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //当前实验的实验id
    private String experimentId;


    // 客户端ID
    private String clientId;

    public ExperimentStatusServer() {
        System.out.println("启动实验状态控制服务器");
        //新连接时的实验状态为最初状态
        this.experimentId = null;
        ExperimentStatus.status = "WAITING";
    }

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        String queryString = session.getQueryString();

        Map<String, String> params = new ConcurrentHashMap<>();
        if (queryString != null) {
            params = Stream.of(queryString.split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(param -> param[0], param -> param[1]));
        }

        String clientId = params.get("username");
        if (clientId == null) {
            clientId = "UnknownUser"; // 如果没有提供用户名，可以设置一个默认值
        }

        this.clientId = clientId;
        webSocketSet.add(this); // 加入set中
        clientMap.put(clientId, this); // 加入clientMap中
        addOnlineCount(); // 在线数加1
        System.out.println("[状态控制服务器]:有新连接加入！当前在线人数为" + getOnlineCount());
        System.out.println("[状态控制服务器]:新连接的客户端ID：" + clientId);

        //把当前实验的状态转发给新连接的设备
        //如果实验id为空则无需转发
        if (experimentId != null){
            this.sendMessage(experimentId+" "+ExperimentStatus.status);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); // 从set中删除
        clientMap.remove(this.clientId); // 从clientMap中删除
        subOnlineCount(); // 在线数减1
        System.out.println("[状态控制服务器]:有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        String clientId = this.clientId;
        System.out.println("[状态控制服务器]:来自客户端用户" + clientId + "的消息:" + message);
        //只要是信息就要改变服务器状态
        String[] messageArr = message.split(" ");
        //修改数据信息
        experimentId = messageArr[0];
        ExperimentStatus.status = messageArr[1];

//      群发消息
        for (ExperimentStatusServer item : webSocketSet) {
            try {
                System.out.println("[状态控制服务器]:向客户端用户" + clientId + "发送消息:" + message);
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
        System.out.println("[状态控制服务器]:发生错误");
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
        ExperimentStatusServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ExperimentStatusServer.onlineCount--;
    }

    public static CopyOnWriteArraySet<ExperimentStatusServer> getWebSocketSet() {
        return webSocketSet;
    }

    public static void setWebSocketSet(
            CopyOnWriteArraySet<ExperimentStatusServer> webSocketSet) {
        ExperimentStatusServer.webSocketSet = webSocketSet;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
