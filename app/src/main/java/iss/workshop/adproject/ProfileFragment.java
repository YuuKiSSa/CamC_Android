package iss.workshop.adproject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;

import iss.workshop.adproject.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    OkHttpClient client = new OkHttpClient();
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = MyOkHttpClient.getInstance(getContext());
        sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        System.out.println(isLoggedIn);
        if (isLoggedIn) {
            return inflater.inflate(R.layout.fragment_profile_logged_in, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_profile, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            TextView tvUsername = view.findViewById(R.id.tv_username);
            Button btnLogout = view.findViewById(R.id.btn_logout);
            Button favourite = view.findViewById(R.id.favourite);

            String username = sharedPreferences.getString("username", "Guest");
            tvUsername.setText(username);

            favourite.setOnClickListener(v -> {
                FragmentManager fragmentManager = getParentFragmentManager();
                clearFragmentContainer(fragmentManager);
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new FavouriteFragment()).addToBackStack(null).commit();
            });

            btnLogout.setOnClickListener(v -> {
                // 创建 OkHttpClient 实例

                // 创建请求
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/api/logout")
                        .post(RequestBody.create(null, new byte[0])) // 空的 POST 请求体
                        .build();

                // 发送请求
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // 请求失败处理
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Logout Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 请求成功处理
                        if (response.isSuccessful()) {
                            getActivity().runOnUiThread(() -> {
                                // 本地登出操作
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", false);
                                editor.remove("username");
                                editor.apply();

                                // 更新 UI
                                FragmentManager fragmentManager = getParentFragmentManager();
                                clearFragmentContainer(fragmentManager);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

                                Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Logout Failed: " + response.message(), Toast.LENGTH_LONG).show());
                        }
                    }
                });
            });
        } else {
            Button btnLogin = view.findViewById(R.id.btn_login);
            Button btnRegister = view.findViewById(R.id.btn_register);

            btnLogin.setOnClickListener(v -> {
                FragmentManager fragmentManager = getParentFragmentManager();
                clearFragmentContainer(fragmentManager);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new LoginFragment()).addToBackStack(null).commit();
            });
            btnRegister.setOnClickListener(v -> {
                FragmentManager fragmentManager = getParentFragmentManager();
                clearFragmentContainer(fragmentManager);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).addToBackStack(null).commit();
            });
        }

    }

    private void clearFragmentContainer(FragmentManager fragmentManager) {
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }
}
