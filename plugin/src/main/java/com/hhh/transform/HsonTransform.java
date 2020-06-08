package com.hhh.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HsonTransform extends Transform {

  private static final String TAG = "HsonTransform:";

  private Project mProject;

  /**
   * @param project 保存下来备用
   */
  public HsonTransform(Project project) {
    mProject = project;
  }

  /**
   * 自定义 transform 的名称
   * transformClassesWithMyClassTransformForDebug 运行时的名字
   * transformClassesWith + getName() + For + Debug或Release
   */
  @Override
  public String getName() {
    return HsonTransform.class.getSimpleName();
  }

  /**
   * 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型,这样确保其他类型的文件不会传入
   * 可供我们去处理的有两种类型, 分别是编译后的java代码, 以及资源文件(非res下文件, 而是assests内的资源)
   */
  @Override
  public Set<QualifiedContent.ContentType> getInputTypes() {
    return TransformManager.CONTENT_CLASS;
  }

  /**
   * 指Transform要操作内容的范围，官方文档Scope有7种类型：
   * EXTERNAL_LIBRARIES 只有外部库
   * PROJECT 只有项目内容
   * PROJECT_LOCAL_DEPS 只有项目的本地依赖(本地jar)
   * PROVIDED_ONLY 只提供本地或远程依赖项
   * SUB_PROJECTS 只有子项目。
   * SUB_PROJECTS_LOCAL_DEPS 只有子项目的本地依赖项(本地jar)。
   * TESTED_CODE 由当前变量(包括依赖项)测试的代码
   */
  @Override
  public Set<? super QualifiedContent.Scope> getScopes() {
    return TransformManager.SCOPE_FULL_PROJECT;
  }

  /**
   * 指明当前Transform是否支持增量编译，
   * 如果支持增量执行, 则变化输入内容可能包含 修改/删除/添加 文件的列表
   */
  @Override
  public boolean isIncremental() {
    return false;
  }

  /**
   * Transform中的核心方法，
   * inputs中是传过来的输入流，其中有两种格式，一种是jar包格式一种是目录格式。
   * outputProvider 获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
   */
  @Override
  public void transform(TransformInvocation transformInvocation) {
    System.out.println(TAG + "transform start");
    Collection<DirectoryInput> directoryInputs = new HashSet<>();
    Collection<JarInput> allJarInputs = new HashSet<>();
    Collection<JarInput> libraryJarInputs = new HashSet<>();

    // 非增量编译模式下修复有时候编译不过的问题
    if (!transformInvocation.isIncremental()) {
      try {
        transformInvocation.getOutputProvider().deleteAll();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    transformInvocation.getInputs().forEach(transformInput -> {
      System.out.println(TAG + "getInputs");
      directoryInputs.addAll(transformInput.getDirectoryInputs());
      allJarInputs.addAll(transformInput.getJarInputs());
      transformInput.getJarInputs().forEach(jarInput -> {
        // 处理 Hson 库
        HsonCodeInject.processHsonJar(jarInput);

        // 排除第三方 jar 包
        if (jarInput.getFile().getAbsolutePath().endsWith("classes.jar")) {
          libraryJarInputs.add(jarInput);
        }
      });
    });

    HsonCodeInject.processJsonCode(directoryInputs, libraryJarInputs);

    copyDirectory(transformInvocation, directoryInputs);
    copyJar(transformInvocation, allJarInputs);
    System.out.println(TAG + "transform end");
  }

  private void copyDirectory(TransformInvocation transformInvocation, Collection<DirectoryInput> directoryInputs) {
    directoryInputs.forEach(directoryInput -> {
      File destFile =
          transformInvocation.getOutputProvider().getContentLocation(directoryInput.getName(),
              directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
      System.out.println(TAG + "Directory:" + directoryInput.getFile().getAbsolutePath() + " : "
          + destFile.getAbsolutePath());
      try {
        // 将input的目录复制到output指定目录
        FileUtils.copyDirectory(directoryInput.getFile(), destFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void copyJar(TransformInvocation transformInvocation, Collection<JarInput> jarInputs) {
    jarInputs.forEach(jarInput -> {
      File destFile = transformInvocation.getOutputProvider().getContentLocation(
          jarInput.getFile().getAbsolutePath(), jarInput.getContentTypes(), jarInput.getScopes(),
          Format.JAR);
      System.out.println(
          TAG + "Jar:" + jarInput.getFile().getAbsolutePath() + " : " + destFile.getAbsolutePath());
      try {
        FileUtils.copyFile(jarInput.getFile(), destFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }
}
