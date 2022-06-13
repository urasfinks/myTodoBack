package com.example.Test2;

import com.google.gson.Gson;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ServerEndpoint("/websocket")
public class Websocket {

    class DataRevision {

        private List<Session> sessions = new ArrayList<>();
        private AtomicLong indexRevision = new AtomicLong(0);

        public void addSession(Session session) {
            sessions.add(session);
        }

        public void removeSession(Session session) {
            sessions.remove(session);
        }

        public void notify(Session session, String dataUID, Map<String, Object> data) {

            Map<String, Object> x = new HashMap<>();
            x.put("DataUID", dataUID);
            x.put("Action", "update_revision");
            x.put("Revision", indexRevision.incrementAndGet());

            try {
                session.getBasicRemote().sendText(new Gson().toJson(x));
            } catch (Exception e) {
                e.printStackTrace();
            }

            data.put("Revision", indexRevision.get());
            String dataSend = new Gson().toJson(data);

            for (Session ses : sessions) {
                if (!ses.equals(session)) {
                    try {
                        ses.getBasicRemote().sendText(dataSend);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "DataRevision{" +
                    "sessions=" + sessions +
                    ", indexRevision=" + indexRevision +
                    '}';
        }

        public int getSessionSize() {
            return sessions.size();
        }
    }

    static Map<String, DataRevision> mapDataUID = new ConcurrentHashMap<>();
    static Map<Session, List<String>> mapSession = new ConcurrentHashMap<>();

    @OnMessage
    public String hello(Session session, String message) {
        Map jsonParsed = new Gson().fromJson(message, Map.class);//{DataUID=Opa 2, Action=Subscribe}
        System.out.println("Received : " + jsonParsed);
        if (jsonParsed.containsKey("DataUID") && jsonParsed.containsKey("Action")) {
            String dataUID = (String) jsonParsed.get("DataUID");
            String actionType = (String) jsonParsed.get("Action");
            if(dataUID == null){
                return null;
            }
            if(actionType == null){
                return null;
            }
            try {
                Action action = Action.valueOf((actionType).toUpperCase());
                switch (action) {
                    case SUBSCRIBE:
                        if (!mapDataUID.containsKey(dataUID)) {
                            mapDataUID.put(dataUID, new DataRevision());
                        }
                        mapDataUID.get(dataUID).addSession(session);
                        if (!mapSession.containsKey(session)) {
                            mapSession.put(session, new ArrayList<>());
                        }
                        mapSession.get(session).add(dataUID);
                        break;
                    case UPDATE_STATE:
                        if (mapDataUID.containsKey(dataUID)) {
                            mapDataUID.get(dataUID).notify(session, dataUID, jsonParsed);
                        }
                        break;
                    case UNSUBSCRIBE:
                        mapSession.get(session).remove(dataUID);
                        unsubscribe(session, dataUID);
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("mapDataUID: " + mapDataUID + "; mapSession: " + mapSession);
        return null;
    }

    @OnOpen
    public void myOnOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getId());

        //TODO Если через 30 секунды не прийдёт авторизация, нафиг закрывать такой сокет
    }

    @OnClose
    public void myOnClose(Session session, CloseReason reason) {
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
        List<String> subscribeList = mapSession.remove(session);
        for (String dataUID : subscribeList) {
            unsubscribe(session, dataUID);
        }
    }

    private void unsubscribe(Session session, String dataUID){
        mapDataUID.get(dataUID).removeSession(session);
        if (mapDataUID.get(dataUID).getSessionSize() == 0) {
            mapDataUID.remove(dataUID);
        }
    }
}
