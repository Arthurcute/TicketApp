package com.example.ticketnew.model;

import java.util.Date;

public class EventNotion {

    private String content;
    private int eventId;
    private String name;
    private String imageUrl;
    private String description;
    private String status;
    private Date startTime;
    private Date endTime;
    private int locationId;
    private int eventTypeId;
    private Date ticketSaleStart;
    private Date ticketSaleEnd;

    public EventNotion() {
    }

    public EventNotion(String content, int eventId, String name, String imageUrl, String description, String status,
                       Date startTime, Date endTime, int locationId, int eventTypeId,
                       Date ticketSaleStart, Date ticketSaleEnd) {
        this.content = content;
        this.eventId = eventId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationId = locationId;
        this.eventTypeId = eventTypeId;
        this.ticketSaleStart = ticketSaleStart;
        this.ticketSaleEnd = ticketSaleEnd;
    }

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }

    public int getEventTypeId() { return eventTypeId; }
    public void setEventTypeId(int eventTypeId) { this.eventTypeId = eventTypeId; }

    public Date getTicketSaleStart() { return ticketSaleStart; }
    public void setTicketSaleStart(Date ticketSaleStart) { this.ticketSaleStart = ticketSaleStart; }

    public Date getTicketSaleEnd() { return ticketSaleEnd; }
    public void setTicketSaleEnd(Date ticketSaleEnd) { this.ticketSaleEnd = ticketSaleEnd; }
}
