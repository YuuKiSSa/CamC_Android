package iss.workshop.adproject;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HomeFragment extends Fragment {
    private static final String URL = "http://10.0.2.2:8080/api/you-may-like";
    private static final String URL1 = "http://10.0.2.2:8080/api/list-tag";
    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private RecyclerView precyclerView;
    private OkHttpClient client = new OkHttpClient();
    private LikeAdapter likeAdapter;
    private LikeAdapter plikeAdapter;
    private List<CameraListDTO> cameraList;
    private SharedPreferences sharedPreferences;
    private List<CameraListDTO> popular;
    private TextView youMayLikeText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = MyOkHttpClient.getInstance(getContext());
 //       sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        precyclerView = view.findViewById(R.id.recycler_popular);
        precyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        sendGetPop();
        return view;
    }
//    private boolean isUserLoggedIn() {
//        System.out.println(sharedPreferences.getBoolean("isLoggedIn", false));
//        return sharedPreferences.getBoolean("isLoggedIn", false); // 根据实际情况修改键值
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (!isUserLoggedIn()) {
//            logout();
//        }
//    }
//    private void logout() {
//        // 本地登出操作
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("isLoggedIn", false);
//        editor.remove("username");
//        editor.apply();
//
//        // 创建请求
//        Request request = new Request.Builder()
//                .url("http://10.0.2.2:8080/api/logout")
//                .post(RequestBody.create(null, new byte[0])) // 空的 POST 请求体
//                .build();
//
//        // 发送请求
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // 请求失败处理
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                // 请求成功处理
//                if (!response.isSuccessful()) {
//                    // 处理登出失败情况
//                }
//            }
//        });
//    }

    private void sendGetPop() {
        Request request = new Request.Builder()
                .url(URL1)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (getActivity() != null) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Request Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
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
                    popular = gson.fromJson(resp, listType);

                    getActivity().runOnUiThread(() -> {
                        plikeAdapter = new LikeAdapter(getActivity(), popular);
                        precyclerView.setAdapter(plikeAdapter);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "JSON Parsing Error", e);
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    // 获取存储的身份验证令牌
    private String getAuthToken() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("authToken", ""); // 根据实际情况修改键值
    }
}