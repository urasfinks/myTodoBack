package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonUtil {

    public static boolean createPerson(String personKey) {
        if (getPerson(personKey) == null) {
            try {
                Database database = new Database();
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                database.addArgument("temp_key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, java.util.UUID.randomUUID().toString()); //Открытый ключ, для регистрации через telegram
                database.exec("java:/PostgreDS", "insert into person (key_person, temp_key_person) values (${key_person}, ${temp_key_person})");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getPersonKey(BigDecimal idPerson) {
        if (idPerson != null) {
            try {
                Database database = new Database();
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPerson);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select key_person from person where id_person = ${id_person}");
                if (exec.size() > 0) {
                    return (String) database.checkFirstRowField(exec, "key_person");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Person getPerson(BigDecimal idPerson) {
        if (idPerson != null) {
            try {
                Database database = new Database();
                database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("temp_key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("id_parent", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPerson);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_chat_telegram, temp_key_person, id_parent from person where id_person = ${id_person}");
                if (exec.size() > 0) {
                    return new Person(
                            idPerson,
                            (BigDecimal) database.checkFirstRowField(exec, "id_chat_telegram"),
                            (String) database.checkFirstRowField(exec, "temp_key_person"),
                            (BigDecimal) database.checkFirstRowField(exec, "id_parent")
                    );
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
                database.addArgument("id_parent", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("temp_key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_person, id_chat_telegram, temp_key_person, id_parent  from person where key_person = ${key_person}");
                if (exec.size() > 0) {
                    return new Person(
                        (BigDecimal) database.checkFirstRowField(exec, "id_person"),
                        (BigDecimal) database.checkFirstRowField(exec, "id_chat_telegram"),
                        (String) database.checkFirstRowField(exec, "temp_key_person"),
                        (BigDecimal) database.checkFirstRowField(exec, "id_parent")
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static BigDecimal getIdPersonByTempKeyPerson(String tempKeyPerson){
        if(tempKeyPerson != null){
            try {
                Database database = new Database();
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("temp_key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, tempKeyPerson);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_person from person where temp_key_person = ${temp_key_person}");
                return (BigDecimal) database.checkFirstRowField(exec, "id_person");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static BigDecimal getIdPersonByIdChatTelegram(BigDecimal idChatTelegram){
        if(idChatTelegram != null){
            try {
                Database database = new Database();
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idChatTelegram);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_person from person where id_chat_telegram = ${id_chat_telegram}");
                return (BigDecimal) database.checkFirstRowField(exec, "id_person");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void logout(RequestContext rc){
        try {
            Database database = new Database();
            database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            database.exec("java:/PostgreDS", "update person set id_chat_telegram = null where id_person = ${id_person}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RequestContext getRequestContextByIdPerson(BigDecimal idPerson){
        RequestContext rc = new RequestContext();
        rc.init(idPerson);
        return rc;
    }

    public static void removeIdChatTelegram(BigDecimal idPerson) {
        try {
            Database database = new Database();
            database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPerson);
            database.exec("java:/PostgreDS", "update person set id_chat_telegram = null where id_person = ${id_person}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncSendTelegram(RequestContext rc, String data) {
        BigDecimal idChatTelegram = rc.getIdChatTelegram(System.getProperty("SECRET"));
        Util.syncTendTelegram(idChatTelegram.toString(), data).checkSuccess(rc.idPerson);
    }

    public static String getPersonState(RequestContext rc) {
        try {
            Database req = new Database();
            req.addArgument("state_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            req.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "select state_person from person where id_person = ${id_person}");
            return (String) req.checkFirstRowField(exec, "state_person");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addTelegramInformation(RequestContext rc, String fio){
        /*Что может быть?
        * 1) Такой id_chat уже добавлен
        *   1.1) Тогда надо все данные привязать этой персоне с этим id_chat
        *   1.2) Переслать новый personKey
        * */
        System.out.println(rc.toString());
        BigDecimal idChatTelegram = rc.getIdChatTelegram(System.getProperty("SECRET"));
        if(idChatTelegram != null && rc.idPerson != null){
            BigDecimal allReadyPerson = getIdPersonByIdChatTelegram(idChatTelegram);
            System.out.println("Old person: "+allReadyPerson);
            if(allReadyPerson != null && !rc.idPerson.equals(allReadyPerson)){
                System.out.println("UPD: "+allReadyPerson);
                try {
                    Database database = new Database();
                    database.addArgument("id_new_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
                    database.addArgument("id_old_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, allReadyPerson);
                    database.exec("java:/PostgreDS", "update data set id_person = ${id_old_person} where id_person = ${id_new_person}");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Database database = new Database();
                    database.addArgument("id_new_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
                    database.addArgument("id_old_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, allReadyPerson);
                    database.exec("java:/PostgreDS", "update person set id_parent = ${id_old_person} where id_person = ${id_new_person}");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    String personState = getPersonState(rc);
                    Map x = (personState != null && !"".equals(personState)) ? new Gson().fromJson(personState, Map.class) : new HashMap();
                    if(!x.containsKey("fio") || "".equals(x.get("fio"))){
                        x.put("fio", fio);
                    }
                    personState = new Gson().toJson(x);

                    Database database = new Database();
                    database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
                    database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idChatTelegram);
                    database.addArgument("state_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personState);
                    database.exec("java:/PostgreDS", "update person set id_chat_telegram = ${id_chat_telegram}, state_person = ${state_person}::json where id_person = ${id_person}");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
