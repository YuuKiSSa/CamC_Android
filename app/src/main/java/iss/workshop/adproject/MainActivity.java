package iss.workshop.adproject;


import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import java.util.List;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity{
    private List<String> itemList;
    private DrawerLayout drawerLayout;
    private EditText minPrice, maxPrice;
    private CheckBox brandCanon, brandSony, brandNikon, tagLandscape, tagPortrait, tagSports;
    private Button applyFilterButton;
    OkHttpClient client = new OkHttpClient();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        TextView navHome = findViewById(R.id.nav_home);
        TextView navCamera = findViewById(R.id.nav_camera);
        TextView navProfile = findViewById(R.id.nav_profile);

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

        navHome.setOnClickListener(view -> switchFragment(new HomeFragment()));
        navCamera.setOnClickListener(view -> switchFragment(new CameraFragment()));
        navProfile.setOnClickListener(view -> switchFragment(new ProfileFragment()));

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        clearFragmentContainer(fragmentManager);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
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
    private void clearFragmentContainer(FragmentManager fragmentManager) {
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }
}