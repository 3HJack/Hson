package com.hhh.hson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates this member should be serialized to JSON with
 * the provided name value as its field name.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface SerializedName {

  /**
   * @return the desired name of the field when it is serialized or deserialized
   */
  String value();

  /**
   * @return the alternative names of the field when it is deserialized
   */
  String[] alternate() default {};
}
