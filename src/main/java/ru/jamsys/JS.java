package ru.jamsys;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import ru.jamsys.database.Database;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

    public static Object runJS(String javaScriptCode, String state) throws Exception{

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
        return invocable.invokeFunction("main", state);
    }

    public static String test() {
        return "Hello JS JAVA";
    }

    public static String sql(String jsonParam) throws Exception{
        List<Map<String, Object>> ret = Database.execJson("java:/PostgreDSR", jsonParam);
        return new Gson().toJson(ret);
    }

    //System.out.println(template("{\"subject\": \"World\", \"test\":{\"x\":\"y\", \"x2\":\"y\"}}", "Hello {{subject}}! {{#each test}} {{@key}} = {{this}} {{/each}}"));
    public static String template(String json, String handlebarsTemplate){
        try {
            Map<String, Object> map = new Gson().fromJson(json, Map.class);
            Template template = new Handlebars().compileInline(handlebarsTemplate);
            Context context = Context.newBuilder(map).build();
            return template.apply(context);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
