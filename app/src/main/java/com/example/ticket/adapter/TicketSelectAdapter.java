package com.example.ticket.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket.R;
import com.example.ticket.model.TicketType;

import java.util.HashMap;
import java.util.List;

public class TicketSelectAdapter extends RecyclerView.Adapter<TicketSelectAdapter.TicketViewHolder> {

    private Context context;
    private List<TicketType> ticketList;
    private HashMap<Integer, Integer> ticketQuantityMap = new HashMap<>(); // Map để lưu số lượng vé được chọn

    public TicketSelectAdapter(Context context, List<TicketType> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selectticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketType ticket = ticketList.get(position);
        holder.ticketName.setText(ticket.getTypeName());
        holder.price.setText(String.valueOf(ticket.getPrice()));

        // Lấy số lượng đã nhập từ map nếu có
        holder.numberSection.setText(String.valueOf(ticketQuantityMap.getOrDefault(ticket.getTicketTypeId(), 0)));

        // Lắng nghe thay đổi trong EditText
        holder.numberSection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int quantity = Integer.parseInt(s.toString());
                    ticketQuantityMap.put(ticket.getTicketTypeId(), quantity);  // Cập nhật số lượng vào map
                } catch (NumberFormatException e) {
                    ticketQuantityMap.put(ticket.getTicketTypeId(), 0); // Nếu không nhập gì hoặc nhập sai định dạng
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Callback để gửi số lượng đã chọn lên Fragment
                if (onTicketQuantityChangeListener != null) {
                    onTicketQuantityChangeListener.onQuantityChange();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public HashMap<Integer, Integer> getTicketQuantityMap() {
        return ticketQuantityMap; // Trả về số lượng vé đã chọn
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView ticketName, price;
        EditText numberSection;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketName = itemView.findViewById(R.id.sectionName);
            price = itemView.findViewById(R.id.price);
            numberSection = itemView.findViewById(R.id.numberSelect);
        }
    }

    // Listener để lắng nghe thay đổi số lượng vé
    public interface OnTicketQuantityChangeListener {
        void onQuantityChange();
    }

    private OnTicketQuantityChangeListener onTicketQuantityChangeListener;

    public void setOnTicketQuantityChangeListener(OnTicketQuantityChangeListener listener) {
        this.onTicketQuantityChangeListener = listener;
    }
}
