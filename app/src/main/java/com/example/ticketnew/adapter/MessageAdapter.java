//package com.example.ticketnew.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.ticketnew.R;
//import com.example.ticketnew.model.Message;
//
//import java.util.List;
//
//public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
//
//    private List<Message> messageList;
//
//    public MessageAdapter(List<Message> messageList) {
//        this.messageList = messageList;
//    }
//
//    @NonNull
//    @Override
//    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
//        return new MessageViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
//        Message message = messageList.get(position);
//        holder.messageContent.setText(message.getMessageContent());
//        holder.timestamp.setText(message.getTimestamp());
//
//        // Thiết lập background dựa vào sender_type
//        if (message.getSenderType().equals("admin")) {
//            holder.itemView.setBackgroundResource(R.drawable.bg_item_admin);
//
//            // Thiết lập margin cho admin
//            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
//            layoutParams.setMargins(10, layoutParams.topMargin, 300, layoutParams.bottomMargin); // marginLeft: 10dp, marginRight: 50dp
//            holder.itemView.setLayoutParams(layoutParams);
//        } else {
//            holder.itemView.setBackgroundResource(R.drawable.bg_item_user);
//
//            // Thiết lập margin cho user
//            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
//            layoutParams.setMargins(300, layoutParams.topMargin, 10, layoutParams.bottomMargin); // marginLeft: 50dp, marginRight: 10dp
//            holder.itemView.setLayoutParams(layoutParams);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return messageList.size();
//    }
//
//    static class MessageViewHolder extends RecyclerView.ViewHolder {
//        TextView messageContent;
//        TextView timestamp;
//
//        public MessageViewHolder(@NonNull View itemView) {
//            super(itemView);
//            messageContent = itemView.findViewById(R.id.messageContent);
//            timestamp = itemView.findViewById(R.id.timestamp);
//        }
//    }
//}
// MessageAdapter.java
package com.example.ticketnew.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketnew.R;
import com.example.ticketnew.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageContent.setText(message.getMessageContent());
        holder.timestamp.setText(message.getTimestamp());

        // Thiết lập background dựa vào sender_type
        if (message.getSenderType().equals("admin")) {
            holder.itemView.setBackgroundResource(R.drawable.bg_item_admin);

            // Thiết lập margin cho admin
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMargins(10, layoutParams.topMargin, 300, layoutParams.bottomMargin); // marginLeft: 10dp, marginRight: 50dp
            holder.itemView.setLayoutParams(layoutParams);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_item_user);

            // Thiết lập margin cho user
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMargins(300, layoutParams.topMargin, 10, layoutParams.bottomMargin); // marginLeft: 50dp, marginRight: 10dp
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        TextView timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.messageContent);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}