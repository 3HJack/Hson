package com.hhh.processor.method;

import com.hhh.hson.Hson;
import com.hhh.hson.annotation.Json;
import com.hhh.hson.constant.Constants;
import com.hhh.processor.HsonProcessor;
import com.hhh.processor.utils.ProcessorUtils;
import com.hhh.processor.utils.ReflectUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

public class FromJsonMethodCreator extends MethodCreator {

  private static final String JSON_OBJECT_NAME = "jsonObject";
  private static final String JSON_KEYS_NAME = "jsonKeys";
  private static final String JSON_KEY_NAME = "jsonKey";

  private static Object sNoType;

  public FromJsonMethodCreator(HsonProcessor processor) {
    super(processor);
  }

  public MethodSpec process(TypeElement typeElement) {
    return MethodSpec.methodBuilder(Constants.FROM_JSON_METHOD)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(ProcessorUtils.JSON_OBJECT, JSON_OBJECT_NAME)
        .addParameter(ClassName.get(typeElement), ELEMENT_NAME)
        .addCode(createCodeBlock(typeElement))
        .build();
  }

  private CodeBlock createCodeBlock(TypeElement typeElement) {
    CodeBlock.Builder builder = CodeBlock.builder();

    // 两个临时变量
    builder.addStatement("$T $L", String.class, JSON_KEY_NAME).addStatement("$T $L = new $T<>()",
        ParameterizedTypeName.get(List.class, String.class), JSON_KEYS_NAME, ArrayList.class);

    // 处理当前对象变量
    ProcessorUtils.getSerializableFields(typeElement).forEach(variableElement -> {
      List<String> jsonKeys = ProcessorUtils.getJsonKeys(variableElement);

      if (jsonKeys.size() == 1) {
        createStatement(builder, variableElement, jsonKeys.get(0));
      } else {
        getJsonKey(builder, jsonKeys);
        createStatement(builder, variableElement, null);
      }
    });

    // 处理基类
    typeElement = (TypeElement) mProcessor.getTypeElement(typeElement.getSuperclass());
    while (typeElement.getAnnotation(Json.class) != null) {
      builder.addStatement("$T.$L($L, $L)", mProcessor.getClassName(typeElement),
          Constants.FROM_JSON_METHOD, JSON_OBJECT_NAME, ELEMENT_NAME);
      typeElement = (TypeElement) mProcessor.getTypeElement(typeElement.getSuperclass());
    }

    return builder.build();
  }

  private void getJsonKey(CodeBlock.Builder builder, List<String> jsonKeys) {
    builder.addStatement("$L = null", JSON_KEY_NAME).addStatement("$L.clear()", JSON_KEYS_NAME);
    for (String key : jsonKeys) {
      builder.addStatement("$L.add($S)", JSON_KEYS_NAME, key);
    }
    builder.beginControlFlow("for (String key : $L)", JSON_KEYS_NAME)
        .beginControlFlow("if ($L.has(key))", JSON_OBJECT_NAME)
        .addStatement("$L = key", JSON_KEY_NAME)
        .addStatement("break")
        .endControlFlow()
        .endControlFlow();
  }

  private void createStatement(CodeBlock.Builder builder, VariableElement element, String key) {
    if (key != null) {
      builder.beginControlFlow("if ($L.has($S))", JSON_OBJECT_NAME, key);
    } else {
      builder.beginControlFlow("if ($L != null)", JSON_KEY_NAME);
    }

    TypeMirror typeMirror = element.asType();
    String jsonOptType = ProcessorUtils.getJsonOptType(typeMirror);
    if (jsonOptType != null) {
      TypeName typeName = TypeName.get(typeMirror);
      if (typeName.isBoxedPrimitive()) {
        typeName = typeName.unbox();
      }
      String fieldName = element.getSimpleName().toString();
      builder.addStatement("$L.$L = ($T) $L.opt$L($" + (key != null ? "S)" : "L)"), ELEMENT_NAME,
          fieldName, typeName, JSON_OBJECT_NAME, jsonOptType, key != null ? key : JSON_KEY_NAME);
    } else if (mProcessor.isArray(element)) {
      createArrayStatement(builder, element, key);
    } else if (mProcessor.isCollection(element)) {
      createCollectionStatement(builder, element, key);
    } else if (mProcessor.isMap(element)) {
      createMapStatement(builder, element, key);
    } else {
      createGeneralStatement(builder, element, key);
    }

    builder.endControlFlow();
  }

