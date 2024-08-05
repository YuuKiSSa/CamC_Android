package iss.workshop.adproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    private List<SettingItem> settingItemList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(SettingItem item);
    }

    public SettingsAdapter(List<SettingItem> settingItemList, OnItemClickListener listener) {
        this.settingItemList = settingItemList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        SettingItem item = settingItemList.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return settingItemList.size();
    }

    static class SettingsViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.settingTitleTextView);
        }

        public void bind(final SettingItem item, final OnItemClickListener listener) {
            titleTextView.setText(item.getTitle());
            itemView.setOnClickListener(view -> listener.onItemClick(item));
        }
    }
}
