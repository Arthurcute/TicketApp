package com.example.ticketnew.activity;

import static com.example.ticketnew.R.layout.activity_notion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketnew.R;
import com.example.ticketnew.adapter.EventNotionAdapter;
import com.example.ticketnew.model.EventNotion;
import com.example.ticketnew.network.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class NotionActivity extends AppCompatActivity {
    private RecyclerView listNotion;
    private EventNotionAdapter adapter;
    private ArrayList<EventNotion> eventNotionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(activity_notion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo RecyclerView
        listNotion = findViewById(R.id.listNotion);
        listNotion.setLayoutManager(new LinearLayoutManager(this));
        eventNotionList = new ArrayList<>();
        adapter = new EventNotionAdapter(eventNotionList, eventNotion -> {
            Intent intent = new Intent(NotionActivity.this, DetailEventActivity.class);
            // Truyền thông tin cần thiết vào Intent
            // Truyền thông tin cần thiết vào Intent
            intent.putExtra("event_id", eventNotion.getEventId());
            intent.putExtra("event_name", eventNotion.getName());
            intent.putExtra("image_url", eventNotion.getImageUrl());
            intent.putExtra("description", eventNotion.getDescription());
            intent.putExtra("start_time", eventNotion.getStartTime().toString());
            intent.putExtra("end_time", eventNotion.getEndTime().toString());
            intent.putExtra("location_id", eventNotion.getLocationId());
            startActivity(intent);
        });
        listNotion.setAdapter(adapter);

        // Lấy dữ liệu từ SharedPreferences
        SharedPreferences sp = getApplicationContext().getSharedPreferences("userId", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");


        BottomNavigationView bottom_menu = findViewById(R.id.bottom_notion);
        bottom_menu.setSelectedItemId(R.id.menu_notion);

        bottom_menu.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_notion) {
                    // No action needed for the home menu as it's already selected
                    return true;
                } else if (itemId == R.id.menu_ticket) {
                    // Start FavouriteActivity and finish the current activity
                    startActivity(new Intent(getApplicationContext(), MyTicketActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.menu_home) {
                    // Start PreparingActivity and finish the current activity
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.menu_account) {
                    // Start ProfileActivity and finish the current activity
                    startActivity(new Intent(getApplicationContext(), MyprofileActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.menu_save) {
                    // Start ProfileActivity and finish the current activity
                    startActivity(new Intent(getApplicationContext(), SearchEventActivity.class));
                    finish();
                    return true;
                } else {
                    // Return false if the selected menu item doesn't match any case
                    return false;
                }
            }
        });

        // Lấy dữ liệu thông báo sự kiện
        fetchEventNotions();
    }

    private void fetchEventNotions() {
        DataManager.fetchEventNotions(this, new DataManager.NotionCallback() {
            @Override
            public void onSuccess(ArrayList<EventNotion> eventNotions) {
                eventNotionList.addAll(eventNotions);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(NotionActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
