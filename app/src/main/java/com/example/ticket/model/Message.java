package com.example.ticket.model;

public class Message {
    private int messageId;
    private int conversationId;
    private String senderType;
    private String messageContent;
    private String timestamp;

    public Message(int messageId, int conversationId, String senderType, String messageContent, String timestamp) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
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
}
