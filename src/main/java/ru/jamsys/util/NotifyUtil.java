package ru.jamsys.util;

import ru.jamsys.TelegramResponse;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;
import ru.jamsys.sub.NotifyObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class NotifyUtil {

    public static void remove(BigDecimal idData) {
        try {
            Database database = new Database();
            database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "delete from notify where id_data = ${id_data}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(NotifyObject notifyObject, TelegramResponse telegramResponse) {
        try {
            Database database = new Database();
            database.addArgument("id_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, notifyObject.id);
            database.addArgument("response", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, telegramResponse.resp);
            database.exec("java:/PostgreDS", "update notify set send_notify = 1, response_notify = ${response} where id_notify = ${id_notify}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NotifyObject getNotify() {
        try {
            Database database = new Database();
            database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("id_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("data_notify", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "SELECT p1.id_person, n1.id_notify, n1.data_notify, p1.id_chat_telegram FROM notify n1\n" +
                    "inner join person p1 on  n1.id_person_to = p1.id_person\n" +
                    "where n1.send_notify = 0\n" +
                    "and n1.timestamp_notify <= now()::timestamp\n" +
                    "and p1.id_chat_telegram is not null\n" +
                    "and n1.data_notify is not null\n" +
                    "ORDER BY n1.id_notify ASC");
            if (exec.size() > 0) {
                return new NotifyObject(
                        (BigDecimal) exec.get(0).get("id_person"),
                        (BigDecimal) exec.get(0).get("id_notify"),
                        (String) exec.get(0).get("data_notify"),
                        (BigDecimal) exec.get(0).get("id_chat_telegram")
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
