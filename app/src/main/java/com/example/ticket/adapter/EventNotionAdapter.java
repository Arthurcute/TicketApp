package com.example.ticket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticket.R;
import com.example.ticket.model.EventNotion;
import java.util.ArrayList;

public class EventNotionAdapter extends RecyclerView.Adapter<EventNotionAdapter.EventNotionViewHolder> {

    private ArrayList<EventNotion> eventNotionList;
    private OnItemClickListener onItemClickListener;

    public EventNotionAdapter(ArrayList<EventNotion> eventNotionList, OnItemClickListener onItemClickListener) {
        this.eventNotionList = eventNotionList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public EventNotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notion, parent, false);
        return new EventNotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventNotionViewHolder holder, int position) {
        EventNotion eventNotion = eventNotionList.get(position);
        holder.itemNameEventNotion.setText(eventNotion.getName());
        holder.itemContentNotion.setText(eventNotion.getContent());

        // Áp dụng màu nền so le cho item
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.even_item_color));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.odd_item_color));
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(eventNotion)); // Truyền đối tượng EventNotion
    }

    @Override
    public int getItemCount() {
        return eventNotionList.size();
    }

    public static class EventNotionViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameEventNotion;
        TextView itemContentNotion;

        public EventNotionViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameEventNotion = itemView.findViewById(R.id.itemNameEventNotion);
            itemContentNotion = itemView.findViewById(R.id.itemContentNotion);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(EventNotion eventNotion); // Cập nhật để nhận đối tượng EventNotion
    }
}
