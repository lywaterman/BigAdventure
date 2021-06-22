package com.bad.bigad;

import com.coveo.nashorn_modules.FilesystemFolder;
import com.coveo.nashorn_modules.Require;
import com.coveo.nashorn_modules.ResourceFolder;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

enum Helper {
    instance;
    static ExecutorService pool = Executors.newCachedThreadPool();
    static String script = "getA(a);";
    Helper() {

    }
}

@SpringBootApplication
public class BigadApplication {
    public static void main(String[] args) throws ScriptException, IOException, NoSuchMethodException {
        String currentPath = new java.io.File(".").getCanonicalPath();
        System.out.println("Current dir:" + currentPath);



        ScriptEngineManager sm = new ScriptEngineManager();

        NashornScriptEngineFactory factory = null;
        for (ScriptEngineFactory f : sm.getEngineFactories()) {
            if (f.getEngineName().equalsIgnoreCase("Oracle Nashorn")) {
                factory = (NashornScriptEngineFactory) f;
                break;
            }
        }

        String[] stringArray = new String[]{"-doe", "--global-per-engine"};
        ScriptEngine scriptEngine = factory.getScriptEngine(stringArray);

        FilesystemFolder rootFolder = FilesystemFolder.create(new File(currentPath), "UTF-8");
        Require.enable((NashornScriptEngine)scriptEngine, rootFolder);

        Require.enable((NashornScriptEngine) scriptEngine, rootFolder);

        //scriptEngine.eval(new FileReader("test.js"));
        final CompiledScript compiled = ((Compilable)scriptEngine).compile(new FileReader("test.js"));
        compiled.eval();



        final Invocable invocable = (Invocable) scriptEngine;

        Bindings bindings = new SimpleBindings();
        bindings.put("a", 2.0);

        Double o = null;
        ScriptObjectMirror m = (ScriptObjectMirror) invocable.invokeFunction("getGezi");
        System.out.println(m.get("news"));

        Integer iii = (Integer) m.callMember("check", m);
        System.out.println(iii);

        String fff = (String) invocable.invokeFunction("getGeziStr");
        System.out.println(fff);


        ScriptObjectMirror m1 = (ScriptObjectMirror) invocable.invokeFunction("getGeziByStr", fff);
        System.out.println(m1.get("news"));



        SpringApplication.run(BigadApplication.class, args);
    }

}
