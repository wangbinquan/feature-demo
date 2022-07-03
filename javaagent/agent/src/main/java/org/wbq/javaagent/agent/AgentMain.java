package org.wbq.javaagent.agent;

import java.lang.instrument.Instrumentation;

public class AgentMain {
    public static void main(String[] args) {
        System.out.println("Call Main Func");
    }

    public static void premain(String[] args, Instrumentation instrumentation) {
        System.out.println("Call PreMain Func");
    }

    public static void agentmain(String[] args, Instrumentation instrumentation) {
        System.out.println("Call AgentMain Func");
    }
}
