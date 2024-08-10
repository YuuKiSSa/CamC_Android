package iss.workshop.adproject;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterFragment extends Fragment {

    private static final String REGISTER_URL = "http://10.0.2.2:8080/api/register";
    public RegisterFragment() {
        // Required empty public constructor
    }

        private EditText etUsername;
        private EditText etPassword;
        private EditText etConfirmPassword;
        private Button btnRegister;
        private OkHttpClient client = new OkHttpClient();

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_register, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            etUsername = view.findViewById(R.id.et_username);
            etPassword = view.findViewById(R.id.et_password);
            etConfirmPassword = view.findViewById(R.id.et_confirm_password);
            btnRegister = view.findViewById(R.id.btn_register);
            // 处理返回按钮
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment())
                            .addToBackStack(null) // 添加到返回栈中
                            .commit();
                }
            });
            btnRegister.setOnClickListener(v -> {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                register(username, password);
            });
        }

        private void register(String username, String password) {
            String url = REGISTER_URL;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("password", password);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    if (isAdded()) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Registration Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if (isAdded()) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();

                                // 返回到登录界面
                                // 处理返回按钮
                                requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                                    @Override
                                    public void handleOnBackPressed() {
                                        FragmentManager fragmentManager = getParentFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.fragment_container, new ProfileFragment())
                                                .addToBackStack(null) // 添加到返回栈中
                                                .commit();
                                    }
                                });
                            });
                        }
                    } else {
                        if (isAdded()) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Registration Failed: " + response.message(), Toast.LENGTH_LONG).show());
                        }
                    }
                }
            });
        }
    }