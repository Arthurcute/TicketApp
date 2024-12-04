package com.example.ticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket.R;
import com.example.ticket.model.TicketType;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TicketTypeAdapter extends RecyclerView.Adapter<TicketTypeAdapter.TicketTypeViewHolder> {

    private Context context;
    private List<TicketType> ticketTypeList;

    public TicketTypeAdapter(Context context, List<TicketType> ticketTypeList) {
        this.context = context;
        this.ticketTypeList = ticketTypeList;
    }

    @NonNull
    @Override
    public TicketTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual ticket item
        View view = LayoutInflater.from(context).inflate(R.layout.item_tickettype, parent, false);
        return new TicketTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketTypeViewHolder holder, int position) {
        // Check if the data is valid before binding
        if (ticketTypeList != null && !ticketTypeList.isEmpty()) {
            TicketType ticketType = ticketTypeList.get(position);

            // Set data to the TextViews
            holder.nameTicket.setText(ticketType.getTypeName()); // Đổi thành getTypeName() nếu không có getSectionName
            holder.numberTicket.setText(String.valueOf(ticketType.getAvailableQuantity())); // Sử dụng tổng số vé

            // Format price using NumberFormat
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            holder.totalTicket.setText(currencyFormat.format(ticketType.getPrice())); // Giá vé
        }
    }

    @Override
    public int getItemCount() {
        // Return 0 if the list is null, else return the size of the list
        return (ticketTypeList != null) ? ticketTypeList.size() : 0;
    }

    // ViewHolder class to hold references to the views for each item
    public static class TicketTypeViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTicket;
        final TextView numberTicket;
        final TextView totalTicket;

        public TicketTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTicket = itemView.findViewById(R.id.item_nameTicket);
            numberTicket = itemView.findViewById(R.id.item_number);
            totalTicket = itemView.findViewById(R.id.item_total);
        }
    }
}
