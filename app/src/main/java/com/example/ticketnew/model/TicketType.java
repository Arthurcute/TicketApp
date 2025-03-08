package com.example.ticketnew.model;

public class TicketType {
    private int ticketTypeId; // ticket_type_id
    private int eventId;      // event_id
    private String typeName;  // type_name
    private double price;     // price
    private int totalQuantity; // total_quantity
    private int availableQuantity; // available_quantity

    public TicketType() {
    }

    public TicketType(int ticketTypeId, int eventId, String typeName, double price, int totalQuantity, int availableQuantity) {
        this.ticketTypeId = ticketTypeId;
        this.eventId = eventId;
        this.typeName = typeName;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(int ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
