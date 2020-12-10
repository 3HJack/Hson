Hson
====

Hson is a Java library that can be used to convert Java Objects into
their JSON representation. It can also be used to convert a JSON string
to an equivalent Java object. Hson can only run on the Android platform,
is by far the fastest json parsing framework for the Android platform.

Goals
=====
* The fastest analysis speed
* Provide simple `toJson()` and `fromJson()` methods to convert Java objects to JSON and vice-versa
* Allow custom representations for objects
* Support arbitrarily complex objects (with deep inheritance hierarchies)


Performance comparison
======================

HsonExample test

| library  | fromJson(t/ms) | toJson(t/ms) |
|:--------:|:--------------:|:------------:|
|   Gson   |       20       |     28       |
| fastjson |       49       |     23       |
|   Hson   |       11       |     7        |


usage
=====

Modify the build.gradle file in the root directory of the project as follows

```groovy
buildscript {

    repositories {
        google()
        jcenter()
        // This repository must be added
        maven { url "https://dl.bintray.com/onepiece/maven" }
    }
    
    dependencies {
        // This gradle plugin must be added
        classpath 'com.hhh.onepiece:hson-plugin:1.0.4'
    }
  
    // Solve dependency problems
    allprojects {
        def rootRepositories = rootProject.buildscript.repositories
        rootRepositories.each { repo ->
            repositories.add(repo)
            buildscript.repositories.add(repo)
        }
    }

}
```

Modify the build.gradle file in the app directory as follows

```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.hhh.hson'

```

Modify the build.gradle file in the relevant library project (including app) directory as follows
```groovy
dependencies {
    
    implementation 'com.hhh.onepiece:hson:1.0.4'
    annotationProcessor 'com.hhh.onepiece:hson-processor:1.0.4'
}

```

```java
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

```