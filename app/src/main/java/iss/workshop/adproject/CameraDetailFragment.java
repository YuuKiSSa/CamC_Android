package iss.workshop.adproject;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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

public class CameraDetailFragment extends Fragment {

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
    private Button saveButton;
    private Button moreInfoButton;

    private final List<ReviewDetailDTO> reviewList = new ArrayList<>();

    private TextView minPrice;
    private String platform;
    private OkHttpClient client;
    private boolean isFavorite;
    private List<FavoriteDTO> favoriteList;
    private int error_flag = 0;
    private static final String ARG_CAMERA_ID = "cameraId";
    private static final String ARG_IMAGE_URL = "imageUrl";

    private String cameraId;
    private String imageUrl;
    private CameraDetailDTO cameraDetail;
    public static CameraDetailFragment newInstance(String cameraId, String imageUrl) {
        CameraDetailFragment fragment = new CameraDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CAMERA_ID, cameraId);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cameraId = getArguments().getString(ARG_CAMERA_ID);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = MyOkHttpClient.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_camera_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 UI 组件
        cameraImageView = view.findViewById(R.id.cameraImageView);
        brandTextView = view.findViewById(R.id.brand);
        modelTextView = view.findViewById(R.id.model);
        saveButton = view.findViewById(R.id.save);
        categoryTextView = view.findViewById(R.id.category);
        descriptionTextView = view.findViewById(R.id.description);
        releaseTimeTextView = view.findViewById(R.id.releaseTime);
        initialPriceTextView = view.findViewById(R.id.initialPrice);
        effectivePixelTextView = view.findViewById(R.id.effectivePixel);
        //isoTextView = findViewById(R.id.iso);
//        focusPointTextView = findViewById(R.id.focusPoint);
//        continuousShotTextView = findViewById(R.id.continuousShot);
//        videoResolutionTextView = findViewById(R.id.videoResolution);
//        videoRateTextView = findViewById(R.id.videoRate);
        RecyclerView reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        Button Button1 = view.findViewById(R.id.Button1);
        Button Button2 = view.findViewById(R.id.Button2);
        minPrice = view.findViewById(R.id.minPrice);
        moreInfoButton=view.findViewById(R.id.moreInfoButton);

        // 配置 RecyclerView
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ReviewAdapter reviewAdapter = new ReviewAdapter(reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);

        Button1.setOnClickListener(v -> {
            Intent intent1 = new Intent(getActivity(), ChartActivity.class);
            startActivity(intent1);
        });

        if (cameraId != null) {
            loadCameraDetail(cameraId);
            loadUserReviews(cameraId);
            fetchMinPrice(cameraId);
            checkFavoriteStatus(cameraId);
        }
        if (imageUrl != null) {
            loadImage(imageUrl);
        }

        saveButton.setOnClickListener(view1 -> {
            if (error_flag == 1) {
                Toast.makeText(getActivity(), "Please log in to like products", Toast.LENGTH_LONG).show();
            } else {
                if (isFavorite) {
                    deleteFavorite(cameraId);
                } else {
                    showIdealPriceDialog(cameraId);
                }
            }
        });

        moreInfoButton.setOnClickListener(v -> {
            if (cameraId != null) {
                // 请求相机详细信息
                showCameraDetailDialog(cameraDetail);
            }
        });

