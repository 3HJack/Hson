package com.hhh.transform;

import com.hhh.hson.constant.Constants;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JsonClassVisitor extends ClassVisitor {

  private static final String TAG = "JsonClassVisitor:";
  private static final String JSON_DESCRIPTOR = "L" + Constants.JSON_TYPE + ";";
  private static final String STRING_BUILDER_DESCRIPTOR = "Ljava/lang/StringBuilder;";
  private static final String OBJECT_DESCRIPTOR = "Ljava/lang/Object;";
  private static final String JSON_OBJECT_DESCRIPTOR = "Lorg/json/JSONObject;";
  private static final String[] IHSON = new String[]{Constants.IHSON_TYPE};

  private int mVersion;
  private int mAccess;
  private String mName;
  private String mSignature;
  private String mSuperName;
  private String[] mInterfaces;

  private boolean mIsJsonClass;
  private String mDescriptor;

  public JsonClassVisitor(ClassVisitor classVisitor) {
    super(Opcodes.ASM8, classVisitor);
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName,
                    String[] interfaces) {
    super.visit(version, access, name, signature, superName, interfaces);
    mVersion = version;
    mAccess = access;
    mName = name;
    mSignature = signature;
    mSuperName = superName;
    mInterfaces = interfaces;
    mDescriptor = "L" + mName + ";";
  }

  @Override
  public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
    if (descriptor.equals(JSON_DESCRIPTOR) && !existIhsonInterface()) {
      System.out.println(TAG + "visitAnnotation:" + mName + " : " + visible);
      mIsJsonClass = true;
      cv.visit(mVersion, mAccess, mName, mSignature, mSuperName, getInterfaces());
    }
    return super.visitAnnotation(descriptor, visible);
  }

  @Override
  public void visitEnd() {
    super.visitEnd();
    if (mIsJsonClass) {
      addFromJsonMethod();
      addToJsonMethod();
    }
  }

  public boolean isClassModified() {
    return mIsJsonClass;
  }

  private boolean existIhsonInterface() {
    if (mInterfaces == null || mInterfaces.length < 1) {
      return false;
    }
    for (int i = 0; i < mInterfaces.length; ++i) {
      if (IHSON[0].equals(mInterfaces[i])) {
        return true;
      }
    }
    return false;
  }

  private String[] getInterfaces() {
    if (mInterfaces == null || mInterfaces.length < 1) {
      return IHSON;
    }
    String[] interfaces = new String[mInterfaces.length + 1];
    for (int i = 0; i < mInterfaces.length; ++i) {
      interfaces[i] = mInterfaces[i];
    }
    interfaces[mInterfaces.length] = IHSON[0];
    return interfaces;
  }

  private void addFromJsonMethod() {
    System.out.println(TAG + "addFromJsonMethod:" + mName);
    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, Constants.FROM_JSON_METHOD,
        getStringDescriptor(OBJECT_DESCRIPTOR), null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitTypeInsn(Opcodes.CHECKCAST, "org/json/JSONObject");
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, mName + Constants.AUXILIARY_CLASS_NAME_SUFFIX,
        Constants.FROM_JSON_METHOD, getStringDescriptor(JSON_OBJECT_DESCRIPTOR, mDescriptor),
        false);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitInsn(Opcodes.RETURN);
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLocalVariable("this", mDescriptor, null, l0, l2, 0);
    mv.visitLocalVariable("jsonObject", OBJECT_DESCRIPTOR, null, l0, l2, 1);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  private void addToJsonMethod() {
    System.out.println(TAG + "addToJsonMethod:" + mName);
    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, Constants.TO_JSON_METHOD,
        getStringDescriptor(STRING_BUILDER_DESCRIPTOR), null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, mName + Constants.AUXILIARY_CLASS_NAME_SUFFIX,
        Constants.TO_JSON_METHOD, getStringDescriptor(STRING_BUILDER_DESCRIPTOR, mDescriptor),
        false);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitInsn(Opcodes.RETURN);
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLocalVariable("this", mDescriptor, null, l0, l2, 0);
    mv.visitLocalVariable("json", STRING_BUILDER_DESCRIPTOR, null, l0, l2, 1);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  private String getStringDescriptor(String... args) {
    StringBuilder stringBuilder = new StringBuilder("(");
    for (String arg : args) {
      stringBuilder.append(arg);
    }
    stringBuilder.append(")V");
    return stringBuilder.toString();
  }
}
