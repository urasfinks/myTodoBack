package ru.jamsys;

import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PersonUtil {

    public static boolean createPerson(String personKey) {
        if (personKey != null && !"".equals(personKey) && Util.isUUID(personKey) && isPerson(personKey) == null) {
            try {
                Database database = new Database();
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                database.exec("java:/PostgreDS", "insert into person (key_person) values (${key_person})");
                return true;
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return false;
    }

    public static BigDecimal isPerson(String personKey){
        if (personKey != null && !"".equals(personKey) && Util.isUUID(personKey)) {
            try {
                Database database = new Database();
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_person from person where key_person = ${key_person}");
                return (BigDecimal) database.checkFirstRowField(exec, "id_person");
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

}
