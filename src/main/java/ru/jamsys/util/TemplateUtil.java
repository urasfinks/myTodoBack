package ru.jamsys.util;

import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.util.List;
import java.util.Map;

public class TemplateUtil {

    public static String get(String name) {
        try {
            Database database = new Database();
            database.addArgument("flutter_ui", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("key_ui", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, name);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select flutter_ui from ui where key_ui = ${key_ui}");
            return (String) database.checkFirstRowField(exec, "flutter_ui");
        } catch (Exception e) {
        }
        return null;
    }

    public static void fillTemplate(List<String> l, Map mapTemplate) {
        if (l.size() > 0) {
            String in = "'" + Util.join(l.toArray(new String[0]), "', '") + "'";
            try {
                Database database = new Database();
                database.addArgument("flutter_ui", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("key_ui", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select flutter_ui, key_ui from ui where key_ui in (" + in + ")");
                if (exec.size() > 0) {
                    for (Map<String, Object> item : exec) {
                        if (item.containsKey("key_ui") && item.containsKey("flutter_ui") && mapTemplate.containsKey(item.get("key_ui"))) {
                            mapTemplate.put(item.get("key_ui"), item.get("flutter_ui"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
