package com.example.ticket.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket.R;
import com.example.ticket.model.Users;
import com.example.ticket.network.DataManager;
import com.example.ticket.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private ImageView backLogin;
    private EditText nameRegister, emailRegister, passRegister, phoneRegister, birthRegister;
    private TextView register;
    private Spinner genderRegister;
    private String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ các view
        initializeViews();

        backLogin.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        register.setOnClickListener(view -> validateAndRegisterUser());

        birthRegister.setOnClickListener(view -> showDatePickerDialog());

        genderRegister.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = "";
            }
        });
    }

    private void validateAndRegisterUser() {
        String name = nameRegister.getText().toString().trim();
        String email = emailRegister.getText().toString().trim();
        String password = passRegister.getText().toString().trim();
        String phone = phoneRegister.getText().toString().trim();
        String birthDateStr = birthRegister.getText().toString().trim();

        // Kiểm tra thông tin người dùng nhập vào
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(birthDateStr)) {
            Utils.showCustomDialog(RegisterActivity.this, "Thông báo", "Vui lòng điền đầy đủ thông tin cần thiết!");
            return;
        }

        // Kiểm tra định dạng email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Utils.showCustomDialog(RegisterActivity.this, "Thông báo", "Email không hợp lệ!");
            return;
        }

        // Kiểm tra mật khẩu
        if (password.length() < 6) {
            Utils.showCustomDialog(RegisterActivity.this, "Thông báo", "Mật khẩu phải ít nhất 6 ký tự!");
            return;
        }

        // Phân tích ngày sinh
        Date birthDate = parseDate(birthDateStr);
        if (birthDate == null || !isAgeValid(birthDate)) {
            Utils.showCustomDialog(RegisterActivity.this, "Thông báo", "Ngày sinh không hợp lệ hoặc tuổi nhỏ hơn 16 tuổi!");
            return;
        }

        // Chuyển đổi ngày sinh sang định dạng yyyy-MM-dd
        SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String birthDateFormatted = sdfOutput.format(birthDate);

        // Tạo JSON từ thông tin người dùng
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("name", name);
            userJson.put("email", email);
            userJson.put("password", password);
            userJson.put("phone", phone);
            userJson.put("birthDate", birthDateFormatted); // Sử dụng định dạng đúng
            userJson.put("gender", gender);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast(RegisterActivity.this, "Đã có lỗi xảy ra khi tạo thông tin người dùng");
            return;
        }

        JSONArray userArray = new JSONArray();
        userArray.put(userJson); // Thêm đối tượng người dùng vào mảng

        // Gọi API đăng ký người dùng thông qua DataManager
        DataManager dataManager = DataManager.getInstance(this); // Nhận instance của DataManager

        dataManager.registerUsers(userArray, new DataManager.RegisterCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Utils.showToast(RegisterActivity.this, message);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish(); // Kết thúc hoạt động đăng ký
                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    if (message.contains("email đã được đăng ký")) {
                        showEmailExistsDialog();
                    } else {
                        Utils.showToast(RegisterActivity.this, message);
                    }
                });
            }
        });
    }

    private void showEmailExistsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Email đã tồn tại")
                .setMessage("Email này đã được đăng ký. Bạn có muốn đăng nhập?")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish(); // Kết thúc activity đăng ký
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                    emailRegister.setText(""); // Xóa nội dung email
                })
                .show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
                (view1, year1, month1, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(selectedDate.getTime());
                    birthRegister.setText(formattedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private boolean isAgeValid(Date birthDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -16); // Kiểm tra xem tuổi có lớn hơn 16 hay không
        return birthDate.before(calendar.getTime());
    }

    // Ánh xạ các thành phần giao diện với ID tương ứng trong file layout
    private void initializeViews() {
        backLogin = findViewById(R.id.backLogin);
        register = findViewById(R.id.register);
        nameRegister = findViewById(R.id.nameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        phoneRegister = findViewById(R.id.phoneRegister);
        passRegister = findViewById(R.id.passRegister);
        birthRegister = findViewById(R.id.birthRegister);
        genderRegister = findViewById(R.id.genderRegister);
    }

    // Phương thức chuyển đổi String thành Date
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
