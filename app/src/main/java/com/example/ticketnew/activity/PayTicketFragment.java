package com.example.ticketnew.activity;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketnew.R;
import com.example.ticketnew.model.Events;
import com.example.ticketnew.model.Location;
import com.example.ticketnew.network.DataManager;
import com.example.ticketnew.utils.Utils;
import com.example.ticketnew.viewmodel.SharedViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PayTicketFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private int eventId;
    private String userId;
    private int locationId, ticketTypeIdPay, payment_method_id;
    private TextView tenSKPay, diachiSKPay, timeSKPay, nameUserPay, emailPay, phonePay, tenLoaiVePay, soLuongPay, tongTienPay;
    private Button thanhToanPay;
    private RadioGroup paymentOptionsGroup;
    private  double totalAmount;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SharedViewModel sharedViewModel;

    public static PayTicketFragment newInstance(int eventId) {
        PayTicketFragment fragment = new PayTicketFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public PayTicketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_ticket, container, false);

        tenSKPay = view.findViewById(R.id.tenSKPay);
        diachiSKPay = view.findViewById(R.id.diachiSKPay);
        timeSKPay = view.findViewById(R.id.timeSKPay);
        nameUserPay = view.findViewById(R.id.nameUserPay);
        emailPay = view.findViewById(R.id.emailUserPay);
        phonePay = view.findViewById(R.id.phoneUserPay);
        soLuongPay = view.findViewById(R.id.soLuongVePay);
        tongTienPay = view.findViewById(R.id.tongTienPay);
        tenLoaiVePay = view.findViewById(R.id.tenLoaiVePay);
        thanhToanPay = view.findViewById(R.id.thanhToanPay);
        paymentOptionsGroup = view.findViewById(R.id.paymentOptionsGroup);

        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID, -1);
        }
        if (eventId == -1) {
            Utils.showToast(getContext(), "Invalid event ID");
            return view;
        }

        fetchEventData(eventId);
        loadUserId();

        setupObservers();
        thanhToanPay.setOnClickListener(v -> handlePayment());

        return view;
    }

    private void loadUserId() {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = preferences.getString("userId", null);

        if (!userId.isEmpty()) {
            loadUserProfile(userId);
        }
    }

    private void setupObservers() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getTicketQuantity().observe(getViewLifecycleOwner(), quantity -> soLuongPay.setText(String.valueOf(quantity)));
        sharedViewModel.getTotalPrice().observe(getViewLifecycleOwner(), price -> {
            // Set giá trị tổng giá vào UI dưới dạng String
            tongTienPay.setText(formatPrice(price));

            // Chuyển giá trị từ Integer sang double và gán vào totalAmount
             totalAmount = price != null ? price.doubleValue() : 0.0;  // Nếu price null, gán giá trị mặc định là 0.0
            Log.d("Tổng tiên: ",""+totalAmount);
        });

        sharedViewModel.getTicketType().observe(getViewLifecycleOwner(), type -> tenLoaiVePay.setText(type));
        sharedViewModel.getTicketTypeId().observe(getViewLifecycleOwner(), ticketTypeId -> ticketTypeIdPay = ticketTypeId);
    }

    private String formatPrice(double price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price);
    }


    private void handlePayment() {
        // Lấy id của radio button được chọn
        int selectedId = paymentOptionsGroup.getCheckedRadioButtonId();

        // Kiểm tra id của radio button được chọn và chuyển tới trang thanh toán tương ứng
        if (selectedId == R.id.radio_momo) {
            payment_method_id = 3;
            // Lấy thông tin cần thiết
//            String totalAmountStr = tongTienPay.getText().toString().replaceAll("[^\\d.]", "");
//            double totalAmount = Double.parseDouble(totalAmountStr);

            String orderDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Gọi phương thức tạo đơn hàng
            DataManager.getInstance(getActivity()).createOrder(userId, payment_method_id, totalAmount, orderDate, eventId, ticketTypeIdPay, Integer.parseInt(soLuongPay.getText().toString()), totalAmount / Integer.parseInt(soLuongPay.getText().toString()), new DataManager.OrderCallback() {
                @Override
                public void onSuccess(int orderId) {
                    Utils.showCustomDialog(getContext(), "Thông báo", "Đã mua vé thành công!");
                    Intent intent = new Intent(getActivity(), MyTicketActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getActivity(), "Lỗi khi tạo đơn hàng: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } else if (selectedId == R.id.radio_zalopay) {
            payment_method_id = 2;
            // Lấy thông tin cần thiết
//            String totalAmountStr = tongTienPay.getText().toString().replaceAll("[^\\d.]", "");
//            double totalAmount = Double.parseDouble(totalAmountStr);

            String orderDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Gọi phương thức tạo đơn hàng
            DataManager.getInstance(getActivity()).createOrder(userId, payment_method_id, totalAmount, orderDate, eventId, ticketTypeIdPay, Integer.parseInt(soLuongPay.getText().toString()), totalAmount / Integer.parseInt(soLuongPay.getText().toString()), new DataManager.OrderCallback() {
                @Override
                public void onSuccess(int orderId) {
                    Utils.showCustomDialog(getContext(), "Thông báo", "Đã mua vé thành công!");
                    Intent intent = new Intent(getActivity(), MyTicketActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getActivity(), "Lỗi khi tạo đơn hàng: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (selectedId == R.id.radio_paypal) {
            payment_method_id = 1;
            // Lấy thông tin cần thiết
//            String totalAmountStr = tongTienPay.getText().toString().replaceAll("[^\\d.]", "");
//            double totalAmount = Double.parseDouble(totalAmountStr);

            String orderDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Gọi phương thức tạo đơn hàng
            DataManager.getInstance(getActivity()).createOrder(userId, payment_method_id, totalAmount, orderDate, eventId, ticketTypeIdPay, Integer.parseInt(soLuongPay.getText().toString()), totalAmount / Integer.parseInt(soLuongPay.getText().toString()), new DataManager.OrderCallback() {
                @Override
                public void onSuccess(int orderId) {
                    // Chuyển tới trang thanh toán hoặc màn hình khác nếu cần
                    Utils.showCustomDialog(getContext(), "Thông báo", "Đã mua vé thành công!");
                    Intent intent = new Intent(getActivity(), MyTicketActivity.class);

                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getActivity(), "Lỗi khi tạo đơn hàng: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
        }
    }

    private void createOrder(double totalAmount, String orderDate) {
        DataManager.getInstance(getActivity()).createOrder(userId, payment_method_id, totalAmount, orderDate, eventId, ticketTypeIdPay, Integer.parseInt(soLuongPay.getText().toString()), totalAmount / Integer.parseInt(soLuongPay.getText().toString()), new DataManager.OrderCallback() {
            @Override
            public void onSuccess(int orderId) {
                Toast.makeText(getActivity(), "Đặt hàng thành công! Mã đơn hàng: " + orderId, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MyTicketActivity.class));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getActivity(), "Lỗi khi tạo đơn hàng: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEventData(int eventId) {
        DataManager.getInstance(getActivity()).fetchEventDatabyId(eventId, new DataManager.EventCallback() {
            @Override
            public void onSuccess(Events event) {
                String formattedStartTime = dateFormat.format(new Date(String.valueOf(event.getStart_time())));
                String formattedEndTime = dateFormat.format(new Date(String.valueOf(event.getEnd_time())));

                tenSKPay.setText(event.getName());
                timeSKPay.setText(formattedStartTime + " - " + formattedEndTime);
                locationId = event.getLocation_id();
                loadLocationData(locationId);
            }

            @Override
            public void onError(String error) {
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
                        diachiSKPay.setText(locationDetails);
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
                nameUserPay.setText(name);
                emailPay.setText(email);
                phonePay.setText(phone);
                Log.d("PayTicket", "User Profile loaded: " + name + ", " + email);
            }
            @Override
            public void onError(String errorMessage) {
                Log.e("PayFragment", "Error fetching user profile: " + errorMessage);
                Toast.makeText(getActivity(), "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
