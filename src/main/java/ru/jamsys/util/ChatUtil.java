package ru.jamsys.util;

import ru.jamsys.RequestContext;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ChatUtil {

    public static void add(BigDecimal idPersonTo, BigDecimal idPersonFrom, String data) {
        try {
            Database database = new Database();
            database.addArgument("id_person_to", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPersonTo);
            database.addArgument("data_chat", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, data);
            database.addArgument("id_person_from", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPersonFrom);
            database.exec("java:/PostgreDS", "insert into chat (id_person_to, data_chat, id_person_from) values (${id_person_to}, ${data_chat}, ${id_person_from})");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCountUnread(RequestContext rc) {
        int ret = 0;
        try {
            Database database = new Database();
            database.addArgument("id_person_to", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            database.addArgument("count", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select count(*) from chat where id_person_to = ${id_person_to} and view_chat = 0");
            BigDecimal count = (BigDecimal) Database.checkFirstRowField(exec, "count");
            if (count != null) {
                ret = count.intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
