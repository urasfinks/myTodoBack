package ru.jamsys;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DataState {

    final String dataUID;
    final private Map<String, Object> state = new ConcurrentHashMap<>();

    public long getIndexRevision() {
        return indexRevision.get();
    }

    final private AtomicLong indexRevision = new AtomicLong(0);

    public void setAutoWriteDB(boolean autoWriteDB) {
        this.autoWriteDB = autoWriteDB;
    }

    private boolean autoWriteDB = true;

    DataState(String dataUID) {
        this.dataUID = dataUID;
        loadFromDb();
    }

    public Object get(String key) {
        return state.get(key);
    }

    public long update(String key, Object value) {
        long timestamp = -1;
        boolean upd = false;
        if (value == null) {
            if (state.containsKey(key)) {
                upd = true;
                state.remove(key);
            }
            if (state.containsKey("time_" + key)) {
                upd = true;
                state.remove("time_" + key);
            }
        } else if (!state.containsKey(key) || !state.get(key).equals(value)) {
            timestamp = System.currentTimeMillis();
            upd = true;
            state.put(key, value);
            state.put("time_" + key, timestamp);
        }
        if (upd && autoWriteDB) {
            indexRevision.incrementAndGet();
            writeToDb();
        }
        return timestamp;
    }

    public void writeToDb() {
        DataUtil.updateState(dataUID, state, indexRevision.get());
    }

    private void loadFromDb() {
        DataUtil.State st = DataUtil.getState(dataUID);
        if(st != null){
            indexRevision.set(st.revisionState);
            for (String key : st.state.keySet()) {
                state.put(key, st.state.get(key));
            }
        }
    }

}
