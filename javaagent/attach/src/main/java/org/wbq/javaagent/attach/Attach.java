package org.wbq.javaagent.attach;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

public class Attach {
    public static void main(String[] args) throws IOException, AttachNotSupportedException {
        int pid = Integer.parseInt(args[1]);
        VirtualMachine attach = VirtualMachine.attach(String.valueOf(pid));
//        attach.loadAgent();
    }
}
