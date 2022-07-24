package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.util.List;
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

    public long update(String key, Object value) {
        long timestamp = -1;
        boolean upd = false;
        if(value == null){
            if(state.containsKey(key)){
                upd = true;
                state.remove(key);
            }
            if(state.containsKey("time_"+key)){
                upd = true;
                state.remove("time_"+key);
            }
        }else if (!state.containsKey(key) || !state.get(key).equals(value)) {
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
        try {
            Database database = new Database();
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, new Gson().toJson(state));
            database.addArgument("revision_state_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, indexRevision.get());
            database.exec("java:/PostgreDS", "update data set state_data = ${state_data}::json, revision_state_data = ${revision_state_data} where uid_data = ${uid_data}");
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
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select state_data, revision_state_data from data where uid_data = ${uid_data}");
            //System.out.println(exec);
            Object revisionStateData = database.checkFirstRowField(exec, "revision_state_data");
            if (revisionStateData != null && !"".equals(revisionStateData)) {
                indexRevision.set(Long.parseLong(revisionStateData.toString()));
            }
            Object stateData = database.checkFirstRowField(exec, "state_data");
            if (stateData != null && !"".equals(stateData)) {
                Map<String, Object> dbState = new Gson().fromJson(stateData.toString(), Map.class);
                for (String key : dbState.keySet()) {
                    state.put(key, dbState.get(key));
                }
            }
            //System.out.println("Load from DB for DataUID = '" + dataUID + "' indexRevision = " + indexRevision.get() + "; state = " + state.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
