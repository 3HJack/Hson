package com.hhh.hson;

public interface IHson {

  void fromJson(Object jsonObject);

  void toJson(StringBuilder json);
}
