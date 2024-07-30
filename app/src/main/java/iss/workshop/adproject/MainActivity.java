package iss.workshop.adproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "http://10.0.2.2:8090/api/cameras/most-preferred/camera-id/4";
    private static final String CHANNEL_ID = "camera_notification_channel";
    OkHttpClient client = new OkHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建通知渠道
        createNotificationChannel();

        // 发送 GET 请求
        sendGet();
    }

    private void sendGet() {
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("NetworkRequest", "Request Failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Request Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorResponse = response.body().string();
                    int statusCode = response.code();
                    Log.e("NetworkRequest", "Unexpected code " + statusCode + ": " + errorResponse);
                    runOnUiThread(() -> Log.e("NetworkRequest", "Unexpected code " + statusCode + ": " + errorResponse));
                    return;
                }

                String resp = response.body().string();
                Log.d("NetworkRequest", "Response: " + resp);

                runOnUiThread(() -> sendNotification("Camera Info", "You May Interested in "+resp));
            }
        });
    }
            private void createNotificationChannel() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "Camera Notification";
                    String description = "Channel for Camera Notifications";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
            }

            @SuppressLint("MissingPermission")
            private void sendNotification(String title, String message) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_camera) // 请确保在res/drawable中有这个图标
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(1, builder.build());
            }

}
