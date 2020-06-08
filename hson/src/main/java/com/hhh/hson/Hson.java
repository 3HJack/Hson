package com.hhh.hson;

import com.hhh.hson.constant.Constants;
import com.hhh.hson.exception.HsonException;

/**
 * This is the main class for using Hson. Hson is typically used by invoking {@link #toJson(Object)}
 * or {@link #fromJson(String, Object)} methods on it.
 */
public class Hson {

  public static void fromJson(String json, Object iHson) throws HsonException {
  }

  public static void fromJson(Object jsonObject, Object iHson) throws HsonException {
    if (jsonObject == null || iHson == null) {
      return;
    }
    checkJsonObject(jsonObject);
    checkIHson(iHson);
    ((IHson) iHson).fromJson(jsonObject);
  }

  public static String toJson(Object iHson) {
    if (iHson == null) {
      return null;
    }
    checkIHson(iHson);
    StringBuilder json = new StringBuilder(Constants.LEFT_BRACE);
    ((IHson) iHson).toJson(json);
    return deleteExtraComma(json).append(Constants.RIGHT_BRACE).toString();
  }

  public static StringBuilder deleteExtraComma(StringBuilder json) {
    int extraCommaIndex = json.lastIndexOf(Constants.COMMA);
    if (extraCommaIndex == json.length() - 1) {
      json.deleteCharAt(extraCommaIndex);
    }
    return json;
  }

  private static void checkIHson(Object iHson) {
    if (!(iHson instanceof IHson)) {
      throw new IllegalArgumentException(
          String.format("%s must have @Json annotation or implement IHson!!!", iHson));
    }
  }

  private static void checkJsonObject(Object jsonObject) {
    if (!jsonObject.getClass().getName().equals(Constants.JSON_OBJECT_CLASS_NAME)) {
      throw new IllegalArgumentException(
          String.format("%s must be org.json.JSONObject!!!", jsonObject));
    }
  }
}
