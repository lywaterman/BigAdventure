package com.bad.bigad.manager;

import com.bad.bigad.service.game.GameRoomService;
import com.bad.bigad.util.BridgeForJs;
import com.coveo.nashorn_modules.FilesystemFolder;
import com.coveo.nashorn_modules.Require;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class ScriptManager {
    @Autowired
    private GameRoomService gameRoomService;

    private File rootFile;
    public ScriptEngine scriptEngine;
    FilesystemFolder rootFolder;

    NashornScriptEngineFactory factory;

    public Object callJs(String name, Object... args) {
        try {
            return ((Invocable) scriptEngine).invokeFunction(name, args);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean reload() {
        try {
            Require.enable((NashornScriptEngine)scriptEngine, rootFolder);
            scriptEngine.put("jsb", BridgeForJs.instance);
            scriptEngine.put("gameRoomService", gameRoomService);
            scriptEngine.eval(Files.newBufferedReader(rootFile.toPath()));
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @SneakyThrows
    public boolean initEngine() {
        Path path = FileSystems.getDefault().getPath("").toAbsolutePath();

        log.info(path.toString());

        ScriptEngineManager sm = new ScriptEngineManager();

        for (ScriptEngineFactory f : sm.getEngineFactories()) {
            if (f.getEngineName().equalsIgnoreCase("Oracle Nashorn")) {
                this.factory = (NashornScriptEngineFactory) f;
                break;
            }
        }

        String[] stringArray = new String[]{"-doe", "--global-per-engine"};
        this.scriptEngine = this.factory.getScriptEngine(stringArray);

        //找到js资源文件里的js目录
        //File file = ResourceUtils.getFile("classpath:js");
        File file = ResourceUtils.getFile(path+"/js");
        rootFolder = FilesystemFolder.create(file, "UTF-8");

        //赋予common.js的require能力
        Require.enable((NashornScriptEngine)scriptEngine, rootFolder);
        scriptEngine.put("jsb", BridgeForJs.instance);
        scriptEngine.put("gameRoomService", gameRoomService);

        //得到root.js
        //File rootFile = ResourceUtils.getFile("classpath:js/root.js");
        this.rootFile = ResourceUtils.getFile(path+"/js/root.js");
        //开始编译脚本
//        final CompiledScript compiled = ((Compilable)scriptEngine).compile(new FileReader(rootFile));
//        compiled.eval();
        scriptEngine.eval(Files.newBufferedReader(rootFile.toPath()));

        return true;
    }

    @SneakyThrows
    public ScriptManager() {
        initEngine();

        testEngine();
    }

    @SneakyThrows
    private void testEngine() {
        //测试相关
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
}
