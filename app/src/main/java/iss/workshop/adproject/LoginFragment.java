package iss.workshop.adproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginFragment extends Fragment {
    private static final String URL_LOGIN = "http://10.0.2.2:8080/api/login";
    private static final String CHANNEL_ID = "camera_notification_channel";
    private static final String URL = "http://10.0.2.2:8080/api/cameras/most-preferred";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = MyOkHttpClient.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            login(username, password);
        });

        // 处理返回按钮
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .addToBackStack(null) // 添加到返回栈中
                        .commit();
            }
        });
        // 创建通知渠道
        createNotificationChannel();

    }

    private void login(String username, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(URL_LOGIN)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (isAdded()) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                String responseBody = response.body().string();
                                Log.d("LoginResponse", "Response Body: " + responseBody);

                                if (responseBody.contains("Logged in successfully")) {
                                    // 保存登录状态和token
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.putString("username", username);
                                    editor.putString("password", password);
                                    editor.apply();

                                    Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    sendGetMostPreferredCamera();
                                    // 替换当前的 Fragment 为 ProfileFragment
                                    FragmentManager fragmentManager = getParentFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.fragment_container, new ProfileFragment())
                                            .addToBackStack(null) // 添加到返回栈中
                                            .commit();
                                }  else {
                                    throw new IllegalStateException("Unexpected response: " + responseBody);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } else {
                    if (isAdded()) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Login Failed: " + response.message(), Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }

    private void sendGetMostPreferredCamera() {
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("NetworkRequest", "Request Failed: " + e.getMessage());
                if (isAdded()) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Request Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorResponse = response.body().string();
                    int statusCode = response.code();
                    Log.e("NetworkRequest", "Unexpected code " + statusCode + ": " + errorResponse);
                    if (isAdded()) {
                        getActivity().runOnUiThread(() -> Log.e("NetworkRequest", "Unexpected code " + statusCode + ": " + errorResponse));
                    }
                    return;
                }

                String resp = response.body().string();
                Log.d("NetworkRequest", "Response: " + resp);

                // 解析JSON响应为CameraDTO对象
                Gson gson = new Gson();
                CameraDTO camera = gson.fromJson(resp, CameraDTO.class);
                System.out.println(resp);
                System.out.println(camera);

                getActivity().runOnUiThread(() -> sendNotification(camera));

            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Camera Notification";
            String description = "Notification for most preferred camera";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(CameraDTO camera) {
        Log.d("Notification", "Sending notification for camera: " + camera.toString());

        new Thread(() -> {
            Bitmap bitmap = getBitmapFromURL(camera.getImageUrl());

            // 创建点击通知时的Intent
            Intent intent = new Intent(getActivity(), CameraDetailActivity.class);
            intent.putExtra("cameraId", String.valueOf(camera.getId()));
            intent.putExtra("imageUrl", camera.getImageUrl());
            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // 创建通知
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_camera) // 请确保在res/drawable中有这个图标
                    .setContentTitle("You may prefer")
                    .setContentText(camera.getBrand()+" "+camera.getModel())
                    .setLargeIcon(Bitmap.createScaledBitmap(bitmap, 100, 100, false))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent) // 设置点击通知的Intent
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND); // 点击后自动取消通知

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
            notificationManager.notify(1, builder.build());

            Log.d("Notification", "Notification sent.");
        }).start();
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            Log.d("Notification", "Fetching bitmap from URL: " + src);
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Notification", "Error fetching bitmap: " + e.getMessage());
            return null;
        }
    }
}