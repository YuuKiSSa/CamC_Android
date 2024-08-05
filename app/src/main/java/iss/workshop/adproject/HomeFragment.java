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

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    private List<CameraListDTO> popular;
    private TextView youMayLikeText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        youMayLikeText = view.findViewById(R.id.you_may_like);
//        recyclerView = view.findViewById(R.id.recycler_view);
        precyclerView = view.findViewById(R.id.recycler_popular);
        precyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
 //       recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
 //       youMayLikeText = view.findViewById(R.id.you_may_like);

        sendGetPop();
//        plikeAdapter = new LikeAdapter(getContext(), popular);
//        precyclerView.setAdapter(plikeAdapter);
//        recyclerView.setVisibility(View.VISIBLE);


//        if (isUserLoggedIn()) {
//            sendGet();
//            likeAdapter = new LikeAdapter(getContext(), cameraList);
//            recyclerView.setAdapter(likeAdapter);
//            youMayLikeText.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.VISIBLE);
//        } else {
//            youMayLikeText.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.GONE);
//        }
        return view;
    }
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false); // 根据实际情况修改键值
    }

    private void sendGet() {
        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", "Bearer " + getAuthToken()) // 添加身份验证头
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
                    cameraList = gson.fromJson(resp, listType);

                    getActivity().runOnUiThread(() -> {
                        likeAdapter = new LikeAdapter(getActivity(), cameraList);
                        recyclerView.setAdapter(likeAdapter);
                        likeAdapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    Log.e(TAG, "JSON Parsing Error", e);
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
    private void sendGetPop() {
        Request request = new Request.Builder()
                .url(URL1)
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