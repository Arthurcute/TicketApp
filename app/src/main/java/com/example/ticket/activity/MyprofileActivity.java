package com.example.ticket.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.ticket.R;
import com.example.ticket.network.DataManager;
import com.example.ticket.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;

import java.util.concurrent.Executor;

public class MyprofileActivity extends AppCompatActivity {

    private TextView save, namePro, logout, xinChao, registerFinger;
    private EditText nameProfile, emailProfile, phoneProfile, birthProfile, genderProfile;
    private ImageView edit;
    private String userId;
    private boolean isFingerprintRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myprofile);

        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = preferences.getString("userId", null);
        isFingerprintRegistered = preferences.getBoolean("isFingerprintRegistered", false);

        if (userId != null) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(this, "User ID không tồn tại", Toast.LENGTH_SHORT).show();
        }

        anhXa();
        setupBottomNavigation();
        displayGreeting();

        // Thiết lập sự kiện cho nút Edit
        edit.setOnClickListener(view -> {
            namePro.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
        });

        // Thiết lập sự kiện cho nút Logout
        logout.setOnClickListener(view -> {
            new AlertDialog.Builder(MyprofileActivity.this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("userId");
                        editor.remove("userName");
                        editor.remove("isFingerprintRegistered");
                        editor.apply();

                        startActivity(new Intent(MyprofileActivity.this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        save.setOnClickListener(view -> {
            if (validateFields()) {
                saveUserProfile(userId);
            } else {
                Utils.showCustomDialog(MyprofileActivity.this, "Thông báo", "Hãy điền đầy đủ thông tin!");
            }
        });

        if (!isFingerprintRegistered) {
            registerFinger.setText("Đăng nhập bằng vân tay!");
        } else {
            registerFinger.setText("Tắt đăng nhập bằng vân tay!");
        }

        registerFinger.setOnClickListener(view -> {
//            if (!isFingerprintRegistered) {
//                showBiometricPrompt();
//            } else if (isFingerprintRegistered){
//                new AlertDialog.Builder(MyprofileActivity.this)
//                        .setTitle("Thông báo")
//                        .setMessage("Tắt chức năng đăng nhập bằng vân tay?")
//                        .setPositiveButton("Tắt", (dialog, which) -> {
//                            isFingerprintRegistered = false;
//                            preferences.edit().putBoolean("isFingerprintRegistered", false).apply();
//                            registerFinger.setText("Đăng nhập bằng vân tay!");
//                        })
//                        .setNegativeButton("Quay lại", null)
//                        .show();
//            }
            isFingerprint(this);

        });
    }

    public boolean isFingerprint(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                if (!isFingerprintRegistered) {
                    showBiometricPrompt();
                } else if (isFingerprintRegistered){
                    new AlertDialog.Builder(MyprofileActivity.this)
                            .setTitle("Thông báo")
                            .setMessage("Tắt chức năng đăng nhập bằng vân tay?")
                            .setPositiveButton("Tắt", (dialog, which) -> {
                                isFingerprintRegistered = false;
                                SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                preferences.edit().putBoolean("isFingerprintRegistered", false).apply();
                                registerFinger.setText("Đăng nhập bằng vân tay!");
                            })
                            .setNegativeButton("Quay lại", null)
                            .show();
                }
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                new AlertDialog.Builder(MyprofileActivity.this)
                        .setTitle("Thông báo")
                        .setMessage("Thiết bị không hỗ trợ xác nhận vân tay")
                        .setPositiveButton("Tắt", (dialog, which) -> {
                            isFingerprintRegistered = false;
                            registerFinger.setText("Đăng nhập bằng vân tay!");
                        })
                        .setNegativeButton("Đóng", null)
                        .show();
                return false;
//
//            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                // Phần cứng sinh trắc học không khả dụng
//                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                new AlertDialog.Builder(MyprofileActivity.this)
                        .setTitle("Thông báo")
                        .setMessage("Thiết bị chưa cài đặt vân tay. Bạn vui lòng cài đặt vân tay trên thiết bị trước")
                        .setPositiveButton("Tắt", (dialog, which) -> {
                            isFingerprintRegistered = false;
                            registerFinger.setText("Đăng nhập bằng vân tay!");
                        })
                        .setNegativeButton("Đóng", null)
                        .show();
                return false;

            default:
                return false;
        }
    }

    private void showBiometricPrompt() {
        new AlertDialog.Builder(MyprofileActivity.this)
                .setTitle("Kích hoạt đăng nhập vân tay")
                .setMessage("Bạn có chắc chắn muốn kích hoạt chức năng đăng nhập bằng vân tay?")
                .setPositiveButton("Kích hoạt", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        showPasswordDialog();

                    } else {
                        showDialog("Thiết bị không hỗ trợ vân tay!", false);
                    }
                })
                .setNegativeButton("Quay lại", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startBiometricAuthentication() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MyprofileActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);


                SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isFingerprintRegistered", true);
                editor.apply();
                showDialog("Kích hoạt đăng nhập bằng vân tay thành công!", true);
                registerFinger.setText("Tắt chức năng đăng nhập bằng vân tay!");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showDialog("Kích hoạt không thành công!", false);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng ký vân tay")
                .setDescription("Hãy xác thực bằng vân tay để đăng ký.")
                .setNegativeButtonText("Hủy")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottom_menu = findViewById(R.id.bottom_profile);
        bottom_menu.setSelectedItemId(R.id.menu_account);

        bottom_menu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_account) {
                return true;
            } else if (itemId == R.id.menu_ticket) {
                startActivity(new Intent(getApplicationContext(), MyTicketActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_save) {
                startActivity(new Intent(getApplicationContext(), SearchEventActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_notion) {
                startActivity(new Intent(getApplicationContext(), NotionActivity.class));
                finish();
                return true;
            } else {
                return false;
            }
        });
    }

    private void saveUserProfile(String userId) {
        String name = nameProfile.getText().toString().trim();
        String email = emailProfile.getText().toString().trim();
        String phone = phoneProfile.getText().toString().trim();
        String birth = birthProfile.getText().toString().trim();
        String gender = genderProfile.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || birth.isEmpty() || gender.isEmpty()) {
            Utils.showCustomDialog(MyprofileActivity.this, "Thông báo", "Hãy điền đầy đủ thông tin!");
            return;
        }

        DataManager.getInstance(this).updateUserProfile(userId, name, email, phone, birth, gender, new DataManager.UpdateProfileCallback() {
            @Override
            public void onSuccess(String message) {
                Utils.showCustomDialog(MyprofileActivity.this, "Thông báo", "Chỉnh sửa thông tin thành công!");
                loadUserProfile(userId);
                enableEditing(false);
                namePro.setVisibility(View.VISIBLE);
                save.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MyprofileActivity.this, "Lỗi cập nhật thông tin: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void anhXa() {
        edit = findViewById(R.id.editProfile);
        xinChao = findViewById(R.id.xinChao);
        namePro = findViewById(R.id.nameProfile);
        save = findViewById(R.id.saveProfile);
        logout = findViewById(R.id.logout);
        nameProfile = findViewById(R.id.nameProfile1);
        emailProfile = findViewById(R.id.emailProfile);
        phoneProfile = findViewById(R.id.phoneProfile);
        birthProfile = findViewById(R.id.birthProfile);
        genderProfile = findViewById(R.id.genderProfile);
        registerFinger = findViewById(R.id.registerFinger);
    }

    private void loadUserProfile(String userId) {
        int userIdInt = Integer.parseInt(userId);

        DataManager.getInstance(this).fetchUserProfile(userIdInt, new DataManager.UserProfileCallback() {
            @Override
            public void onSuccess(String userName, String email, String phone, String birth, String gender) {
                namePro.setText(userName);
                nameProfile.setText(userName);
                emailProfile.setText(email);
                phoneProfile.setText(phone);
                birthProfile.setText(birth);
                genderProfile.setText(gender);


                SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userName", userName);
                editor.apply();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MyprofileActivity.this, "Lỗi tải thông tin người dùng: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showPasswordDialog() {
        // Tạo Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // Không cho phép đóng bằng cách nhấn ngoài dialog

        // Inflate layout cho dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.adilog_checkpass, null);
        builder.setView(dialogView);

        // Lấy các view trong layout
        EditText passwordInput = dialogView.findViewById(R.id.passwordInput);
        TextView errorText = dialogView.findViewById(R.id.errorText);
        Button checkButton = dialogView.findViewById(R.id.checkButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        // Tạo dialog
        AlertDialog dialog = builder.create();

        // Xử lý nút Check
        checkButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();



            // Kiểm tra mật khẩu
            if (password.isEmpty()) {
                errorText.setText("Vui lòng nhập mật khẩu");
                errorText.setVisibility(View.VISIBLE);
            } else {


                String email = emailProfile.getText().toString().trim();

                DataManager.getInstance(MyprofileActivity.this).checkUserInDatabase(email, password, new DataManager.LoginCallback() {
                    @Override
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    public void onSuccess(String userId) {
                        dialog.dismiss();
                        startBiometricAuthentication();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Hiển thị thông báo lỗi nếu có
                        // Toast.makeText(MyprofileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        errorText.setText(errorMessage);
                        errorText.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        // Xử lý nút Close
        closeButton.setOnClickListener(v -> dialog.dismiss());
        // Hiển thị dialog
        dialog.show();
    }

    private void displayGreeting() {

        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = preferences.getString("userName", "Người dùng");
        xinChao.setText("Xin chào " + userName);
    }

    private void enableEditing(boolean isEditable) {
        nameProfile.setFocusable(isEditable);
        nameProfile.setFocusableInTouchMode(isEditable);
        emailProfile.setFocusable(isEditable);
        emailProfile.setFocusableInTouchMode(isEditable);
        phoneProfile.setFocusable(isEditable);
        phoneProfile.setFocusableInTouchMode(isEditable);
        birthProfile.setFocusable(isEditable);
        birthProfile.setFocusableInTouchMode(isEditable);
        genderProfile.setFocusable(isEditable);
        genderProfile.setFocusableInTouchMode(isEditable);
    }

    private boolean validateFields() {
        return !nameProfile.getText().toString().trim().isEmpty() && !emailProfile.getText().toString().trim().isEmpty()
                && !phoneProfile.getText().toString().trim().isEmpty() && !birthProfile.getText().toString().trim().isEmpty()
                && !genderProfile.getText().toString().trim().isEmpty();
    }

    private void showDialog(String message, boolean isSuccess) {
        Toast.makeText(MyprofileActivity.this, message, isSuccess ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }
}
