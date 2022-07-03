package org.wbq.javaagent.agent.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.List;

public class Transformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if ("org/wbq/javaagent/client/controller/Hello".equals(className)) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            List<MethodNode> methodNodes = classNode.methods;
            for (MethodNode methodNode : methodNodes) {
                if (methodNode.name.equals("hello") && methodNode.desc.equals("()Ljava/lang/String;")) {
                    InsnList insnList = methodNode.instructions;
                    InsnList newInsnList = new InsnList();

                    newInsnList.add(new LdcInsnNode("Hello Agent Hack World"));
                    newInsnList.add(new InsnNode(Opcodes.ARETURN));

                    insnList.clear();
                    insnList.add(newInsnList);
                    methodNode.visitEnd();

                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                    classNode.accept(classWriter);

                    return classWriter.toByteArray();
                }
            }
        }
        return classfileBuffer;
    }
}
