package com.example.Test2;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket")
public class Websocket {
    static Map<String, Session> listSession = new ConcurrentHashMap<>();

    @OnMessage
    public String hello(String message) {
        System.out.println("Received : " + message);
        return null;
    }

    @OnOpen
    public void myOnOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getId());
        listSession.put(session.getId(), session);
        /*try {
            session.getBasicRemote().sendText("Hello: "+session.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @OnClose
    public void myOnClose(Session session, CloseReason reason) {
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
        listSession.remove(session.getId());
    }
}
