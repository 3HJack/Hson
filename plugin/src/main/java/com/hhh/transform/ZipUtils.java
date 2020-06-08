package com.hhh.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class ZipUtils {

  private static final String TAG = "ZipUtils:";

  public static void zipDir(String outputFileName, String inputDir) throws IOException {
    System.out.println(TAG + outputFileName + " : " + inputDir);
    File dir = new File(inputDir);
    FileOutputStream fos = new FileOutputStream(outputFileName);
    JarOutputStream jos = new JarOutputStream(fos);
    zipDir(jos, dir, "");
    jos.close();
  }

  private static void zipDir(JarOutputStream jos, File file, String base) throws IOException {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      jos.putNextEntry(new JarEntry(base + File.separator));
      base = base.length() == 0 ? "" : base + File.separator;
      for (int i = 0; i < files.length; i++) {
        zipDir(jos, files[i], base + files[i].getName());
      }
    } else {
      jos.putNextEntry(new JarEntry(base));
      FileInputStream fis = new FileInputStream(file);
      int length;
      while ((length = fis.read()) != -1) {
        jos.write(length);
      }
      fis.close();
    }
  }
}
