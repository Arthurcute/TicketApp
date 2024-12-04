package com.example.ticket.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static final String ROOT_URL = "http://192.168.0.110/ticket";
    public static final String REGISTER_URL = "register.php";
    public static final String CODEREGISTER_URL = "verify_code.php";

    public static  final  String USER_PROFILE_URL = "user_profile.php";
    public static final String UPDATE_PROFILE_URL = "update_user.php";
    public static final String EVENTNEW_URL = "get_four_events.php";
    public static final String EVENT_URL = "get_event.php";
public static final String CHECK_CONVER = "check_conversation.php";
    public static final String EVENTSEARCH_URL = "search_event.php";
    public static final String EVENTDETAIL_URL="get_eventdetail.php";
    public static final String GET_LOCATION_URL = "get_location.php";
    public static final String LOGIN_URL = "login.php";

    public static final String TICKETTYPE_URL = "get_ticket_types.php";
    public static  final String NOTION_URL ="get_notion.php";
    public static final String CHECK_EMAIL_URL="check_email.php";
    // Phương thức hiển thị Toast thông báo
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showCustomDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }



}
