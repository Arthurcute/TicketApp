package com.example.ticket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticket.R;

public class NotionPayActivity extends AppCompatActivity {
    TextView noiDung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notion_pay);

        noiDung = findViewById(R.id.noiDungThongBao);

        Intent intent = getIntent();

        noiDung.setText( intent.getStringExtra("noiDung"));

    }
}