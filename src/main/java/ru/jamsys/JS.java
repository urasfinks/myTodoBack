package ru.jamsys;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import ru.jamsys.database.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Map;

public class JS {

    public static void main(String[] args) throws Exception {
        //SmsAuth smsAuth = SmsAuth.getInstance();
        //smsAuth.generateCode(new BigDecimal(1));
        /*for (int i = 0; i < 60; i++) {
            try {
                Thread.sleep(1000);
                boolean status = smsAuth.checkCode(new BigDecimal(1), "1234");
                System.out.println(status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        /*System.out.println(Util.check("hello1-'", "^[a-zА-Я0-9-]+$"));
        try {
            String code = new String(Files.readAllBytes(Paths.get("src/main/webapp/4.js")));
            System.out.println(JS.runJS(code, "Opa"));
        }catch (Exception e){
            e.printStackTrace();
        }*/

        /*Map d = new Gson().fromJson("{\n" +
                "  \"update_id\": 602443727,\n" +
                "  \"message\": {\n" +
                "    \"message_id\": 4,\n" +
                "    \"from\": {\n" +
                "      \"id\": 290029195,\n" +
                "      \"is_bot\": false,\n" +
                "      \"first_name\": \"\\u042e\\u0440\\u0430 \\u041c\\u0443\\u0445\\u0438\\u043d\",\n" +
                "      \"username\": \"urasfinks\",\n" +
                "      \"language_code\": \"ru\"\n" +
                "    },\n" +
                "    \"chat\": {\n" +
                "      \"id\": 290029195,\n" +
                "      \"first_name\": \"\\u042e\\u0440\\u0430 \\u041c\\u0443\\u0445\\u0438\\u043d\",\n" +
                "      \"username\": \"urasfinks\",\n" +
                "      \"type\": \"private\"\n" +
                "    },\n" +
                "    \"date\": 1658936351,\n" +
                "    \"text\": \"Hello\"\n" +
                "  }\n" +
                "}", Map.class);
        Double x = (Double) Util.selector(d, "message.chat.id", null);
        System.out.println(Util.doubleRemoveExponent(x));*/

        long x = Util.dateToTimestamp("03.08.2022 14:20", "dd.MM.yyyy hh:mm");
        System.out.println(x);
    }

    public static String runJS(String javaScriptCode, String state, RequestContext rc) throws Exception {

        class MyCF implements ClassFilter {
            @Override
            public boolean exposeToScripts(String className) {
                return className.compareTo("ru.jamsys.JS") == 0 || className.compareTo("ru.jamsys.ContentOutput") == 0;
            }
        }
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new MyCF());
        engine.eval(new StringReader(javaScriptCode));
        Invocable invocable = (Invocable) engine;
        ContentOutput response = new ContentOutput();
        invocable.invokeFunction("main", state, rc, response);
        return response.getResponse(rc.idParent);
    }

    public static String test() {
        return "Hello JS JAVA";
    }

    public static boolean isAuth(RequestContext rc) {
        return rc.isAuth();
    }

    public static void logout(RequestContext rc) {
        PersonUtil.logout(rc);
    }

    public static String getTempKeyPerson(RequestContext rc) throws NoSuchProviderException, UnsupportedEncodingException {
        Person p = PersonUtil.getPerson(rc.idPerson);
        if (p != null) {
            return p.tempKeyPerson;
        }
        return null;
    }

    public static String hash(String data, String hashType) throws NoSuchProviderException, UnsupportedEncodingException {
        return Util.getHashCharset(data, hashType, "utf-8");
    }

    public static String sql(String jsonParam) throws Exception {
        List<Map<String, Object>> ret = Database.execJson("java:/PostgreDSR", jsonParam);
        return new Gson().toJson(ret);
    }

    //System.out.println(template("{\"subject\": \"World\", \"test\":{\"x\":\"y\", \"x2\":\"y\"}}", "Hello {{subject}}! {{#each test}} {{@key}} = {{this}} {{/each}}"));
    public static String template(String json, String handlebarsTemplate) {
        try {
            Map<String, Object> map = new Gson().fromJson(json, Map.class);
            Template template = new Handlebars().compileInline(handlebarsTemplate);
            Context context = Context.newBuilder(map).build();
            return template.apply(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getComplexDateTime(String date, String time) {
        StringBuilder sb = new StringBuilder();
        if (date != null) {
            sb.append(date);
        }
        if (time != null) {
            sb.append(" " + time);
        }
        return sb.toString();
    }

    public static void updateDataState(RequestContext rc, String dataUID, String json) {
        //Before update on remoteNotify
        String oldComplexDateTime = getComplexDateTime(
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
        String newComplexDateTime = getComplexDateTime(
                (String) map.get("deadLineDate"),
                (String) map.get("deadLineTime")
        );
        //System.out.println("JSON: " + json + "; OLD: " + oldComplexDateTime + "; NEW: " + newComplexDateTime);

        if (map.containsKey("deadLineDate") && !oldComplexDateTime.equals(newComplexDateTime)) {
            //03.08.2022
            //14:20

            long ts = 0;
            BigDecimal idData = null;
            try {
                ts = Util.dateToTimestamp(newComplexDateTime, map.containsKey("deadLineTime") ? "dd.MM.yyyy hh:mm" : "dd.MM.yyyy");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ts > 0) {
                try {
                    Database database = new Database();
                    database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, dataUID);
                    database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                    List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select id_data from data where uid_data = ${uid_data}");
                    idData = (BigDecimal) database.checkFirstRowField(exec, "id_data");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (idData != null) {
                try {
                    Database database = new Database();
                    database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
                    List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "delete from notify where id_data = ${id_data}");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TelegramUtil.asyncSend(rc.idPerson, new BigDecimal(1), "YHOOO", ts, idData);
            }
        }
    }

    public static void removeData(RequestContext rc, String dataUID) {
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

    public static String getPersonState(RequestContext rc, String def) {
        try {
            String statePerson = PersonUtil.getPersonState(rc);
            return Util.mergeJson(def, statePerson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static String getDataState(ContentOutput content, String dataUID, String def) {
        content.loadState(dataUID);
        if (content.stateJson != null) {
            return Util.mergeJson(def, content.stateJson);
        }
        return def;
    }

    public static String getData(RequestContext rc, String dataUID, String def) {
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

    public static void updatePersonState(RequestContext rc, String json) {
        String statePerson = null;
        try {
            Database req = new Database();
            req.addArgument("state_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, rc.idPerson);
            req.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "select state_person from person where id_person = ${id_person}");
            statePerson = (String) req.checkFirstRowField(exec, "state_person");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newStatePerson = Util.mergeJson(statePerson, json);
        try {
            Database req = new Database();
            req.addArgument("state_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, newStatePerson);
            req.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            List<Map<String, Object>> exec = req.exec("java:/PostgreDS", "update person set state_person = ${state_person}::json where id_person = ${id_person}");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
