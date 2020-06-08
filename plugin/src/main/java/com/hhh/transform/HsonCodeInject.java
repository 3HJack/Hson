package com.hhh.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.hhh.hson.constant.Constants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class HsonCodeInject {

  private static final String TAG = "HsonCodeInject:";

  private static final String LIBRARY_CLASS_SUFFIX = "javac/debug/classes";
  private static final String LIBRARY_JAR_SUFFIX = "runtime_library_classes/debug/classes.jar";
  private static final String CLASS_SUFFIX = "class";

  private static final Map<String, Boolean> mDirectoryModified = new HashMap<>();

  private static String HSON_ENTRY_NAME;

  public static void processHsonJar(JarInput jarInput) {
    File hsonJarFile = jarInput.getFile();
    if (!hsonJarFile.getAbsolutePath().endsWith(".jar")) {
      return;
    }
    try {
      JarFile jarFile = new JarFile(hsonJarFile);
      JarEntry jarEntry = jarFile.getJarEntry(getHsonEntryName());
      if (jarEntry != null) {
        processHsonJar(jarFile, hsonJarFile);
      }
      jarFile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void processHsonJar(JarFile jarFile, File hsonJarFile) throws IOException {
    File tempHsonJarFile = new File(hsonJarFile.getParent(), "tempHsonJar.jar");
    // 避免上次的缓存被重复插入
    if (tempHsonJarFile.exists()) {
      tempHsonJarFile.delete();
    }
    FileOutputStream fos = new FileOutputStream(tempHsonJarFile);
    JarOutputStream jos = new JarOutputStream(fos);
    Enumeration<JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      InputStream is = jarFile.getInputStream(entry);
      if (!entry.getName().equals(getHsonEntryName())) {
        jos.putNextEntry(entry);
        jos.write(IOUtils.toByteArray(is));
      } else {
        // 这里对字节码进行了修改，一定要 new 一个新的 entry，否则会崩
        // java.util.zip.ZipException: invalid entry size (expected 1275 but got 1493 bytes)
        jos.putNextEntry(new JarEntry(getHsonEntryName()));
        ClassReader classReader = new ClassReader(is);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        HsonClassVisitor classVisitor = new HsonClassVisitor(classWriter);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        jos.write(classWriter.toByteArray());
      }
      jos.closeEntry();
    }
    jos.close();
    FileUtils.copyFile(tempHsonJarFile, hsonJarFile);
  }

  public static void processJsonCode(Collection<DirectoryInput> directoryInputs,
                                     Collection<JarInput> jarInputs) {
    processApplication(directoryInputs);
    if (jarInputs != null && !jarInputs.isEmpty()) {
      processJar(jarInputs);
    }
  }

  private static void processApplication(Collection<DirectoryInput> directoryInputs) {
    directoryInputs.forEach(directoryInput -> {
      processDirectory(directoryInput.getFile().getAbsolutePath());
    });
  }

  private static void processJar(Collection<JarInput> jarInputs) {
    jarInputs.forEach(jarInput -> {
      String directory = jarInput.getFile().getAbsolutePath();
      directory = directory.replace(LIBRARY_JAR_SUFFIX, LIBRARY_CLASS_SUFFIX);
      processDirectory(directory);
      Boolean modified = mDirectoryModified.get(directory);
      if (modified != null && modified) {
        System.out.println(TAG + "processJar:" + directory);
        try {
          ZipUtils.zipDir(jarInput.getFile().getAbsolutePath(), directory);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private static void processDirectory(String directory) {
    FileUtils.listFiles(new File(directory), new String[]{CLASS_SUFFIX}, true)
        .forEach(file -> {
          System.out.println(TAG + file.getAbsolutePath());
          processClass(file, directory);
        });
  }

  private static void processClass(File file, String directory) {
    try {
      FileInputStream fis = new FileInputStream(file);
      ClassReader classReader = new ClassReader(fis);
      // 不能设置为 ClassWriter.COMPUTE_FRAMES，否则会崩溃，异常为
      // Caused by: java.lang.ClassNotFoundException: android.text.Spanned
      ClassWriter classWriter = new ClassWriter(classReader, 0);
      JsonClassVisitor classVisitor = new JsonClassVisitor(classWriter);
      classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
      if (classVisitor.isClassModified()) {
        mDirectoryModified.put(directory, true);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(classWriter.toByteArray());
        fos.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String getHsonEntryName() {
    if (HSON_ENTRY_NAME != null) {
      return HSON_ENTRY_NAME;
    }
    HSON_ENTRY_NAME = Constants.HSON_TYPE + ".class";
    return HSON_ENTRY_NAME;
  }
}
