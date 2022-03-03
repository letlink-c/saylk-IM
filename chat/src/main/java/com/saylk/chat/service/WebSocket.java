package com.saylk.chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GET请求传入参数
 */

@ServerEndpoint(value = "/websocket/{user}")
@Component
public class WebSocket {

    /**
     * 在线人数
     */

    public static AtomicInteger onlineNumber = new AtomicInteger(0);

    /**
     * 所有的对象，每次建立连接，都会将定义的WebSocket存放到List中，
     */

    public static List<WebSocket> webSockets = new CopyOnWriteArrayList<>();

    /**
     * 会话
     */

    private Session session;

    /**
     * 每个会话的用户
     */

    private String user;

    /**
     * 建立连接
     *
     */

    @OnOpen
    public void onOpen(Session session, @PathParam("user") String user) {
        if (user == null || "".equals(user)) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        onlineNumber.incrementAndGet();
        for (WebSocket WebSocket : webSockets) {
            if (user.equals(WebSocket.user)) {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        this.session = session;
        this.user = user;
        webSockets.add(this);
        System.out.println("有新连接加入,当前在线人数为:" + onlineNumber.get());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        onlineNumber.decrementAndGet();
        webSockets.remove(this);
        System.out.println("有连接关闭,当前在线人数为:" + onlineNumber.get());
    }

    /**
     * 收到消息
     *
     */

    @OnMessage
    public void onMessage(String messages, @PathParam("user") String user) {
        JSONObject jsonObject = (JSONObject) JSON.parse(messages);
        String message = (String) jsonObject.get("datas");
        String uuid = (String) jsonObject.get("uuid");
        if (uuid == null || "".equals(uuid)) {
            pushMessage_public(user, message);
        } else {
            pushMessage_private(user, message, uuid);
        }
    }

    /**
     * 发送消息
     *
     */

    public void sendMessage(String user,String message) {
        try {
            session.getBasicRemote().sendText(user+":"+message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息推送
     *
     */
    public static void pushMessage_private(String user, String message, String uuid) {
            for (WebSocket WebSocket : webSockets) {
                if (uuid.equals(WebSocket.user)) {
                    WebSocket.sendMessage(user,message);
                }
            }
    }

    public static void pushMessage_public(String user, String message){
        for (WebSocket WebSocket : webSockets) {
            WebSocket.sendMessage(user,message);
        }
    }
}
