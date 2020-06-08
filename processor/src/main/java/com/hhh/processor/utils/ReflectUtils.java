package com.hhh.processor.utils;

import java.lang.reflect.Field;

public class ReflectUtils {

  public static Object getStaticFieldValue(String className, String fieldName) {
    try {
      Class<?> clazz = Class.forName(className);
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(null);
    } catch (Exception e) {
      throw new RuntimeException(
          "getStaticFieldValue exception, clazz = " + className + ", fieldName = " + fieldName, e);
    }
  }

  public static Object getFieldValue(Object object, String fieldName) {
    try {
      Field field = getDeclaredField(object, fieldName);
      field.setAccessible(true);
      return field.get(object);
    } catch (Exception e) {
      throw new RuntimeException(
          "getFieldValue exception, object = " + object + ", fieldName = " + fieldName, e);
    }
  }

  public static Field getDeclaredField(Object object, String fieldName) {
    Class<?> clazz = object.getClass();
    while (clazz != Object.class) {
      try {
        return clazz.getDeclaredField(fieldName);
      } catch (Exception e) {
        clazz = clazz.getSuperclass();
      }
    }
    throw new RuntimeException(
        "getDeclaredField exception, object = " + object + ", fieldName = " + fieldName);
  }
}
