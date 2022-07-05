package org.wbq.javaagent.runcode;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class RunCode {
    public static void main(String[] args) {
        try {
            for(VirtualMachineDescriptor virtualMachine: VirtualMachine.list()) {
                if ("org.wbq.javaagent.client.ApplicationMain".equals(virtualMachine.displayName())) {
                    int pid = Integer.parseInt(virtualMachine.id());
                    writeAgentJar();
                    VirtualMachine attach = VirtualMachine.attach(String.valueOf(pid));
                    attach.loadAgent("agent.jar");
                    attach.detach();
                    System.out.println(new String(Files.readAllBytes(Paths.get("out.txt"))));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeAgentJar() throws IOException {
        JarOutputStream jarOutputStream = new JarOutputStream(
                Files.newOutputStream(Paths.get("agent.jar")),
                new Manifest() {{
                    getMainAttributes().putValue("Manifest-Version", "1.0");
                    getMainAttributes().putValue("Agent-Class", "org.wbq.javaagent.runcode.Agent");
                    getMainAttributes().putValue("Premain-Class", "org.wbq.javaagent.runcode.Agent");
                    getMainAttributes().putValue("Can-Redefine-Classes", "true");
                    getMainAttributes().putValue("Can-Retransform-Classes", "true");
                    getMainAttributes().putValue("Can-Set-Native-Method-Prefix", "true");
                }});
        jarOutputStream.putNextEntry(new JarEntry("org/wbq/javaagent/runcode/Agent.class"));
        InputStream inputStream = Agent.class.getResourceAsStream("Agent.class");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int count;
        while ((count = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, count);
        }
        inputStream.close();
        jarOutputStream.write(byteArrayOutputStream.toByteArray());
        jarOutputStream.close();
    }
}

class Agent {
    public static void premain(String arg, Instrumentation instrumentation) throws Exception {
        Field text = Class.forName("org.wbq.javaagent.client.controller.RandomText")
                .getDeclaredField("text");
        text.setAccessible(true);
        BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"));
        writer.write((String) text.get(null));
        writer.close();
    }

    public static void agentmain(String arg, Instrumentation instrumentation) throws Exception {
        premain(arg, instrumentation);
    }
}
