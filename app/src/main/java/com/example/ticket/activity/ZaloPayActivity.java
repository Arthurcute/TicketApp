package com.example.ticket.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket.Api.CreateOrder;
import com.example.ticket.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ZaloPayActivity extends AppCompatActivity {

    private TextView userNameZ, emailZ, phoneZ, loaiVeZ, soLuongZ, tongTienZ, txtToken;
    private Button thanhToan;
    private String token;

    // Sử dụng ExecutorService để chạy các tác vụ nặng trong background thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zalo_pay);

        // Khởi tạo ZaloPay SDK trong môi trường sandbox
        ZaloPaySDK.init(2554, Environment.SANDBOX);

        // Gán các view từ layout vào các biến
        anhXa();

        // Nhận dữ liệu từ Intent và gán vào TextView tương ứng
        Intent intent = getIntent();
        layThongTinIntent(intent);

        // Đăng ký sự kiện thanh toán khi người dùng nhấn vào nút thanh toán
        thanhToan.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                taoDonHang();
            }
        });
    }

    // Gán các view từ XML vào các biến
    private void anhXa() {
        userNameZ = findViewById(R.id.nameUserZaloPay);
        emailZ = findViewById(R.id.emailUserZaloPay);
        phoneZ = findViewById(R.id.phoneUserZaloPay);
        loaiVeZ = findViewById(R.id.tenLoaiVeZaloPay);
        soLuongZ = findViewById(R.id.soLuongVeZaloPay);
        tongTienZ = findViewById(R.id.tongTienZaloPay);
        thanhToan = findViewById(R.id.thanhToanZaloPay);
        txtToken = findViewById(R.id.txtToken);
    }

    // Lấy thông tin từ Intent và hiển thị lên giao diện
    private void layThongTinIntent(Intent intent) {
        userNameZ.setText(intent.getStringExtra("userName"));
        emailZ.setText(intent.getStringExtra("email"));
        phoneZ.setText(intent.getStringExtra("phone"));
        loaiVeZ.setText(intent.getStringExtra("loaiVe"));
        soLuongZ.setText(intent.getStringExtra("soLuong"));
        tongTienZ.setText(intent.getStringExtra("tongTien"));  // Tổng tiền sẽ được lấy từ intent

        Log.d("ZaloPayActivity", "Received eventId: " + intent.getIntExtra("eventId", -1));
    }

    // Tạo đơn hàng và xử lý dữ liệu từ API
    private void taoDonHang() {
        String tongTien = tongTienZ.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (tongTien.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show());
            return;
        }

        // Thực hiện API tạo đơn hàng trong background thread
        executor.execute(() -> {
            try {
                CreateOrder orderApi = new CreateOrder();
                JSONObject data = orderApi.createOrder(tongTien); // Gọi API tạo đơn hàng với tổng tiền

                // Lấy các tham số từ phản hồi API
                int returnCode = data.getInt("return_code");
                String returnMessage = data.getString("return_message");
                int subReturnCode = data.optInt("sub_return_code", -1); // Có thể không có
                String subReturnMessage = data.optString("sub_return_message", "Không có thông tin chi tiết");

                // Hiển thị thông tin mã lỗi và thông điệp trả về
                Log.d("ZaloPayActivity", "Return code: " + returnCode);
                Log.d("ZaloPayActivity", "Return message: " + returnMessage);
                Log.d("ZaloPayActivity", "Sub return code: " + subReturnCode);
                Log.d("ZaloPayActivity", "Sub return message: " + subReturnMessage);

                // Xử lý kết quả dựa trên mã trả về
                runOnUiThread(() -> {
                    if (returnCode == 1) {  // Thành công
                        try {
                            token = data.getString("zp_trans_token");
                            txtToken.setText(token); // Hiển thị token trên giao diện
                            batDauThanhToan(); // Bắt đầu thanh toán
                        } catch (JSONException e) {
                            Toast.makeText(this, "Lỗi xử lý token", Toast.LENGTH_SHORT).show();
                            Log.e("ZaloPayActivity", "JSONException: " + e.getMessage());
                        }
                    } else {  // Thất bại
                        String message = "Tạo đơn hàng thất bại: " + returnMessage +
                                "\nChi tiết: " + subReturnMessage;
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e("ZaloPayActivity", "Exception: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Lỗi tạo đơn hàng", Toast.LENGTH_LONG).show());
            }
        });
    }

    // Bắt đầu quá trình thanh toán với ZaloPay SDK
    private void batDauThanhToan() {
        ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                hienThiDialog("Thanh toán thành công", "Giao dịch: " + transactionId, transToken);
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                hienThiDialog("Thanh toán bị hủy", "Giao dịch đã bị hủy", zpTransToken);
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                hienThiDialog("Lỗi thanh toán", zaloPayError.toString(), zpTransToken);
            }
        });
    }

    // Hiển thị dialog thông báo kết quả giao dịch
    private void hienThiDialog(String title, String message, String token) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(String.format("Thông tin: %s\nToken: %s", message, token != null ? token : "N/A"))
                .setPositiveButton("OK", (dialog, which) -> {
                    if (title.equals("Thanh toán thành công")) {
                        chuyenDenTrangThongBao("Thanh toán thành công!");
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Chuyển sang trang thông báo sau khi thanh toán thành công
    private void chuyenDenTrangThongBao(String noiDung) {
        Intent intent = new Intent(this, NotionPayActivity.class);
        intent.putExtra("noiDung", noiDung);
        startActivity(intent);
    }

    // Xử lý kết quả trả về từ ZaloPay
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
