//package org.dspace.app.rest.diracai.util;
//
//import javax.crypto.Cipher;
//import javax.crypto.CipherOutputStream;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//
//public class AESUtil {
//    private static final String ALGORITHM = "AES";
//    private static final String TRANSFORMATION = "AES";
//
//    public static byte[] encrypt(InputStream input, String keyStr) throws Exception {
//        byte[] key = Arrays.copyOf(keyStr.getBytes(StandardCharsets.UTF_8), 32); // 256-bit
//        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
//
//        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        CipherOutputStream cipherOut = new CipherOutputStream(outputStream, cipher);
//
//        byte[] buffer = new byte[4096];
//        int bytesRead;
//        while ((bytesRead = input.read(buffer)) != -1) {
//            cipherOut.write(buffer, 0, bytesRead);
//        }
//
//        cipherOut.close();
//        return outputStream.toByteArray();
//    }
//}



package org.dspace.app.rest.diracai.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.crypto.CipherOutputStream;


public class AESUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static byte[] encrypt(InputStream input, String keyStr) throws Exception {
        byte[] key = Arrays.copyOf(keyStr.getBytes(StandardCharsets.UTF_8), 32); // 256-bit
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CipherOutputStream cipherOut = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                cipherOut.write(buffer, 0, bytesRead);
            }
        }

        return outputStream.toByteArray();
    }

    public static byte[] decrypt(byte[] encryptedBytes, String keyStr) throws Exception {
        byte[] key = Arrays.copyOf(keyStr.getBytes(StandardCharsets.UTF_8), 32); // 256-bit
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encryptedBytes);
    }
}

