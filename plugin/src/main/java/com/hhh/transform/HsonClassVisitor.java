package com.hhh.transform;

import com.hhh.hson.constant.Constants;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HsonClassVisitor extends ClassVisitor {

  private static final String TAG = "HsonClassVisitor:";

  private static final String METHOD_DESCRIPTOR = "(Ljava/lang/String;Ljava/lang/Object;)V";

  public HsonClassVisitor(ClassVisitor classVisitor) {
    super(Opcodes.ASM8, classVisitor);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                   String[] exceptions) {
    MethodVisitor methodVisitor =
        super.visitMethod(access, name, descriptor, signature, exceptions);
    if (Constants.FROM_JSON_METHOD.equals(name) && METHOD_DESCRIPTOR.equals(descriptor)) {
      System.out.println(TAG + "visitMethod");
      return new FromJsonMethodVisitor(Opcodes.ASM8, methodVisitor, access, name, descriptor);
    }
    return methodVisitor;
  }
}
