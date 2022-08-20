package ru.jamsys.util;

import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;
import ru.jamsys.sub.NotifyObject;
import ru.jamsys.sub.PlanNotify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class NotifyUtil {

    public static void remove(BigDecimal idData) {
        //TODO сделать проверку кто удаляет
        try {
            Database database = new Database();
            database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "delete from notify where id_data = ${id_data}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(NotifyObject notifyObject, String telegramResponse, boolean isDone) {
        PlanNotify planNotify = new PlanNotify(notifyObject.data, notifyObject.timestamp, notifyObject.interval.longValue(), notifyObject.count.intValue());
        boolean isNext = planNotify.next();
        if(isDone == true || isNext == false){
            try {
                Database database = new Database();
                database.addArgument("id_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, notifyObject.id);
                database.addArgument("response", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, telegramResponse);
                database.exec("java:/PostgreDS", "update notify set send_notify = 1, count_notify = 0, response_notify = ${response} where id_notify = ${id_notify}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                Database database = new Database();
                database.addArgument("id_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, notifyObject.id);
                database.addArgument("response", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, telegramResponse);
                database.addArgument("ts", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, planNotify.timestamp);
                database.addArgument("count", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, planNotify.repeat);
                database.exec("java:/PostgreDS", "update notify set response_notify = ${response}, timestamp_notify = to_timestamp(${ts}), count_notify = ${count} where id_notify = ${id_notify}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static NotifyObject getNotify() {
        try {
            Database database = new Database();
            database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("id_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("data_notify", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("interval_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("count_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("timestamp", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "SELECT p1.id_person, \n" +
                    "n1.id_notify, \n" +
                    "n1.data_notify, \n" +
                    "p1.id_chat_telegram, \n" +
                    "n1.id_data,\n" +
                    "n1.interval_notify,\n" +
                    "n1.count_notify,\n" +
                    "extract(epoch from timestamp_notify::TIMESTAMP WITH TIME ZONE)::bigint as timestamp \n" +
                    "FROM notify n1\n" +
                    "inner join person p1 on  n1.id_person_to = p1.id_person\n" +
                    "where n1.send_notify = 0\n" +
                    "and n1.timestamp_notify <= now()::timestamp\n" +
                    "and n1.data_notify is not null\n" +
                    "ORDER BY n1.id_notify ASC");
            if (exec.size() > 0) {
                return new NotifyObject(
                        (BigDecimal) exec.get(0).get("id_person"),
                        (BigDecimal) exec.get(0).get("id_notify"),
                        (String) exec.get(0).get("data_notify"),
                        (BigDecimal) exec.get(0).get("id_chat_telegram"),
                        (BigDecimal) exec.get(0).get("id_data"),
                        (BigDecimal) exec.get(0).get("interval_notify"),
                        (BigDecimal) exec.get(0).get("count_notify"),
                        ((BigDecimal) exec.get(0).get("timestamp")).longValue()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BigDecimal getQueueSize() {
        try {
            Database database = new Database();
            database.addArgument("count", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "SELECT count(*) FROM notify \n" +
                    "where send_notify = 0\n" +
                    "and timestamp_notify <= now()::timestamp");
            BigDecimal bd = (BigDecimal) database.checkFirstRowField(exec, "count");
            if (bd != null) {
                return bd;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

}
