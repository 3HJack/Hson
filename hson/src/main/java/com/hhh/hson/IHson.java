package com.hhh.hson;

import org.json.JSONObject;

public interface IHson {

  void fromJson(JSONObject jsonObject);

  void toJson(StringBuilder json);
}
