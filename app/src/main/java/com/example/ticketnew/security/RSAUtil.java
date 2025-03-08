package com.example.ticketnew.security;

import android.os.Build;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAUtil {


    // Mã hóa khóa AES bằng RSA
    public static String encryptWithRSA(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(encrypted);
        }
        return data;
    }


    // Giải mã khóa AES bằng RSA
    public static String decryptWithRSA(String encryptedData, PrivateKey privateKey) throws Exception {
        // Đảm bảo sử dụng đúng thuật toán và padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        }
        return new String(decrypted);
    }

}
