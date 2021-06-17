package org.morgorithm.frames.util;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileUtils {
    public static String createDirIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    public static String toBase64(String path) throws IOException {
        File f = new File(path);
        if (f.isFile()) {
            byte[] bt = new byte[(int) f.length()];
            FileInputStream fis = new FileInputStream(f);

            try {
                fis.read(bt);
                String sbase64 = new String(Base64.encodeBase64(bt));
                return sbase64;
            } catch (Exception e) {
            } finally {
                fis.close();
            }
        }
        return null;
    }

    public static boolean isBase64(String url) {
        return url.contains("base64");
    }

    public static byte[] base64ToBytes(String base64) {
        if (isBase64(base64)) {
            base64 = base64.split("base64")[1];
            if (base64.startsWith(",")) base64 = base64.substring(1);
        }
        byte[] data = Base64.decodeBase64(base64);
        return data;
    }

    public static void downloadFromByte(String path, byte[] imageBytes) throws IOException {
        File test = new File(path);
        FileOutputStream fos = new FileOutputStream(test);
        fos.write(imageBytes);
        fos.close();
    }

    public static byte[] httpToByte(String imageUrl) {
        URL url = null;
        InputStream is = null;
        byte[] imageBytes = null;
        try {
            url = new URL(imageUrl);
            is = url.openStream();
            imageBytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes: %s", e.getMessage());
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageBytes;
    }

    public static String getFileExtension(String url) {
        String ext;
        if (FileUtils.isBase64(url)) {
            ext = url.substring("data:image/".length(), url.indexOf(";base64"));
            if (ext.equals("jpeg")) ext = "jpg";
        } else {
            ext = url.substring(url.lastIndexOf(".") + 1);
        }
        return ext;
    }

    public static byte[] urlToByte(String url) {
        byte[] bytes;
        if (FileUtils.isBase64(url)) {
            bytes = FileUtils.base64ToBytes(url);
        } else {
            bytes = FileUtils.httpToByte(url);
        }
        return bytes;
    }

    public static String getBase64EncodedImage(String imageURL) throws IOException {
        URL url = new URL(imageURL);
        InputStream is = url.openStream();
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        return Base64.encodeBase64String(bytes);
    }

    public static String generatedImagePath(String uploadPath) {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);
        File uploadPathFolder = new File(uploadPath, folderPath);
        if (!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs();
        }
        return folderPath;
    }
}
