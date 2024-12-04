package com.example.ticket.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket.R;
import com.example.ticket.adapter.MessageAdapter;
import com.example.ticket.model.Conversation;
import com.example.ticket.model.Message;
import com.example.ticket.network.DataManager;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private String userId,userName;
    private int eventId;

    private ImageView closeCmt;
    private TextView eventNameMes;
    private RecyclerView listMes;
    private EditText edtMes;
    private Button btnSend;
    private String nameSK;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initializeViews();

        // Lấy userId từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = preferences.getString("userId", "");
        userName = preferences.getString("userName", "");
        Log.d("UserName", userName);
        Log.d("Userid", userId);
        eventId = getIntent().getIntExtra("event_id", -1);
        nameSK = getIntent().getStringExtra("name");

        eventNameMes.setText(nameSK);

        // Khởi tạo danh sách tin nhắn và adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        listMes.setLayoutManager(new LinearLayoutManager(this));
        listMes.setAdapter(messageAdapter);

        // Kiểm tra cuộc trò chuyện
        checkConversationForEvent();

        btnSend.setOnClickListener(v -> sendMessage());

        closeCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeViews() {
        closeCmt = findViewById(R.id.closeMes);
        eventNameMes = findViewById(R.id.eventNameMes);
        listMes = findViewById(R.id.listMessages);
        edtMes = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
    }

    private void sendMessage() {
        String messageContent = edtMes.getText().toString().trim(); // Lấy nội dung tin nhắn

        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return; // Nếu nội dung tin nhắn rỗng, hiển thị thông báo và thoát
        }

        // Kiểm tra xem có cuộc trò chuyện nào cho sự kiện này không
        DataManager.getInstance(this).checkConversation(eventId, userId, new DataManager.ConversationCallback() {
            @Override
            public void onConversationCheck(Conversation conversation) {
                if (conversation.isExists()) {
                    // Nếu cuộc trò chuyện đã tồn tại, cập nhật trạng thái và thêm tin nhắn
                    updateConversationStatus(conversation.getConversationId(), messageContent);
                } else {
                    // Nếu không, tạo cuộc trò chuyện mới và thêm tin nhắn
                    createNewConversation(messageContent);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommentActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewConversation(String messageContent) {
        Log.d("Message create Conversation", "EventId" + eventId + "UserId" + userId + "UserName" + userName);
        DataManager.getInstance(this).createConversation(eventId, userId, userName, new DataManager.ConversationCallback() {
            @Override
            public void onConversationCheck(Conversation conversation) {
                if (conversation != null) {
                    // Thêm tin nhắn vào cuộc trò chuyện
                    int conversationId = conversation.getConversationId();
                    Log.d("Comment Activity: ", String.valueOf(conversationId));
                    addMessageToConversation(conversationId, messageContent);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommentActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateConversationStatus(int conversationId, String messageContent) {
        DataManager.getInstance(this).updateConversationStatus(conversationId, "pending", new DataManager.ConversationCallback() {
            @Override
            public void onConversationCheck(Conversation conversation) {
                // Thêm tin nhắn vào cuộc trò chuyện
                addMessageToConversation(conversationId, messageContent);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommentActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addMessageToConversation(int conversationId, String messageContent) {
        DataManager.getInstance(this).addMessage(conversationId, userId, messageContent, new DataManager.MessageCallback() {
            @Override
            public void onMessageAdded() {
                // Xóa nội dung tin nhắn sau khi gửi
                edtMes.setText("");
                Toast.makeText(CommentActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                // Lấy danh sách tin nhắn để cập nhật RecyclerView
                fetchMessages(conversationId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommentActivity.this, "Error sending message: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkConversationForEvent() {
        DataManager.getInstance(this).checkConversation(eventId, userId, new DataManager.ConversationCallback() {
            @Override
            public void onConversationCheck(Conversation conversation) {
                if (conversation.isExists()) {
                    // Nếu có cuộc trò chuyện, lấy danh sách tin nhắn
                    fetchMessages(conversation.getConversationId());
                } else {
                    // Nếu không có cuộc trò chuyện
                    Toast.makeText(CommentActivity.this, "Conversation does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommentActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMessages(int conversationId) {
        DataManager.getInstance(this).getMessages(conversationId, new DataManager.MessageListCallback() {
            @Override
            public void onMessageListFetched(List<Message> messages) {
                // Xóa danh sách hiện tại và thêm tin nhắn mới
                messageList.clear();
                messageList.addAll(messages);
                messageAdapter.notifyDataSetChanged(); // Thông báo cho adapter để làm mới danh sách
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommentActivity.this, "Error fetching messages: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
