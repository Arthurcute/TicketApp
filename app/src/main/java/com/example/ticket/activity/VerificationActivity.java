package com.example.ticket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket.R;
import com.example.ticket.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerificationActivity extends AppCompatActivity {
    private EditText verificationCode;
    private Button verify;
    private String email;  // Variable to store the email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Initialize views
        verificationCode = findViewById(R.id.editTextCode);
        verify = findViewById(R.id.buttonSubmit);

        // Get email from Intent extras
        email = getIntent().getStringExtra("email");

        Log.d("VerificationActivity", "Email nguoi dung: " + email);
        verify.setOnClickListener(view -> {
            String code = verificationCode.getText().toString().trim();
            if (code.isEmpty()) {
                Utils.showToast(VerificationActivity.this, "Hãy nhập mã xác nhận");
            } else {
                verifyCode(code);
            }
        });
    }

    private void verifyCode(String code) {
        String url = Utils.ROOT_URL + "/" + Utils.CODEREGISTER_URL;

        // Create a JSONObject to send in the POST request
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", email);
            jsonRequest.put("code", code);
        } catch (Exception e) {
            Log.e("VerificationActivity", "Error creating JSON request", e);
            return;
        }

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonRequest,
                response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        Log.d("VerificationActivity", "Server Response: " + message);

                        if ("success".equals(status)) {
                            Utils.showToast(VerificationActivity.this, "Xác nhận thành công");
                            Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Utils.showToast(VerificationActivity.this, message);
                        }
                    } catch (Exception e) {
                        Log.e("VerificationActivity", "Error parsing JSON response", e);
                        Utils.showToast(VerificationActivity.this, "Lỗi xử lý phản hồi từ server.");
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.toString());
                    Utils.showToast(VerificationActivity.this, "Lỗi kết nối. Vui lòng thử lại sau.");
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }


}
