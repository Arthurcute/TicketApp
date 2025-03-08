package com.example.ticketnew.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketnew.R;
import com.example.ticketnew.model.Events;
import com.example.ticketnew.model.Order;
import com.example.ticketnew.network.DataManager;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<com.example.ticketnew.adapter.OrderAdapter.OrderViewHolder> {
        private List<Order> orderList;
        private DataManager dataManager;

        public OrderAdapter(List<Order> orderList, DataManager dataManager) {
            this.orderList = orderList;
            this.dataManager = dataManager; // Nhận DataManager từ bên ngoài
        }

        @NonNull
        @Override
        public com.example.ticketnew.adapter.OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_ticket, parent, false);
            return new com.example.ticketnew.adapter.OrderAdapter.OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull com.example.ticketnew.adapter.OrderAdapter.OrderViewHolder holder, int position) {

            Order order = orderList.get(position);
            int eventId = order.getEventId();
            int ticketTypeId = order.getTicketTypeId();
            Log.d("eventId:" ,""+ eventId);

            // Gọi fetchEventDatabyId để lấy thông tin sự kiện
            dataManager.fetchEventDatabyId(eventId, new DataManager.EventCallback() {
                @Override
                public void onSuccess(Events event) {
                    holder.item_tenSK.setText(event.getName());
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(holder.itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                    Log.e("Lỗi OrderAdapter ", "Không có lấy được tên sự kiện");
                }
            });
            // Lấy type_name từ bảng ticket_types dựa vào ticketTypeId
            dataManager.fetchTicketTypeById(ticketTypeId, new DataManager.TicketTypeCallback() {
                @Override
                public void onSuccess(String typeName) {
                    holder.item_tenLoaiVe.setText("Loại vé: " + typeName);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(holder.itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
            holder.item_soLuongVeMua.setText("Số lượng: " + order.getQuantity());
            holder.item_tongTienMua.setText("Tổng tiền: " + order.getTotalAmount());
            holder.item_ngayMua.setText("Ngày mua: " + order.getOrderDate());
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        public static class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView item_tenSK, item_ngayMua, item_soLuongVeMua, item_tongTienMua, item_tenLoaiVe;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                item_tenSK = itemView.findViewById(R.id.item_tenSuKienMy);
                item_ngayMua = itemView.findViewById(R.id.item_ngayMua);
                item_soLuongVeMua = itemView.findViewById(R.id.item_soLuongVeMua);
                item_tongTienMua = itemView.findViewById(R.id.item_tongTienMua);
                item_tenLoaiVe = itemView.findViewById(R.id.item_tenLoaiVe);
            }
        }
    }

