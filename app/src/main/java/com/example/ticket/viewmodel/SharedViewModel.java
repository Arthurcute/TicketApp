package com.example.ticket.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Integer> ticketQuantity = new MutableLiveData<>();
    private final MutableLiveData<Integer> ticketTypeId = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPrice = new MutableLiveData<>();
    private final MutableLiveData<String> ticketType = new MutableLiveData<>();
    public void setTicketTypeId(int ticket_type_id) {
        ticketTypeId.setValue(ticket_type_id);
    }

    public LiveData<Integer> getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketQuantity(int quantity) {
        ticketQuantity.setValue(quantity);
    }

    public LiveData<Integer> getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTotalPrice(int price) {
        totalPrice.setValue(price);
    }

    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public void setTicketType(String type) {
        ticketType.setValue(type);
    }

    public LiveData<String> getTicketType() {
        return ticketType;
    }
}
