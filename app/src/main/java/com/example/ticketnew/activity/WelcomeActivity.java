package com.example.ticketnew.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.ticketnew.R;
import com.example.ticketnew.adapter.WelcomePagerAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private int[] imageIds = {R.drawable.img1, R.drawable.img2, R.drawable.img3};
    private String[] captions = {
            "Mua vé tham dự các sự kiện nổi bật yêu thích một cách dễ dàng, tiện lợi trên điện thoại thông minh.",
            "Thanh toán an toàn và nhanh chóng qua phương thức thanh toán thông minh.",
            "Nắm bắt thông tin các sự kiện sẽ diễn ra trong tương lai sớm nhất tại TicketOn"
    };

    private Timer timer;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        WelcomePagerAdapter adapter = new WelcomePagerAdapter(this, imageIds, captions);
        viewPager.setAdapter(adapter);

        // Đặt lịch chuyển trang tự động
        startAutoSlide();
    }
    private void startAutoSlide() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    int currentPage = viewPager.getCurrentItem();
                    if (currentPage < imageIds.length - 1) {
                        // Chuyển đến trang tiếp theo
                        viewPager.setCurrentItem(currentPage + 1, true);
                    } else {
                        // Đến trang cuối cùng, chuyển đến trang Login phù hợp
                        timer.cancel();

                        // Kiểm tra trạng thái vân tay và thông tin người dùng đã được lưu trong SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        boolean isFingerprintRegistered = preferences.getBoolean("isFingerprintRegistered", false);
                        String userId = preferences.getString("userId", null); // Lấy userId từ SharedPreferences

                        if (userId != null && !userId.isEmpty()) {
                            // Nếu có thông tin người dùng (userId), chuyển sang LoginNewActivity
                              Intent intent = new Intent(WelcomeActivity.this, LoginNewActivity.class);
                                startActivity(intent);
                                finish();
                        } else {
                            // Nếu không có thông tin người dùng, chuyển đến LoginActivity (đăng nhập qua email/mật khẩu)
                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
            }
        }, 0, 3000); // Bắt đầu ngay lập tức và sau đó cứ mỗi 3 giây
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel(); // Đảm bảo Timer được hủy khi Activity bị hủy
        }
    }
}
