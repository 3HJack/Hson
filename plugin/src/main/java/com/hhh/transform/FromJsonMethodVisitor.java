package com.hhh.transform;

import com.hhh.hson.constant.Constants;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class FromJsonMethodVisitor extends AdviceAdapter {

  private static final String TAG = "FromJsonMethodVisitor:";

  public FromJsonMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name,
                               String descriptor) {
    super(api, methodVisitor, access, name, descriptor);
  }

  @Override
  protected void onMethodExit(int opcode) {
    super.onMethodExit(opcode);
    System.out.println(TAG + "onMethodExit:" + opcode);
    Label l0 = new Label();
    Label l1 = new Label();
    Label l2 = new Label();
    mv.visitTryCatchBlock(l0, l1, l2, "org/json/JSONException");
    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLineNumber(9, l3);
    mv.visitVarInsn(ALOAD, 0);
    Label l4 = new Label();
    mv.visitJumpInsn(IFNULL, l4);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
    mv.visitJumpInsn(IFEQ, l4);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitLdcInsn("{}");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
    mv.visitJumpInsn(IFEQ, l0);
    mv.visitLabel(l4);
    mv.visitLineNumber(10, l4);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitInsn(RETURN);
    mv.visitLabel(l0);
    mv.visitLineNumber(13, l0);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitTypeInsn(NEW, "org/json/JSONObject");
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "org/json/JSONObject", "<init>", "(Ljava/lang/String;)V",
        false);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKESTATIC, Constants.HSON_TYPE, Constants.FROM_JSON_METHOD,
        "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
    mv.visitLabel(l1);
    mv.visitLineNumber(16, l1);
    Label l5 = new Label();
    mv.visitJumpInsn(GOTO, l5);
    mv.visitLabel(l2);
    mv.visitLineNumber(14, l2);
    mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"org/json/JSONException"});
    mv.visitVarInsn(ASTORE, 2);
    Label l6 = new Label();
    mv.visitLabel(l6);
    mv.visitLineNumber(15, l6);
    mv.visitTypeInsn(NEW, Constants.HSON_EXCEPTION_TYPE);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitMethodInsn(INVOKESPECIAL, Constants.HSON_EXCEPTION_TYPE, "<init>",
        "(Ljava/lang/Throwable;)V", false);
    mv.visitInsn(ATHROW);
    mv.visitLabel(l5);
    mv.visitLineNumber(17, l5);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitInsn(RETURN);
    Label l7 = new Label();
    mv.visitLabel(l7);
    mv.visitLocalVariable("e", "Lorg/json/JSONException;", null, l6, l5, 2);
    mv.visitLocalVariable("json", "Ljava/lang/String;", null, l3, l7, 0);
    mv.visitLocalVariable("iHson", "Ljava/lang/Object;", null, l3, l7, 1);
  }
}
