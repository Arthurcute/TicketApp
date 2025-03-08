package com.example.ticketnew.network;

import static android.content.ContentValues.TAG;

import static java.net.URLEncoder.encode;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticketnew.model.Conversation;
import com.example.ticketnew.model.EventNotion;
import com.example.ticketnew.model.Events;
import com.example.ticketnew.model.Message;
import com.example.ticketnew.model.Order;
import com.example.ticketnew.model.Section;
import com.example.ticketnew.model.Ticket;
import com.example.ticketnew.model.TicketType;
import com.example.ticketnew.model.Location;
import com.example.ticketnew.utils.Utils;
import com.google.crypto.tink.subtle.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
public class DataManager {
    private static DataManager instance;
    private RequestQueue requestQueue;
    private Context context;
    private List<Ticket> ticketList;
    private SimpleDateFormat dateFormat;

    private DataManager(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    public Date parseDateTime(String dateStr) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Hoặc xử lý lỗi theo cách bạn muốn
        }
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    public interface DataCallback {
        void onSuccess(List<Events> eventList);

        void onError(String errorMessage);
    }

    public interface TicketSelectionCallback {
        void onSuccess(List<TicketType> ticketSelectionList);

        void onError(String errorMessage);
    }

    public interface LocationCallback {
        void onSuccess(List<Location> locationList);

        void onError(String errorMessage);
    }

    public interface LoginCallback {
        void onSuccess(String message);

        void onError(String errorMessage);
    }

    public interface SectionCallback {
        void onSuccess(Section section);

        void onError(String errorMessage);
    }

    public interface SavePendingOrderCallback {
        void onSuccess();

        void onError(String errorMessage);
    }

    // Parse string thành Date
    private Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Callback cho quá trình đăng ký
    public interface RegisterCallback {
        void onSuccess(String message);

        void onFailure(String message);
    }

    public interface UserProfileCallback {
        void onSuccess(String name, String email, String phone, String birth, String gender);

        void onError(String errorMessage);
    }

    public interface UpdateProfileCallback {
        void onSuccess(String message);

        void onError(String errorMessage);
    }

    public interface NotionCallback {
        void onSuccess(ArrayList<EventNotion> eventNotions);

        void onError(String errorMessage);
    }

    public interface EventCallback {
        void onSuccess(Events event);

        void onError(String error);
    }

    // Fetch event data
    public void fetchEventData(final DataCallback callback) {
        String url = Utils.ROOT_URL + "/" + Utils.EVENTNEW_URL;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Events> eventList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject eventObj = response.getJSONObject(i);
                                Events event = new Events(
                                        eventObj.getInt("event_id"),
                                        eventObj.getString("name"),
                                        eventObj.getInt("artist_id"), // Thêm artist_id
                                        parseDate(eventObj.getString("start_time")),
                                        parseDate(eventObj.getString("end_time")),
                                        eventObj.getInt("location_id"),
                                        eventObj.getInt("event_type_id"),
                                        eventObj.getString("description"),
                                        eventObj.getString("image_url"),
                                        eventObj.getString("status"),
                                        parseDate(eventObj.getString("ticket_sale_start")),
                                        parseDate(eventObj.getString("ticket_sale_end"))
                                );
                                eventList.add(event);
                            }
                            callback.onSuccess(eventList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Failed to parse events data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Failed to load events: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(request);
    }


