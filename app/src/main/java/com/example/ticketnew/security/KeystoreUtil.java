package com.example.ticketnew.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import javax.crypto.Cipher;
import java.security.KeyStore;

public class KeystoreUtil {
    private static final String KEY_ALIAS = "MyRSAKey";

    public static void generateRSAKeyPairIfNeeded() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        // Nếu alias đã tồn tại, không cần tạo khóa mới
        if (keyStore.containsAlias(KEY_ALIAS)) {
            Log.d("KeystoreUtil", "Cặp khóa RSA đã tồn tại, không cần tạo lại.");
            return;
        }

        // Tạo cặp khóa RSA mới
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
        keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(2048) // Kích thước khóa
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build());

        keyPairGenerator.generateKeyPair();
        Log.d("KeystoreUtil", "Cặp khóa RSA đã được tạo thành công.");
    }

    // Lấy khóa công khai từ Keystore
    public static PublicKey getPublicKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        // Kiểm tra alias có tồn tại
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            throw new Exception("Alias không tồn tại trong Keystore");
        }

        // Lấy chứng chỉ và kiểm tra null
        if (keyStore.getCertificate(KEY_ALIAS) == null) {
            throw new Exception("Certificate không tồn tại cho alias: " + KEY_ALIAS);
        }

        return keyStore.getCertificate(KEY_ALIAS).getPublicKey();
    }

    // Lấy khóa riêng từ Keystore
    public static PrivateKey getPrivateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        // Kiểm tra alias có tồn tại
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            throw new Exception("Alias không tồn tại trong Keystore");
        }

        // Lấy khóa riêng và kiểm tra null
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(KEY_ALIAS, null);
        if (privateKey == null) {
            throw new Exception("Không thể lấy khóa riêng từ Keystore");
        }

        return privateKey;
    }
}
