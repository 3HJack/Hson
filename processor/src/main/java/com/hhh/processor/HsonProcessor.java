package com.hhh.processor;

import com.google.auto.service.AutoService;
import com.hhh.hson.annotation.Json;
import com.hhh.hson.constant.Constants;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class HsonProcessor extends AbstractProcessor {

  private static final String TAG = "HsonProcessor: ";

  private Messager mMessager;
  private Filer mFiler;
  private Types mTypes;
  private Elements mElements;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    mMessager = processingEnvironment.getMessager();
    mFiler = processingEnvironment.getFiler();
    mTypes = processingEnvironment.getTypeUtils();
    mElements = processingEnvironment.getElementUtils();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportTypes = new HashSet<>();
    supportTypes.add(Json.class.getCanonicalName());
    return supportTypes;
  }

  /**
   * 此函数会被调用三次
   *
   * @param set              只有第一次此值 size 大于 0
   * @param roundEnvironment 只有第一次此值可以获取到相关注解信息
   * @return 若返回true，则排在后面的注解处理器不能再处理同一个注解，比如这里的 {@link Json}，false则反之
   */
  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    if (set == null || set.isEmpty()) {
      return false;
    }
    roundEnvironment.getElementsAnnotatedWith(Json.class)
        .stream()
        .filter((Predicate<Element>) element -> element instanceof TypeElement)
        .map((Function<Element, TypeElement>) element -> (TypeElement) element)
        .map(element -> {
          note(null, element);
          TypeSpec typeSpec = new ClassCreator(HsonProcessor.this).process(element);
          return JavaFile.builder(getPackageName(element), typeSpec)
              .skipJavaLangImports(true)
              .build();
        }).forEach(javaFile -> {
      try {
        javaFile.writeTo(mFiler);
      } catch (IOException e) {
        e.printStackTrace();
        error(e.getMessage(), null);
      }
    });
    return false;
  }

  /**
   * 获取辅助类的类名，包名不变，注意对内部类的处理
   */
  public ClassName getClassName(TypeElement typeElement) {
    String packageName = getPackageName(typeElement);
    String simpleName =
        typeElement.getQualifiedName().toString().substring(packageName.length() + 1).replace('.',
            '$') + Constants.AUXILIARY_CLASS_NAME_SUFFIX;
    return ClassName.get(packageName, simpleName);
  }

  public String getPackageName(Element element) {
    return mElements.getPackageOf(element).getQualifiedName().toString();
  }

  public TypeElement getTypeElement(CharSequence fullClassName) {
    return mElements.getTypeElement(fullClassName);
  }

  public Element getTypeElement(TypeMirror typeMirror) {
    return mTypes.asElement(typeMirror);
  }

  public boolean isArray(Element element) {
    return element.asType().getKind() == TypeKind.ARRAY;
  }

  // 必须擦除泛型
  public boolean isCollection(Element element) {
    TypeMirror elementType = element.asType();
    if (elementType.getKind() != TypeKind.DECLARED) {
      return false;
    }
    TypeMirror collectionType = getTypeElement("java.util.Collection").asType();
    return mTypes.isSubtype(mTypes.erasure(elementType), mTypes.erasure(collectionType));
  }

  // 必须擦除泛型
  public boolean isMap(Element element) {
    TypeMirror elementType = element.asType();
    if (elementType.getKind() != TypeKind.DECLARED) {
      return false;
    }
    TypeMirror mapType = getTypeElement("java.util.Map").asType();
    return mTypes.isSubtype(mTypes.erasure(elementType), mTypes.erasure(mapType));
  }

  // 子类型即可返回 true
  public boolean isAssignable(TypeMirror typeMirror, String fullClassName) {
    return mTypes.isAssignable(mTypes.erasure(typeMirror),
        mTypes.erasure(getTypeElement(fullClassName).asType()));
  }

  // 同种类型才返回 true
  public boolean isSameType(TypeMirror typeMirror, String fullClassName) {
    return mTypes.isSameType(mTypes.erasure(typeMirror),
        mTypes.erasure(getTypeElement(fullClassName).asType()));
  }

  public void note(String message, Element element) {
    mMessager.printMessage(Diagnostic.Kind.NOTE, TAG + message, element);
  }

  public void warn(String message, Element element) {
    mMessager.printMessage(Diagnostic.Kind.WARNING, TAG + message, element);
  }

  // 错误日志将终止编译
  public void error(String message, Element element) {
    mMessager.printMessage(Diagnostic.Kind.ERROR, TAG + message, element);
  }
}
