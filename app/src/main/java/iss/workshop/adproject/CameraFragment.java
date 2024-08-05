package iss.workshop.adproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CameraFragment extends Fragment {
    private EditText searchBar;
    private static final String TAG = "CameraFragment";
    CameraListAdapter adapter;
    private ImageView filterIcon;
    private DrawerLayout drawerLayout;
    private static final String URL = "http://10.0.2.2:8080/api/list-tag";
    private OkHttpClient client = new OkHttpClient();
    private ListView listView;
    private Button searchButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        listView = view.findViewById(R.id.listView);
        sendGet();
        filterIcon = view.findViewById(R.id.filter_icon);
        searchBar = view.findViewById(R.id.search_bar);
        searchButton = view.findViewById(R.id.search_button);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CameraListDTO selectedCamera = (CameraListDTO) parent.getItemAtPosition(position);
                String cameraId = String.valueOf(selectedCamera.getId());
                String imageUrl= selectedCamera.getImageUrl();
                // 启动 CameraDetailActivity
                Intent intent = new Intent(getActivity(), CameraDetailActivity.class);
                intent.putExtra("cameraId", cameraId);
                intent.putExtra("imageUrl",imageUrl);
                startActivity(intent);
            }
        });

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.requestFocus();
                // 延迟显示下拉框
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 触发搜索
                        String query = searchBar.getText().toString();
                        adapter.getFilter().filter(query);
                    }
                }, 200); // 延迟200毫秒，确保键盘已经弹出
            }
        });

        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 延迟显示下拉框
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 触发搜索
                            String query = searchBar.getText().toString();
                            adapter.getFilter().filter(query);
                        }
                    }, 200); // 延迟200毫秒，确保键盘已经弹出
                } else {
                    // 隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        searchButton.setOnClickListener(v -> {
            // 获取搜索栏的文本
            String query = searchBar.getText().toString();
            // 设置搜索查询
            adapter.setSearchQuery(query);
            // 过滤列表
            adapter.getFilter().filter("");

            // 隐藏键盘
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            searchBar.clearFocus(); // 清除焦点
        });


        filterIcon.setOnClickListener(v -> {
            // 通过 MainActivity 打开筛选菜单
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        return view;
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
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Request Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorResponse = response.body().string();
                    int statusCode = response.code();
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Unexpected code " + statusCode + ": " + errorResponse, Toast.LENGTH_LONG).show());
                    return;
                }

                String resp = response.body().string();
                Log.d(TAG, "Response JSON: " + resp); // 打印服务器返回的 JSON 数据

                try {
                    // 解析JSON对象
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<CameraListDTO>>(){}.getType();
                    List<CameraListDTO> cameras = gson.fromJson(resp, listType);

                    getActivity().runOnUiThread(() -> {
                        adapter = new CameraListAdapter(getActivity(), cameras);
                        listView.setAdapter(adapter);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "JSON Parsing Error", e);
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
    public void applyFilters(String minPriceStr, String maxPriceStr, boolean isCanonChecked, boolean isSonyChecked, boolean isNikonChecked, boolean isLandscapeChecked, boolean isPortraitChecked, boolean isSportsChecked) {
        if (adapter != null) {
            adapter.setFilterOptions(minPriceStr, maxPriceStr, isCanonChecked, isSonyChecked, isNikonChecked, isLandscapeChecked, isPortraitChecked, isSportsChecked);
            adapter.getFilter().filter("");
        }
    }

}
