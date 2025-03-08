package com.example.ticketnew.model;

public class Conversation {
    private int conversationId;
    private int eventId;
    private String userId;
    private String adminId;
    private String status;
private String publicKeyUser;
private String publicKeyAdmin;
    private boolean exists;

    public Conversation(int conversationId, int eventId, String userId, boolean exists) {
        this.conversationId = conversationId;
        this.eventId = eventId;
        this.userId = userId;
        this.exists = exists;
    }

    public Conversation(int conversationId, int eventId, String userId, String status, String publicKeyAdmin, boolean exists) {
        this.conversationId = conversationId;
        this.eventId = eventId;
        this.userId = userId;
        this.status = status;
        this.publicKeyAdmin = publicKeyAdmin;
        this.exists = exists;
    }

    public Conversation(int conversationId, String userId, String status, String publicKeyAdmin, boolean exists) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.status = status;
        this.publicKeyAdmin = publicKeyAdmin;
        this.exists = exists;
    }

    public Conversation(int conversationId, int eventId, String userId, String status, String publicKeyAdmin) {
        this.conversationId = conversationId;
        this.eventId = eventId;
        this.userId = userId;
        this.status = status;
        this.publicKeyAdmin = publicKeyAdmin;
    }

    public Conversation(int conversationId, int eventId, String userId, String adminId, String status, String publicKeyAdmin, boolean exists) {
        this.conversationId = conversationId;
        this.eventId = eventId;
        this.userId = userId;
        this.adminId = adminId;
        this.status = status;
        this.publicKeyAdmin = publicKeyAdmin;
        this.exists = exists;
    }

    public Conversation(int conversationId, int eventId, String userId, String adminId, String status, String publicKeyUser, String publicKeyAdmin) {
        this.conversationId = conversationId;
        this.eventId = eventId;
        this.userId = userId;
        this.adminId = adminId;
        this.status = status;
        this.publicKeyUser = publicKeyUser;
        this.publicKeyAdmin = publicKeyAdmin;
    }

    public String getPublicKeyUser() {
        return publicKeyUser;
    }

    public void setPublicKeyUser(String publicKeyUser) {
        this.publicKeyUser = publicKeyUser;
    }

    public String getPublicKeyAdmin() {
        return publicKeyAdmin;
    }

    public void setPublicKeyAdmin(String publicKeyAdmin) {
        this.publicKeyAdmin = publicKeyAdmin;
    }

    public void setExists(boolean exists) {
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
