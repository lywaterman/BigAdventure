package com.bad.bigad.manager;

import com.bad.bigad.service.game.GameReelService;
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
    public static ScriptManager INSTANCE;

    @Autowired
    private GameRoomService gameRoomService;

    @Autowired
    private GameReelService gameReelService;

    private File rootFile;
    public ScriptEngine scriptEngine;
    private boolean inited;
    FilesystemFolder rootFolder;

    NashornScriptEngineFactory factory;

    public ScriptManager() {
        INSTANCE = this;
    }

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
        if (!inited) {
            return false;
        }
        try {
            Require.enable((NashornScriptEngine)scriptEngine, rootFolder);
            scriptEngine.put("jsb", BridgeForJs.instance);
            scriptEngine.put("log", log);
            scriptEngine.put("gameRoomService", gameRoomService);
            scriptEngine.put("gameReelService", gameReelService);
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

        //??????js??????????????????js??????
        //File file = ResourceUtils.getFile("classpath:js");
        File file = ResourceUtils.getFile(path+"/js");
        rootFolder = FilesystemFolder.create(file, "UTF-8");

        //??????common.js???require??????
        Require.enable((NashornScriptEngine)scriptEngine, rootFolder);
        scriptEngine.put("jsb", BridgeForJs.instance);
        scriptEngine.put("log", log);
        scriptEngine.put("gameRoomService", gameRoomService);
        scriptEngine.put("gameReelService", gameReelService);

        //??????root.js
        //File rootFile = ResourceUtils.getFile("classpath:js/root.js");
        this.rootFile = ResourceUtils.getFile(path+"/js/root.js");
        //??????????????????
//        final CompiledScript compiled = ((Compilable)scriptEngine).compile(new FileReader(rootFile));
//        compiled.eval();
        scriptEngine.eval(Files.newBufferedReader(rootFile.toPath()));

        inited = true;
        return true;
    }

    @SneakyThrows
    public void testEngine() {
        //????????????
        final Invocable invocable = (Invocable) scriptEngine;

        Bindings bindings = new SimpleBindings();
        bindings.put("a", 2.0);

        Double o = null;

        //?????????????????????
        ScriptObjectMirror m = (ScriptObjectMirror) invocable.invokeFunction("getGezi");
        System.out.println(m.get("news"));
        //??????????????????
        Integer iii = (Integer) m.callMember("check", m);
        System.out.println(iii);

        //??????????????????????????????
        String fff = (String) invocable.invokeFunction("getGeziStr");
        System.out.println(fff);
        //?????????????????????????????????
        ScriptObjectMirror m1 = (ScriptObjectMirror) invocable.invokeFunction("getGeziByStr", fff);
        System.out.println(m1.get("news"));
    }
}
