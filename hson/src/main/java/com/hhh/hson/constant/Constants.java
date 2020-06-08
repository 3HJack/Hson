package com.hhh.hson.constant;

import com.hhh.hson.Hson;
import com.hhh.hson.IHson;
import com.hhh.hson.annotation.Json;
import com.hhh.hson.exception.HsonException;

public class Constants {

  public static final String HSON_TYPE = getType(Hson.class.getName());
  public static final String IHSON_TYPE = getType(IHson.class.getName());
  public static final String JSON_TYPE = getType(Json.class.getName());
  public static final String HSON_EXCEPTION_TYPE = getType(HsonException.class.getName());

  public static final String AUXILIARY_CLASS_NAME_SUFFIX = "_Hson";
  public static final String JSON_OBJECT_CLASS_NAME = "org.json.JSONObject";

  public static final String FROM_JSON_METHOD = "fromJson";
  public static final String TO_JSON_METHOD = "toJson";

  public static final String COLON = ":";
  public static final String COMMA = ",";
  public static final String QUOTATION = "\"";
  public static final String LEFT_BRACKET = "[";
  public static final String RIGHT_BRACKET = "]";
  public static final String LEFT_BRACE = "{";
  public static final String RIGHT_BRACE = "}";

  private static String getType(String className) {
    return className.replace('.', '/');
  }
}
