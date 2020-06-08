package com.hhh.processor.utils;

import com.google.auto.common.MoreTypes;
import com.hhh.hson.annotation.SerializedName;
import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ProcessorUtils {

  public static final List<Modifier> IGNORE_MODIFIERS =
      Arrays.asList(Modifier.STATIC, Modifier.FINAL, Modifier.TRANSIENT);
  public static final List<Modifier> NEEDS_MODIFIERS =
      Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED);

  public static final String JSON_PACKAGE_NAME = "org.json";
  public static final ClassName JSON_OBJECT = ClassName.get(JSON_PACKAGE_NAME, "JSONObject");
  public static final ClassName JSON_ARRAY = ClassName.get(JSON_PACKAGE_NAME, "JSONArray");

  /**
   * Boolean.TYPE 其实就是 boolean.class，其它类型同理
   */
  public static final Class<?>[] BOOL_CLASS = {Boolean.class, Boolean.TYPE};
  public static final Class<?>[] INT_CLASS = {
      Integer.class, Integer.TYPE,
      Short.class, Short.TYPE,
      Character.class, Character.TYPE,
      Byte.class, Byte.TYPE};
  public static final Class<?>[] LONG_CLASS = {Long.class, Long.TYPE};
  public static final Class<?>[] DOUBLE_CLASS = {
      Double.class, Double.TYPE,
      Float.class, Float.TYPE};
  public static final Class<?>[] STRING_CLASS = {String.class};
  public static final Class<?>[] NON_STRING_CLASS = {
      Boolean.class, Boolean.TYPE,
      Integer.class, Integer.TYPE,
      Short.class, Short.TYPE,
      Byte.class, Byte.TYPE,
      Long.class, Long.TYPE,
      Double.class, Double.TYPE,
      Float.class, Float.TYPE};

  /**
   * 不包括基类元素
   */
  public static List<VariableElement> getAllFields(TypeElement typeElement) {
    return typeElement.getEnclosedElements().stream()
        .filter((Predicate<Element>) element -> element instanceof VariableElement)
        .map((Function<Element, VariableElement>) element -> (VariableElement) element)
        .collect(Collectors.toList());
  }

  /**
   * 返回参与序列化的元素，注意无任何修饰符元素的 getModifiers 返回为空，而不是 Modifier.DEFAULT
   */
  public static List<VariableElement> getSerializableFields(TypeElement element) {
    return getAllFields(element).stream().filter(variableElement -> {
      Set<Modifier> modifiers = variableElement.getModifiers();
      return modifiers.isEmpty() || (!Collections.disjoint(modifiers, NEEDS_MODIFIERS)
          && Collections.disjoint(modifiers, IGNORE_MODIFIERS));
    }).collect(Collectors.toList());
  }

  /**
   * 返回元素在 Json 中的 key，用于反序列化
   */
  public static List<String> getJsonKeys(VariableElement element) {
    List<String> jsonKeys = new ArrayList<>();
    SerializedName serializedName = element.getAnnotation(SerializedName.class);
    if (serializedName == null) {
      jsonKeys.add(element.getSimpleName().toString());
    } else {
      jsonKeys.add(serializedName.value());
      jsonKeys.addAll(Arrays.asList(serializedName.alternate()));
    }
    return jsonKeys;
  }

  /**
   * 返回元素在 Json 中的第一个可用 key，用于序列化
   */
  public static String getJsonKey(VariableElement element) {
    SerializedName serializedName = element.getAnnotation(SerializedName.class);
    if (serializedName == null) {
      return element.getSimpleName().toString();
    }
    return serializedName.value();
  }

  public static String getJsonOptType(TypeMirror typeMirror) {
    if (checkType(INT_CLASS, typeMirror)) {
      return "Int";
    } else if (checkType(LONG_CLASS, typeMirror)) {
      return "Long";
    } else if (checkType(DOUBLE_CLASS, typeMirror)) {
      return "Double";
    } else if (checkType(BOOL_CLASS, typeMirror)) {
      return "Boolean";
    } else if (checkType(STRING_CLASS, typeMirror)) {
      return "String";
    } else if (JSON_OBJECT.toString().equals(typeMirror.toString())) {
      return "JSONObject";
    } else if (JSON_ARRAY.toString().equals(typeMirror.toString())) {
      return "JSONArray";
    }
    return null;
  }

  public static boolean isNonString(TypeMirror typeMirror) {
    return checkType(NON_STRING_CLASS, typeMirror);
  }

  public static boolean checkType(Class<?>[] classes, TypeMirror typeMirror) {
    return Arrays.stream(classes).anyMatch(it -> MoreTypes.isTypeOf(it, typeMirror));
  }
}
