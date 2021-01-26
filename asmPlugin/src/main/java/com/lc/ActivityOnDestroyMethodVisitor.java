package com.lc;


import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.POP;

public class ActivityOnDestroyMethodVisitor extends MethodVisitor {


    public ActivityOnDestroyMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        mv.visitLdcInsn("ASMPlugin");
        mv.visitLdcInsn("-------------------- MainActivity onDestroy --------------------");
        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;" +
                "Ljava/lang/String;)I", false);
        mv.visitInsn(POP);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }
}
