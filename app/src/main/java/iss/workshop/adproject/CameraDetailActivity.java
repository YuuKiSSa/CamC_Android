package iss.workshop.adproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CameraDetailActivity extends AppCompatActivity {

    private static final String CAMERA_DETAIL_URL = "http://10.0.2.2:8080/api/details/";
    private static final String USER_REVIEWS_URL = "http://10.0.2.2:8080/api/review/";
    private static final String MIN_PRICE_URL = "http://10.0.2.2:8080/api/minPrice/";
    private static final String ADD_FAVORITE_URL = "http://10.0.2.2:8080/api/favorite/add";
    private static final String DELETE_FAVORITE_URL = "http://10.0.2.2:8080/api/favorite/delete";
    private ImageView cameraImageView;
    private TextView brandTextView;
    private TextView modelTextView;
    private TextView categoryTextView;
    private TextView descriptionTextView;
    private TextView releaseTimeTextView;
    private TextView initialPriceTextView;
    private TextView effectivePixelTextView;
    private TextView isoTextView;
    private Button saveButton;
    private TextView focusPointTextView;
    private TextView continuousShotTextView;
    private TextView videoResolutionTextView;
    private TextView videoRateTextView;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<ReviewDetailDTO> reviewList = new ArrayList<>();
    private TextView minPrice;
    private String platform;
    private OkHttpClient client = new OkHttpClient();
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        client = MyOkHttpClient.getInstance(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_detail);

        // 初始化 UI 组件
        cameraImageView = findViewById(R.id.cameraImageView);
        brandTextView = findViewById(R.id.brand);
        modelTextView = findViewById(R.id.model);
        saveButton = findViewById(R.id.save);
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
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        Button Button1 = findViewById(R.id.Button1);
        Button Button2 = findViewById(R.id.Button2);
        minPrice = findViewById(R.id.minPrice);

        // 配置 RecyclerView
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);

        Button1.setOnClickListener(v -> {
            Intent intent1 = new Intent(CameraDetailActivity.this, ChartActivity.class);
            startActivity(intent1);
        });


        // 获取传递的数据
        Intent intent = getIntent();
        String cameraId = intent.getStringExtra("cameraId");
        String imageUrl = intent.getStringExtra("imageUrl");

        if (cameraId != null) {
            loadCameraDetail(cameraId);
            loadUserReviews(cameraId);
            fetchMinPrice(cameraId);
            checkFavoriteStatus(cameraId);
        }
        if (imageUrl != null) {
            loadImage(imageUrl);
        }
        saveButton.setOnClickListener(view -> {
            if (isFavorite) {
                deleteFavorite(cameraId);
            } else {
                addFavorite(cameraId);
            }
        });

        Button2.setOnClickListener(view -> {
            if (platform != null) {
                handlePlatformAction(platform);
            } else {
                Toast.makeText(this, "Platform data not loaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCameraDetail(String cameraId) {
        Request request = new Request.Builder()
                .url(CAMERA_DETAIL_URL + cameraId)
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

    private void loadUserReviews(String cameraId) {
        Request request = new Request.Builder()
                .url(USER_REVIEWS_URL + cameraId)
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
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .create();
                Type listType = new TypeToken<List<UserReviewDTO>>() {}.getType();
                List<UserReviewDTO> userReviews = gson.fromJson(resp, listType);

                // 展开所有的 reviews 到一个列表中
                List<ReviewDetailDTO> allReviews = new ArrayList<>();
                if (userReviews != null) { // 确保 userReviews 不为空
                    for (UserReviewDTO userReview : userReviews) {
                        if (userReview.getReviews() != null) { // 确保每个 userReview 的 reviews 不为空
                            allReviews.addAll(userReview.getReviews());
                        }
                    }
                }

                runOnUiThread(() -> {
                    if (allReviews.isEmpty()) {
                        Toast.makeText(CameraDetailActivity.this, "No reviews found.", Toast.LENGTH_LONG).show();
                    } else {
                        ReviewAdapter adapter = new ReviewAdapter(allReviews);
                        RecyclerView recyclerView = findViewById(R.id.reviewRecyclerView);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();  // 确保数据更新
                    }
                });
            }
        });
    }

    private void loadImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(cameraImageView);
    }

    private void fetchMinPrice(String cameraId) {
        String url = MIN_PRICE_URL + cameraId;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 在UI线程上显示错误信息
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();

                    MinPriceDTO minPriceDTO = gson.fromJson(responseBody, MinPriceDTO.class);

                    // 在UI线程上更新UI
                    runOnUiThread(() -> {
                        if (minPriceDTO != null) {
                            platform = minPriceDTO.getPlatform();
                            double price = minPriceDTO.getPrice();
                            minPrice.setText(price+"("+platform+")");
                        }
                    });
                }
            }
        });
    }
    private void handlePlatformAction(String platform) {
        switch (platform.toUpperCase()) {
            case "JD":
                openAppOrWebsite("com.jingdong.app.mall", "https://www.jd.com");
                break;
            case "TB":
                openAppOrWebsite("com.taobao.taobao", "https://www.taobao.com");
                break;
            case "AMAZON":
                openAppOrWebsite("com.amazon.mShop.android.shopping", "https://www.amazon.com");
                break;
            default:
                Toast.makeText(this, "Platform not supported yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openAppOrWebsite(String packageName, String url) {
        if (isAppInstalled(packageName)) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to open app", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private void checkFavoriteStatus(String cameraId) {
        // 发起请求检查当前相机是否已被收藏（此处假设有相应的 API）
        // 假设 API 返回一个布尔值，表示是否已收藏
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/favorite")
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
                isFavorite = Boolean.parseBoolean(resp);
                runOnUiThread(() -> updateSaveButton());
            }
        });
    }

    private void updateSaveButton() {
        if (isFavorite) {
            saveButton.setText("Dislike");
        } else {
            saveButton.setText("Like");
        }
    }

    private void addFavorite(String cameraId) {
        // 确保传入的 cameraId 不是空值
        if (cameraId == null || cameraId.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(CameraDetailActivity.this, "Camera ID must not be null", Toast.LENGTH_LONG).show());
            return;
        }

        FavoriteDTO favoriteDTO = new FavoriteDTO(Long.parseLong(cameraId), 0.0); // 假设 favoriteDTO 需要一个 cameraId 和 idealPrice

        Gson gson = new Gson();
        String json = gson.toJson(favoriteDTO);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(ADD_FAVORITE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CameraDetailActivity.this, "Failed to add favorite: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    isFavorite = true;
                    runOnUiThread(() -> updateSaveButton());
                } else {
                    String errorResponse = response.body().string();
                    runOnUiThread(() -> Toast.makeText(CameraDetailActivity.this, "Error: " + errorResponse, Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private void deleteFavorite(String cameraId) {
        // 确保传入的 cameraId 不是空值
        if (cameraId == null || cameraId.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(CameraDetailActivity.this, "Camera ID must not be null", Toast.LENGTH_LONG).show());
            return;
        }

        FavoriteDTO favoriteDTO = new FavoriteDTO(Long.parseLong(cameraId), 0.0); // 假设 favoriteDTO 需要一个 cameraId 和 idealPrice

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
                runOnUiThread(() -> Toast.makeText(CameraDetailActivity.this, "Failed to delete favorite: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    isFavorite = false;
                    runOnUiThread(() -> updateSaveButton());
                } else {
                    String errorResponse = response.body().string();
                    runOnUiThread(() -> Toast.makeText(CameraDetailActivity.this, "Error: " + errorResponse, Toast.LENGTH_LONG).show());
                }
            }
        });
    }

}

