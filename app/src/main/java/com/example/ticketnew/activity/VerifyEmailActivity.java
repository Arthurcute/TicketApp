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

public class VerifyEmailActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnVerifyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        edtEmail = findViewById(R.id.edt_email);
        btnVerifyEmail = findViewById(R.id.btn_Verify_Email);

        btnVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();

                // Kiểm tra email trống hoặc không hợp lệ
                if (email.isEmpty()) {
                    Toast.makeText(VerifyEmailActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(VerifyEmailActivity.this, "Định dạng email không hợp lệ. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi OTP qua email bằng cách gọi API
                sendOtpToEmail(email);
            }
        });
    }

    private void sendOtpToEmail(String email) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);

            // Log JSON request để kiểm tra
            Log.d("VerifyEmailActivity", "JSON gửi đi: " + jsonRequest.toString());

            String url = Utils.ROOT_URL + "/" + Utils.SENDOTP_URL;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                    response -> {
                        try {
                            Log.d("VerifyEmailActivity", "Phản hồi từ server: " + response.toString());

                            if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "OTP đã được gửi! Vui lòng kiểm tra email của bạn", Toast.LENGTH_SHORT).show();
                                });

                                // Chuyển sang màn hình nhập OTP, truyền email đi cùng
                                Intent intent = new Intent(VerifyEmailActivity.this, EnterOTPActivity.class);
                                intent.putExtra("email", email); // Truyền email
                                startActivity(intent);
                            } else {
                                String message = response.optString("message", "Lỗi không xác định từ server");
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("VerifyEmailActivity", "Lỗi phân tích JSON: " + e.getMessage());
                            Toast.makeText(this, "Phản hồi không hợp lệ từ server", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> handleVolleyError(error)
            );

            // Thêm request vào hàng đợi
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (JSONException e) {
            Log.e("VerifyEmailActivity", "Lỗi tạo JSON request: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi khi gửi yêu cầu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleVolleyError(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            String errorMsg = new String(error.networkResponse.data);
            Log.e("VerifyEmailActivity", "Phản hồi lỗi từ server: " + errorMsg);
            Toast.makeText(this, "Lỗi từ server: " + errorMsg, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("VerifyEmailActivity", "Lỗi kết nối: " + error.getMessage());
            Toast.makeText(this, "Không thể kết nối tới server. Vui lòng kiểm tra mạng.", Toast.LENGTH_SHORT).show();
        }
    }
}
