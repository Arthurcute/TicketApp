package com.example.ticketnew.security;

import android.os.Build;

import androidx.annotation.RequiresApi;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class AESUtil {
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    // Mã hóa AES
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String data, String key) throws Exception {
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]); // IV mặc định
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Giải mã AES
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String encryptedData, String key) throws Exception {
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]); // IV mặc định
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted);
    }

    // Tạo khóa AES ngẫu nhiên
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // 128
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}

