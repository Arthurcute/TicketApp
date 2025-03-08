// EnterOTPActivity.java
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

public class EnterOTPActivity extends AppCompatActivity {

    private EditText edtOtp;
    private Button btnVerifyOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otpactivity);

        edtOtp = findViewById(R.id.edt_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);

        // Lấy email từ Intent
        final String email = getIntent().getStringExtra("email");

        btnVerifyOtp.setOnClickListener(v -> {
            String inputOtp = edtOtp.getText().toString().trim();
            if (inputOtp.isEmpty()) {
                Toast.makeText(EnterOTPActivity.this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi yêu cầu xác minh OTP
            verifyOtp(email, inputOtp);
        });
    }

    private void verifyOtp(String email, String otp) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);  // Đảm bảo gửi cả email và OTP
            jsonRequest.put("otp", otp);

            String url = Utils.ROOT_URL + "/" + Utils.VERIFYOTP_URL;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                    response -> {
                        try {
                            if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                                Toast.makeText(this, "OTP hợp lệ!", Toast.LENGTH_SHORT).show();
                                // Chuyển sang màn hình reset password và truyền email, otp
                                Intent intent = new Intent(EnterOTPActivity.this, ResetPwdActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            } else {
                                String message = response.optString("message", "Lỗi không xác định từ server");
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("EnterOTPActivity", "Lỗi phân tích JSON: " + e.getMessage());
                            Toast.makeText(this, "Phản hồi không hợp lệ từ server", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> handleVolleyError(error)
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        } catch (JSONException e) {
            Log.e("EnterOTPActivity", "Lỗi tạo JSON request: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi khi gửi yêu cầu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleVolleyError(VolleyError error) {
        Log.e("EnterOTPActivity", "Lỗi kết nối: " + error.getMessage());
        Toast.makeText(this, "Không thể kết nối tới server. Vui lòng kiểm tra mạng.", Toast.LENGTH_SHORT).show();
    }
}
