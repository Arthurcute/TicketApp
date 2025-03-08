package com.example.ticketnew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticketnew.R;
import com.example.ticketnew.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPwdActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnResetPassword;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_new_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");
        Log.d("ResetPwdActivity", "Email received: " + email);

        btnResetPassword.setOnClickListener(v -> {
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ResetPwdActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ResetPwdActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi yêu cầu reset mật khẩu
            resetPassword(email, newPassword);
        });
    }

    private void resetPassword(String email, String newPassword) {
        try {
            // Tạo JSON request
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("new_password", newPassword);

            String url = Utils.ROOT_URL + "/" + Utils.RESETPWD_URL;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                    response -> {
                        try {
                            Log.d("ResetPwdActivity", "Server response: " + response.toString());
                            if (response.has("success") && response.getBoolean("success")) {
                                // Chuyển Toast vào main thread
                                runOnUiThread(() -> {
                                    Toast.makeText(ResetPwdActivity.this, "Mật khẩu đã được thay đổi thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ResetPwdActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                            } else {
                                // Xử lý lỗi từ server
                                String message = response.optString("message", "Lỗi không xác định");
                                Log.e("ResetPwdActivity", "Server error: " + message);
                                runOnUiThread(() -> {
                                    Toast.makeText(ResetPwdActivity.this, message, Toast.LENGTH_SHORT).show();
                                });
                            }

                        } catch (JSONException e) {
                            Log.e("ResetPwdActivity", "Lỗi phân tích JSON: " + e.getMessage());
                            Toast.makeText(ResetPwdActivity.this, "Phản hồi không hợp lệ từ server", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> handleVolleyError(error)
            );

            // Thêm request vào hàng đợi
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (JSONException e) {
            Log.e("ResetPwdActivity", "Lỗi tạo JSON request: " + e.getMessage());
            Toast.makeText(ResetPwdActivity.this, "Đã xảy ra lỗi khi gửi yêu cầu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleVolleyError(VolleyError error) {
        Log.e("ResetPwdActivity", "Lỗi kết nối: " + error.getMessage());
        Toast.makeText(ResetPwdActivity.this, "Không thể kết nối tới server. Vui lòng kiểm tra mạng.", Toast.LENGTH_SHORT).show();
    }
}
