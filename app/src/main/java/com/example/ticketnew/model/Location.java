package com.example.ticketnew.model;

public class Location {
    private int locationId;  // ID của địa điểm
    private String name;     // Tên địa điểm
    private String address;  // Địa chỉ
    private String city;     // Thành phố
    private int capacity;
    private String description; // Mô tả
    private String imgUrl;   // URL hình ảnh

    public Location() {
    }

    public Location(int locationId, String name, String address, String city, int capacity, String description, String imgUrl) {
        this.locationId = locationId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.capacity = capacity;
        this.description = description;
        this.imgUrl = imgUrl;
    }


// Getter và Setter cho locationId

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

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
