package com.example.ticketnew.model;

import java.util.List;

public class EventModel {
    boolean success;
    String message;
    List<Events> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Events> getResult() {
        return result;
    }

    public void setResult(List<Events> result) {
        this.result = result;
    }
}
