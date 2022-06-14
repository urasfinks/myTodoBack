package ru.jamsys;

import org.openjdk.nashorn.api.scripting.ClassFilter;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.StringReader;

public class JS {

    public static void main(String[] args) {
        runJS();
    }

    public static void runJS() {
        try {
            //ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine("-strict", "--no-java", "--no-syntax-extensions");
            ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine(new ClassFilter() {
                @Override
                public boolean exposeToScripts(String className) {
                    if (className.compareTo("ru.jamsys.JS") == 0) return true;
                    return false;
                }
            });

            engine.eval(new StringReader("function main() { return Java.type('ru.jamsys.JS').bar();}"));
            Invocable invocable = (Invocable) engine;
            Object result = invocable.invokeFunction("main");
            System.out.println(result);
            System.out.println(result.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String bar(){
        return "YOHOO";
    }
}
