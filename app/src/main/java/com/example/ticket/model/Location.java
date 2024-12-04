package com.example.ticket.model;

public class Location {
    private int locationId;  // ID của địa điểm
    private String name;     // Tên địa điểm
    private String address;  // Địa chỉ
    private String city;     // Thành phố
    private String description; // Mô tả
    private String imgUrl;   // URL hình ảnh

    // Constructor mặc định
    public Location() {
    }

    // Constructor có tham số
    public Location(int locationId, String name, String address, String city, String description, String imgUrl) {
        this.locationId = locationId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.description = description;
        this.imgUrl = imgUrl;
    }

    // Getter và Setter cho locationId
    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    // Getter và Setter cho name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter và Setter cho address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Getter và Setter cho city
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter và Setter cho description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter và Setter cho imgUrl
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
