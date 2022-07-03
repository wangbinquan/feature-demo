package org.wbq.javaagent.attach;

import com.sun.tools.attach.VirtualMachine;

public class Attach {
    public static void main(String[] args) {
        try {
            int pid = Integer.parseInt(args[1]);
            VirtualMachine attach = VirtualMachine.attach(String.valueOf(pid));
            attach.loadAgent("javaagent-client-1.0-SNAPSHOT.jar");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
