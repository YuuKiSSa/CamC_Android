package iss.workshop.adproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FavouriteFragment extends Fragment {

    private ListView favouriteListView;
    OkHttpClient client = new OkHttpClient();
    private static final String CAMERA_DETAIL_URL = "http://10.0.2.2:8080/api/details/";

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = MyOkHttpClient.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favouriteListView = view.findViewById(R.id.favouriteListView);

        // Fetch favourites from the server
        fetchFavourites();
    }

    private void fetchFavourites() {

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/favorites-details")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Failed to fetch favourites: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<CameraFavouriteDTO>>() {}.getType();
                    List<CameraFavouriteDTO> favourites = gson.fromJson(responseBody, listType);

                    if (favourites == null || favourites.isEmpty()) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "No favourites found", Toast.LENGTH_LONG).show();
                            });
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                FavoriteAdapter adapter = new FavoriteAdapter(getActivity(), FavouriteFragment.this, favourites);
                                favouriteListView.setAdapter(adapter);
                            });
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Failed to fetch favourites: " + response.message(), Toast.LENGTH_LONG).show();
                            System.err.println("Response code: " + response.code());
                            System.err.println("Response message: " + response.message());
                        });
                    }
                }
            }
        });
    }
}
