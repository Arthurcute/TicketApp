package com.example.ticketnew.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketnew.R;
import com.example.ticketnew.adapter.OrderAdapter;
import com.example.ticketnew.model.Order;
import com.example.ticketnew.network.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MyTicketActivity extends AppCompatActivity {
    private RecyclerView viewTicket;
    private DataManager dataManager;
    private  List<Order> orderList;
    private OrderAdapter orderAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_ticket);

        // Thiết lập padding cho View
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy userId từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = preferences.getString("userId", null);

        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID is not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        anhXa();

        // Khởi tạo DataManager
        dataManager = DataManager.getInstance(this);

        // Khởi tạo TicketAdapter với danh sách vé và đối tượng DataManager
        orderList = new ArrayList<>();
         orderAdapter = new OrderAdapter(orderList, dataManager);
        viewTicket.setLayoutManager(new LinearLayoutManager(this)); // Thiết lập LayoutManager
        viewTicket.setAdapter(orderAdapter);

        fetchOrders(userId);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottom_menu = findViewById(R.id.bottom_ticket);
        bottom_menu.setSelectedItemId(R.id.menu_ticket);

        bottom_menu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_ticket) {
                return true;
            } else if (itemId == R.id.menu_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_save) {
                startActivity(new Intent(getApplicationContext(), SearchEventActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_account) {
                startActivity(new Intent(getApplicationContext(), MyprofileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_notion) {
                startActivity(new Intent(getApplicationContext(), NotionActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void fetchOrders(String userId) {
        Log.d("MyTicketActivity", "Fetching order tickets for userId: " + userId);
        dataManager.fetchTicketsOrder(userId, new DataManager.OrderCallBackList() {
            @Override
            public void onSuccess(List<Order> order) {
                Log.d("MyTicketActivity", "Tickets fetched successfully: " + order.size());
                orderList.clear();
                orderList.addAll(order);
                orderAdapter.notifyDataSetChanged(); // Cập nhật adapter để hiển thị vé

                if (order.isEmpty()) {
//                    Toast.makeText(MyTicketActivity.this, "No tickets found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
//                 Toast.makeText(MyTicketActivity.this, "Error fetching tickets: " + errorMessage, Toast.LENGTH_SHORT).show();
                 Log.e("MyTicketActivity", "Error fetching tickets: " + errorMessage);
            }
        });
    }


    private void anhXa() {
        viewTicket = findViewById(R.id.viewTicket);
    }
}
