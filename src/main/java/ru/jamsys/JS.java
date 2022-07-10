package ru.jamsys;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Base64;

public class JS {

    public static void main(String[] args) {
        /*System.out.println(Util.check("hello1-'", "^[a-zА-Я0-9-]+$"));
        try {
            String code = new String(Files.readAllBytes(Paths.get("src/main/webapp/4.js")));
            System.out.println(JS.runJS(code, "Opa"));
        }catch (Exception e){
            e.printStackTrace();
        }*/
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
        return response.toString();
    }

    public static String test() {
        return "Hello JS JAVA";
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

    public static void updateDataState(RequestContext rc, String data_uid, String json) {
        Map<String, Object> map = new Gson().fromJson(json, Map.class);
        for (String key : map.keySet()) {
            Websocket.remoteNotify(rc, data_uid, key, (String) map.get(key));
        }
    }

    public static void addData(RequestContext rc, String json, List<String> tags) {
        try{
            Database database = new Database();
            database.addArgument("data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, json);
            database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idPerson);
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, java.util.UUID.randomUUID().toString());
            database.addArgument("id_prj", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, rc.idProject);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "insert into data (id_struct, chmod_data, data, id_person, id_group, uid_data, id_prj) values (1, 775, ${data}, ${id_person}, 1, ${uid_data}, ${id_prj})");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
