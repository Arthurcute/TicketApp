package com.example.ticket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ticket.R;
import com.example.ticket.adapter.TicketFragmentAdapter;

public class BuyTicketActivity extends AppCompatActivity {
    private ImageView exitBuyTicket;
    private TextView titleBuy;
    private ViewPager viewPagerTicket;
    private int eventId;
    private String nameSK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buy_ticket);

        // Áp dụng window insets cho layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các View
        anhXa();

        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", -1);

        nameSK = intent.getStringExtra("name");
        titleBuy.setText(nameSK); // Gán tên sự kiện

        // Thiết lập ViewPager với FragmentAdapter
        TicketFragmentAdapter ticketFragmentAdapter = new TicketFragmentAdapter(
                getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                eventId // Truyền eventId vào FragmentAdapter
        );
        viewPagerTicket.setAdapter(ticketFragmentAdapter);

        // Xử lý sự kiện nhấn nút thoát
        exitBuyTicket.setOnClickListener(view -> {
            new AlertDialog.Builder(BuyTicketActivity.this)
                    .setTitle("Xác nhận hủy")
                    .setMessage("Bạn có muốn hủy đơn?")
                    .setPositiveButton("Có", (dialog, which) -> finish()) // Đóng Activity
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    // Phương thức để ánh xạ các View
    private void anhXa() {
        viewPagerTicket = findViewById(R.id.viewPagerTicket);
        exitBuyTicket = findViewById(R.id.exitBuyTicket);
        titleBuy = findViewById(R.id.titleBuy);
    }
}
