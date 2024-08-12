package iss.workshop.adproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    private Context context;
    private List<CameraListDTO> cameraList;

    public LikeAdapter(Context context, List<CameraListDTO> cameraList) {
        this.context = context;
        this.cameraList = cameraList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CameraListDTO camera = cameraList.get(position);

        Picasso.get()
                .load(camera.getImageUrl())
                .resize(100, 100)
                .centerCrop()
                .into(holder.imageView);

        holder.brandView.setText(camera.getBrand().toString());
        holder.modelView.setText(camera.getModel());
        holder.priceView.setText(String.format("￥%.2f", camera.getInitialPrice()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("cameraId", String.valueOf(camera.getId()));
            intent.putExtra("imageUrl", camera.getImageUrl());
            intent.setAction("SHOW_CAMERA_DETAIL");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cameraList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView priceView;
        TextView brandView;
        TextView modelView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.camera_image);
            priceView = itemView.findViewById(R.id.camera_price);
            brandView = itemView.findViewById(R.id.camera_brand);
            modelView = itemView.findViewById(R.id.camera_model);
        }
    }
}
