package com.example.ticket.activity;

import android.content.Context;
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

import com.example.ticket.R;
import com.example.ticket.adapter.TicketAdapter;
import com.example.ticket.model.Ticket;
import com.example.ticket.network.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MyTicketActivity extends AppCompatActivity {
    private RecyclerView viewTicket;
    private TicketAdapter ticketAdapter;
    private DataManager dataManager;
    private List<Ticket> ticketList;

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
        SharedPreferences sp = getApplicationContext().getSharedPreferences("userId", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID is not found", Toast.LENGTH_SHORT).show();
            finish(); // Hoặc chuyển hướng người dùng đến màn hình khác nếu không có userId
            return;
        }

        // Ánh xạ các View
        anhXa();

        // Khởi tạo DataManager
        dataManager = DataManager.getInstance(this);
        ticketList = new ArrayList<>(); // Khởi tạo danh sách vé

        // Khởi tạo TicketAdapter với danh sách vé và đối tượng DataManager
        ticketAdapter = new TicketAdapter(ticketList, dataManager);
        viewTicket.setLayoutManager(new LinearLayoutManager(this)); // Thiết lập LayoutManager
        viewTicket.setAdapter(ticketAdapter); // Thiết lập adapter cho RecyclerView

        // Lọc vé ban đầu
        fetchTickets(userId);

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

    private void fetchTickets(String userId) {
        Log.d("MyTicketActivity", "Fetching tickets for userId: " + userId);
        dataManager.fetchTickets(userId, new DataManager.TicketCallBack() {
            @Override
            public void onSuccess(List<Ticket> tickets) {
                Log.d("MyTicketActivity", "Tickets fetched successfully: " + tickets.size());
                ticketList.clear();
                ticketList.addAll(tickets);
                ticketAdapter.notifyDataSetChanged(); // Cập nhật adapter để hiển thị vé

                if (tickets.isEmpty()) {
                    Toast.makeText(MyTicketActivity.this, "No tickets found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
               // Toast.makeText(MyTicketActivity.this, "Error fetching tickets: " + errorMessage, Toast.LENGTH_SHORT).show();
               // Log.e("MyTicketActivity", "Error fetching tickets: " + errorMessage);
            }
        });
    }

    private void anhXa() {
        viewTicket = findViewById(R.id.viewTicket);
    }
}
