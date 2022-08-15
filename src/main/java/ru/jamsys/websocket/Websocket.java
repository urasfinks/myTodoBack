package ru.jamsys.websocket;

import com.google.gson.Gson;
import ru.jamsys.RequestContext;
import ru.jamsys.servlet.Project;
import ru.jamsys.sub.Person;
import ru.jamsys.util.DataUtil;
import ru.jamsys.util.PersonUtil;

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
    static Map<Session, Person> mapPerson = new ConcurrentHashMap<>();

    public static DataRevision getDataRevision(String dataUID) {
        loadDataRevision(dataUID);
        return mapDataUID.get(dataUID);
    }

    static void loadDataRevision(String dataUID) {
        if (!mapDataUID.containsKey(dataUID)) {
            mapDataUID.put(dataUID, new DataRevision(dataUID));
        }
    }

    public static void remoteNotify(RequestContext rc, String dataUID, String key, Object value) {
        loadDataRevision(dataUID);

        Map data = new HashMap();
        data.put("key", key);
        data.put("value", value);

        Map jsonParsed = new HashMap();
        jsonParsed.put("PersonKey", Project.map.get(rc.idPerson));
        jsonParsed.put("DataUID", dataUID);
        jsonParsed.put("Action", Action.UPDATE_STATE.toString());
        jsonParsed.put("Data", data);

        DataRevision dataRevision = mapDataUID.get(dataUID);
        if(dataRevision != null){
            dataRevision.notify(rc.idPerson, null, dataUID, jsonParsed);
        }
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
                        if (mapDataUID.containsKey(dataUID) && mapPerson.containsKey(session)) {
                            mapDataUID.get(dataUID).notify(mapPerson.get(session).idPerson, session, dataUID, jsonParsed);
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
        Person person = PersonUtil.getPerson(personKey);
        System.out.println("WebSocket opened: " + session.getId() + " by PersonKey: " + personKey + "; idPerson: " + person.toString());
        if (person == null || person.idPerson == null) {
            try {
                session.close();
            } catch (Exception e) {
            }
        } else {
            mapPerson.put(session, person);
        }
    }

    @OnClose
    public void myOnClose(Session session, CloseReason reason) {
        //System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
        mapPerson.remove(session);
        List<String> subscribeList = mapSession.remove(session);
        if (subscribeList != null) {
            for (String dataUID : subscribeList) {
                unsubscribe(session, dataUID);
            }
        }
    }

    private void unsubscribe(Session session, String dataUID) {
        DataRevision dataRevision = mapDataUID.get(dataUID);
        if (dataRevision != null) {
            dataRevision.removeSession(session);
            if (dataRevision.getSessionSize() == 0) {
                mapDataUID.remove(dataUID);
            }
        }
    }

}
