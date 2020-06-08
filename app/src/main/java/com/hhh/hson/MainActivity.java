package com.hhh.hson;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hhh.hson.test.HsonExample;
import com.hhh.hson.test.NoValueHsonExample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "hhh";
  private static final String CHARSET_NAME = "utf-8";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    test();
  }

  private void test() {
    findViewById(R.id.Gson_fromJson).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Gson gson = new Gson();
        String json = getHsonExampleJson();
        long startTime = System.currentTimeMillis();
        NoValueHsonExample hsonExample = gson.fromJson(json, NoValueHsonExample.class);
        Log.e(TAG, "Gson_fromJson:" + (System.currentTimeMillis() - startTime));
      }
    });

    findViewById(R.id.Gson_toJson).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Gson gson = new Gson();
        HsonExample hsonExample = new HsonExample();
        long startTime = System.currentTimeMillis();
        String json = gson.toJson(hsonExample);
        Log.e(TAG, "Gson_toJson:" + (System.currentTimeMillis() - startTime));
      }
    });

    findViewById(R.id.fastjson_fromJson).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String json = getHsonExampleJson();
        long startTime = System.currentTimeMillis();
        NoValueHsonExample hsonExample = JSON.parseObject(json, NoValueHsonExample.class);
        Log.e(TAG, "fastjson_fromJson:" + (System.currentTimeMillis() - startTime));
      }
    });

    findViewById(R.id.fastjson_toJson).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        HsonExample hsonExample = new HsonExample();
        long startTime = System.currentTimeMillis();
        String json = JSON.toJSONString(hsonExample);
        Log.e(TAG, "fastjson_toJson:" + (System.currentTimeMillis() - startTime));
      }
    });

    findViewById(R.id.Hson_fromJson).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String json = getHsonExampleJson();
        long startTime = System.currentTimeMillis();
        NoValueHsonExample hsonExample = new NoValueHsonExample();
        Hson.fromJson(json, hsonExample);
        Log.e(TAG, "Hson_fromJson:" + (System.currentTimeMillis() - startTime));
      }
    });

    findViewById(R.id.Hson_toJson).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        HsonExample hsonExample = new HsonExample();
        long startTime = System.currentTimeMillis();
        String json = Hson.toJson(hsonExample);
        Log.e(TAG, "Hson_toJson:" + (System.currentTimeMillis() - startTime));
      }
    });
  }

  private String getHsonExampleJson() {
    StringBuilder sb = new StringBuilder();
    try {
      InputStream is = getAssets().open("hsonExample.json");
      BufferedReader br = new BufferedReader(new InputStreamReader(is, CHARSET_NAME));
      String str;
      while ((str = br.readLine()) != null) {
        sb.append(str);
      }
      br.close();
      return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }
}