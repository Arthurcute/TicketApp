package com.example.ticketnew.model;

import java.util.Date;

public class Events {
    private int event_id;
    private String name;
    private int artist_id; // Thêm artist_id
    private String image_url;
    private String description;
    private String status;
    private Date start_time;
    private Date end_time;
    private int location_id;
    private int event_type_id;
    private Date ticket_sale_start;
    private Date ticket_sale_end;

    // Constructor đầy đủ các trường
    public Events(int event_id, String name, int artist_id, Date start_time, Date end_time, int location_id, int event_type_id, String description, String image_url, String status, Date ticket_sale_start, Date ticket_sale_end) {
        this.event_id = event_id;
        this.name = name;
        this.artist_id = artist_id; // Khởi tạo artist_id
        this.start_time = start_time;
        this.end_time = end_time;
        this.location_id = location_id;
        this.event_type_id = event_type_id;
        this.description = description;
        this.image_url = image_url;
        this.status = status;
        this.ticket_sale_start = ticket_sale_start;
        this.ticket_sale_end = ticket_sale_end;
    }

    public Events(int eventId, String name, String imageUrl, String description, String status, Date startTime, Date endTime, int locationId, int eventTypeId, Date ticketSaleStart, Date ticketSaleEnd) {
    }

    // Getters và Setters cho tất cả các trường
    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public int getEvent_type_id() {
        return event_type_id;
    }

    public void setEvent_type_id(int event_type_id) {
        this.event_type_id = event_type_id;
    }

    public Date getTicket_sale_start() {
        return ticket_sale_start;
    }

    public void setTicket_sale_start(Date ticket_sale_start) {
        this.ticket_sale_start = ticket_sale_start;
    }

    public Date getTicket_sale_end() {
        return ticket_sale_end;
    }

    public void setTicket_sale_end(Date ticket_sale_end) {
        this.ticket_sale_end = ticket_sale_end;
    }
}
