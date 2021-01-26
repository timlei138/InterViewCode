package com.lc;


import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.POP;

public class ActivityOnCreateMethodVisitor extends MethodVisitor {

    public ActivityOnCreateMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitCode() {
        mv.visitLdcInsn("ASMPlugin");
        mv.visitLdcInsn("-------------------- MainActivity onCreate --------------------");
        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;" +
                "Ljava/lang/String;)I", false);
        mv.visitInsn(POP);

        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }
}
