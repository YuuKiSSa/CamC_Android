package iss.workshop.adproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDate;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CameraDetailActivity extends AppCompatActivity {

    private static final String URL = "http://10.0.2.2:8080/api/details/";

    private ImageView cameraImageView;
    private TextView brandTextView;
    private TextView modelTextView;
    private TextView categoryTextView;
    private TextView descriptionTextView;
    private TextView releaseTimeTextView;
    private TextView initialPriceTextView;
    private TextView effectivePixelTextView;
    private TextView isoTextView;
    private TextView focusPointTextView;
    private TextView continuousShotTextView;
    private TextView videoResolutionTextView;
    private TextView videoRateTextView;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_detail);

        // 初始化 ImageView 和 TextView
        cameraImageView = findViewById(R.id.cameraImageView);
        brandTextView = findViewById(R.id.brand);
        modelTextView = findViewById(R.id.model);
        categoryTextView = findViewById(R.id.category);
        descriptionTextView = findViewById(R.id.description);
        releaseTimeTextView = findViewById(R.id.releaseTime);
        initialPriceTextView = findViewById(R.id.initialPrice);
        effectivePixelTextView = findViewById(R.id.effectivePixel);
        isoTextView = findViewById(R.id.iso);
        focusPointTextView = findViewById(R.id.focusPoint);
        continuousShotTextView = findViewById(R.id.continuousShot);
        videoResolutionTextView = findViewById(R.id.videoResolution);
        videoRateTextView = findViewById(R.id.videoRate);

        // 获取传递的数据
        Intent intent = getIntent();
        String cameraId = intent.getStringExtra("cameraId");
        String imageUrl = intent.getStringExtra("imageUrl");

        if (cameraId != null) {
            loadCameraDetail(cameraId);
        }
        if (imageUrl != null) {
            loadImage(imageUrl);
        }
    }

    private void loadCameraDetail(String cameraId) {
        Request request = new Request.Builder()
                .url(URL + cameraId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CameraDetailActivity.this, "Request Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorResponse = response.body().string();
                    runOnUiThread(() -> Toast.makeText(CameraDetailActivity.this, "Unexpected code " + response.code() + ": " + errorResponse, Toast.LENGTH_LONG).show());
                    return;
                }

                String resp = response.body().string();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                        .create();

                CameraDetailDTO cameraDetail = gson.fromJson(resp, CameraDetailDTO.class);

                runOnUiThread(() -> {
                    // 更新 UI
                    brandTextView.setText(cameraDetail.getBrand());
                    modelTextView.setText(cameraDetail.getModel());
                    categoryTextView.setText(cameraDetail.getCategory());
                    descriptionTextView.setText(cameraDetail.getDescription());
                    releaseTimeTextView.setText(cameraDetail.getReleaseTime().toString());
                    initialPriceTextView.setText(String.valueOf(cameraDetail.getInitialPrice()));
                    effectivePixelTextView.setText(String.valueOf(cameraDetail.getEffectivePixel()));
                    isoTextView.setText(String.valueOf(cameraDetail.getISO()));
                    focusPointTextView.setText(cameraDetail.getFocusPoint() != null ? cameraDetail.getFocusPoint().toString() : "N/A");
                    continuousShotTextView.setText(String.valueOf(cameraDetail.getContinuousShot()));
                    videoResolutionTextView.setText(String.valueOf(cameraDetail.getVideoResolution()));
                    videoRateTextView.setText(String.valueOf(cameraDetail.getVideoRate()));
                });
            }
        });
    }

    private void loadImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image) // 预设一个占位符图片
                .into(cameraImageView);
    }
}
