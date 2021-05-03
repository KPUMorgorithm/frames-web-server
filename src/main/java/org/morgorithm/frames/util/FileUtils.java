package org.morgorithm.frames.util;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;
import java.net.URL;

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
        if ( f.isFile() ) {
            byte[] bt = new byte[ (int) f.length() ];
            FileInputStream fis = new FileInputStream( f );

            try {
                fis.read( bt );
                String sbase64 = new String ( Base64.encodeBase64( bt ) );
                return sbase64;
            } catch(Exception e ) {
            } finally {
                fis.close();
            }
        }
        return null;
    }

    public static void downloadFromByte(String path, byte imageBytes[]) throws IOException {
        File test =  new File(path);
        FileOutputStream fos = new FileOutputStream(test);
        fos.write(imageBytes);
        fos.close();
    }

    public static byte[] toByte(String imageUrl) {
        URL url = null;
        InputStream is = null;
        byte[] imageBytes = null;
        try {
            url = new URL(imageUrl);
            is = url.openStream();
            imageBytes = IOUtils.toByteArray(is);
        }
        catch (IOException e) {
            System.err.printf ("Failed while reading bytes: %s", e.getMessage());
            e.printStackTrace ();
        }
        finally {
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

    public static String getBase64EncodedImage(String imageURL) throws IOException {
        URL url = new URL(imageURL);
        InputStream is = url.openStream();
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        return Base64.encodeBase64String(bytes);
    }

}
