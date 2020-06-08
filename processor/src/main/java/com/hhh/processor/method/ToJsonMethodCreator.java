package com.hhh.processor.method;

import com.hhh.hson.Hson;
import com.hhh.hson.annotation.Json;
import com.hhh.hson.constant.Constants;
import com.hhh.processor.HsonProcessor;
import com.hhh.processor.utils.ProcessorUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

public class ToJsonMethodCreator extends MethodCreator {

  private static final String JSON_NAME = "json";

  public ToJsonMethodCreator(HsonProcessor processor) {
    super(processor);
  }

  public MethodSpec process(TypeElement typeElement) {
    return MethodSpec.methodBuilder(Constants.TO_JSON_METHOD)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(StringBuilder.class, JSON_NAME)
        .addParameter(ClassName.get(typeElement), ELEMENT_NAME)
        .addCode(createCodeBlock(typeElement))
        .build();
  }

  private CodeBlock createCodeBlock(TypeElement typeElement) {
    CodeBlock.Builder builder = CodeBlock.builder();

    // 处理当前对象变量
    ProcessorUtils.getSerializableFields(typeElement).forEach(variableElement -> {
      String jsonKey = ProcessorUtils.getJsonKey(variableElement);
      createStatement(builder, variableElement, jsonKey);
    });

    // 处理基类
    typeElement = (TypeElement) mProcessor.getTypeElement(typeElement.getSuperclass());
    while (typeElement.getAnnotation(Json.class) != null) {
      builder.addStatement("$T.$L($L, $L)",
          mProcessor.getClassName(typeElement), Constants.TO_JSON_METHOD, JSON_NAME, ELEMENT_NAME);
      typeElement = (TypeElement) mProcessor.getTypeElement(typeElement.getSuperclass());
    }

    return builder.build();
  }

  private void createStatement(CodeBlock.Builder builder, VariableElement element, String key) {
    TypeMirror typeMirror = element.asType();
    TypeName typeName = TypeName.get(typeMirror);
    String fieldName = element.getSimpleName().toString();
    if (!typeName.isPrimitive()) {
      builder.beginControlFlow("if ($L.$L != null)", ELEMENT_NAME, fieldName);
    }
    String jsonOptType = ProcessorUtils.getJsonOptType(typeMirror);
    if (jsonOptType != null) {
      if (ProcessorUtils.isNonString(typeMirror)) {
        builder.addStatement("$L.append($S + $S + $S + $S + $L.$L + $S)", JSON_NAME,
            Constants.QUOTATION, key, Constants.QUOTATION, Constants.COLON, ELEMENT_NAME, fieldName,
            Constants.COMMA);
      } else {
        builder.addStatement("$L.append($S + $S + $S + $S + $S + $L.$L + $S + $S)", JSON_NAME,
            Constants.QUOTATION, key, Constants.QUOTATION, Constants.COLON, Constants.QUOTATION,
            ELEMENT_NAME, fieldName, Constants.QUOTATION, Constants.COMMA);
      }
    } else if (mProcessor.isArray(element)) {
      createArrayStatement(builder, element, key);
    } else if (mProcessor.isCollection(element)) {
      createCollectionStatement(builder, element, key);
    } else if (mProcessor.isMap(element)) {
      createMapStatement(builder, element, key);
    } else {
      createGeneralStatement(builder, element, key);
    }

    if (!typeName.isPrimitive()) {
      builder.endControlFlow();
    }
  }

  private void createArrayStatement(CodeBlock.Builder builder, VariableElement element,
                                    String key) {
    TypeMirror componentType = ((ArrayType) element.asType()).getComponentType();
    builder.beginControlFlow("if ($L.$L.length > 0)",
        ELEMENT_NAME, element.getSimpleName().toString());
    createJsonArrayStatement(builder, element, key, componentType);
  }

  private void createCollectionStatement(CodeBlock.Builder builder, VariableElement element,
                                         String key) {
    TypeMirror componentType = checkCollectionType(element);
    builder.beginControlFlow("if (!$L.$L.isEmpty())",
        ELEMENT_NAME, element.getSimpleName().toString());
    createJsonArrayStatement(builder, element, key, componentType);
  }

