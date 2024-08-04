package iss.workshop.adproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CameraListAdapter extends BaseAdapter {
    private Context context;
    private List<CameraListDTO> cameras;
    private LayoutInflater inflater;

    public CameraListAdapter(Context context, List<CameraListDTO> cameras) {
        this.context = context;
        this.cameras = cameras;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cameras.size();
    }

    @Override
    public Object getItem(int position) {
        return cameras.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cameras.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_camera, parent, false);
        }

        CameraListDTO camera = cameras.get(position);

        TextView brandView = convertView.findViewById(R.id.camera_brand);
        TextView modelView = convertView.findViewById(R.id.camera_model);
        TextView priceView = convertView.findViewById(R.id.camera_price);
        ImageView imageView = convertView.findViewById(R.id.camera_image);

        brandView.setText(camera.getBrand().toString()); // 假设Brand类有一个getName方法
        modelView.setText(camera.getModel());
        priceView.setText(String.format("from "+"￥%.2f", camera.getInitialPrice()));

        // 使用 Picasso 加载图像
        Picasso.get()
                .load(camera.getImageUrl())
                .resize(200, 200) // 设置图像大小
                .centerCrop() // 图像填充 ImageView
                .into(imageView);

        return convertView;
    }
}
