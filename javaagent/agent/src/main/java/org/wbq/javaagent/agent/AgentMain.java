package org.wbq.javaagent.agent;

import org.wbq.javaagent.agent.transformer.Transformer;

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

    public static void agentmain(String arg, Instrumentation instrumentation) {
        System.out.println("Call AgentMain Func");

        instrumentation.addTransformer(new Transformer(), true);

        try {
            instrumentation.retransformClasses(Class.forName("org.wbq.javaagent.client.controller.Hello"));
        } catch (UnmodifiableClassException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
