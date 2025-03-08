//package com.example.ticketnew.model;
//
//public class Message {
//    private int messageId;
//    private int conversationId;
//    private String senderType;
//    private String messageContent;
//    private String timestamp;
//
//    public Message(int messageId, int conversationId, String senderType, String messageContent, String timestamp) {
//        this.messageId = messageId;
//        this.conversationId = conversationId;
//        this.senderType = senderType;
//        this.messageContent = messageContent;
//        this.timestamp = timestamp;
//    }
//
//    public int getMessageId() {
//        return messageId;
//    }
//
//    public int getConversationId() {
//        return conversationId;
//    }
//
//    public String getSenderType() {
//        return senderType;
//    }
//
//    public String getMessageContent() {
//        return messageContent;
//    }
//
//    public String getTimestamp() {
//        return timestamp;
//    }
//}
// Message.java
package com.example.ticketnew.model;

public class Message {
    private int messageId;
    private int conversationId;
    private String senderType;
    private String messageContent;
    private String encrypted_aes_key;
    private String encrypted_aes_key_admin;
    private String timestamp;

    public Message(int messageId, int conversationId, String senderType, String messageContent, String timestamp) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    public Message(int messageId, int conversationId, String senderType, String messageContent, String encrypted_aes_key, String timestamp) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.messageContent = messageContent;
        this.encrypted_aes_key = encrypted_aes_key;
        this.timestamp = timestamp;
    }

    public Message(int messageId, int conversationId, String senderType, String messageContent, String encrypted_aes_key, String encrypted_aes_key_admin, String timestamp) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.messageContent = messageContent;
        this.encrypted_aes_key = encrypted_aes_key;
        this.encrypted_aes_key_admin = encrypted_aes_key_admin;
        this.timestamp = timestamp;
    }

    public String getEncrypted_aes_key_admin() {
        return encrypted_aes_key_admin;
    }

    public void setEncrypted_aes_key_admin(String encrypted_aes_key_admin) {
        this.encrypted_aes_key_admin = encrypted_aes_key_admin;
    }

    public String getEncrypted_aes_key() {
        return encrypted_aes_key;
    }

    public void setEncrypted_aes_key(String encrypted_aes_key) {
        this.encrypted_aes_key = encrypted_aes_key;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public String getSenderType() {
        return senderType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}