  private void createArrayStatement(CodeBlock.Builder builder, VariableElement element,
                                    String key) {
    String fieldName = element.getSimpleName().toString();
    String jsonArray = "jsonArray";
    ArrayType arrayType = (ArrayType) element.asType();
    TypeMirror componentType = arrayType.getComponentType();
    String jsonOptType = ProcessorUtils.getJsonOptType(componentType);

    builder.addStatement("$T $L = $L.optJSONArray($" + (key != null ? "S)" : "L)"),
        ProcessorUtils.JSON_ARRAY, jsonArray, JSON_OBJECT_NAME, key != null ? key : JSON_KEY_NAME)
        .beginControlFlow("if ($L != null)", jsonArray)
        .addStatement("$L.$L = new $T[$L.length()]",
            ELEMENT_NAME, fieldName, componentType, jsonArray)
        .beginControlFlow("for (int i = 0; i < $L.length(); ++i) ", jsonArray);

    TypeName typeName = TypeName.get(componentType);
    if (jsonOptType != null) {
      if (typeName.isBoxedPrimitive()) {
        typeName = typeName.unbox();
      }
      builder.addStatement("$L.$L[i] = ($T) $L.opt$L(i)",
          ELEMENT_NAME, fieldName, typeName, jsonArray, jsonOptType);
    } else {
      checkElementAnnotation(componentType, element);
      builder.addStatement("$L.$L[i] = new $T()", ELEMENT_NAME, fieldName, typeName)
          .addStatement("$T.fromJson($L.optJSONObject(i), $L.$L[i])",
              Hson.class, jsonArray, ELEMENT_NAME, fieldName);
    }

    builder.endControlFlow().endControlFlow();
  }

  private void createCollectionStatement(CodeBlock.Builder builder, VariableElement element,
                                         String key) {
    TypeMirror componentType = checkCollectionType(element);
    TypeMirror collectionType = element.asType();

    ClassName collectionImpl;
    if (mProcessor.isAssignable(collectionType, "java.util.List")
        || mProcessor.isSameType(collectionType, "java.util.Collection")) {
      collectionImpl = ClassName.bestGuess("java.util.ArrayList");
    } else if (mProcessor.isAssignable(collectionType, "java.util.Set")) {
      collectionImpl = ClassName.bestGuess("java.util.HashSet");
    } else {
      mProcessor.error("集合目前仅支持Collection、List、Set, 当前类型：" + collectionType, element);
      return;
    }

    String fieldName = element.getSimpleName().toString();
    String jsonArray = "jsonArray";
    String jsonOptType = ProcessorUtils.getJsonOptType(componentType);

    builder.addStatement("$T $L = $L.optJSONArray($" + (key != null ? "S)" : "L)"),
        ProcessorUtils.JSON_ARRAY, jsonArray, JSON_OBJECT_NAME, key != null ? key : JSON_KEY_NAME)
        .beginControlFlow("if ($L != null)", jsonArray)
        .beginControlFlow("if ($L.$L == null)", ELEMENT_NAME, fieldName)
        .addStatement("$L.$L = new $T<>()", ELEMENT_NAME, fieldName, collectionImpl)
        .endControlFlow()
        .beginControlFlow("for (int i = 0; i < $L.length(); ++i) ", jsonArray);

    if (jsonOptType != null) {
      TypeName typeName = TypeName.get(componentType);
      if (typeName.isBoxedPrimitive()) {
        typeName = typeName.unbox();
      }
      builder.addStatement("$L.$L.add(($T) $L.opt$L(i))",
          ELEMENT_NAME, fieldName, typeName, jsonArray, jsonOptType);
    } else {
      checkElementAnnotation(componentType, element);
      String tempObject = "tempObject";
      builder.addStatement("$T $L = new $T()", componentType, tempObject, componentType)
          .addStatement("$L.$L.add($L)", ELEMENT_NAME, fieldName, tempObject)
          .addStatement("$T.fromJson($L.optJSONObject(i), $L)", Hson.class, jsonArray, tempObject);
    }

    builder.endControlFlow().endControlFlow();
  }