        Button2.setOnClickListener(view12 -> {
            if (platform != null && cameraId != null) {
                fetchPriceDetails(cameraId);
            } else {
                Toast.makeText(getActivity(), "Platform or camera ID not loaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPriceDetails(String cameraId) {
        String url = "http://10.0.2.2:8080/api/price/" + cameraId;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to fetch price details", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<PriceDTO>>() {}.getType();
                    List<PriceDTO> priceList = gson.fromJson(responseBody, listType);

                    getActivity().runOnUiThread(() -> handlePlatformAction(priceList, platform));
                }
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Request Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorResponse = response.body().string();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unexpected code " + response.code() + ": " + errorResponse, Toast.LENGTH_LONG).show());
                    return;
                }

                String resp = response.body().string();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                        .create();

                cameraDetail = gson.fromJson(resp, CameraDetailDTO.class);

                getActivity().runOnUiThread(() -> {
                    View rootView = getView();
                    if (rootView != null) {
                        // 更新 UI
                        brandTextView.setText(cameraDetail.getBrand());
                        modelTextView.setText(cameraDetail.getModel());
                        categoryTextView.setText(cameraDetail.getCategory());
                        descriptionTextView.setText(cameraDetail.getDescription());
                        releaseTimeTextView.setText(cameraDetail.getReleaseTime().toString());
                        initialPriceTextView.setText(String.valueOf(cameraDetail.getInitialPrice()));
                        effectivePixelTextView.setText(String.valueOf(cameraDetail.getEffectivePixel()));
                    }
                });
            }
        });
    }

    private void showCameraDetailDialog(CameraDetailDTO cameraDetail) {

        if (cameraDetail != null) {
            CameraDetailDialogFragment fragment = CameraDetailDialogFragment.newInstance(cameraDetail);
            fragment.show(getChildFragmentManager(), "cameraDetailDialog");
        }
    }

    private void loadUserReviews(String cameraId) {
        Request request = new Request.Builder()
                .url(USER_REVIEWS_URL + cameraId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Request Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorResponse = response.body().string();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unexpected code " + response.code() + ": " + errorResponse, Toast.LENGTH_LONG).show());
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

                getActivity().runOnUiThread(() -> {
                    View rootView = getView();
                    if (rootView != null) {
                        RecyclerView recyclerView = rootView.findViewById(R.id.reviewRecyclerView);
                        TextView noReviewsText = rootView.findViewById(R.id.no_reviews_text);
                        if (allReviews.isEmpty()) {
                            noReviewsText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noReviewsText.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            ReviewAdapter adapter = new ReviewAdapter(allReviews);

                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();  // 确保数据更新
                        }
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                // 在UI线程上显示错误信息
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();

                    MinPriceDTO minPriceDTO = gson.fromJson(responseBody, MinPriceDTO.class);

                    // 在UI线程上更新UI
                    getActivity().runOnUiThread(() -> {
                        if (minPriceDTO != null) {
                            platform = minPriceDTO.getPlatform();
                            double price = minPriceDTO.getPrice();
                            minPrice.setText(price + " (" + platform + ")");
                        }
                    });
                }
            }
        });
    }

    private void handlePlatformAction(List<PriceDTO> priceList, String platform) {
        for (PriceDTO priceDTO : priceList) {
            if (platform.equalsIgnoreCase(priceDTO.getPlatform())) {
                if (!priceDTO.getDetails().isEmpty()) {
                    String productLink = priceDTO.getDetails().get(0).getLink(); // 获取商品链接
                    openAppOrWebsite(platform, productLink); // 使用商品链接替换原有平台链接
                } else {
                    Toast.makeText(getContext(), "No product details found for platform: " + platform, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        Toast.makeText(getContext(), "Platform not supported yet", Toast.LENGTH_SHORT).show();
    }

    private void openAppOrWebsite(String platform, String productLink) {
        String packageName;
        switch (platform.toUpperCase()) {
            case "JD":
                packageName = "com.jingdong.app.mall";
                break;
            case "TB":
                packageName = "com.taobao.taobao";
                break;
            case "AMAZON":
                packageName = "com.amazon.mShop.android.shopping";
                break;
            default:
                packageName = null;
                break;
        }

        if (packageName != null && isAppInstalled(packageName)) {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.setData(Uri.parse(productLink));
                Log.d("ProductLink", productLink);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // 如果应用未找到，使用浏览器打开
                    openInBrowser(productLink);
                }
            }
        } else {
            openInBrowser(productLink);
        }
    }

    private void openInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager packageManager = getActivity().getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void checkFavoriteStatus(String cameraId) {
        // 构建URL
        String url = "http://10.0.2.2:8080/api/favorite";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Request Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    error_flag = 1;
                    return;
                }
                error_flag = 0;
                String resp = response.body().string();
                Gson gson = new Gson();
                Type favoriteListType = new TypeToken<List<FavoriteDTO>>(){}.getType();
                favoriteList = gson.fromJson(resp, favoriteListType);

                isFavorite = favoriteList.stream().anyMatch(favorite -> String.valueOf(favorite.getCameraId()).equals(cameraId));
                getActivity().runOnUiThread(() -> updateSaveButton());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateSaveButton() {
        if (isFavorite) {
            saveButton.setText("Dislike");
        } else {
            saveButton.setText("Like");
        }
    }

    private void showIdealPriceDialog(String cameraId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ideal_price, null);
        builder.setView(dialogView);

        EditText editTextIdealPrice = dialogView.findViewById(R.id.editTextIdealPrice);
        Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);

        AlertDialog dialog = builder.create();

        buttonSubmit.setOnClickListener(v -> {
            String idealPriceStr = editTextIdealPrice.getText().toString();
            if (!idealPriceStr.isEmpty()) {
                double idealPrice = Double.parseDouble(idealPriceStr);
                addFavorite(cameraId, idealPrice);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter an ideal price", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void addFavorite(String cameraId, double idealPrice) {
        if (cameraId == null || cameraId.isEmpty()) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Camera ID must not be null", Toast.LENGTH_LONG).show());
            return;
        }

        FavoriteDTO favoriteDTO = new FavoriteDTO(Long.parseLong(cameraId), idealPrice);

        Gson gson = new Gson();
        String json = gson.toJson(favoriteDTO);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(ADD_FAVORITE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to add favorite: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    isFavorite = true;
                    getActivity().runOnUiThread(() -> {
                        updateSaveButton();
                        Toast.makeText(getContext(), "Favorite added successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    String errorResponse = response.body().string();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + errorResponse, Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private void deleteFavorite(String cameraId) {
        // 确保传入的 cameraId 不是空值
        if (cameraId == null || cameraId.isEmpty()) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Camera ID must not be null", Toast.LENGTH_LONG).show());
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to delete favorite: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    isFavorite = false;
                    getActivity().runOnUiThread(() -> updateSaveButton());
                } else {
                    String errorResponse = response.body().string();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + errorResponse, Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}