  private void createJsonArrayStatement(CodeBlock.Builder builder, VariableElement element,
                                        String key, TypeMirror componentType) {
    builder.addStatement("$L.append($S + $S + $S + $S + $S)", JSON_NAME, Constants.QUOTATION, key,
        Constants.QUOTATION, Constants.COLON, Constants.LEFT_BRACKET)
        .beginControlFlow("for ($T var : $L.$L)",
            componentType, ELEMENT_NAME, element.getSimpleName().toString());
    String jsonOptType = ProcessorUtils.getJsonOptType(componentType);
    if (jsonOptType != null) {
      if (ProcessorUtils.isNonString(componentType)) {
        builder.addStatement("$L.append(var + $S)", JSON_NAME, Constants.COMMA);
      } else {
        builder.addStatement("$L.append($S + var + $S + $S)",
            JSON_NAME, Constants.QUOTATION, Constants.QUOTATION, Constants.COMMA);
      }
    } else {
      checkElementAnnotation(componentType, element);
      builder.addStatement("$L.append($T.toJson(var) + $S)",
          JSON_NAME, Hson.class, Constants.COMMA);
    }

    builder.endControlFlow()
        .addStatement("$T.deleteExtraComma($L)", Hson.class, JSON_NAME)
        .addStatement("$L.append($S + $S)", JSON_NAME, Constants.RIGHT_BRACKET, Constants.COMMA)
        .endControlFlow();
  }

  private void createMapStatement(CodeBlock.Builder builder, VariableElement element, String key) {
    TypeMirror valueType = checkMapType(element);
    String fieldName = element.getSimpleName().toString();
    builder.beginControlFlow("if (!$L.$L.isEmpty())", ELEMENT_NAME, fieldName)
        .addStatement("$L.append($S + $S + $S + $S + $S)", JSON_NAME, Constants.QUOTATION, key,
            Constants.QUOTATION, Constants.COLON, Constants.LEFT_BRACE)
        .beginControlFlow("for ($T<String, $T> entry : $L.$L.entrySet())",
            Map.Entry.class, valueType, ELEMENT_NAME, fieldName);
    String jsonOptType = ProcessorUtils.getJsonOptType(valueType);
    if (jsonOptType != null) {
      if (ProcessorUtils.isNonString(valueType)) {
        builder.addStatement("$L.append($S + entry.getKey() + $S + $S + entry.getValue() + $S)",
            JSON_NAME, Constants.QUOTATION, Constants.QUOTATION, Constants.COLON, Constants.COMMA);
      } else {
        builder.addStatement(
            "$L.append($S + entry.getKey() + $S + $S + $S + entry.getValue() + $S + $S)", JSON_NAME,
            Constants.QUOTATION, Constants.QUOTATION, Constants.COLON, Constants.QUOTATION,
            Constants.QUOTATION, Constants.COMMA);
      }
    } else {
      checkElementAnnotation(valueType, element);
      builder.addStatement(
          "$L.append($S + entry.getKey() + $S + $S + $T.toJson(entry.getValue()) + $S)", JSON_NAME,
          Constants.QUOTATION, Constants.QUOTATION, Constants.COLON, Hson.class, Constants.COMMA);
    }
    builder.endControlFlow()
        .addStatement("$T.deleteExtraComma($L)", Hson.class, JSON_NAME)
        .addStatement("$L.append($S + $S)", JSON_NAME, Constants.RIGHT_BRACE, Constants.COMMA)
        .endControlFlow();
  }

  private void createGeneralStatement(CodeBlock.Builder builder, VariableElement element,
                                      String key) {
    checkElementAnnotation(element.asType(), element);
    String fieldName = element.getSimpleName().toString();
    builder.addStatement("$L.append($S + $S + $S + $S + $T.toJson($L.$L) + $S)", JSON_NAME,
        Constants.QUOTATION, key, Constants.QUOTATION, Constants.COLON, Hson.class, ELEMENT_NAME,
        fieldName, Constants.COMMA);
  }
}
