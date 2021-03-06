package com.bad.bigad;

import com.bad.bigad.service.game.GameRoomService;
import com.coveo.nashorn_modules.FilesystemFolder;
import com.coveo.nashorn_modules.Folder;
import com.coveo.nashorn_modules.Require;
import com.coveo.nashorn_modules.ResourceFolder;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//enum Helper {
//    instance;
//    static ExecutorService pool = Executors.newCachedThreadPool();
//
//    public void startJsEngine() throws ScriptException, IOException, NoSuchMethodException {
//        String currentPath = new java.io.File(".").getCanonicalPath();
//        System.out.println("Current dir:" + currentPath);
//
//        ScriptEngineManager sm = new ScriptEngineManager();
//
//        NashornScriptEngineFactory factory = null;
//        for (ScriptEngineFactory f : sm.getEngineFactories()) {
//            if (f.getEngineName().equalsIgnoreCase("Oracle Nashorn")) {
//                factory = (NashornScriptEngineFactory) f;
//                break;
//            }
//        }
//
//        String[] stringArray = new String[]{"-doe", "--global-per-engine"};
//        ScriptEngine scriptEngine = factory.getScriptEngine(stringArray);
//
//        File file = ResourceUtils.getFile("classpath:js");
//        FilesystemFolder rootFolder = FilesystemFolder.create(file, "UTF-8");
//        Require.enable((NashornScriptEngine)scriptEngine, rootFolder);
//
//        File rootFile = ResourceUtils.getFile("classpath:js/root.js");
//
//        //scriptEngine.eval(new FileReader("root.js"));
//        final CompiledScript compiled = ((Compilable)scriptEngine).compile(new FileReader(rootFile));
//        compiled.eval();
//
//
//
//        final Invocable invocable = (Invocable) scriptEngine;
//
//        Bindings bindings = new SimpleBindings();
//        bindings.put("a", 2.0);
//
//        Double o = null;
//
//        //?????????????????????
//        ScriptObjectMirror m = (ScriptObjectMirror) invocable.invokeFunction("getGezi");
//        System.out.println(m.get("news"));
//        //??????????????????
//        Integer iii = (Integer) m.callMember("check", m);
//        System.out.println(iii);
//
//        //??????????????????????????????
//        String fff = (String) invocable.invokeFunction("getGeziStr");
//        System.out.println(fff);
//        //?????????????????????????????????
//        ScriptObjectMirror m1 = (ScriptObjectMirror) invocable.invokeFunction("getGeziByStr", fff);
//        System.out.println(m1.get("news"));
//
//    }
//
//    Helper() {
//
//    }
//}

@Slf4j
@EnableScheduling
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class BigadApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(BigadApplication.class, args);
    }
}
