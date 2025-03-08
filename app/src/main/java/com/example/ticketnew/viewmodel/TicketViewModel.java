package com.example.ticketnew.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TicketViewModel extends ViewModel {
    // Lưu trữ tên loại vé
    private final MutableLiveData<String> ticketTypeName = new MutableLiveData<>();

    // Lưu trữ số lượng vé
    private final MutableLiveData<Integer> ticketQuantity = new MutableLiveData<>();

    // Lưu trữ tổng tiền
    private final MutableLiveData<Integer> totalPrice = new MutableLiveData<>();

    // Lưu trữ ID sự kiện
    private final MutableLiveData<Integer> eventId = new MutableLiveData<>();

    // Hàm lấy tên loại vé
    public LiveData<String> getTicketTypeName() {
        return ticketTypeName;
    }

    // Hàm cập nhật tên loại vé
    public void setTicketTypeName(String name) {
        ticketTypeName.setValue(name);
    }

    // Hàm lấy số lượng vé
    public LiveData<Integer> getTicketQuantity() {
        return ticketQuantity;
    }

    // Hàm cập nhật số lượng vé
    public void setTicketQuantity(int quantity) {
        ticketQuantity.setValue(quantity);
    }

    // Hàm lấy tổng tiền
    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    // Hàm cập nhật tổng tiền
    public void setTotalPrice(int price) {
        totalPrice.setValue(price);
    }

    // Hàm lấy ID sự kiện
    public LiveData<Integer> getEventId() {
        return eventId;
    }

    // Hàm cập nhật ID sự kiện
    public void setEventId(int id) {
        eventId.setValue(id);
    }
}
