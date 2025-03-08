package com.example.ticketnew.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketnew.R;
import com.example.ticketnew.model.Events;
import com.example.ticketnew.model.Ticket;
import com.example.ticketnew.network.DataManager;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private List<Ticket> ticketList;
    private DataManager dataManager;

    public TicketAdapter(List<Ticket> ticketList, DataManager dataManager) {
        this.ticketList = ticketList;
        this.dataManager = dataManager; // Nhận DataManager từ bên ngoài
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ticket_info, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        int eventId = ticket.getEventId();
        int ticketTypeId = ticket.getTicketTypeId();

        // Gọi fetchEventDatabyId để lấy thông tin sự kiện
        dataManager.fetchEventDatabyId(eventId, new DataManager.EventCallback() {
            @Override
            public void onSuccess(Events event) {
                holder.tvEventId.setText(event.getName());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(holder.itemView.getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        // Lấy type_name từ bảng ticket_types dựa vào ticketTypeId
        dataManager.fetchTicketTypeById(ticketTypeId, new DataManager.TicketTypeCallback() {
            @Override
            public void onSuccess(String typeName) {
                holder.tvTicketTypeId.setText("Ticket Type: " + typeName);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(holder.itemView.getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        holder.tvQuantity.setText("Quantity: " + ticket.getQuantity());
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventId, tvTicketTypeId, tvQuantity, tvPrice;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventId = itemView.findViewById(R.id.tvNameEvent);
            tvTicketTypeId = itemView.findViewById(R.id.tvLoaiVe);
            tvQuantity = itemView.findViewById(R.id.tvSoLuong);
        }
    }
}
