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

    public void notify(Session session, String dataUID, Map<String, Object> data) {
        Map<String, Object> jsonData = (Map<String, Object>) data.get("Data");
        long timestamp = System.currentTimeMillis();
        state.update(jsonData.get("key").toString(), jsonData.get("value"), timestamp);

        Map<String, Object> x = new HashMap<>();
        x.put("DataUID", dataUID);
        x.put("Action", Action.UPDATE_REVISION.toString());
        x.put("Revision", state.getIndexRevision());
        //x.put("time_"+jsonData.get("key").toString(), timestamp);

        if(session != null){
            try {
                session.getBasicRemote().sendText(new Gson().toJson(x));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
