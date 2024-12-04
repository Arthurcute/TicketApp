package com.example.ticket.model;

public class PendingOrder {

    private int pendingOrderId;
    private int userId;
    private int eventId;
    private int number;
    private int ticketTypeId;
    private double price;
    private double total;

    // Constructor
    public PendingOrder(int pendingOrderId, int userId, int eventId, int number, int ticketTypeId, double price) {
        this.pendingOrderId = pendingOrderId;
        this.userId = userId;
        this.eventId = eventId;
        this.number = number;
        this.ticketTypeId = ticketTypeId;
        this.price = price;
        updateTotal(); // Cập nhật total ngay khi khởi tạo
    }

    // Getter và Setter
    public int getPendingOrderId() {
        return pendingOrderId;
    }

    public void setPendingOrderId(int pendingOrderId) {
        this.pendingOrderId = pendingOrderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        updateTotal(); // Cập nhật total khi number thay đổi
    }

    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(int ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        updateTotal(); // Cập nhật total khi price thay đổi
    }

    public double getTotal() {
        return total;
    }

    private void updateTotal() {
        this.total = this.number * this.price; // Tính toán total
    }
}
