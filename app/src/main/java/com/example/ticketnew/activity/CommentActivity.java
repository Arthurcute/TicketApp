package com.example.ticketnew.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketnew.R;
import com.example.ticketnew.adapter.MessageAdapter;
import com.example.ticketnew.model.Conversation;
import com.example.ticketnew.model.Message;
import com.example.ticketnew.network.DataManager;
import com.example.ticketnew.security.AESUtil;
import com.example.ticketnew.security.KeystoreUtil;
import com.example.ticketnew.security.RSAUtil;
import com.google.crypto.tink.subtle.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private String userId, userName;
    private int eventId;
    private String nameSK;
    private String publicKeyAdmin;

    private ImageView closeCmt;
    private TextView eventNameMes;
    private RecyclerView listMes;
    private EditText edtMes;
    private Button btnSend;
    private boolean isConversation;

    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initializeViews();
//        retrieveUserInfo();
//        setupMessageAdapter();
//        handleEvents();
//        checkConversationForEvent();
    }

    private void initializeViews() {
        closeCmt = findViewById(R.id.closeMes);
        eventNameMes = findViewById(R.id.eventNameMes);
        listMes = findViewById(R.id.listMessages);
        edtMes = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
    }

    private void retrieveUserInfo() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sp.getString("userId", "");
        if (userId != null) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(this, "User ID không tồn tại", Toast.LENGTH_SHORT).show();
        }
        eventId = getIntent().getIntExtra("event_id", -1);
        nameSK = getIntent().getStringExtra("name");

        Log.d("UserInfo", "UserId: " + userId + ", UserName: " + userName);
        eventNameMes.setText(nameSK);
    }

    private void setupMessageAdapter() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        listMes.setLayoutManager(new LinearLayoutManager(this));
        listMes.setAdapter(messageAdapter);
    }

    private void handleEvents() {
        btnSend.setOnClickListener(view -> sendMessage());
        closeCmt.setOnClickListener(view -> finish());
    }

    private void sendMessage() {
        String messageContent = edtMes.getText().toString().trim();
        if (messageContent.isEmpty()) {
            showToast("Please enter a message");
            return;
        }
        DataManager.getInstance(this).checkConversation(eventId, userId, new DataManager.ConversationCallback() {
            @Override
            public void onConversationCheck(Conversation conversation) {
                if (conversation.isExists()) {
                    if ("pending".equals(conversation.getStatus())) {
                        Toast.makeText(CommentActivity.this, "Yêu cầu nhắn tin với admin chưa được xác nhận!", Toast.LENGTH_SHORT).show();
                    } else {
                        publicKeyAdmin = conversation.getPublicKeyAdmin();
                        addMessageToConversation(conversation.getConversationId(), messageContent);
                    }
                } else {
                    showNewConversationDialog(messageContent);
                }
            }

            @Override
            public void onError(String error) {
                showToast("Error: " + error);
            }
        });
    }

    private void showNewConversationDialog(final String messageContent) {
        new AlertDialog.Builder(CommentActivity.this)
                .setMessage("Bạn muốn gửi yêu cầu nhắn tin tới admin?")
                .setPositiveButton("Có", (dialog, which) -> {
                    try {
                        createNewConversation(messageContent);
                    } catch (Exception e) {
//                        showToast("Lỗi khi tạo cuộc trò chuyện: " + e.getMessage());
                    }
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void createNewConversation(String messageContent) throws Exception {
        KeystoreUtil.generateRSAKeyPairIfNeeded();
        PublicKey rsaPublicKey = KeystoreUtil.getPublicKey();
        Log.d("thông tin tạo conversation: ",""+rsaPublicKey);
        DataManager.getInstance(this).createConversation(eventId, userId, userName, rsaPublicKey, new DataManager.ConversationCallback() {
            @Override
            public void onConversationCheck(Conversation conversation) {
                if (conversation != null) {
                    isConversation = true;
                    addMessageToConversation(conversation.getConversationId(), messageContent);
                }
            }

            @Override
            public void onError(String error) {
                showToast("Error: " + error);
            }
        });
    }

    private void addMessageToConversation(int conversationId, String messageContent) {
        try {
            String aesKey = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                aesKey = AESUtil.generateAESKey();
            }
            String encryptedMessage = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                encryptedMessage = AESUtil.encrypt(messageContent, aesKey);
            }
            KeystoreUtil.generateRSAKeyPairIfNeeded();
            PublicKey rsaPublicKeyUser = KeystoreUtil.getPublicKey();
            String encryptedAesKey = RSAUtil.encryptWithRSA(aesKey, rsaPublicKeyUser);
            Log.d("RSA Admin: ", ""+ publicKeyAdmin);

            PublicKey rsaPublicKeyAdmin = decodePublicKeyFromBase64(publicKeyAdmin);
            String encryptedAesKeyAdmin = RSAUtil.encryptWithRSA(aesKey, rsaPublicKeyAdmin);
            Log.d("AES Admin mã hóa: ", ""+ encryptedAesKeyAdmin);
            DataManager.getInstance(this).addMessage(conversationId, userId, encryptedMessage, encryptedAesKey, encryptedAesKeyAdmin, new DataManager.MessageCallback() {
                @Override
                public void onMessageAdded() {
                    edtMes.setText("");
                    showToast("Message sent");
                    fetchMessages(conversationId);
                }

                @Override
                public void onError(String error) {
                    showToast("Error sending message: " + error);
                }
            });
        } catch (Exception e) {
            //showToast("Error encrypting message: " + e.getMessage());
        }
    }

    private void checkConversationForEvent() {
        DataManager.getInstance(this).checkConversation(eventId, userId, new DataManager.ConversationCallback() {
            @Override
            public void onConversationCheck(Conversation conversation) {
                if (conversation.isExists()) {
                    isConversation = true;
                    fetchMessages(conversation.getConversationId());
                } else {
                    isConversation = false;
                }
            }

            @Override
            public void onError(String error) {
//                showToast("Error: " + error);
            }
        });
    }

    private void fetchMessages(int conversationId) {
        DataManager.getInstance(this).getMessages(conversationId, new DataManager.MessageListCallback() {
            @Override
            public void onMessageListFetched(List<Message> messages) {
                messageList.clear();
                for (Message message : messages) {
                    try {
                        KeystoreUtil.generateRSAKeyPairIfNeeded();
                        PrivateKey rsaPrivateKey = KeystoreUtil.getPrivateKey();
                        String decryptedAesKey = RSAUtil.decryptWithRSA(message.getEncrypted_aes_key(), rsaPrivateKey);
                        String decryptedMessage = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            decryptedMessage = AESUtil.decrypt(message.getMessageContent(), decryptedAesKey);
                        }
                        message.setMessageContent(decryptedMessage);
                    } catch (Exception e) {
                        message.setMessageContent("Lỗi giải mã tin nhắn");
                    }
                }
                messageList.addAll(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
//                showToast("Error fetching messages: " + error);
            }
        });
    }

    private void loadUserProfile(String userId) {
        int userIdInt = Integer.parseInt(userId);
        DataManager.getInstance(this).fetchUserProfile(userIdInt, new DataManager.UserProfileCallback() {
            @Override
            public void onSuccess(String name, String email, String phone, String birth, String gender) {
                userName = name;
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Chat Activity", "Error fetching user profile: " + errorMessage);
//                showToast("Lỗi tải thông tin người dùng");
            }
        });
    }
    private PublicKey decodePublicKeyFromBase64(String publicKeyBase64) {
        try {
            // Giải mã chuỗi Base64 thành mảng byte
            byte[] publicKeyBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT);

            // Tạo đối tượng KeyFactory với thuật toán RSA
            KeyFactory keyFactoryAd = KeyFactory.getInstance("RSA");

            // Tạo đối tượng PublicKey từ mảng byte
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactoryAd.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
