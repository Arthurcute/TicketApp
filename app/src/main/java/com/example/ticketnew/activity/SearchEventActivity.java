package com.example.ticketnew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketnew.R;
import com.example.ticketnew.adapter.EventOtherAdapter;
import com.example.ticketnew.model.Events;
import com.example.ticketnew.network.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchEventActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView listEventSearch;
    private EventOtherAdapter adapter;
    private DataManager dataManager;
    private List<Events> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();
        setupRecyclerView();
        dataManager = DataManager.getInstance(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    searchEvents(query);
                } else {
                    Toast.makeText(SearchEventActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: Search while typing
                return false;
            }
        });

        BottomNavigationView bottomMenu = findViewById(R.id.bottom_save);
        bottomMenu.setSelectedItemId(R.id.menu_save);

        bottomMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_save) {
                return true;
            } else if (itemId == R.id.menu_ticket) {
                startActivity(new Intent(getApplicationContext(), MyTicketActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
    }

    private void setupRecyclerView() {
        searchList = new ArrayList<>();
        adapter = new EventOtherAdapter(this, searchList);
        listEventSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listEventSearch.setAdapter(adapter);
    }

    private void searchEvents(String query) {
        dataManager.searchEvents(query, new DataManager.DataCallback() {
            @Override
            public void onSuccess(List<Events> eventList) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                adapter = new EventOtherAdapter(SearchEventActivity.this, eventList);
                listEventSearch.setAdapter(adapter);
                adapter.setOnItemClickListener(event -> {
                    Intent intent = new Intent(SearchEventActivity.this, DetailEventActivity.class);
                    intent.putExtra("event_id", event.getEvent_id());
                    intent.putExtra("location_id", event.getLocation_id());
                    startActivity(intent);
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SearchEventActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return date != null ? sdf.format(date) : "";
    }

    private void anhXa() {
        searchView = findViewById(R.id.edtSearch);
        listEventSearch = findViewById(R.id.listEventSearch);
    }
}
