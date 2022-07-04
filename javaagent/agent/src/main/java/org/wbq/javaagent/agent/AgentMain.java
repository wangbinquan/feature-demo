package org.wbq.javaagent.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.wbq.javaagent.agent.tools.JarLoader;
import org.wbq.javaagent.agent.transformer.Transformer;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Map;

public class AgentMain {
    public static void main(String[] args) {
        System.out.println("Call Main Func");
        Map<String, byte[]> clazzMap =
                JarLoader.loadJarClass("./javaagent/agent/target/javaagent-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");
        System.out.println(clazzMap.size());
    }

    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("Call PreMain Func");

        instrumentation.addTransformer(new Transformer());
    }

    public static void agentmain(String arg, Instrumentation instrumentation) {
        System.out.println("Call AgentMain Func");

        instrumentation.addTransformer(new Transformer(), true);

        try {
            instrumentation.retransformClasses(Class.forName("org.wbq.javaagent.client.controller.Hello"));
        } catch (UnmodifiableClassException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void agentmain_bakk(String arg, Instrumentation instrumentation) {
        System.out.println("Call AgentMain Func");
        try {
            final ClassPool classPool = ClassPool.getDefault();
            final CtClass clazz = classPool.get("org.wbq.javaagent.client.controller.Hello");
            CtMethod ctMethod = clazz.getDeclaredMethod("hello");
            String methodBody = "return \"Hello Agent Hack World v2\";";
            ctMethod.setBody(methodBody);
            // 返回字节码，并且detachCtClass对象
            byte[] byteCode = clazz.toBytecode();
            //detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
            clazz.detach();
            ClassDefinition classDefinition = new ClassDefinition(Class.forName("org.wbq.javaagent.client.controller.Hello"), byteCode);
            instrumentation.redefineClasses(classDefinition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void agentmain_bakkk(String arg, Instrumentation instrumentation) {
        System.out.println("Call AgentMain Func");
        Map<String, byte[]> clazzMap =
                JarLoader.loadJarClass("./javaagent/client_mock/target/javaagent-client_mock-2.7.1.jar.original");
        System.out.println("Mock Jar With Class: " + clazzMap.size());
        for (Map.Entry<String, byte[]> entry : clazzMap.entrySet()) {
            byte[] classBytes = entry.getValue();

            if (classBytes == null || classBytes.length == 0) {
                System.out.println("Class Error: " + entry.getKey());
                continue;
            }

            try {
                ClassDefinition classDefinition = new ClassDefinition(Class.forName(entry.getKey()), classBytes);
                instrumentation.redefineClasses(classDefinition);
            } catch (ClassNotFoundException | UnmodifiableClassException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
