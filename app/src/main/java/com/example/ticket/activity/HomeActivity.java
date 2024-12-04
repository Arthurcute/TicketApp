package com.example.ticket.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.ticket.R;
import com.example.ticket.adapter.EventAdapter;
import com.example.ticket.adapter.EventNewAdapter;
import com.example.ticket.adapter.EventOtherAdapter;
import com.example.ticket.model.Events;
import com.example.ticket.network.DataManager;
import com.example.ticket.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import me.relex.circleindicator.CircleIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private ImageView imgSearch;
    private TextView xinChao;
    private ViewPager viewEventNew;
    private CircleIndicator circleEventNew;
    private EventNewAdapter eventNewAdapter;
    private List<Events> eventNewList;
    private RecyclerView eventMusic, eventConcert, eventOther;
    private EventAdapter musicAdapter, concertAdapter;
    private EventOtherAdapter otherAdapter;
    private List<Events> musicList, concertList, otherList;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        loadEventData();
        displayGreeting();

        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = preferences.getString("userId", null);

        // Kiểm tra user_id và tải thông tin người dùng
        if (userId != null) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(this, "User ID không tồn tại", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomMenu = findViewById(R.id.bottom_home);
        bottomMenu.setSelectedItemId(R.id.menu_home);
        bottomMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                return true;
            } else if (itemId == R.id.menu_ticket) {
                startActivity(new Intent(getApplicationContext(), MyTicketActivity.class));
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
            } else {
                return false;
            }
        });

        imgSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchEventActivity.class);
            startActivity(intent);
        });

        eventNewAdapter.setOnItemClickListener(event -> {
            Intent intent = new Intent(HomeActivity.this, DetailEventActivity.class);
            intent.putExtra("event_id", Integer.valueOf(event));
            startActivity(intent);
        });

        otherAdapter.setOnItemClickListener(event -> {
            Intent intent = new Intent(HomeActivity.this, DetailEventActivity.class);
            intent.putExtra("event_id", Integer.valueOf(event.getEvent_id()));
            startActivity(intent);
        });
    }

    private void initializeViews() {
        imgSearch = findViewById(R.id.imgSearch);
        viewEventNew = findViewById(R.id.viewEventNew);
        circleEventNew = findViewById(R.id.circleEventNew);

        eventNewList = new ArrayList<>();
        eventNewAdapter = new EventNewAdapter(HomeActivity.this, eventNewList);
        viewEventNew.setAdapter(eventNewAdapter);
        circleEventNew.setViewPager(viewEventNew);
        eventNewAdapter.registerDataSetObserver(circleEventNew.getDataSetObserver());

        eventConcert = findViewById(R.id.eventConcert);
        eventMusic = findViewById(R.id.eventMusic);
        eventOther = findViewById(R.id.evenOther);

        musicList = new ArrayList<>();
        concertList = new ArrayList<>();
        otherList = new ArrayList<>();

        musicAdapter = new EventAdapter(this, musicList);
        concertAdapter = new EventAdapter(this, concertList);
        otherAdapter = new EventOtherAdapter(this, otherList);

        eventMusic.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventMusic.setAdapter(musicAdapter);

        eventConcert.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventConcert.setAdapter(concertAdapter);

        eventOther.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        eventOther.setAdapter(otherAdapter);

        xinChao = findViewById(R.id.xinChaoHome);
    }

    private void loadEventData() {
        DataManager.getInstance(this).fetchEventData(new DataManager.DataCallback() {
            @Override
            public void onSuccess(List<Events> eventList) {
                eventNewList.clear();
                eventNewList.addAll(eventList);
                eventNewAdapter.notifyDataSetChanged();
                fetchAndCategorizeEvents(); // Fetch and categorize events after loading new events
            }

            @Override
            public void onError(String errorMessage) {
                Utils.showToast(HomeActivity.this, "Failed to load events: " + errorMessage);
            }
        });
    }

    private void fetchAndCategorizeEvents() {
        DataManager.getInstance(this).fetchCategorizedEvents(new DataManager.DataCallback() {
            @Override
            public void onSuccess(List<Events> eventList) {
                // Split the list into categories
                musicList.clear();
                concertList.clear();
                otherList.clear();

                for (Events event : eventList) {
                    switch (event.getEvent_type_id()) {
                        case 1: // Assuming 1 corresponds to "Âm nhạc"
                            musicList.add(event);
                            break;
                        case 2: // Assuming 2 corresponds to "Concert"
                            concertList.add(event);
                            break;
                        default:
                            otherList.add(event);
                            break;
                    }
                }

                musicAdapter.notifyDataSetChanged();
                concertAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Utils.showToast(HomeActivity.this, "Failed to load categorized events: " + errorMessage);
            }
        });
    }

    private void loadUserProfile(String userId) {
        int userIdInt = Integer.parseInt(userId);

        DataManager.getInstance(this).fetchUserProfile(userIdInt, new DataManager.UserProfileCallback() {
            @Override
            public void onSuccess(String name, String email, String phone, String birth, String gender) {
                Log.d("HomeActivity", "User Profile loaded: " + name + ", " + email);

                SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userName", name);
                editor.apply();
            }
            @Override
            public void onError(String errorMessage) {
                // Xử lý lỗi
                Log.e("HomeActivity", "Error fetching user profile: " + errorMessage);
                Toast.makeText(HomeActivity.this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return date != null ? sdf.format(date) : "";
    }
    private void displayGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);  // Lấy giờ hiện tại (24-hour format)

        if (hour >= 0 && hour < 12) {
            xinChao.setText("Chào buổi sáng");
        } else if (hour >= 12 && hour < 15) {
            xinChao.setText("Chào buổi trưa");
        } else if (hour >= 15 && hour < 18) {
            xinChao.setText("Chào buổi chiều");
        } else {
            xinChao.setText("Chào buổi tối");
        }
    }
}
