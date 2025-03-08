package com.example.ticketnew.adapter;

import android.content.Context;
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
import com.example.ticketnew.R;
import com.example.ticketnew.model.Events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventOtherAdapter extends RecyclerView.Adapter<EventOtherAdapter.ViewHolder> {
    private final Context context;
    private List<Events> listEvents; // Thay đổi từ final để có thể cập nhật
    private OnItemClickListener onItemClickListener;

    public EventOtherAdapter(Context context, List<Events> listEvents) {
        this.context = context;
        this.listEvents = listEvents;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void updateEvents(List<Events> newEvents) {
        this.listEvents = newEvents; // Cập nhật danh sách sự kiện
        notifyDataSetChanged(); // Thông báo rằng dữ liệu đã thay đổi
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_other_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Events event = listEvents.get(position);

        // Kiểm tra null cho event
        if (event == null) {
            holder.titleEventOther.setText("No Event");
            holder.timeEventOther.setText("No Time");
            holder.imgEventOther.setImageResource(R.drawable.img); // Hình ảnh mặc định
            return; // Kết thúc nếu event là null
        }

        // Định dạng ngày giờ cho sự kiện
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        holder.titleEventOther.setText(event.getName() != null ? event.getName() : "No Title");

        String formattedStartTime = event.getStart_time() != null ? dateFormat.format(event.getStart_time()) : "Unknown Start Time";
        String formattedEndTime = event.getEnd_time() != null ? dateFormat.format(event.getEnd_time()) : "Unknown End Time";
        holder.timeEventOther.setText(String.format("%s - %s", formattedStartTime, formattedEndTime));

        // Kiểm tra và xử lý ảnh theo 3 trường hợp: URL, Base64, hoặc ảnh mặc định
        if (event.getImage_url() != null && !event.getImage_url().isEmpty()) {
            // Trường hợp ảnh là URL
            if (event.getImage_url().startsWith("http")) {
                // Sử dụng Glide để tải ảnh từ URL
                Glide.with(context)
                        .load(event.getImage_url())
                        .placeholder(R.drawable.img) // Ảnh chờ trong khi tải ảnh
                        .error(R.drawable.img) // Ảnh hiển thị nếu có lỗi khi tải ảnh
                        .into(holder.imgEventOther);
            } else {
                try {
                    // Trường hợp ảnh là Base64
                    byte[] decodedString = Base64.decode(event.getImage_url(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.imgEventOther.setImageBitmap(decodedByte);
                } catch (IllegalArgumentException e) {
                    // Trường hợp không giải mã được Base64, hiển thị ảnh mặc định
                    holder.imgEventOther.setImageResource(R.drawable.img);
                }
            }
        } else {
            // Trường hợp không có ảnh, hiển thị ảnh mặc định
            holder.imgEventOther.setImageResource(R.drawable.img);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listEvents != null ? listEvents.size() : 0;
    }

    public interface OnItemClickListener {
        void onItemClick(Events event);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEventOther;
        TextView titleEventOther, timeEventOther;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEventOther = itemView.findViewById(R.id.imgEventOther);
            titleEventOther = itemView.findViewById(R.id.titleEventOther);
            timeEventOther = itemView.findViewById(R.id.timeEventOther);
        }
    }
}
