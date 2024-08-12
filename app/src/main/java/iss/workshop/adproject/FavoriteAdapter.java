package iss.workshop.adproject;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FavoriteAdapter extends ArrayAdapter<CameraFavouriteDTO> {

    private Context context;
    private List<CameraFavouriteDTO> favoriteList;
    private OkHttpClient client = new OkHttpClient();
    private static final String DELETE_FAVORITE_URL = "http://10.0.2.2:8080/api/favorite/delete";
    private FavouriteFragment fragment;
    private static final String CHANNEL_ID = "price_alert_channel";

    public FavoriteAdapter(@NonNull Context context, FavouriteFragment fragment, List<CameraFavouriteDTO> favoriteList) {
        super(context, R.layout.item_favorite, favoriteList);
        this.context = context;
        this.favoriteList = favoriteList;
        this.fragment = fragment;
        this.client = MyOkHttpClient.getInstance(context);
        NotificationHelper.createNotificationChannel(context); // 创建通知通道
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        }

        CameraFavouriteDTO favorite = favoriteList.get(position);

        ImageView imageView = convertView.findViewById(R.id.camera_image);
        TextView brandView = convertView.findViewById(R.id.camera_brand);
        TextView modelView = convertView.findViewById(R.id.camera_model);
        TextView priceView = convertView.findViewById(R.id.camera_price);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        Picasso.get()
                .load(favorite.getImageUrl())
                .resize(100, 100)
                .centerCrop()
                .into(imageView);

        brandView.setText(favorite.getBrand().toString());
        modelView.setText(favorite.getModel());
        priceView.setText(String.format("￥%.2f", favorite.getIdealPrice()));

        // 价格检查
        if (favorite.getIdealPrice() >= favorite.getLatestPrice()) {
            sendPriceAlertNotification(favorite);
        }

        deleteButton.setOnClickListener(v -> {
            deleteFavorite(favorite.getId(), position);
        });

        return convertView;
    }
    @SuppressLint("MissingPermission")
    private void sendPriceAlertNotification(CameraFavouriteDTO favorite) {
        if (isNotificationSent(favorite.getId())) {
            return; // 如果通知已经发送，则不再发送
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("cameraId", String.valueOf(favorite.getId()));
        intent.putExtra("imageUrl", favorite.getImageUrl());
        intent.setAction("SHOW_CAMERA_DETAIL");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_camera) // 请确保在res/drawable中有这个图标
                .setContentTitle("Price Alert")
                .setContentText(favorite.getBrand().toString() + " " + favorite.getModel() + " is now within your ideal price!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(String.valueOf(favorite.getId()).hashCode(), builder.build());

        markNotificationAsSent(favorite.getId()); // 标记通知已发送
    }

    private void deleteFavorite(Long cameraId, int position) {
        // 确保传入的 cameraId 不是空值
        if (cameraId == null) {
            fragment.getActivity().runOnUiThread(() -> Toast.makeText(context, "Camera ID must not be null", Toast.LENGTH_LONG).show());
            return;
        }

        FavoriteDTO favoriteDTO = new FavoriteDTO(cameraId, 0.0);

        Gson gson = new Gson();
        String json = gson.toJson(favoriteDTO);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(DELETE_FAVORITE_URL)
                .delete(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context != null) {
                    fragment.getActivity().runOnUiThread(() -> Toast.makeText(context, "Failed to delete favorite: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (context != null) {
                        fragment.getActivity().runOnUiThread(() -> {
                            favoriteList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    if (context != null) {
                        fragment.getActivity().runOnUiThread(() -> Toast.makeText(context, "Failed to delete favorite: " + response.message(), Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }
    private boolean isNotificationSent(Long cameraId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(String.valueOf(cameraId), false);
    }

    private void markNotificationAsSent(Long cameraId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(String.valueOf(cameraId), true);
        editor.apply();
    }

}
