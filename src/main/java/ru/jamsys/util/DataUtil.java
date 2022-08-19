package ru.jamsys.util;

import com.google.gson.Gson;
import ru.jamsys.RequestContext;
import ru.jamsys.websocket.Websocket;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;
import ru.jamsys.sub.PlanNotify;
import ru.jamsys.sub.DataState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataUtil {

    public static String getUIDById(BigDecimal idData) {
        try {
            Database database = new Database();
            database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select uid_data from data where id_data = ${id_data}");
            return (String) database.checkFirstRowField(exec, "uid_data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public static String add(RequestContext rc, String state, List<String> tags) {
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
                    addTag(tag, idData);
                }
            }
            analyzeDataStateOnAddNotify(new Gson().fromJson(state, Map.class), dataUID, rc.idPerson, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataUID;
    }

    public static String getInformation(String dataUID) {
        //Не очень безопасная штука, соотвественно и данные надо выдавать не чувствительные
        Map ret = new HashMap();
        Map data = _get(dataUID);
        Map x = new Gson().fromJson((String) data.get("state_data"), Map.class);
        ret.put("name", x.get("name"));
        ret.put("time", data.get("time_add_data"));
        ret.put("author", PersonUtil.getPersonInformation((BigDecimal) data.get("id_person")));
        return new Gson().toJson(ret);
    }

    private static Map _get(String dataUID) {
        Map ret = new HashMap();
        try {
            Database req = new Database();
            req.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("time_add_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "select state_data, data, to_char(time_add_data, 'dd.MM.yyyy HH24:MI:SS') as time_add_data, id_person from data where uid_data = ${uid_data}");
            if (exec.size() > 0 && exec.get(0) != null) {
                return exec.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String get(RequestContext rc, String dataUID, String def) {
        return isAccess(rc, dataUID) ? Util.mergeJson(def, new Gson().toJson(_get(dataUID))) : def;
    }

    public static void remove(RequestContext rc, String dataUID) {
        //Это всё что касается собственной информации
        try {
            Database req = new Database();
            req.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            req.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "delete from \"data\" where id_data IN (\n" +
                    "    select d1.id_data from \"data\" d1\n" +
                    "    inner join tag t1 on t1.id_data = d1.id_data\n" +
                    "    where (d1.uid_data = ${uid_data} or t1.key_tag = ${uid_data})\n" +
                    "    and d1.id_person = ${id_person}\n" +
                    ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Это всё что касается прилинкованной общей информации
        try {
            Database req = new Database();
            req.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            req.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "delete from data_share ds1 where \n" +
                    "ds1.id_person = ${id_person} \n" +
                    "and id_data IN (\n" +
                    "    select d1.id_data from data d1 \n" +
                    "    where d1.uid_data = ${uid_data}\n" +
                    ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPersonInfoDataShared(RequestContext rc, String dataUID) {
        //В первую очередь надо понять, есть ли у нас вообще возможность получить список расшареных персон
        //Для этого надо проверить, что я есть либо создатель либо сам являюсь расшаренным пользователем
        List<BigDecimal> idPersons = getIdPersonDataShared(dataUID);
        if (idPersons.contains(rc.idPerson)) {
            // Опасные, опасности!) для SQL инъекции, но уж если дженерики обошли рефлексией, я тут просто ничтожество, надеюсь вы меня понимаете
            String joined = idPersons.stream()
                    .map(BigDecimal::toString)
                    .collect(Collectors.joining(", "));
            try {
                Database req = new Database();
                req.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                req.addArgument("temp_key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                req.addArgument("fio", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                req.addArgument("bday", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);

                List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "select \n" +
                        "    p1.id_person, \n" +
                        "    p1.temp_key_person, \n" +
                        "    p1.state_person->>'fio' as fio,\n" +
                        "    p1.state_person->>'bday' as bday\n" +
                        "from person p1 \n" +
                        "where id_person in (" + joined + ") \n" +
                        "order by fio");
                return new Gson().toJson(exec);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "[]";
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
        if (isAccess(rc, dataUID)) {
            Map<String, Object> map = new Gson().fromJson(json, Map.class);
            String oldComplexDateTime = Util.getComplexDateTime(
                    (String) Websocket.getDataRevision(dataUID).getState().get("deadLineDate"),
                    (String) Websocket.getDataRevision(dataUID).getState().get("deadLineTime")
            );
            analyzeDataStateOnAddNotify(map, dataUID, rc.idPerson, oldComplexDateTime);

            for (String key : map.keySet()) {
                if (key != null && !key.startsWith("time_") && !key.startsWith("person_")) {
                    Websocket.remoteNotify(rc, dataUID, key, map.get(key));
                }
            }
        }
    }

    public static DataState getParentState(String dataUID) {
        DataState dataState = new DataState();
        try {
            Database database = new Database();
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select d2.state_data from data d1\n" +
                    "join tag t1 on t1.id_data = d1.id_data\n" +
                    "join data d2 on d2.uid_data = t1.key_tag\n" +
                    "where d1.uid_data = ${uid_data}");
            parseStateData(exec, dataState);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataState;
    }

    public static DataState getParentStateById(BigDecimal idData) {
        DataState dataState = new DataState();
        try {
            Database database = new Database();
            database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
            database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select d2.state_data from data d1\n" +
                    "join tag t1 on t1.id_data = d1.id_data\n" +
                    "join data d2 on d2.uid_data = t1.key_tag\n" +
                    "where d1.id_data = ${id_data}");
            parseStateData(exec, dataState);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataState;
    }

    public static DataState getState(String dataUID) {
        DataState dataState = new DataState();
        try {
            Database database = new Database();
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("revision_state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select state_data, revision_state_data from data where uid_data = ${uid_data}");
            parseStateData(exec, dataState);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataState;
    }

    public static void addSharedPerson(RequestContext rc, String tempPersonKey, String dataUID) {
        if (isAccess(rc, dataUID)) {
            BigDecimal idPerson = PersonUtil.getIdPersonByTempKeyPerson(tempPersonKey);
            BigDecimal idData = getIdByUID(dataUID);
            if (idPerson != null && idData != null) {
                try {
                    Database database = new Database();
                    database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
                    database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPerson);
                    database.addArgument("id_person_action", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
                    List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "insert into data_share (id_data, id_person, id_person_action) values (${id_data}, ${id_person}, ${id_person_action})");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isShared(RequestContext rc, String dataUID) {
        if(isAccess(rc, dataUID)){
            try {
                Database database = new Database();
                database.addArgument("data_uid", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
                database.addArgument("count", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select count(*) from data d1\n" +
                        "inner join data_share ds1 on ds1.id_data = d1.id_data\n" +
                        "where d1.uid_data = ${data_uid}");
                BigDecimal count = (BigDecimal) Database.checkFirstRowField(exec, "count");
                if(count != null && count.intValue() > 0){
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void removeSharedPerson(RequestContext rc, String tempPersonKey, String dataUID) {
        if (isAccess(rc, dataUID)) {
            BigDecimal idPerson = PersonUtil.getIdPersonByTempKeyPerson(tempPersonKey);
            BigDecimal idData = getIdByUID(dataUID);
            if (idPerson != null && idData != null) {
                try {
                    Database database = new Database();
                    database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
                    database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPerson);
                    List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "delete from data_share where id_person = ${id_person} and id_data = ${id_data}");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void analyzeDataStateOnAddNotify(Map<String, Object> newState, String dataUID, BigDecimal idPerson, String oldComplexDateTime) {
        if (newState.containsKey("deadLineDate")) {
            String newComplexDateTime = Util.getComplexDateTime(
                    (String) newState.get("deadLineDate"),
                    (String) newState.get("deadLineTime")
            );
            if (!oldComplexDateTime.equals(newComplexDateTime)) {
                long ts = 0;
                BigDecimal idData = null;
                long now = System.currentTimeMillis() / 1000;
                try {
                    ts = Util.dateToTimestamp(newComplexDateTime, newState.containsKey("deadLineTime") ? "dd.MM.yyyy HH:mm" : "dd.MM.yyyy");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ts > 0) {
                    idData = DataUtil.getIdByUID(dataUID);
                }
                if (idData != null) {
                    DataUtil.updateTimeAdd(now, dataUID);
                    NotifyUtil.remove(idData);
                    //System.out.println(Util.timestampToDate(now, "dd.MM.yyyy HH:mm") +" -> "+Util.timestampToDate(ts, "dd.MM.yyyy HH:mm"));
                    DataState parentState = getParentState(dataUID);
                    String title = parentState.state.get("name") + "/ " + Websocket.getDataRevision(dataUID).getState().get("name");
                    List<PlanNotify> listPlan = Util.getStandardPlanNotify(now, ts, title);
                    //System.out.println(listPlan);
                    for (PlanNotify p : listPlan) {
                        TelegramUtil.asyncSend(idPerson, PersonUtil.systemPerson, p.data, p.timestamp, idData);
                    }
                }
            }
        }
    }

    private static void parseStateData(List<Map<String, Object>> exec, DataState dataState) {
        String stateData = (String) Database.checkFirstRowField(exec, "state_data");
        if (stateData != null && !"".equals(stateData)) {
            dataState.state = new Gson().fromJson(stateData, Map.class);
            dataState.stateJson = stateData;
        }
        String revisionStateData = (String) Database.checkFirstRowField(exec, "revision_state_data");
        if (revisionStateData != null && !"".equals(revisionStateData)) {
            try {
                dataState.revisionState = Long.parseLong(revisionStateData);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private static BigDecimal addTag(String nameTag, BigDecimal idData) {
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

    private static List<BigDecimal> getIdPersonDataShared(String dataUID) {
        List<BigDecimal> ret = new ArrayList<>();
        try {
            Database req = new Database();
            req.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
            req.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "select id_person from data d1\n" +
                    "where d1.uid_data = ${uid_data} \n" +
                    "union all select id_person from data_share where id_data IN (\n" +
                    "    select id_data from data where uid_data = ${uid_data}\n" +
                    ")");
            for (Map<String, Object> item : exec) {
                BigDecimal idPerson = (BigDecimal) item.get("id_person");
                if (idPerson != null) {
                    ret.add(idPerson);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static void updateTimeAdd(long timestamp, String dataUID) {
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

    public static boolean isAccess(RequestContext rc, String dataUID) {
        if (rc == null) {
            return false;
        }
        return isAccess(rc.idPerson, dataUID);
    }

    public static boolean isAccess(BigDecimal idPerson, String dataUID) {
        if (idPerson == null) {
            return false;
        }
        if (dataUID == null) {
            return false;
        }
        List<BigDecimal> listIdPerson = getIdPersonDataShared(dataUID);
        return listIdPerson.contains(idPerson);
    }

}