    public void fetchEventDatabyId(int eventId, final EventCallback callback) {
        // Xây dựng URL để lấy thông tin sự kiện với event_id là tham số GET
        String url = Utils.ROOT_URL + "/" + Utils.EVENTDETAIL_URL + "?event_id=" + eventId;
        Log.d("DetailEventActivity", "Fetching event from URL: " + url);

        // Tạo yêu cầu JSON để lấy thông tin sự kiện
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // In ra phản hồi để kiểm tra
                            Log.d("DetailEventActivity", "Response: " + response.toString());

                            // Kiểm tra nếu phản hồi có chứa thông tin sự kiện
                            if (response.has("event_id")) {
                                Events event = new Events(
                                        response.getInt("event_id"),
                                        response.getString("name"),
                                        response.optInt("artist_id", -1),
                                        parseDate(response.getString("start_time")),
                                        parseDate(response.getString("end_time")),
                                        response.getInt("location_id"),
                                        response.getInt("event_type_id"),
                                        response.getString("description"),
                                        response.getString("image_url"),
                                        response.getString("status"),
                                        parseDate(response.getString("ticket_sale_start")),
                                        parseDate(response.getString("ticket_sale_end"))
                                );
                                callback.onSuccess(event);
                            } else {
                                callback.onError("Event data is incomplete: missing event_id");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Failed to parse event data: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DetailEventActivity", "Error: " + error.getMessage());
                        callback.onError("Failed to load event: " + error.getMessage());
                    }
                }
        );

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(request);
    }

    // Fetch categorized events
    public void fetchCategorizedEvents(final DataCallback callback) {
        String url = Utils.ROOT_URL + "/" + Utils.EVENT_URL;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Events> musicList = new ArrayList<>();
                        List<Events> concertList = new ArrayList<>();
                        List<Events> otherList = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject eventObj = response.getJSONObject(i);
                                Events event = new Events(
                                        eventObj.getInt("event_id"),
                                        eventObj.getString("name"),
                                        eventObj.getInt("artist_id"), // Thêm artist_id
                                        parseDate(eventObj.getString("start_time")),
                                        parseDate(eventObj.getString("end_time")),
                                        eventObj.getInt("location_id"),
                                        eventObj.getInt("event_type_id"),
                                        eventObj.getString("description"),
                                        eventObj.getString("image_url"),
                                        eventObj.getString("status"),
                                        parseDate(eventObj.getString("ticket_sale_start")),
                                        parseDate(eventObj.getString("ticket_sale_end"))
                                );

                                // Phân loại sự kiện dựa trên loại sự kiện (event_type_id)
                                int category = eventObj.getInt("event_type_id");
                                if (category == 1) {
                                    musicList.add(event);
                                } else if (category == 2) {
                                    concertList.add(event);
                                } else {
                                    otherList.add(event);
                                }
                            }

                            // Kết hợp các danh sách sự kiện lại
                            List<Events> allEvents = new ArrayList<>();
                            allEvents.addAll(musicList);
                            allEvents.addAll(concertList);
                            allEvents.addAll(otherList);

                            callback.onSuccess(allEvents);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Failed to parse categorized events data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Failed to load categorized events: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(request);
    }


    // Fetch all ticket selections
    public void fetchTicketSelectionsByEventId(int eventId, final TicketSelectionCallback callback) {
        // Tạo URL cho yêu cầu GET, bao gồm event_id như một tham số truy vấn
        String url = Utils.ROOT_URL + "/" + Utils.TICKETTYPE_URL + "?event_id=" + eventId;

        // Tạo yêu cầu JSON Array
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Danh sách để lưu trữ các loại vé
                        List<TicketType> ticketTypes = new ArrayList<>();
                        try {
                            // Duyệt qua từng đối tượng trong mảng JSON
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                // Tạo đối tượng TicketType từ dữ liệu JSON
                                TicketType ticketType = new TicketType(
                                        jsonObject.getInt("ticket_type_id"),
                                        jsonObject.getInt("event_id"),
                                        jsonObject.getString("type_name"),
                                        jsonObject.getDouble("price"),
                                        jsonObject.getInt("total_quantity"),
                                        jsonObject.getInt("available_quantity")
                                );
                                // Thêm đối tượng vào danh sách
                                ticketTypes.add(ticketType);
                            }

                            // Gọi callback thành công với danh sách các loại vé
                            if (callback != null) {
                                callback.onSuccess(ticketTypes);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Gọi callback lỗi nếu có lỗi trong quá trình phân tích JSON
                            if (callback != null) {
                                callback.onError("Error parsing JSON data");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gọi callback lỗi nếu có lỗi trong yêu cầu
                        if (callback != null) {
                            callback.onError("Error fetching data from server: " + error.getMessage());
                        }
                    }
                }
        );

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(request);
    }

    // Fetch search results for events based on a search query
    public void searchEvents(String query, final DataCallback callback) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String url = Utils.ROOT_URL + "/" + Utils.EVENTSEARCH_URL + "?name=" + encodedQuery;
        Log.d("SearchEventActivity", "Search URL: " + url);

        // Sử dụng JsonArrayRequest thay vì JsonObjectRequest
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Events> eventList = new ArrayList<>();
                        try {
                            Log.d("SearchEventActivity", "Search response: " + response.toString());

                            // Duyệt qua các đối tượng sự kiện trong JSONArray
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject eventObj = response.getJSONObject(i);
                                Events event = new Events(
                                        eventObj.getInt("event_id"),
                                        eventObj.getString("name"),
                                        eventObj.getInt("artist_id"),
                                        parseDate(eventObj.getString("start_time")),
                                        parseDate(eventObj.getString("end_time")),
                                        eventObj.getInt("location_id"),
                                        eventObj.getInt("event_type_id"),
                                        eventObj.getString("description"),
                                        eventObj.getString("image_url"),
                                        eventObj.getString("status"),
                                        parseDate(eventObj.getString("ticket_sale_start")),
                                        parseDate(eventObj.getString("ticket_sale_end"))
                                );
                                eventList.add(event);
                            }

                            // Gọi lại callback thành công với danh sách sự kiện
                            callback.onSuccess(eventList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Failed to parse search results: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Failed to load search results: " + error.getMessage());
                    }
                }
        );

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(request);
    }


    // Fetch locations based on location_id
    public void fetchLocationsByEventId(int locationId, final LocationCallback callback) {
        String url = Utils.ROOT_URL + "/" + Utils.GET_LOCATION_URL + "?location_id=" + locationId;
        Log.d("DataManager", "Fetching locations from URL: " + url);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Location> locationList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject locationObj = response.getJSONObject(i);
                                Location location = new Location(
                                        locationObj.getInt("location_id"),
                                        locationObj.getString("name"),
                                        locationObj.getString("address"),
                                        locationObj.getString("city"),
                                        locationObj.getInt("capacity"),
                                        locationObj.getString("description"),
                                        locationObj.getString("img_url")
                                );
                                locationList.add(location);
                            }
                            callback.onSuccess(locationList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Failed to parse locations data: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataManager", "Error fetching locations: " + error.getMessage());
                        callback.onError("Failed to load locations: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(request);
    }

    // Check user login credentials
    public void checkUserInDatabase(String email, String password, final LoginCallback callback) {
        String API_URL = Utils.ROOT_URL + "/" + Utils.LOGIN_URL;

        // Tạo một JSONObject chứa dữ liệu
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Lỗi tạo dữ liệu");
            return;
        }

        // Tạo một yêu cầu JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // In ra phản hồi để kiểm tra dữ liệu trả về
                        Log.d("LoginResponse", "Response: " + response.toString());

                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {
                                // Nhận user_id từ phản hồi
                                String userId = response.getString("user_id");
                                callback.onSuccess(userId);
                            } else {
                                callback.onError(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Lỗi phân tích dữ liệu");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // In ra lỗi để kiểm tra chi tiết
                        Log.e("VolleyError", "Error: " + error.getMessage());
                        callback.onError("Lỗi kết nối");
                    }
                }) {
            // Override phương thức để kiểm tra mã trạng thái HTTP
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                String jsonString = "";
                try {
                    // Lấy dữ liệu từ phản hồi HTTP
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    // Kiểm tra nếu phản hồi là JSON hợp lệ
                    if (jsonString.contains("<br")) {
                        // Nếu phản hồi có chứa HTML, tức là có lỗi xảy ra
                        return Response.error(new VolleyError("Lỗi từ máy chủ"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(jsonObjectRequest);
    }

    // Đăng ký tài khoản người dùng
    public void registerUsers(JSONArray userArray, RegisterCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(Utils.ROOT_URL + "/" + Utils.REGISTER_URL); // Thay thế bằng URL của bạn
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Gửi dữ liệu JSON
                OutputStream os = connection.getOutputStream();
                os.write(userArray.toString().getBytes());
                os.flush();
                os.close();

                // Nhận phản hồi
                InputStream inputStream = connection.getInputStream();
                String response = new Scanner(inputStream).useDelimiter("\\A").next();
                inputStream.close();

                // Xử lý phản hồi
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess(response); // Gọi callback thành công
                } else {
                    callback.onFailure("Đăng ký không thành công");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Có lỗi xảy ra: " + e.getMessage());
            }
        }).start();
    }

    //thông tin người dùng
    // Fetch user profile data
    public void fetchUserProfile(int userId, final UserProfileCallback callback) {
        String url = Utils.ROOT_URL + "/" + Utils.USER_PROFILE_URL;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                JSONObject user = response.getJSONObject("user");
                                String name = user.getString("name");
                                String email = user.getString("email");
                                String phone = user.getString("phone");
                                String birth = user.getString("birthdate");
                                String gender = user.getString("gender");

                                callback.onSuccess(name, email, phone, birth, gender);
                            } else {
                                String message = response.getString("message");
                                callback.onError(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Error parsing JSON data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Failed to load user profile: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    public void updateUserProfile(String userId, String name, String email, String phone, String birth, String gender, final UpdateProfileCallback updateProfileCallback) {
        String API_URL = Utils.ROOT_URL + "/" + Utils.UPDATE_PROFILE_URL;

        // Tạo một JSON object với dữ liệu người dùng
        JSONObject userProfile = new JSONObject();
        try {
            userProfile.put("user_id", userId);
            userProfile.put("name", name);
            userProfile.put("email", email);
            userProfile.put("phone", phone);
            userProfile.put("birth", birth);
            userProfile.put("gender", gender);
        } catch (JSONException e) {
            e.printStackTrace();
            updateProfileCallback.onError("Lỗi tạo dữ liệu JSON");
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userProfile", userProfile); // Đặt tất cả thông tin vào một đối tượng con
        } catch (JSONException e) {
            e.printStackTrace();
            updateProfileCallback.onError("Lỗi tạo dữ liệu JSON");
            return;
        }

        // Tạo một yêu cầu JSON Object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {
                                updateProfileCallback.onSuccess(message);
                            } else {
                                updateProfileCallback.onError(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            updateProfileCallback.onError("Lỗi không mong đợi xảy ra");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Không thể cập nhật thông tin: ";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            // Lấy thông báo lỗi từ phản hồi nếu có
                            String responseBody = new String(error.networkResponse.data);
                            errorMessage += responseBody;
                        } else {
                            errorMessage += error.getMessage();
                        }
                        updateProfileCallback.onError(errorMessage);
                    }
                }
        );

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(jsonObjectRequest);
    }

    public static void fetchEventNotions(Context context, NotionCallback callback) {
        String BASE_URL = Utils.ROOT_URL + "/" + Utils.NOTION_URL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("eventNotions");
                        ArrayList<EventNotion> eventNotionList = new ArrayList<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject eventNotionJson = jsonArray.getJSONObject(i);
                            // Lấy thông tin từ JSON
                            int eventId = eventNotionJson.getInt("eventId");
                            String eventName = eventNotionJson.getString("eventName");
                            String content = eventNotionJson.getString("content");
                            String imageUrl = eventNotionJson.getString("imageUrl");
                            String description = eventNotionJson.getString("description");
                            String status = eventNotionJson.getString("status");

                            // Phân tích thời gian từ chuỗi
                            Date startTime = parseDate(eventNotionJson.getString("startTime"), sdf);
                            Date endTime = parseDate(eventNotionJson.getString("endTime"), sdf);
                            Date ticketSaleStart = parseDate(eventNotionJson.getString("ticketSaleStart"), sdf);
                            Date ticketSaleEnd = parseDate(eventNotionJson.getString("ticketSaleEnd"), sdf);
                            int locationId = eventNotionJson.getInt("locationId");
                            int eventTypeId = eventNotionJson.getInt("eventTypeId");

                            // Thêm sự kiện vào danh sách
                            eventNotionList.add(new EventNotion(content, eventId, eventName, imageUrl, description, status,
                                    startTime, endTime, locationId, eventTypeId,
                                    ticketSaleStart, ticketSaleEnd));
                        }
                        callback.onSuccess(eventNotionList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Lỗi khi phân tích dữ liệu.");
                    }
                },
                error -> callback.onError("Lỗi kết nối.")
        );

        // Thêm yêu cầu vào hàng đợi
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
    public interface EmailCallback {
        void onSuccess(String userId);  // Khi email đã tồn tại trong database
        void onNotRegistered();         // Khi email chưa đăng ký
        void onError(String errorMessage); // Khi có lỗi
    }

    public void checkEmailInDatabase(String email, final EmailCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.CHECK_EMAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("exists")) {
                                String userId = jsonObject.getString("userId");
                                callback.onSuccess(userId);
                            } else {
                                callback.onNotRegistered();
                            }
                        } catch (JSONException e) {
                            callback.onError("Lỗi phân tích dữ liệu.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Lỗi kết nối.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    public void checkConversation(final int eventId, final String userId, final ConversationCallback callback) {

        String url = Utils.ROOT_URL + "/" + Utils.CHECK_CONVER;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // Lấy dữ liệu từ phản hồi JSON
                            int conversationId = jsonObject.optInt("conversation_id", -1); // Nếu không có thì mặc định là -1
                            String status = jsonObject.optString("statusConver", null);
                            String publicKeyAdmin = jsonObject.optString("public_rsa_admin", null);
                            boolean exists = conversationId != -1;

                            // Tạo đối tượng Conversation
                            Conversation conversation = new Conversation(
                                    conversationId,
                                    eventId,
                                    userId,
                                    status,
                                    publicKeyAdmin,
                                    exists
                            );

                            // Trả kết quả qua callback
                            callback.onConversationCheck(conversation);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Failed to parse conversation data: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                        Log.e("DataManager", "Error checking conversation: " + errorMessage);
                        callback.onError("Failed to check conversation: " + errorMessage);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", String.valueOf(eventId));
                params.put("user_id", userId);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    // Callback interface
    public interface ConversationCallback {
        void onConversationCheck(Conversation conversation);
        void onError(String error);
    }


    public void createConversation(int eventId, String userId, String userName, final PublicKey publicKey, ConversationCallback callback) {
        // Mã hóa publicKey thành Base64
        String encodedPublicKey = encodePublicKeyToBase64(publicKey);
        Log.d("DataManager createConversation", "EventId: " + eventId + ", UserId: " + userId + ", UserName: " + userName);

        // Địa chỉ URL API
        String url = Utils.ROOT_URL + "/createConversation.php";

        // Tạo đối tượng JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("eventId", eventId);
            jsonObject.put("userId", userId);
            jsonObject.put("userName", userName); // Thêm userName vào JSON
            jsonObject.put("publicKey", encodedPublicKey); // Gửi publicKey đã mã hóa

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Error creating JSON: " + e.getMessage());
            return;
        }

        // Gửi yêu cầu POST đến server
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        if (response.has("success") && response.getBoolean("success")) {
                            int conversationId = response.getInt("conversation_id");
                            callback.onConversationCheck(new Conversation(conversationId, eventId, userId, true));
                        } else {
                            callback.onError("Failed to create conversation: " + response.optString("message", "No message provided"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Error parsing response: " + e.getMessage());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    String errorMessage;
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        errorMessage = "Server response: " + responseBody;
                    } else {
                        errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                    }
                    Log.e("DataManager", "Error creating conversation: " + errorMessage);
                    callback.onError("Error creating conversation: " + errorMessage);
                }
        );

        // Thêm yêu cầu vào requestQueue
        requestQueue.add(jsonObjectRequest);
    }

    // Hàm mã hóa publicKey thành Base64
    private String encodePublicKeyToBase64(PublicKey publicKey) {
        try {
            byte[] publicKeyBytes = publicKey.getEncoded();
            return Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//    public void updateConversationStatus(int conversationId, String status, ConversationCallback callback) {
//        // Kiểm tra để chắc chắn rằng conversationId và status không null
//        if (conversationId <= 0 || status == null) {
//            callback.onError("conversationId must be greater than 0 and status cannot be null.");
//            return;
//        }
//
//        String url = Utils.ROOT_URL + "/update_conversation_status.php";
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("conversationId", conversationId);
//            jsonObject.put("status", status);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            callback.onError("Error creating JSON: " + e.getMessage());
//            return;
//        }
//
//        Log.d("UpdateConversationStatus", "Sending JSON: " + jsonObject.toString());
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
//                response -> {
//                    try {
//                        Log.d("Response", "Server response: " + response.toString());
//
//                        if (response.has("success") && response.getBoolean("success")) {
//                            callback.onConversationCheck(new Conversation(conversationId, -1, "", true));
//                        } else {
//                            callback.onError("Failed to update conversation status: " + response.optString("message", "No message provided"));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        callback.onError("Error parsing response: " + e.getMessage());
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                },
//                error -> {
//                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
//                    Log.e("DataManager", "Error updating conversation status: " + errorMessage);
//                    callback.onError("Error updating conversation status: " + errorMessage);
//                }
//        );
//
//        requestQueue.add(jsonObjectRequest);
//    }
//
//    // Phương thức để lưu public key vào CSDL
//    public static void savePublicKey(final Context context, final int userId, final PublicKey publicKey, final SavePublicKeyCallback callback) {
//        // Mã hóa public key thành chuỗi base64 để gửi lên server
////        String encodedPublicKey = encodePublicKeyToBase64(publicKey);
////
//        // Tạo yêu cầu HTTP gửi dữ liệu lên server
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST,
//                Utils.ROOT_URL+"/savePublicKeyUser.php",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("DataManager", "Public key saved: " + response);
//                        // Kiểm tra phản hồi từ server
//                        if (response.contains("success")) {
//                            callback.onSuccess("Public key saved successfully");
//                        } else {
//                            callback.onError("Failed to save public key: " + response);
//                        }
//                    }
//                },
//                error -> {
//                    // Xử lý lỗi nếu có
//                    String errorMessage = "Error saving public key: " + error.getMessage();
//                    callback.onError(errorMessage);
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                // Thêm các tham số để gửi lên server
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", String.valueOf(userId));
////                params.put("public_key", encodedPublicKey);
//                return params;
//            }
//        };
//
//        // Thêm yêu cầu vào queue của Volley
//        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
//    }
//    // Callback để xử lý kết quả lưu public key
//    public interface SavePublicKeyCallback {
//        void onSuccess(String message);
//        void onError(String error);
//    }

//    public void addMessage(int conversationId, String userId, String messageContent, MessageCallback callback) {
//        String url = Utils.ROOT_URL + "/add_message.php"; // Đường dẫn đến script PHP thêm tin nhắn
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("conversationId", conversationId);
//            jsonObject.put("userId", userId);
//            jsonObject.put("messageContent", messageContent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            callback.onError("Error creating JSON: " + e.getMessage());
//            return;
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
//                response -> {
//                    try {
//                        if (response.has("success") && response.getBoolean("success")) {
//                            callback.onMessageAdded();
//                        } else {
//                            callback.onError("Failed to add message.");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        callback.onError("Error parsing response: " + e.getMessage());
//                    }
//                },
//                error -> {
//                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
//                    Log.e("DataManager", "Error adding message: " + errorMessage);
//                    callback.onError("Error adding message: " + errorMessage);
//                }
//        );
//
//        requestQueue.add(jsonObjectRequest);
//    }

    public void addMessage(int conversationId, String userId, String messageContent, String aesKey,String aesKeyAdmin, MessageCallback callback) {
        String url = Utils.ROOT_URL + "/add_message.php"; // Đường dẫn đến script PHP thêm tin nhắn

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("conversationId", conversationId);
            jsonObject.put("userId", userId);
            jsonObject.put("messageContent", messageContent);
            jsonObject.put("encrypted_aes_key", aesKey);
            jsonObject.put("encrypted_aes_key_admin", aesKeyAdmin);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Error creating JSON: " + e.getMessage());
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        if (response.has("success") && response.getBoolean("success")) {
                            callback.onMessageAdded();
                        } else {
                            callback.onError("Failed to add message.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                    Log.e("DataManager", "Error adding message: " + errorMessage);
                    callback.onError("Error adding message: " + errorMessage);
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
    public interface MessageCallback {
        void onMessageAdded(); // Phương thức gọi khi tin nhắn đã được thêm thành công

        void onError(String error); // Phương thức gọi khi có lỗi xảy ra
    }
    // Phương thức để phân tích chuỗi ngày giờ
    private static Date parseDate(String dateString, SimpleDateFormat sdf) {
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            Log.e("DataManager", "Failed to parse date: " + dateString + " - " + e.getMessage());
            return null; // Hoặc xử lý theo cách khác
        }
    }

    // Phương thức lấy danh sách tin nhắn
    // Phương thức lấy danh sách tin nhắn
    public void getMessages(int conversationId, MessageListCallback callback) {
        String url = Utils.ROOT_URL + "/get_message.php?conversation_id=" + conversationId;

        // Tạo yêu cầu JSON array
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Message> messages = new ArrayList<>();
                        try {
                            // Sửa ở đây: Response là JSONArray, nên phải duyệt qua các phần tử của nó
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject messageJson = response.getJSONObject(i);  // Lấy mỗi đối tượng JSON trong mảng

                                // Khai báo các biến cho dữ liệu
                                int messageId = messageJson.getInt("message_id");
                                int convId = messageJson.getInt("conversation_id");
                                String senderType = messageJson.getString("sender_type");
                                String messageContent = messageJson.getString("message_content");
                                String aes_key = messageJson.getString("encrypted_aes_key");
                                String aes_key_admin = messageJson.getString("encrypted_aes_key_admin");
                                String timestamp = messageJson.getString("timestamp");

                                // Tạo đối tượng Message và thêm vào danh sách
                                Message message = new Message(messageId, convId, senderType, messageContent, aes_key, aes_key_admin, timestamp);
                                messages.add(message);
                            }

                            // Gọi callback với danh sách tin nhắn
                            callback.onMessageListFetched(messages);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Error parsing messages: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                        Log.e("DataManager", "Error fetching messages: " + errorMessage);
                        callback.onError("Error fetching messages: " + errorMessage);
                    }
                });

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(jsonArrayRequest);
    }

    public interface MessageListCallback {
        void onMessageListFetched(List<Message> messages);
        void onError(String error);
    }
    // Phương thức tạo đơn hàng
    public void createOrder(String userId, int paymentMethodId, double totalAmount, String orderDate, int eventId, int ticketTypeId, int quantity, double price, OrderCallback callback) {
        String url = Utils.ROOT_URL + "/create_order.php";

        // Tạo JSONObject để gửi lên server
        JSONObject orderData = new JSONObject();
        try {
            orderData.put("user_id", userId);
            orderData.put("payment_method_id", paymentMethodId);
            orderData.put("total_amount", totalAmount);
            orderData.put("order_date", orderDate);
            orderData.put("event_id", eventId);
            orderData.put("ticket_type_id", ticketTypeId);
            orderData.put("quantity", quantity);
            orderData.put("price", price);

            Log.d("Order Data", orderData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Lỗi tạo dữ liệu đơn hàng: " + e.getMessage());
            return; // Kết thúc phương thức nếu xảy ra lỗi
        }

        // Gửi yêu cầu lên server
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, orderData,
                response -> {
                    try {
                        // In ra phản hồi từ server để kiểm tra
                        Log.d("Server Response", response.toString());

                        // Kiểm tra phản hồi có phải là JSON không và có chứa order_id không
                        if (response.has("order_id")) {
                            int orderId = response.getInt("order_id");
                            callback.onSuccess(orderId);
                        } else {
                            callback.onError("Không nhận được order_id từ phản hồi. Phản hồi: " + response.toString());
                        }
                    } catch (JSONException e) {
                        callback.onError("Lỗi phân tích phản hồi: " + e.getMessage());
                    }
                },
                error -> {
                    // Xử lý trường hợp lỗi kết nối
                    String errorMessage = "Lỗi kết nối: " + error.getMessage();
                    // Kiểm tra nếu có phản hồi từ server
                    if (error.networkResponse != null) {
                        String responseString = new String(error.networkResponse.data);
                        errorMessage += ". Phản hồi từ server: " + responseString;
                    }
                    callback.onError(errorMessage);
                }
        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Xử lý khi nhận phản hồi từ máy chủ
                String jsonString = new String(response.data);
                Log.d("Raw Response", jsonString); // In ra phản hồi gốc
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException e) {
                    // Nếu không thể chuyển đổi sang JSON, gửi thông báo lỗi
                    return Response.error(new ParseError(e));
                }
            }
        };

        // Thêm yêu cầu vào queue
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    // Callback để xử lý kết quả tạo đơn hàng
    public interface OrderCallback {
        void onSuccess(int orderId);
        void onError(String error);
    }
    public void fetchTicketsOrder(String userId, OrderCallBackList callBack) {
        String url = Utils.ROOT_URL + "/get_orders.php?user_id=" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Order> orderTicket = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject orderJson = response.getJSONObject(i);

                                int fetchedUserId = orderJson.getInt("user_id");
                                int orderId = orderJson.getInt("order_id");
                                int eventId = orderJson.getInt("event_id");
                                int ticketTypeId = orderJson.getInt("ticket_type_id");
                                int quantity = orderJson.getInt("quantity");
                                int paymentMethodId = orderJson.getInt("payment_method_id");
                                String orderDate = orderJson.getString("order_date");
                                double totalAmount = orderJson.getDouble("total_amount");
                                String paymentStatus = orderJson.getString("payment_status");

                                Order order = new Order(orderId, fetchedUserId, eventId, ticketTypeId, quantity, paymentMethodId, orderDate, totalAmount, paymentStatus);

                                orderTicket.add(order);
                            } catch (JSONException e) {
                                Log.e("DataManager", "JSON parsing error at index " + i + ": " + e.getMessage());
                            } catch (Exception e) {
                                Log.e("DataManager", "Unexpected error: " + e.getMessage());
                            }
                        }

                        if (!orderTicket.isEmpty()) {
                            callBack.onSuccess(orderTicket);
                        } else {
                            callBack.onError("No tickets found for user ID: " + userId);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(context, "Error fetching tickets", Toast.LENGTH_SHORT).show();
                        callBack.onError(error.getMessage());
                    }
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
    public interface OrderCallBackList {
        void onSuccess(List<Order> order);
        void onError(String errorMessage);
    }

    public void fetchTickets(String userId, TicketCallBack callBack) {
        String url = Utils.ROOT_URL + "/get_tickets.php?user_id=" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Ticket> fetchedTickets = new ArrayList<>(); // Sử dụng danh sách tạm thời

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject ticketJson = response.getJSONObject(i);
                                int orderDetailId = ticketJson.getInt("order_detail_id");
                                int orderId = ticketJson.getInt("order_id");
                                int eventId = ticketJson.getInt("event_id");
                                int ticketTypeId = ticketJson.getInt("ticket_type_id");
                                int quantity = ticketJson.getInt("quantity");
                                double price = ticketJson.getDouble("price");
                                String createdAtString = ticketJson.getString("created_at");
                                String updatedAtString = ticketJson.getString("updated_at");

                                // Chuyển đổi chuỗi ngày tháng thành Date
                                Date createdAt = dateFormat.parse(createdAtString);
                                Date updatedAt = dateFormat.parse(updatedAtString);

                                Ticket ticket = new Ticket(orderDetailId, orderId, eventId, ticketTypeId, quantity, price, createdAt, updatedAt);
                                fetchedTickets.add(ticket);
                            } catch (JSONException e) {
                                Log.e("DataManager", "JSON parsing error: " + e.getMessage());
                            } catch (Exception e) {
                                Log.e("DataManager", "Date parsing error: " + e.getMessage());
                            }
                        }

                        if (fetchedTickets.isEmpty()) {
                            // Nếu không có vé nào được tìm thấy, gọi onError
                            callBack.onError("No tickets found for user.");
                        } else {
                            callBack.onSuccess(fetchedTickets); // Gọi phương thức onSuccess của TicketCallBack
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error fetching tickets", Toast.LENGTH_SHORT).show();
                        callBack.onError(error.getMessage()); // Gọi phương thức onError của TicketCallBack
                    }
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public interface TicketCallBack {
        void onSuccess(List<Ticket> tickets);
        void onError(String errorMessage);
    }

    public void fetchTicketTypeById(int ticketTypeId, TicketTypeCallback callback) {
        String url = Utils.ROOT_URL + "/get_ticket_user.php?ticket_type_id=" + ticketTypeId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String typeName = response.getString("type_name"); // Giả định rằng phản hồi có chứa type_name
                        callback.onSuccess(typeName);
                    } catch (JSONException e) {
                        callback.onError("Error parsing JSON: " + e.getMessage());
                    }
                },
                error -> callback.onError("Error fetching ticket type: " + error.getMessage())
        );

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public interface TicketTypeCallback {
        void onSuccess(String typeName);
        void onError(String errorMessage);
    }
    // Phương pháp kiểm tra trạng thái chặn
    public void checkUserBlockStatus(int userId, final BlockStatusCallback callback) {
        String url = Utils.ROOT_URL + "/check_block_user.php";

        // Tạo request JSON
        JSONObject params = new JSONObject();
        try {
            params.put("userId", userId);
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }

        // Gửi request POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                String message = response.getString("message");
                                callback.onStatusReceived(message);
                            } else {
                                String errorMessage = response.getString("message");
                                callback.onError(errorMessage);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Response parsing error: " + e.getMessage());
                            callback.onError("Parsing error");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error: " + error.getMessage());
                        callback.onError("Network error");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    public interface BlockStatusCallback {
        void onStatusReceived(String message);

        void onError(String error);
    }

}

