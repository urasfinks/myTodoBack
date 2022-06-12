package com.example.Test2;

import com.google.gson.Gson;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket")
public class Websocket {

    class DataRevision {

        private List<Session> sessions = new ArrayList<>();
        private double indexRevision = 0;

        public double getIndexRevision() {
            return indexRevision;
        }

        public void setIndexRevision(double indexRevision) {
            this.indexRevision = indexRevision;
        }

        public void addSession(Session session) {
            sessions.add(session);
        }

        public void removeSession(Session session) {
            sessions.remove(session);
        }

        public void notify(Session session, String data){
            for(Session ses: sessions){
                if(!ses.equals(session)){
                    try {
                        ses.getBasicRemote().sendText(data);
                    }catch (Exception e){
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
        Map jsonParses = new Gson().fromJson(message, Map.class);//{DataUID=Opa 2, Action=Subscribe}
        System.out.println("Received : " + jsonParses);
        if (jsonParses.containsKey("DataUID") && jsonParses.containsKey("Action")) {
            String dataUID = (String) jsonParses.get("DataUID");
            Action action = Action.valueOf(((String) jsonParses.get("Action")).toUpperCase());
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
                case UPDATE:
                    if(mapDataUID.containsKey(dataUID)){
                        mapDataUID.get(dataUID).notify(session, message);
                    }
                    break;
            }
        }
        System.out.println("mapDataUID: " + mapDataUID + "; mapSession: " + mapSession);
        return null;
    }

    @OnOpen
    public void myOnOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getId());
    }

    @OnClose
    public void myOnClose(Session session, CloseReason reason) {
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
        List<String> subscribeList = mapSession.remove(session);
        for (String dataUID : subscribeList) {
            mapDataUID.get(dataUID).removeSession(session);
            if (mapDataUID.get(dataUID).getSessionSize() == 0) {
                mapDataUID.remove(dataUID);
            }
        }
    }
}
