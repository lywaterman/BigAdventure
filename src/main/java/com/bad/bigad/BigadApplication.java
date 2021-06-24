package com.bad.bigad;

import com.coveo.nashorn_modules.FilesystemFolder;
import com.coveo.nashorn_modules.Folder;
import com.coveo.nashorn_modules.Require;
import com.coveo.nashorn_modules.ResourceFolder;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

enum Helper {
    instance;
    static ExecutorService pool = Executors.newCachedThreadPool();

    public void startJsEngine() throws ScriptException, IOException, NoSuchMethodException {
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

        File file = ResourceUtils.getFile("classpath:js");
        FilesystemFolder rootFolder = FilesystemFolder.create(file, "UTF-8");
        Require.enable((NashornScriptEngine)scriptEngine, rootFolder);

        File rootFile = ResourceUtils.getFile("classpath:js/test.js");

        //scriptEngine.eval(new FileReader("test.js"));
        final CompiledScript compiled = ((Compilable)scriptEngine).compile(new FileReader(rootFile));
        compiled.eval();



        final Invocable invocable = (Invocable) scriptEngine;

        Bindings bindings = new SimpleBindings();
        bindings.put("a", 2.0);

        Double o = null;

        //从脚本取出对象
        ScriptObjectMirror m = (ScriptObjectMirror) invocable.invokeFunction("getGezi");
        System.out.println(m.get("news"));
        //调用对象方法
        Integer iii = (Integer) m.callMember("check", m);
        System.out.println(iii);

        //从脚本取出对象字符串
        String fff = (String) invocable.invokeFunction("getGeziStr");
        System.out.println(fff);
        //用脚本转化字符串为对象
        ScriptObjectMirror m1 = (ScriptObjectMirror) invocable.invokeFunction("getGeziByStr", fff);
        System.out.println(m1.get("news"));

    }

    Helper() {

    }
}

@SpringBootApplication
public class BigadApplication {
    public static void main(String[] args) {
        try {
            Helper.instance.startJsEngine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(BigadApplication.class, args);
    }

}
