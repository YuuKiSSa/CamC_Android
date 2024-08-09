package iss.workshop.adproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FavoriteAdapter extends ArrayAdapter<CameraFavouriteDTO> {

    private Context context;
    private List<CameraFavouriteDTO> favoriteList;
    private OkHttpClient client = new OkHttpClient();
    private static final String DELETE_FAVORITE_URL = "http://10.0.2.2:8080/api/favorite/delete";
    private FavouriteFragment fragment;

    public FavoriteAdapter(@NonNull Context context, FavouriteFragment fragment, List<CameraFavouriteDTO> favoriteList) {
        super(context, R.layout.item_favorite, favoriteList);
        this.context = context;
        this.favoriteList = favoriteList;
        this.fragment = fragment;
        this.client = MyOkHttpClient.getInstance(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        }

        CameraFavouriteDTO favorite = favoriteList.get(position);

        ImageView imageView = convertView.findViewById(R.id.camera_image);
        TextView brandView = convertView.findViewById(R.id.camera_brand);
        TextView modelView = convertView.findViewById(R.id.camera_model);
        TextView priceView = convertView.findViewById(R.id.camera_price);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        Picasso.get()
                .load(favorite.getImageUrl())
                .resize(100, 100)
                .centerCrop()
                .into(imageView);

        brandView.setText(favorite.getBrand().toString());
        modelView.setText(favorite.getModel());
        priceView.setText(String.format("￥%.2f", favorite.getIdealPrice()));

        deleteButton.setOnClickListener(v -> {
            deleteFavorite(favorite.getId(), position);
        });

        return convertView;
    }

    private void deleteFavorite(Long cameraId, int position) {
        // 确保传入的 cameraId 不是空值
        if (cameraId == null) {
            fragment.getActivity().runOnUiThread(() -> Toast.makeText(context, "Camera ID must not be null", Toast.LENGTH_LONG).show());
            return;
        }

        FavoriteDTO favoriteDTO = new FavoriteDTO(cameraId, 0.0);

        Gson gson = new Gson();
        String json = gson.toJson(favoriteDTO);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(DELETE_FAVORITE_URL)
                .delete(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context != null) {
                    fragment.getActivity().runOnUiThread(() -> Toast.makeText(context, "Failed to delete favorite: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (context != null) {
                        fragment.getActivity().runOnUiThread(() -> {
                            favoriteList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    if (context != null) {
                        fragment.getActivity().runOnUiThread(() -> Toast.makeText(context, "Failed to delete favorite: " + response.message(), Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }
}
