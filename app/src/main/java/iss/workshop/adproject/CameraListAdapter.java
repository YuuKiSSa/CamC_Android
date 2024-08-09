package iss.workshop.adproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CameraListAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<CameraListDTO> cameras;
    private LayoutInflater inflater;
    private List<CameraListDTO> filteredCameras;

    private String minPriceStr = "";
    private String maxPriceStr = "";
    private boolean isCanonChecked = false;
    private boolean isSonyChecked = false;
    private boolean isNikonChecked = false;
    private boolean isLandscapeChecked = false;
    private boolean isPortraitChecked = false;
    private boolean isSportsChecked = false;
    private String searchQuery = "";

    public CameraListAdapter(Context context, List<CameraListDTO> cameras) {
        this.context = context;
        this.cameras = cameras;
        this.filteredCameras = new ArrayList<>(cameras);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredCameras.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredCameras.get(position);
    }

    @Override
    public long getItemId(int position) {
        return filteredCameras.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_camera, parent, false);
        }

        CameraListDTO camera = filteredCameras.get(position);

        TextView brandView = convertView.findViewById(R.id.camera_brand);
        TextView modelView = convertView.findViewById(R.id.camera_model);
        TextView latestPriceView = convertView.findViewById(R.id.camera_price);
        ImageView imageView = convertView.findViewById(R.id.camera_image);

        brandView.setText(camera.getBrand().name());
        modelView.setText(camera.getModel());
        latestPriceView.setText(String.format("Latest: ￥%.2f", camera.getLatestPrice()));

        // 使用 Picasso 加载图像
        Picasso.get()
                .load(camera.getImageUrl())
                .resize(200, 200) // 设置图像大小
                .centerCrop() // 图像填充 ImageView
                .into(imageView);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new CameraFilter();
    }

    public void setFilterOptions(String minPriceStr, String maxPriceStr, boolean isCanonChecked, boolean isSonyChecked, boolean isNikonChecked, boolean isLandscapeChecked, boolean isPortraitChecked, boolean isSportsChecked) {
        this.minPriceStr = minPriceStr;
        this.maxPriceStr = maxPriceStr;
        this.isCanonChecked = isCanonChecked;
        this.isSonyChecked = isSonyChecked;
        this.isNikonChecked = isNikonChecked;
        this.isLandscapeChecked = isLandscapeChecked;
        this.isPortraitChecked = isPortraitChecked;
        this.isSportsChecked = isSportsChecked;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    private class CameraFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CameraListDTO> filteredList = new ArrayList<>();

            for (CameraListDTO camera : cameras) {
                boolean matches = true;

                // 价格筛选
                if (!minPriceStr.isEmpty()) {
                    double min = Double.parseDouble(minPriceStr);
                    if (camera.getLatestPrice() < min) matches = false;
                }
                if (!maxPriceStr.isEmpty()) {
                    double max = Double.parseDouble(maxPriceStr);
                    if (camera.getLatestPrice() > max) matches = false;
                }

                // 品牌筛选
                if (isCanonChecked && camera.getBrand() != Brand.Canon) matches = false;
                if (isSonyChecked && camera.getBrand() != Brand.Sony) matches = false;
                if (isNikonChecked && camera.getBrand() != Brand.Nikon) matches = false;

                // 标签筛选
                if (isLandscapeChecked && !camera.getTags().contains("Landscape")) matches = false;
                if (isPortraitChecked && !camera.getTags().contains("Portrait")) matches = false;
                if (isSportsChecked && !camera.getTags().contains("Sports")) matches = false;

                // 搜索过滤
                if (!searchQuery.isEmpty() && !(camera.getBrand().name().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        camera.getModel().toLowerCase().contains(searchQuery.toLowerCase()))) {
                    matches = false;
                }

                if (matches) {
                    filteredList.add(camera);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredCameras.clear();
            filteredCameras.addAll((List<CameraListDTO>) results.values);
            notifyDataSetChanged();
        }
    }
}
