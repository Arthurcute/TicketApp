package com.example.ticket.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.ticket.R;

import java.util.concurrent.Executor;

public class RegisterFingerprintActivity extends AppCompatActivity {

    private ImageView closeFinger, addFinger;
    private String userId, nameUser;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_fingerprint);


        SharedPreferences sp = getApplicationContext().getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userId = sp.getString("userId","");
        nameUser = sp.getString("userName","");

        addFinger = findViewById(R.id.addFinger);
        closeFinger = findViewById(R.id.closeFinger);

        // Nút đóng
        closeFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterFingerprintActivity.this, MyprofileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Nút thêm vân tay
        addFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    showBiometricPrompt();
                } else {
                    showDialog("Thiết bị không hỗ trợ vân tay!", false);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(RegisterFingerprintActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                // Lưu trạng thái đăng ký vân tay
                SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userId", userId);
                editor.putString("userName", nameUser);
                editor.putBoolean("isFingerprintRegistered", true);
                editor.apply();

                showDialog("Đăng ký vân tay thành công!", true);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showDialog("Đăng ký thất bại. Vui lòng thử lại!", false);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng ký vân tay")
                .setDescription("Hãy xác thực bằng vân tay để đăng ký.")
                .setNegativeButtonText("Hủy")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    // Hàm hiển thị Dialog
    private void showDialog(String message, boolean isSuccess) {
        new androidx.appcompat.app.AlertDialog.Builder(RegisterFingerprintActivity.this)
                .setTitle(isSuccess ? "Thành công" : "Thất bại")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }
}
