package com.example.ticket.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.ticket.R;
import com.example.ticket.model.Events;
import com.example.ticket.model.Location;
import com.example.ticket.network.DataManager;
import com.example.ticket.utils.Utils;
import com.example.ticket.viewmodel.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckInforFragment extends Fragment {

    private EditText emailBuyTicket;
    private EditText phoneBuyTicket;
    private TextView nextPayTicket, chonLaiVe, tenSK, diachiSK, timeSK;
    private TextView tenLoaiVe, tongTien, soLuongVe;
    private int locationId, ticket_type_id_check;
    private SharedViewModel sharedViewModel;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static final String ARG_EVENT_ID = "event_id";
    private int eventId;

    private String userId;
    public static CheckInforFragment newInstance(int eventId) {
        CheckInforFragment fragment = new CheckInforFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_infor, container, false);


        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        // Ánh xạ các View
        tenLoaiVe = view.findViewById(R.id.tenLoaiVeCheck);
        soLuongVe = view.findViewById(R.id.soLuongVeCheck);
        tongTien = view.findViewById(R.id.tongTienCheck);
        emailBuyTicket = view.findViewById(R.id.emailBuyTicket);
        phoneBuyTicket = view.findViewById(R.id.phoneBuyTicket);
        nextPayTicket = view.findViewById(R.id.thanhToan);
        chonLaiVe = view.findViewById(R.id.chonLaiVe);
        tenSK = view.findViewById(R.id.tenSK);
        diachiSK = view.findViewById(R.id.diachiSK);
        timeSK = view.findViewById(R.id.timeSK);

        sharedViewModel.getTicketTypeId().observe(getViewLifecycleOwner(), ticket_type_id -> {
            ticket_type_id_check = ticket_type_id;
        });

        sharedViewModel.getTicketQuantity().observe(getViewLifecycleOwner(), quantity -> {
            soLuongVe.setText(""+quantity);
        });

        sharedViewModel.getTotalPrice().observe(getViewLifecycleOwner(), price -> {
            tongTien.setText(price + " VND");
        });

        sharedViewModel.getTicketType().observe(getViewLifecycleOwner(), type -> {
            tenLoaiVe.setText(""+type);
        });

        eventId = getArguments().getInt(ARG_EVENT_ID, -1);
        Log.d("CheckInforFragment", "Received eventId: " + eventId +"Ticket Type ID: " + ticket_type_id_check);
        if (eventId == -1) {
                Utils.showToast(getContext(), "Invalid event ID");
                return view; // Ngăn không cho tiếp tục
        }

        // Lấy thông tin sự kiện
        fetchEventData(eventId);

        // Lấy userId từ SharedPreferences
        SharedPreferences sp = getActivity().getSharedPreferences("userId", Context.MODE_PRIVATE);
        userId = sp.getString("userId", "");

        // Kiểm tra user_id và tải thông tin người dùng
        if (!userId.isEmpty()) {
            loadUserProfile(userId);
        }

        // Thiết lập sự kiện cho nút thanh toán
        nextPayTicket.setOnClickListener(v -> {
            if (isInformationComplete()) {
                ViewPager viewPager = getActivity().findViewById(R.id.viewPagerTicket);
                if (viewPager != null) {
                    viewPager.setCurrentItem(2); // Chuyển sang PayTicketFragment
                }
            }
        });

        // Thiết lập sự kiện cho nút chọn lại vé
        chonLaiVe.setOnClickListener(v -> {
            ViewPager viewPager = getActivity().findViewById(R.id.viewPagerTicket);
            if (viewPager != null) {
                viewPager.setCurrentItem(0); // Chuyển về SelectTicketFragment
            }
        });

        return view;
    }

    private void fetchEventData(int eventId) {
        DataManager.getInstance(getActivity()).fetchEventDatabyId(eventId, new DataManager.EventCallback() {
            @Override
            public void onSuccess(Events event) {
                // Cập nhật giao diện người dùng với dữ liệu sự kiện
                String formattedStartTime = dateFormat.format(new Date(String.valueOf(event.getStart_time()))); // Chuyển đổi thời gian nếu cần
                String formattedEndTime = dateFormat.format(new Date(String.valueOf(event.getEnd_time())));

                tenSK.setText(event.getName());
                timeSK.setText(formattedStartTime + " - " + formattedEndTime);
                diachiSK.setText("Địa điểm ID: " + event.getLocation_id());
                locationId = event.getLocation_id();
                loadLocationData(locationId);
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLocationData(int locationId) {
        if (locationId != -1) {
            DataManager.getInstance(getActivity()).fetchLocationsByEventId(locationId, new DataManager.LocationCallback() {
                @Override
                public void onSuccess(List<Location> locationList) {
                    if (!locationList.isEmpty()) {
                        Location location = locationList.get(0);
                        String locationDetails = location.getName() + "\n" + location.getAddress();
                        diachiSK.setText(locationDetails);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Utils.showToast(getActivity(), "Failed to load location details: " + errorMessage);
                }
            });
        }
    }

    private void loadUserProfile(String userId) {
        int userIdInt = Integer.parseInt(userId);

        DataManager.getInstance(getActivity()).fetchUserProfile(userIdInt, new DataManager.UserProfileCallback() {
            @Override
            public void onSuccess(String name, String email, String phone, String birth, String gender) {
                // Cập nhật các trường thông tin người dùng
                emailBuyTicket.setText(email);
                phoneBuyTicket.setText(phone);
                Log.d("CheckInforFragment", "User Profile loaded: " + name + ", " + email);
            }

            @Override
            public void onError(String errorMessage) {
                // Xử lý lỗi
                Log.e("CheckInforFragment", "Error fetching user profile: " + errorMessage);
                Toast.makeText(getActivity(), "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInformationComplete() {
        // Kiểm tra thông tin để chuyển fragment mới
        String email = emailBuyTicket.getText().toString().trim();
        String phone = phoneBuyTicket.getText().toString().trim();

        if (email.isEmpty()) {
            emailBuyTicket.setError("Vui lòng nhập email.");
            return false;
        }

        // Kiểm tra định dạng email
        if (!isValidEmail(email)) {
            emailBuyTicket.setError("Email không hợp lệ.");
            return false;
        }

        if (phone.isEmpty()) {
            phoneBuyTicket.setError("Vui lòng nhập số điện thoại.");
            return false;
        }

        // Kiểm tra định dạng số điện thoại
        if (!isValidPhoneNumber(phone)) {
            phoneBuyTicket.setError("Số điện thoại không hợp lệ.");
            return false;
        }

        return true; // Nếu tất cả đều hợp lệ
    }

    private boolean isValidEmail(String email) {
        // Định dạng kiểm tra email
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phone) {
        // Kiểm tra số điện thoại có phải là số hợp lệ
        return phone.matches("\\d{10,11}"); // Số điện thoại từ 10 đến 11 chữ số
    }
}
