package com.example.ticketnew.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.ticketnew.R;
import com.example.ticketnew.model.TicketType;
import com.example.ticketnew.network.DataManager;
import com.example.ticketnew.utils.Utils;
import com.example.ticketnew.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class SelectTicketFragment extends Fragment {

    private Button nextBuyTicket;
    private Spinner spinnerTicketType;
    private EditText edtTicketQuantity;
    private TextView tongTienSelect;
    private List<TicketType> ticketSelectList = new ArrayList<>();

    private ArrayAdapter<String> ticketTypeAdapter;
    private static final String ARG_EVENT_ID = "event_id";
    private SharedViewModel sharedViewModel;
    private int eventId, quantity, totalPrice, ticket_type_id, available_quantity;
    private String ticketName;

    public static SelectTicketFragment newInstance(int eventId) {
        SelectTicketFragment fragment = new SelectTicketFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_ticket, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Ánh xạ các View
        spinnerTicketType = view.findViewById(R.id.spinnerTicketType);
        nextBuyTicket = view.findViewById(R.id.nextBuyTicket);
        edtTicketQuantity = view.findViewById(R.id.edtTicketQuantity);
        tongTienSelect = view.findViewById(R.id.tongTienSelect);

        // Nhận eventId từ Bundle
        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID, -1);
        }

        // Nếu eventId không hợp lệ, thông báo lỗi và thoát
        if (eventId == -1) {
            Utils.showToast(getContext(), "Invalid event ID");
            return view;
        }

        // Gọi hàm lấy dữ liệu vé
        fetchTicketSelections();


        nextBuyTicket.setOnClickListener(v -> {
            if (!isTicketSelected()) {
                Utils.showCustomDialog(getContext(), "Thông báo", "Hãy chọn số lượng vé trước khi thực hiện tiếp!");
                return;
            }

            if (available_quantity == 0) {
                // Hiển thị thông báo analog khi vé đã bán hết
                Utils.showCustomDialog(getContext(), "Hãy chọn loại vé khác", "Vé này đã bán hết");
                return;
            }

            if (quantity > available_quantity) {
                // Hiển thị thông báo analog khi số lượng yêu cầu vượt quá số vé còn lại
                Utils.showCustomDialog(getContext(), "Số vé còn lại", "Loại vé này còn " + available_quantity + " vé! Hãy thực hiện lại.");
                return;
            }

            // Lưu thông tin vé vào ViewModel
            sharedViewModel.setTicketQuantity(quantity);
            sharedViewModel.setTotalPrice(totalPrice);
            sharedViewModel.setTicketType(ticketName);
            sharedViewModel.setTicketTypeId(ticket_type_id);

            // Chuyển ViewPager
            ViewPager viewPager = getActivity().findViewById(R.id.viewPagerTicket);
            viewPager.setCurrentItem(1);
        });

        // Thêm TextWatcher để theo dõi sự thay đổi số lượng vé
        edtTicketQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalPrice(); // Cập nhật tổng tiền
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì
            }
        });

        return view;
    }

    private void fetchTicketSelections() {
        DataManager.getInstance(getContext()).fetchTicketSelectionsByEventId(eventId, new DataManager.TicketSelectionCallback() {
            @Override
            public void onSuccess(List<TicketType> ticketSelectionList) {
                ticketSelectList.clear();
                ticketSelectList.addAll(ticketSelectionList);

                // Tạo danh sách tên loại vé để hiển thị trong Spinner
                List<String> ticketNames = new ArrayList<>();
                for (TicketType ticket : ticketSelectList) {
                    ticketNames.add(ticket.getTypeName() + " - " + ticket.getPrice() + " VND");
                }

                // Cài đặt adapter cho Spinner
                ticketTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ticketNames);
                ticketTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTicketType.setAdapter(ticketTypeAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Utils.showToast(getContext(), "Error loading ticket types: " + errorMessage);
            }
        });
    }

    // Cập nhật tổng tiền dựa trên số lượng vé
    private void updateTotalPrice() {
        String ticketQuantityStr = edtTicketQuantity.getText().toString().trim();
        if (!ticketQuantityStr.isEmpty() && spinnerTicketType.getSelectedItem() != null) {
            // Lấy giá vé của loại vé đã chọn
            int selectedTicketIndex = spinnerTicketType.getSelectedItemPosition();
            if (selectedTicketIndex >= 0 && selectedTicketIndex < ticketSelectList.size()) {
                TicketType selectedTicket = ticketSelectList.get(selectedTicketIndex);

                available_quantity = selectedTicket.getAvailableQuantity();
                ticket_type_id = selectedTicket.getTicketTypeId();
                quantity = Integer.parseInt(ticketQuantityStr);

                totalPrice = (int) (quantity * selectedTicket.getPrice());

                // Cập nhật tổng tiền vào TextView
                tongTienSelect.setText(totalPrice + " VND");
                ticketName = selectedTicket.getTypeName();
                // Log giá trị quantity và totalPrice
                Log.d("Select Ticket", "Quantity: " + quantity + ", Total Price: " + totalPrice + ", Ticket Name: " + ticketName + "ticket type id " + ticket_type_id);
            }
        } else {
            // Nếu không có số lượng vé hoặc chưa chọn loại vé, đặt tổng tiền về 0
            tongTienSelect.setText("0 VND");
        }
    }

    // Kiểm tra xem người dùng đã chọn số lượng vé chưa
    private boolean isTicketSelected() {
        String ticketQuantityStr = edtTicketQuantity.getText().toString().trim();
        return !ticketQuantityStr.isEmpty() && Integer.parseInt(ticketQuantityStr) > 0;
    }
}
