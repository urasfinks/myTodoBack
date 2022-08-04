package ru.jamsys.util;

import com.google.gson.Gson;
import ru.jamsys.RequestContext;
import ru.jamsys.Websocket;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;
import ru.jamsys.sub.PlanNotify;
import ru.jamsys.sub.DataState;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DataUtil {

    public static void updateTimeAdd(long timestamp, String dataUID) {
        try {
            //Такой случай, циклически изменяем дату на подстрижку и автоматом меняется дата создания, что бы корректно сформировать опоыещения
            Database database = new Database();
            database.addArgument("ts", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, timestamp);
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            database.exec("java:/PostgreDS", "update data set time_add_data = to_timestamp(${ts}) where uid_data = ${uid_data}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BigDecimal getIdByUID(String dataUID) {
        try {
            Database database = new Database();
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_data from data where uid_data = ${uid_data}");
            return (BigDecimal) database.checkFirstRowField(exec, "id_data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BigDecimal createTag(String nameTag, BigDecimal idData) {
        if (nameTag == null || "".equals(nameTag)) {
            return null;
        }
        try {
            Database req = new Database();
            req.addArgument("id_tag", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("key_tag", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, nameTag);
            req.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "select insert_tag(${key_tag}, ${id_data}) as id_tag");
            return (BigDecimal) req.checkFirstRowField(exec, "id_tag");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String addData(RequestContext rc, String state, List<String> tags) {
        String dataUID = java.util.UUID.randomUUID().toString();
        try {
            Database req1 = new Database();
            req1.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            req1.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, state);
            req1.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            req1.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            req1.addArgument("id_prj", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idProject);
            List<Map<String, Object>> exec = req1.exec("java:/PostgreDS", "insert into data (id_struct, chmod_data, state_data, id_person, id_group, uid_data, id_prj) values (1, 775, ${state_data}::json, ${id_person}, 1, ${uid_data}, ${id_prj}) RETURNING id_data");
            BigDecimal idData = (BigDecimal) req1.checkFirstRowField(exec, "id_data");
            //System.out.println("ID_DATA: " + idData + " TAGS: " + tags);
            if (idData != null) {
                for (String tag : tags) {
                    createTag(tag, idData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataUID;
    }

    public static String get(RequestContext rc, String dataUID, String def) {
        try {
            Database req = new Database();
            req.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("time_add_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "select state_data, data, to_char(time_add_data, 'dd.MM.yyyy HH24:MI:SS') as time_add_data from data where uid_data = ${uid_data}");
            if (exec.size() > 0 && exec.get(0) != null) {
                return Util.mergeJson(def, new Gson().toJson(exec.get(0)));
            }
            return def;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static void remove(RequestContext rc, String dataUID) {
        try {
            Database req = new Database();
            req.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "delete from \"data\" where id_data IN (\n" +
                    "    select d1.id_data from \"data\" d1\n" +
                    "    inner join tag t1 on t1.id_data = d1.id_data\n" +
                    "    where d1.uid_data = ${uid_data} or t1.key_tag = ${uid_data}\n" +
                    ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateState(String dataUID, Map state, long indexRevision) {
        try {
            Database database = new Database();
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, new Gson().toJson(state));
            database.addArgument("revision_state_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, indexRevision);
            database.exec("java:/PostgreDS", "update data set state_data = ${state_data}::json, revision_state_data = ${revision_state_data} where uid_data = ${uid_data}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateState(RequestContext rc, String dataUID, String json) {
        //Before update on remoteNotify
        String oldComplexDateTime = Util.getComplexDateTime(
                (String) Websocket.getDataRevision(dataUID).getState().get("deadLineDate"),
                (String) Websocket.getDataRevision(dataUID).getState().get("deadLineTime")
        );


        Map<String, Object> map = new Gson().fromJson(json, Map.class);
        for (String key : map.keySet()) {
            if (key != null && !key.startsWith("time_")) {
                Websocket.remoteNotify(rc, dataUID, key, map.get(key));
            }
        }
        //new date from DataState
        String newComplexDateTime = Util.getComplexDateTime(
                (String) map.get("deadLineDate"),
                (String) map.get("deadLineTime")
        );
        //System.out.println("JSON: " + json + "; OLD: " + oldComplexDateTime + "; NEW: " + newComplexDateTime);

        if (map.containsKey("deadLineDate") && !oldComplexDateTime.equals(newComplexDateTime)) {
            long ts = 0;
            BigDecimal idData = null;
            long now = System.currentTimeMillis() / 1000;
            try {
                ts = Util.dateToTimestamp(newComplexDateTime, map.containsKey("deadLineTime") ? "dd.MM.yyyy HH:mm" : "dd.MM.yyyy");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ts > 0) {
                idData = DataUtil.getIdByUID(dataUID);
            }
            if (idData != null) {
                DataUtil.updateTimeAdd(now, dataUID);
                NotifyUtil.remove(idData);
                List<PlanNotify> xx = Util.getPlanNotify(now, ts, "Почистить зубы");
                //TelegramUtil.asyncSend(rc.idPerson, new BigDecimal(1), "YHOOO", ts, idData);
            }
        }
    }



    public static DataState getState(String dataUID) {
        DataState dataState = new DataState();
        try {
            Database database = new Database();
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("revision_state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select state_data, revision_state_data from data where uid_data = ${uid_data}");
            String stateData = (String) database.checkFirstRowField(exec, "state_data");
            if (stateData != null && !"".equals(stateData)) {
                dataState.state = new Gson().fromJson(stateData, Map.class);
                dataState.stateJson = stateData;
            }
            String revisionStateData = (String) database.checkFirstRowField(exec, "revision_state_data");
            if (revisionStateData != null && !"".equals(revisionStateData)) {
                try {
                    dataState.revisionState = Long.parseLong(revisionStateData);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataState;
    }

}
