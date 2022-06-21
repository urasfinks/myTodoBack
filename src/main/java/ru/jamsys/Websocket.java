package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ServerEndpoint("/websocket/{personKey}")
public class Websocket {

    class State {

        final String dataUID;
        final private Map<String, Object> state = new ConcurrentHashMap<>();

        public long getIndexRevision() {
            return indexRevision.get();
        }

        final private AtomicLong indexRevision = new AtomicLong(0);

        State(String dataUID) {
            this.dataUID = dataUID;
            loadFromDb();
        }

        public void update(String key, Object value) {
            boolean upd = false;
            if (!state.containsKey(key) || !state.get(key).equals(value)) {
                upd = true;
                state.put(key, value);
            }
            if (upd) {
                indexRevision.incrementAndGet();
                loadToDb();
            }
        }

        private void loadToDb() {
            try {
                Database database = new Database();
                database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
                database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, new Gson().toJson(state));
                database.addArgument("revision_state_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, indexRevision.get());
                database.exec("java:/PostgreDS", "update data set state_data = ${state_data}, revision_state_data = ${revision_state_data} where uid_data = ${uid_data}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void loadFromDb() {
            try {
                Database database = new Database();
                database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
                database.addArgument("revision_state_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDSR", "select state_data, revision_state_data from data where uid_data = ${uid_data}");
                System.out.println(exec);
                if (exec.size() > 0) {
                    Object revisionStateData = exec.get(0).get("revision_state_data");
                    if (revisionStateData != null && !"".equals(revisionStateData.toString())) {
                        indexRevision.set(Long.parseLong(revisionStateData.toString()));
                    }
                    Object stateData = exec.get(0).get("state_data");
                    if (stateData != null && !"".equals(stateData.toString())) {
                        Map<String, Object> dbState = new Gson().fromJson(stateData.toString(), Map.class);
                        for (String key : dbState.keySet()) {
                            state.put(key, dbState.get(key));
                        }
                    }
                    System.out.println("Load from DB for DataUID = '" + dataUID + "' indexRevision = " + indexRevision.get() + "; state = " + state.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DataRevision {

        private List<Session> sessions = new ArrayList<>();
        final State state;

        public DataRevision(String dataUID) {
            this.state = new State(dataUID);
        }

        public void addSession(Session session) {
            sessions.add(session);
        }

        public void removeSession(Session session) {
            sessions.remove(session);
        }

        public void notify(Session session, String dataUID, Map<String, Object> data) {
            Map<String, Object> jsonData = (Map<String, Object>) data.get("Data");
            state.update(jsonData.get("key").toString(), jsonData.get("value"));

            Map<String, Object> x = new HashMap<>();
            x.put("DataUID", dataUID);
            x.put("Action", Action.UPDATE_REVISION.toString());
            x.put("Revision", state.getIndexRevision());

            try {
                session.getBasicRemote().sendText(new Gson().toJson(x));
            } catch (Exception e) {
                e.printStackTrace();
            }

            data.put("Revision", state.getIndexRevision());
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
                    ", indexRevision=" + state.getIndexRevision() +
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
        System.out.println("mapDataUID: " + mapDataUID + "; mapSession: " + mapSession);
        return null;
    }

    @OnOpen
    public void myOnOpen(@PathParam("personKey") String personKey, Session session) {
        System.out.println("WebSocket opened: " + session.getId() + " by PersonKey: " + personKey);
        if(personKey != null && !"".equals(personKey)){
            try {
                Database database = new Database();
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                database.exec("java:/PostgreDS", "insert into person (key_person) values (${key_person})");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private void unsubscribe(Session session, String dataUID) {
        mapDataUID.get(dataUID).removeSession(session);
        if (mapDataUID.get(dataUID).getSessionSize() == 0) {
            mapDataUID.remove(dataUID);
        }
    }

}
