package ru.jamsys;

import com.google.gson.Gson;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import ru.jamsys.database.Database;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class JS {

    public static void main(String[] args) {

    }

    public static Object runJS(String javaScriptCode) throws Exception{

        class MyCF implements ClassFilter {
            @Override
            public boolean exposeToScripts(String className) {
                return className.compareTo("ru.jamsys.JS") == 0;
            }
        }
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new MyCF());
        engine.eval(new StringReader(javaScriptCode));
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction("main");
    }

    public static String test() {
        return "Hello JS JAVA";
    }

    public static String sql(String jsonParam) throws Exception{
        List<Map<String, Object>> ret = Database.execJson("java:/PostgreDSR", jsonParam);
        return new Gson().toJson(ret);
    }

}
