package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class PersonUtil {

    public static boolean createPerson(String personKey) {
        if (getPerson(personKey) == null) {
            try {
                Database database = new Database();
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                database.exec("java:/PostgreDS", "insert into person (key_person) values (${key_person})");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Person getPerson(BigDecimal idPerson) {
        if (idPerson != null) {
            try {
                Database database = new Database();
                database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPerson);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_chat_telegram from person where id_person = ${id_person}");
                if (exec.size() > 0) {
                    return new Person(idPerson, (BigDecimal) database.checkFirstRowField(exec, "id_chat_telegram"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Person getPerson(String personKey) {
        if (personKey != null && !"".equals(personKey) && Util.isUUID(personKey)) {
            try {
                Database database = new Database();
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_person, id_chat_telegram from person where key_person = ${key_person}");
                if (exec.size() > 0) {
                    return new Person(
                        (BigDecimal) database.checkFirstRowField(exec, "id_person"),
                        (BigDecimal) database.checkFirstRowField(exec, "id_chat_telegram")
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void addIdChatTelegram(RequestContext rc){
        if(rc.idChatTelegram != null && rc.idPerson != null){
            try {
                Database database = new Database();
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
                database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idChatTelegram);
                database.exec("java:/PostgreDS", "update person set id_chat_telegram = ${id_chat_telegram} where id_person = ${id_person}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendTelegram(RequestContext rc, String data) throws IOException {
        if(rc.idChatTelegram != null && !Util.sendTelegram(rc.idChatTelegram.toString(), data)){
            try {
                Database database = new Database();
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
                database.exec("java:/PostgreDS", "update person set id_chat_telegram = null where id_person = ${id_person}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
