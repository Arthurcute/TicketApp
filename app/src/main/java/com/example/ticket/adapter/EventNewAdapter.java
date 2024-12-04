package com.example.ticket.adapter;

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
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.ticket.R;
import com.example.ticket.model.Events;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventNewAdapter extends PagerAdapter {
    private final Context context;
    private final List<Events> listEventNew;
    private OnItemClickListener onItemClickListener;

    public EventNewAdapter(Context context, List<Events> listEventNew) {
        this.context = context;
        this.listEventNew = listEventNew;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_new, container, false);

        ImageView imgEventNew = view.findViewById(R.id.imgEventNew);
        TextView titleEventNew = view.findViewById(R.id.titleEventNew);
        TextView timeEventNew = view.findViewById(R.id.timeEventNew);

        Events event = listEventNew.get(position);

        // Định dạng ngày giờ cho sự kiện
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Kiểm tra và thiết lập tên sự kiện
        titleEventNew.setText(event.getName() != null ? event.getName() : "No Title");

        // Kiểm tra thời gian và định dạng
        String formattedTime = "";
        if (event.getStart_time() != null && event.getEnd_time() != null) {
            formattedTime = dateFormat.format(event.getStart_time()) + " - " + dateFormat.format(event.getEnd_time());
        }
        timeEventNew.setText(formattedTime.isEmpty() ? "Time not available" : formattedTime);


        // Kiểm tra và xử lý ảnh theo 3 trường hợp: URL, Base64, hoặc ảnh mặc định
        if (event.getImage_url() != null && !event.getImage_url().isEmpty()) {
            // Trường hợp ảnh là URL
            if (event.getImage_url().startsWith("http")) {
                // Sử dụng Glide để tải ảnh từ URL
                Glide.with(context)
                        .load(event.getImage_url())
                        .placeholder(R.drawable.img) // Ảnh chờ trong khi tải ảnh
                        .error(R.drawable.img) // Ảnh hiển thị nếu có lỗi khi tải ảnh
                        .into(imgEventNew);
            } else {
                try {
                    // Trường hợp ảnh là Base64
                    byte[] decodedString = Base64.decode(event.getImage_url(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgEventNew.setImageBitmap(decodedByte);
                } catch (IllegalArgumentException e) {
                    // Trường hợp không giải mã được Base64, hiển thị ảnh mặc định
                    imgEventNew.setImageResource(R.drawable.img);
                }
            }
        } else {
            // Trường hợp không có ảnh, hiển thị ảnh mặc định
            imgEventNew.setImageResource(R.drawable.img);
        }

        // Thiết lập sự kiện click
        view.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(event.getEvent_id()); // Truyền event_id
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return listEventNew != null ? listEventNew.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    // Định nghĩa interface để lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(int eventId); // Chỉ nhận eventId
    }
}
