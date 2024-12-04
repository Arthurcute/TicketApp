package com.example.ticket.activity;

import static android.webkit.URLUtil.isValidUrl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticket.R;
import com.example.ticket.adapter.TicketTypeAdapter;
import com.example.ticket.model.Events;
import com.example.ticket.model.Location;
import com.example.ticket.model.TicketType;
import com.example.ticket.network.DataManager;
import com.example.ticket.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailEventActivity extends AppCompatActivity {

    private TextView buyTicket, nameEvent, timeEvent, addressEvent, desEvent, timeTicket;
    private ImageView exitDetailEvent, comment, imgEvent, imgSeat, btnShare;
    private RecyclerView listTicketType;
    private int eventId, locationId;
    private TicketTypeAdapter ticketTypeAdapter;
    private List<TicketType> ticketTypeList = new ArrayList<>();
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Events event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        initializeViews();

        eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Event ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventId != -1) {
            Log.d("DetailEventActivity", "Received eventId: " + eventId);
            Toast.makeText(this, "Event id " + eventId, Toast.LENGTH_SHORT).show();
            DataManager.getInstance(this).fetchEventDatabyId(eventId, new DataManager.EventCallback() {
                @Override
                public void onSuccess(Events fetchedEvent) {
                    event = fetchedEvent;
                    String formattedStartTime = datetimeFormat.format(new Date(String.valueOf(event.getStart_time())));
                    String formattedEndTime = datetimeFormat.format(new Date(String.valueOf(event.getEnd_time())));
                    String formattedStartTicketTime = dateFormat.format(new Date(String.valueOf(event.getTicket_sale_start())));
                    String formattedEndTicketTime = dateFormat.format(new Date(String.valueOf(event.getTicket_sale_end())));

                    nameEvent.setText(event.getName());
                    desEvent.setText(event.getDescription());
                    timeEvent.setText(formattedStartTime + " - " + formattedEndTime);
                    addressEvent.setText("Địa điểm ID: " + event.getLocation_id());
                    timeTicket.setText(formattedStartTicketTime + " - " + formattedEndTicketTime);

                    if (event.getImage_url() != null && !event.getImage_url().isEmpty()) {
                        // Kiểm tra nếu URL hợp lệ
                        if (isValidUrl(event.getImage_url())) {
                            // Tải ảnh từ URL bằng Glide
                            Glide.with(DetailEventActivity.this)
                                    .load(event.getImage_url())
                                    .placeholder(R.drawable.img) // Ảnh mặc định khi tải
                                    .into(imgEvent);
                        } else {
                            // Nếu không phải URL, kiểm tra xem có phải mã hóa Base64 hay không
                            try {
                                Bitmap eventImage = base64ToBitmap(event.getImage_url());
                                imgEvent.setImageBitmap(eventImage);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                // Nếu không phải Base64, set ảnh mặc định
                                imgEvent.setImageResource(R.drawable.img); // Hình ảnh mặc định
                            }
                        }
                    } else {
                        imgEvent.setImageResource(R.drawable.img); // Hình ảnh mặc định nếu không có URL hoặc Base64
                    }

                    locationId = event.getLocation_id();
                    loadLocationData(locationId);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(DetailEventActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Không có event_id", Toast.LENGTH_SHORT).show();
        }

        ticketTypeAdapter = new TicketTypeAdapter(this, ticketTypeList);
        listTicketType.setLayoutManager(new LinearLayoutManager(this));
        listTicketType.setAdapter(ticketTypeAdapter);

        if (eventId != -1) {
            fetchTicketSelections();
        }

        buyTicket.setOnClickListener(view -> {
            if (event == null) {
                Toast.makeText(DetailEventActivity.this, "Sự kiện chưa được tải", Toast.LENGTH_SHORT).show();
                return;
            }

            Date currentTime = new Date();

            if (event.getTicket_sale_start().after(currentTime)) {
                Utils.showCustomDialog(DetailEventActivity.this, "Thông báo", "Sự kiện chưa mở bán vé!");
            } else if (event.getTicket_sale_end().before(currentTime)) {
                Utils.showCustomDialog(DetailEventActivity.this, "Thông báo", "Vé không còn mở bán!");
            } else {
                Intent buyTicketIntent = new Intent(DetailEventActivity.this, BuyTicketActivity.class);
                buyTicketIntent.putExtra("event_id", eventId);
                buyTicketIntent.putExtra("name", nameEvent.getText().toString());
                startActivity(buyTicketIntent);
            }
        });
        //btnShare.setOnClickListener(view -> shareEventDetails());

        comment.setOnClickListener(view -> {
            Intent commentIntent = new Intent(DetailEventActivity.this, CommentActivity.class);
            commentIntent.putExtra("event_id", eventId);
            commentIntent.putExtra("name", nameEvent.getText().toString());
            startActivity(commentIntent);
        });

        exitDetailEvent.setOnClickListener(view -> finish());
    }

    private void loadLocationData(int locationId) {
        if (locationId != -1) {
            DataManager.getInstance(this).fetchLocationsByEventId(locationId, new DataManager.LocationCallback() {
                @Override
                public void onSuccess(List<Location> locationList) {
                    if (!locationList.isEmpty()) {
                        Location location = locationList.get(0);

                        String locationDetails = location.getName() + "\n" + location.getAddress();
                        addressEvent.setText(locationDetails);

//                        if (location.getImgUrl() != null && !location.getImgUrl().isEmpty()) {
//                            Glide.with(DetailEventActivity.this)
//                                    .load(location.getImgUrl())
//                                    .placeholder(R.drawable.img_1)
//                                    .into(imgSeat);
//                        }

                        if (location.getImgUrl() != null && !location.getImgUrl().isEmpty()) {
                            // Kiểm tra nếu URL hợp lệ
                            if (isValidUrl(location.getImgUrl())) {
                                // Tải ảnh từ URL bằng Glide
                                Glide.with(DetailEventActivity.this)
                                    .load(location.getImgUrl())
                                    .placeholder(R.drawable.img_1)
                                    .into(imgSeat);
                            } else {
                                // Nếu không phải URL, kiểm tra xem có phải mã hóa Base64 hay không
                                try {
                                    Bitmap eventImage = base64ToBitmap(location.getImgUrl());
                                    imgSeat.setImageBitmap(eventImage);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                    // Nếu không phải Base64, set ảnh mặc định
                                    imgSeat.setImageResource(R.drawable.img_1); // Hình ảnh mặc định
                                }
                            }
                        } else {
                            imgSeat.setImageResource(R.drawable.img_1); // Hình ảnh mặc định nếu không có URL hoặc Base64
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Utils.showToast(DetailEventActivity.this, "Failed to load location details: " + errorMessage);
                }
            });
        }
    }

    private void initializeViews() {
        buyTicket = findViewById(R.id.buyTicket);
        exitDetailEvent = findViewById(R.id.exitDetailEvent);
        comment = findViewById(R.id.comment);
        nameEvent = findViewById(R.id.nameEvent);
        timeEvent = findViewById(R.id.timeEvent);
        addressEvent = findViewById(R.id.addressEvent);
        desEvent = findViewById(R.id.desEvent);
        imgEvent = findViewById(R.id.imgEvent);
        imgSeat = findViewById(R.id.imgSeat);
        listTicketType = findViewById(R.id.listTicketType);
        timeTicket = findViewById(R.id.timeTicket);
        btnShare = findViewById(R.id.btnShare);
    }

    private void fetchTicketSelections() {
        DataManager.getInstance(this).fetchTicketSelectionsByEventId(eventId, new DataManager.TicketSelectionCallback() {
            @Override
            public void onSuccess(List<TicketType> ticketSelectionList) {
                ticketTypeList.clear();
                ticketTypeList.addAll(ticketSelectionList);
                ticketTypeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Utils.showToast(DetailEventActivity.this, "Error loading ticket types: " + errorMessage);
            }
        });
    }

    private Bitmap base64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private void shareEventDetails() {
        if (event == null) {
            Toast.makeText(this, "Sự kiện chưa được tải", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo liên kết đến trang web với ID sự kiện
        String eventLink = "http://192.168.1.105/ticket/index.php?id=" + event.getEvent_id();

        // Tạo nội dung chia sẻ
        String shareBody = "Hãy xem sự kiện này: " + eventLink;

        // Tạo Intent chia sẻ
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ sự kiện");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        // Kiểm tra xem có ứng dụng nào có thể xử lý Intent chia sẻ không
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ thông tin sự kiện qua"));
        }
    }
}
