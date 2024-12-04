package com.example.ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticket.R;
import com.example.ticket.activity.DetailEventActivity;
import com.example.ticket.model.Events;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final Context context;
    private final List<Events> eventsList;

    public EventAdapter(Context context, List<Events> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Events event = eventsList.get(position);

        // Kiểm tra nếu vị trí hợp lệ
        if (position >= 0 && position < eventsList.size()) {

            // Đặt tên sự kiện
            holder.title.setText(event.getName() != null ? event.getName() : "No Title");

            // Kiểm tra và xử lý ảnh
            if (event.getImage_url() != null && !event.getImage_url().isEmpty()) {
                // Kiểm tra xem chuỗi có phải là URL (bắt đầu bằng "http")
                if (event.getImage_url().startsWith("http")) {
                    // Nếu là URL, sử dụng Glide để tải ảnh từ URL
                    Glide.with(context)
                            .load(event.getImage_url())
                            .placeholder(R.drawable.img) // Ảnh chờ trong khi tải ảnh
                            .error(R.drawable.img) // Ảnh hiển thị nếu có lỗi khi tải ảnh
                            .into(holder.image);
                } else {
                    try {
                        // Nếu không phải URL, giả định đó là Base64 và giải mã
                        byte[] decodedString = Base64.decode(event.getImage_url(), Base64.DEFAULT);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedString);
                        Bitmap decodedBitmap = BitmapFactory.decodeStream(inputStream);

                        // Đặt ảnh đã giải mã vào ImageView
                        holder.image.setImageBitmap(decodedBitmap);
                    } catch (Exception e) {
                        // Nếu xảy ra lỗi khi giải mã Base64, hiển thị ảnh mặc định
                        holder.image.setImageResource(R.drawable.img);
                    }
                }
            } else {
                // Nếu không có ảnh hoặc chuỗi ảnh trống, sử dụng ảnh mặc định
                holder.image.setImageResource(R.drawable.img);
            }

            // Xử lý sự kiện click của item
            holder.itemView.setOnClickListener(v -> {
                // Khởi chạy DetailEventActivity và truyền dữ liệu sự kiện
                Intent intent = new Intent(context, DetailEventActivity.class);
                intent.putExtra("event_id", event.getEvent_id());
                context.startActivity(intent);
            });
        }

}

    @Override
    public int getItemCount() {
        return eventsList != null ? eventsList.size() : 0;
    }

    // ViewHolder class để đại diện cho từng item view
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_imgEvent);
            title = itemView.findViewById(R.id.item_nameEvent);
        }
    }
}