  private void createMapStatement(CodeBlock.Builder builder, VariableElement element, String key) {
    TypeMirror valueType = checkMapType(element);
    TypeMirror mapType = element.asType();

    ClassName mapImpl;
    if (mProcessor.isSameType(mapType, "java.util.Map")
        || mProcessor.isAssignable(mapType, "java.util.Map")) {
      mapImpl = ClassName.bestGuess("java.util.HashMap");
    } else {
      mProcessor.error("无法获取 Map 的 ClassName，当前类型是" + mapType, element);
      return;
    }
    String fieldName = element.getSimpleName().toString();
    String jsonMap = "jsonMap";
    ClassName iteratorClass = ClassName.bestGuess("java.util.Iterator");
    builder.addStatement("$T $L = $L.optJSONObject($" + (key != null ? "S)" : "L)"),
        ProcessorUtils.JSON_OBJECT, jsonMap, JSON_OBJECT_NAME, key != null ? key : JSON_KEY_NAME)
        .beginControlFlow("if ($L != null)", jsonMap)
        .beginControlFlow("if ($L.$L == null)", ELEMENT_NAME, fieldName)
        .addStatement("$L.$L = new $T<>()", ELEMENT_NAME, fieldName, mapImpl)
        .endControlFlow()
        .addStatement("$T<String> keys = $L.keys()", iteratorClass, jsonMap)
        .beginControlFlow("while (keys.hasNext())")
        .addStatement("String key = keys.next()");

    String jsonOptType = ProcessorUtils.getJsonOptType(valueType);
    if (jsonOptType != null) {
      TypeName typeName = TypeName.get(valueType);
      if (typeName.isBoxedPrimitive()) {
        typeName = typeName.unbox();
      }
      builder.addStatement("$L.$L.put(key,($T) $L.opt$L(key))",
          ELEMENT_NAME, fieldName, typeName, jsonMap, jsonOptType);
    } else {
      checkElementAnnotation(valueType, element);
      String tempObject = "tempObject";
      builder.addStatement("$T $L = new $T()", valueType, tempObject, valueType)
          .addStatement("$L.$L.put(key,$L)", ELEMENT_NAME, fieldName, tempObject)
          .addStatement("$T.fromJson($L.optJSONObject(key), $L)", Hson.class, jsonMap, tempObject);
    }
    builder.endControlFlow().endControlFlow();
  }

  private void createGeneralStatement(CodeBlock.Builder builder, VariableElement element,
                                      String key) {
    String fieldName = element.getSimpleName().toString();
    // 此 typeMirror 的真实类型其实是 com.sun.tools.javac.code.Type.ClassType，但是此类在 tools.jar 中，
    // 无法直接引用，只能通过反射获取相关信息
    TypeMirror typeMirror = element.asType();
    checkElementAnnotation(typeMirror, element);
    Object outerField = ReflectUtils.getFieldValue(typeMirror, "outer_field");
    // 只有静态内部类才可以 new，非静态内部类需要开发者 new，否则运行时报错
    builder.beginControlFlow("if ($L.$L == null)", ELEMENT_NAME, fieldName);
    if (outerField == getNoType()) {
      builder.addStatement("$L.$L = new $T()", ELEMENT_NAME, fieldName, TypeName.get(typeMirror));
    } else {
      builder.addStatement("throw new RuntimeException(\"$T.$L cannot be null!!!\")",
          TypeName.get(element.getEnclosingElement().asType()), fieldName);
    }
    builder.endControlFlow();
    builder.addStatement("$T.fromJson($L.optJSONObject($" + (key != null ? "S" : "L") + "), $L.$L)",
        Hson.class, JSON_OBJECT_NAME, key != null ? key : JSON_KEY_NAME, ELEMENT_NAME, fieldName);
  }

  private Object getNoType() {
    if (sNoType == null) {
      sNoType = ReflectUtils.getStaticFieldValue("com.sun.tools.javac.code.Type", "noType");
    }
    return sNoType;
  }
}
