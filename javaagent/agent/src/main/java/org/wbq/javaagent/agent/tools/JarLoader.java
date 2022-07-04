package org.wbq.javaagent.agent.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarLoader {
    public static Map<String, byte[]> loadJarClass(String jarPath) {
        Map<String, byte[]> className2Classes = new HashMap<>();
        try (ZipFile zipFile = new ZipFile(jarPath)) {
            Enumeration<? extends ZipEntry> entrys = zipFile.entries();
            while (entrys.hasMoreElements()) {
                ZipEntry zipEntry = entrys.nextElement();
                if (zipEntry.getSize() > 0) {
                    String fileName = zipEntry.getName();
                    if (!fileName.endsWith(".class")) {
                        continue;
                    }

                    try (InputStream input = zipFile.getInputStream(zipEntry);
                         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                        if (input == null) {
                            continue;
                        }
                        int b;
                        while ((b = input.read()) != -1) {
                            byteArrayOutputStream.write(b);
                        }
                        byte[] bytes = byteArrayOutputStream.toByteArray();

                        String name1 = fileName.replaceAll("\\.class", "");
                        String name2 = name1.replaceAll("/", ".");

                        className2Classes.put(name2, bytes);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return className2Classes;
    }
}
