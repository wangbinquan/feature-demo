package org.wbq.javaagent.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.wbq.javaagent.agent.transformer.Transformer;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AgentMain {
    public static void main(String[] args) {
        System.out.println("Call Main Func");
    }

    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("Call PreMain Func");

        instrumentation.addTransformer(new Transformer());
    }

    public static void agentmain_bak(String arg, Instrumentation instrumentation) {
        System.out.println("Call AgentMain Func");

        instrumentation.addTransformer(new Transformer(), true);

        try {
            instrumentation.retransformClasses(Class.forName("org.wbq.javaagent.client.controller.Hello"));
        } catch (UnmodifiableClassException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void agentmain(String arg, Instrumentation instrumentation) {
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
}
