package ru.jamsys;

import com.google.gson.Gson;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRevision {
    private List<Session> sessions = new ArrayList<>();
    final DataState state;

    public DataRevision(String dataUID) {
        this.state = new DataState(dataUID);
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    private void sendLoopBackRevisionIndex(Session session, String dataUID, String keyData, long timestamp) {
        if (session != null) {
            Map<String, Object> loopBack = new HashMap<>();
            loopBack.put("DataUID", dataUID);
            loopBack.put("Key", keyData);
            loopBack.put("Time", timestamp);
            loopBack.put("Action", timestamp == -1 ? Action.RELOAD_PAGE.toString() : Action.UPDATE_REVISION.toString());
            loopBack.put("Revision", state.getIndexRevision());
            try {
                session.getBasicRemote().sendText(new Gson().toJson(loopBack));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notify(Session session, String dataUID, Map<String, Object> data) {
        Map<String, Object> jsonData = (Map<String, Object>) data.get("Data");

        if(jsonData.get("value") == null){ //It's remove data
            data.put("Action", Action.RELOAD_PAGE.toString());
        }

        long timestamp = state.update(jsonData.get("key").toString(), jsonData.get("value"));
        sendLoopBackRevisionIndex(session, dataUID, jsonData.get("key").toString(), timestamp);

        data.put("Revision", state.getIndexRevision());
        data.put("Key", jsonData.get("key").toString());
        data.put("Time", timestamp);
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
