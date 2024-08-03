package iss.workshop.adproject;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private static final String URL = "http://10.0.2.2:8080/api/list";
    private OkHttpClient client = new OkHttpClient();
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        listView = view.findViewById(R.id.listView);
        sendGet();
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

                try {
                    // 解析JSON对象
                    JsonObject jsonObject = JsonParser.parseString(resp).getAsJsonObject();
                    // 提取cameras数组
                    String camerasJson = jsonObject.getAsJsonArray("cameras").toString();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<CameraListDTO>>(){}.getType();
                    List<CameraListDTO> cameras = gson.fromJson(camerasJson, listType);

                    getActivity().runOnUiThread(() -> {
                        CameraListAdapter adapter = new CameraListAdapter(getActivity(), cameras);
                        listView.setAdapter(adapter);
                    });
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}
