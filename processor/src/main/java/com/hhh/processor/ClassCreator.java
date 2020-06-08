package com.hhh.processor;

import com.hhh.processor.method.FromJsonMethodCreator;
import com.hhh.processor.method.ToJsonMethodCreator;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class ClassCreator {

  private final HsonProcessor mProcessor;

  public ClassCreator(HsonProcessor processor) {
    mProcessor = processor;
  }

  public TypeSpec process(TypeElement typeElement) {
    return TypeSpec.classBuilder(mProcessor.getClassName(typeElement))
        .addModifiers(Modifier.PUBLIC)
        .addMethod(new FromJsonMethodCreator(mProcessor).process(typeElement))
        .addMethod(new ToJsonMethodCreator(mProcessor).process(typeElement))
        .build();
  }
}
