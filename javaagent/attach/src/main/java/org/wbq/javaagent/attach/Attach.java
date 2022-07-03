package org.wbq.javaagent.attach;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Attach {
    public static void main(String[] args) {
        try {
            for(VirtualMachineDescriptor virtualMachine: VirtualMachine.list()) {
                if ("org.wbq.javaagent.client.ApplicationMain".equals(virtualMachine.displayName())) {
                    int pid = Integer.parseInt(virtualMachine.id());
                    VirtualMachine attach = VirtualMachine.attach(String.valueOf(pid));
                    attach.loadAgent("./javaagent/agent/target/javaagent-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");
                    attach.detach();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
