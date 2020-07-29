package com.hhh.hson;

import com.hhh.hson.constant.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the main class for using Hson. Hson is typically used by invoking {@link #toJson(Object)}
 * or {@link #fromJson(String, Object)} methods on it.
 */
public class Hson {

  public static void fromJson(String json, Object iHson) {
    if (json != null && json.length() != 0 && !json.equals("{}")) {
      try {
        fromJson(new JSONObject(json), iHson);
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void fromJson(JSONObject jsonObject, Object iHson) {
    if (jsonObject == null || iHson == null) {
      return;
    }
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
      throw new IllegalArgumentException(String.format("%s must have @Json annotation or " +
        "implement IHson!!!", iHson));
    }
  }
}
