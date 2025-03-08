//package com.example.ticketnew.activity;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.ticketnew.R;
//import com.example.ticketnew.network.DataManager;
//import com.example.ticketnew.utils.Utils;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.Task;
//
//public class LoginActivity extends AppCompatActivity {
//    private TextView logIn, nextRegister, forgotPass;
//    private EditText emailLogin, passLogin;
//    private ImageView imgLogin;
//
//    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        anhXa();
//
//        logIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = emailLogin.getText().toString().trim();
//                String password = passLogin.getText().toString().trim();
//
//                // Kiểm tra các trường đầu vào
//                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
//                    Utils.showCustomDialog(LoginActivity.this, "Thông báo", "Vui lòng điền đầy đủ thông tin!");
//                    return;
//                }
//
//                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    Toast.makeText(LoginActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                DataManager.getInstance(LoginActivity.this).checkUserInDatabase(email, password, new DataManager.LoginCallback() {
//                    @Override
//                    public void onSuccess(String userId) {
//                        //Toast.makeText(LoginActivity.this, "Đăng nhập thành công! User ID: " + userId, Toast.LENGTH_SHORT).show();
//
//                        // Lưu trạng thái đăng ký vân tay
//                        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putString("userId", userId);
//                        editor.apply();
//
//
//                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                        //intent.putExtra("USER_ID", userId);
//                        startActivity(intent);
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(String errorMessage) {
//                        // Hiển thị thông báo lỗi nếu có
//                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//        });
//
//        nextRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this,gso);
//
//        imgLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signIn();
//            }
//        });
//    }
//
//    private void signIn() {
//        Intent signInIntent = gsc.getSignInIntent();
//        startActivityForResult(signInIntent, 1000);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1000){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//
//            try {
//                task.getResult(ApiException.class);
//                navigateToHomeActivity();
//            }
//            catch (ApiException e){
//                Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
//
//    private void navigateToHomeActivity() {
//        finish();
//        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//        startActivity(intent);
//    }
//
//    private void anhXa() {
//        logIn = findViewById(R.id.logIn);
//        nextRegister = findViewById(R.id.nextRegister);
//        forgotPass = findViewById(R.id.forgotPass);
//        emailLogin = findViewById(R.id.emailLogin);
//        passLogin = findViewById(R.id.passLogin);
//        imgLogin = findViewById(R.id.imgLogin);
//    }
//
//}
package com.example.ticketnew.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketnew.R;
import com.example.ticketnew.network.DataManager;
import com.example.ticketnew.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private TextView logIn, nextRegister, forgotPass;
    private EditText emailLogin, passLogin;
    private ImageView imgLogin;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailLogin.getText().toString().trim();
                String password = passLogin.getText().toString().trim();

                // Kiểm tra các trường đầu vào
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Utils.showCustomDialog(LoginActivity.this, "Thông báo", "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                DataManager.getInstance(LoginActivity.this).checkUserInDatabase(email, password, new DataManager.LoginCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        //Toast.makeText(LoginActivity.this, "Đăng nhập thành công! User ID: " + userId, Toast.LENGTH_SHORT).show();

                        // Lưu trạng thái đăng ký vân tay
                        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("userId", userId);
                        editor.apply();


                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        //intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Hiển thị thông báo lỗi nếu có
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        nextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        imgLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
                startActivity(intent);
            }
        });

    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToHomeActivity();
            }
            catch (ApiException e){
                Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void navigateToHomeActivity() {
        finish();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void anhXa() {
        logIn = findViewById(R.id.logIn);
        nextRegister = findViewById(R.id.nextRegister);
        forgotPass = findViewById(R.id.forgotPass);
        emailLogin = findViewById(R.id.emailLogin);
        passLogin = findViewById(R.id.passLogin);
        imgLogin = findViewById(R.id.imgLogin);
    }

}
