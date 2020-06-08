package com.hhh.processor.method;

import com.hhh.hson.annotation.Json;
import com.hhh.processor.HsonProcessor;
import com.hhh.processor.utils.ProcessorUtils;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public abstract class MethodCreator {

  protected static final String ELEMENT_NAME = "model";

  protected final HsonProcessor mProcessor;

  public MethodCreator(HsonProcessor processor) {
    mProcessor = processor;
  }

  public abstract MethodSpec process(TypeElement typeElement);

  // 所有被 Hson 处理的类都必须加上 @Json 注解
  protected void checkElementAnnotation(TypeMirror typeMirror, Element element) {
    if (mProcessor.getTypeElement(typeMirror.toString()).getAnnotation(Json.class) == null) {
      mProcessor.error(String.format("由于缺少 @Json 注解，无法处理该元素类型: %s", typeMirror), element);
    }
  }

  protected TypeMirror checkCollectionType(Element element) {
    TypeMirror collectionType = element.asType();
    List<? extends TypeMirror> typeArguments = ((DeclaredType) collectionType).getTypeArguments();
    if (typeArguments.isEmpty()) {
      mProcessor.error("集合必须指定泛型，无法处理原始类型，当前类型:" + collectionType, element);
      return null;
    }
    return typeArguments.get(0);
  }

  protected TypeMirror checkMapType(Element element) {
    TypeMirror mapType = element.asType();
    List<? extends TypeMirror> typeArguments = ((DeclaredType) mapType).getTypeArguments();
    if (typeArguments.isEmpty()) {
      mProcessor.error("Map 必须指定泛型，无法处理原始类型", element);
      return null;
    }

    TypeMirror keyType = typeArguments.get(0);
    if (!ProcessorUtils.checkType(ProcessorUtils.STRING_CLASS, keyType)) {
      mProcessor.error(String.format("Map 的 key 需要是 String 类型，当前类型是 %s", keyType), element);
      return null;
    }

    return typeArguments.get(1);
  }
}
