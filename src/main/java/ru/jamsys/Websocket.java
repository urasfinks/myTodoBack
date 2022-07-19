package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.servlet.Project;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/{personKey}")
public class Websocket {

    static Map<String, DataRevision> mapDataUID = new ConcurrentHashMap<>();
    static Map<Session, List<String>> mapSession = new ConcurrentHashMap<>();

    static void remoteNotify(RequestContext rc, String dataUID, String key, Object value){
        if (!mapDataUID.containsKey(dataUID)) {
            mapDataUID.put(dataUID, new DataRevision(dataUID));
        }

        Map data = new HashMap();
        data.put("key", key);
        data.put("value", value);

        Map jsonParsed = new HashMap();
        jsonParsed.put("PersonKey", Project.map.get(rc.idPerson));
        jsonParsed.put("DataUID", dataUID);
        jsonParsed.put("Action", "UPDATE_STATE");
        jsonParsed.put("Data", data);
        mapDataUID.get(dataUID).notify(null, dataUID, jsonParsed);
    }

    @OnMessage
    public String hello(Session session, String message) {
        Map jsonParsed = new Gson().fromJson(message, Map.class);//{DataUID=Opa 2, Action=Subscribe}
        //System.out.println("Received : " + jsonParsed);
        if (jsonParsed.containsKey("DataUID") && jsonParsed.containsKey("Action")) {
            String dataUID = (String) jsonParsed.get("DataUID");
            String actionType = (String) jsonParsed.get("Action");
            if (dataUID == null) {
                return null;
            }
            if (actionType == null) {
                return null;
            }
            try {
                Action action = Action.valueOf((actionType).toUpperCase());
                switch (action) {
                    case SUBSCRIBE:
                        if (!mapDataUID.containsKey(dataUID)) {
                            mapDataUID.put(dataUID, new DataRevision(dataUID));
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.out.println("mapDataUID: " + mapDataUID + "; mapSession: " + mapSession);
        return null;
    }

    @OnOpen
    public void myOnOpen(@PathParam("personKey") String personKey, Session session) {
        //System.out.println("WebSocket opened: " + session.getId() + " by PersonKey: " + personKey);
        if(PersonUtil.isPerson(personKey) == null){
            try {
                session.close();
            }catch (Exception e){}
        }
    }

    @OnClose
    public void myOnClose(Session session, CloseReason reason) {
        //System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
        List<String> subscribeList = mapSession.remove(session);
        for (String dataUID : subscribeList) {
            unsubscribe(session, dataUID);
        }
    }

    private void unsubscribe(Session session, String dataUID) {
        mapDataUID.get(dataUID).removeSession(session);
        if (mapDataUID.get(dataUID).getSessionSize() == 0) {
            mapDataUID.remove(dataUID);
        }
    }

}
