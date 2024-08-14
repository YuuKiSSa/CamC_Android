package iss.workshop.adproject;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ProcessLifecycleOwner;


import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity{
    private List<String> itemList;
    private static final String URL_LOGIN = "http://13.213.1.218/api/login";
    private DrawerLayout drawerLayout;
    private EditText minPrice, maxPrice;
    private CheckBox brandCanon, brandSony, brandNikon, tagLandscape, tagPortrait, tagSports;
    private Button applyFilterButton;
    private AppLifecycleObserver appLifecycleObserver;
    OkHttpClient client = new OkHttpClient();
    SharedPreferences sharedPreferences;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        client = MyOkHttpClient.getInstance(getApplicationContext());

        drawerLayout = findViewById(R.id.drawer_layout);
        TextView navHome = findViewById(R.id.nav_home);
        TextView navCamera = findViewById(R.id.nav_camera);
        TextView navProfile = findViewById(R.id.nav_profile);
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        minPrice = findViewById(R.id.min_price);
        maxPrice = findViewById(R.id.max_price);
        brandCanon = findViewById(R.id.brand_canon);
        brandSony = findViewById(R.id.brand_sony);
        brandNikon = findViewById(R.id.brand_nikon);
        tagLandscape = findViewById(R.id.tag_landscape);
        tagPortrait = findViewById(R.id.tag_portrait);
        tagSports = findViewById(R.id.tag_sports);
        applyFilterButton = findViewById(R.id.apply_filter_button);
        applyFilterButton.setOnClickListener(v -> {
            applyFilters();
            hideKeyboard();
        });
        if (isLoggedIn) {
            String username=sharedPreferences.getString("username", null);
            String password=sharedPreferences.getString("password", null);
            login(username,password);
        }
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver(this));
        navHome.setOnClickListener(view -> switchFragment(new HomeFragment()));
        navCamera.setOnClickListener(view -> switchFragment(new CameraFragment()));
        navProfile.setOnClickListener(view -> switchFragment(new ProfileFragment()));

        if (getIntent() != null && "SHOW_CAMERA_DETAIL".equals(getIntent().getAction())) {
            String cameraId = getIntent().getStringExtra("cameraId");
            String imageUrl = getIntent().getStringExtra("imageUrl");

            if (cameraId != null && imageUrl != null) {
                showCameraDetailFragment(cameraId, imageUrl);
            }
        }

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        }

        handleIntent(getIntent());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && "SHOW_CAMERA_DETAIL".equals(intent.getAction())) {
            String cameraId = intent.getStringExtra("cameraId");
            String imageUrl = intent.getStringExtra("imageUrl");

            if (cameraId != null && imageUrl != null) {
                showCameraDetailFragment(cameraId, imageUrl);
            }
        }
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        try {
                            String responseBody = response.body().string();
                            Log.d("LoginResponse", "Response Body: " + responseBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void showCameraDetailFragment(String cameraId, String imageUrl) {
        CameraDetailFragment fragment = CameraDetailFragment.newInstance(cameraId, imageUrl);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 清除frame_layout中的所有Fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment != null) {
            transaction.remove(currentFragment);
        }

        // 添加新的CameraDetailFragment
        transaction.replace(R.id.frame_layout, fragment)
                .addToBackStack(null) // 添加到返回栈中
                .commit();
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        clearFragmentContainer(fragmentManager);  // 清理先前的 Fragment
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                //.addToBackStack(null)  // 确保事务被添加到返回栈中
                .commit();
    }

    private void clearFragmentContainer(FragmentManager fragmentManager) {
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    private void launchCameraDetailFragment(String cameraId, String imageUrl) {
        CameraDetailFragment cameraDetailFragment = CameraDetailFragment.newInstance(cameraId, imageUrl);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cameraDetailFragment)
                .addToBackStack(null) // 添加到返回栈中
                .commit();
    }

    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    private void applyFilters() {
        // 获取筛选条件
        String minPriceStr = minPrice.getText().toString();
        String maxPriceStr = maxPrice.getText().toString();
        boolean isCanonChecked = brandCanon.isChecked();
        boolean isSonyChecked = brandSony.isChecked();
        boolean isNikonChecked = brandNikon.isChecked();
        boolean isLandscapeChecked = tagLandscape.isChecked();
        boolean isPortraitChecked = tagPortrait.isChecked();
        boolean isSportsChecked = tagSports.isChecked();

        // 通知 CameraFragment 过滤条件
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (fragment instanceof CameraFragment) {
            ((CameraFragment) fragment).applyFilters(minPriceStr, maxPriceStr, isCanonChecked, isSonyChecked, isNikonChecked, isLandscapeChecked, isPortraitChecked, isSportsChecked);
        }

        // 关闭侧边菜单
        closeDrawer();
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}