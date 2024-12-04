package com.example.ticket.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.ticket.R;

import java.util.concurrent.Executor;

public class LoginNewActivity extends AppCompatActivity {

    private TextView nameUser, newAccount, forgotPass;
    private EditText pass;
    private Button login;
    private ImageView loginFinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_new);
        // Ánh xạ các thành phần giao diện
        nameUser = findViewById(R.id.nameUserFinger);
        newAccount = findViewById(R.id.newAccount);
        forgotPass = findViewById(R.id.forgotPassFinger);
        pass = findViewById(R.id.passLoginFinger);
        login = findViewById(R.id.loginPassFinger);
        loginFinger = findViewById(R.id.loginWithFinger);

        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isFingerprintRegistered = preferences.getBoolean("isFingerprintRegistered", false);
        String userName = preferences.getString("userName", null);
        String userId = preferences.getString("userId", null);

        // Hiển thị tên người dùng nếu có
        if (userName != null) {
            nameUser.setText(userName);
        }

        // Nếu vân tay đã được đăng ký, cho phép đăng nhập bằng vân tay
        loginFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFingerprintRegistered) {
                    // Nếu đã đăng ký vân tay, tiến hành xác thực vân tay
                    showBiometricPrompt();
                } else {
                    // Nếu chưa đăng ký vân tay, yêu cầu đăng nhập bằng mật khẩu
                    showFingerprintNotRegisteredDialog();
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút đăng nhập (dùng mật khẩu)
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = pass.getText().toString().trim();
                if (password.isEmpty()) {
                    Toast.makeText(LoginNewActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý đăng nhập bằng mật khẩu ở đây
                    navigateToHomeScreen();
                }
            }
        });
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Toast.makeText(LoginNewActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                navigateToHomeScreen();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginNewActivity.this, "Vân tay không chính xác!", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập bằng vân tay")
                .setSubtitle("Sử dụng vân tay để tiếp tục")
                .setNegativeButtonText("Hủy")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(LoginNewActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // Hiển thị hộp thoại nếu người dùng chưa đăng ký vân tay
    private void showFingerprintNotRegisteredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginNewActivity.this);
        builder.setTitle("Thông báo")
                .setMessage("Bạn chưa đăng ký đăng nhập bằng vân tay. Hãy đăng nhập bằng mật khẩu!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cho phép người dùng nhập mật khẩu
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
