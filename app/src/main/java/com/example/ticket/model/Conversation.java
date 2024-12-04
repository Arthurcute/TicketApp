package com.example.ticket.model;

public class Conversation {
    private int conversationId;
    private int eventId;
    private String userId;
    private String adminId;
    private String status;
    private boolean exists;

    public Conversation(int conversationId, int eventId, String userId, boolean exists) {
        this.conversationId = conversationId;
        this.eventId = eventId;
        this.userId = userId;
        this.exists = exists;
    }

    // Getters và Setters
    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isExists() {
        return exists;
    }
}
