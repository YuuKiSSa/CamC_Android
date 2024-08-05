package iss.workshop.adproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView userNameTextView;
    private TextView userIdTextView;
    private RecyclerView settingsRecyclerView;

    private TextView couponsCountTextView;
    private TextView footprintCountTextView;
    private TextView historyCountTextView;
    private TextView likesCountTextView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        //profileImageView = view.findViewById(R.id.profileImageView);
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userIdTextView = view.findViewById(R.id.userIdTextView);
        settingsRecyclerView = view.findViewById(R.id.settingsRecyclerView);

        couponsCountTextView = view.findViewById(R.id.couponsCountTextView);
        footprintCountTextView = view.findViewById(R.id.footprintCountTextView);
        historyCountTextView = view.findViewById(R.id.historyCountTextView);
        likesCountTextView = view.findViewById(R.id.likesCountTextView);

        // Load user data
        loadUserData();

        // Setup RecyclerView
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsRecyclerView.setAdapter(new SettingsAdapter(getSettingItems(), this::onSettingItemClick));


        return view;
    }

    private void loadUserData() {
        // 假设这里加载用户数据，后面可以从 ViewModel 或者网络获取
        userNameTextView.setText("Loopy");
        userIdTextView.setText("ID:22369874");

        couponsCountTextView.setText("5");
        footprintCountTextView.setText("268");
        historyCountTextView.setText("78");
        likesCountTextView.setText("136");
    }

    private List<SettingItem> getSettingItems() {
        List<SettingItem> items = new ArrayList<>();
        items.add(new SettingItem("Problem Feedback"));
        items.add(new SettingItem("Help Center"));
        items.add(new SettingItem("Settings"));
        return items;
    }

    private void onSettingItemClick(SettingItem item) {
        Toast.makeText(getContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        // 处理设置项点击事件
        switch (item.getTitle()) {
            case "Problem Feedback":
                // 跳转到问题反馈页面
                break;
            case "Help Center":
                // 跳转到帮助中心页面
                break;
            case "Settings":
                // 跳转到设置页面
                break;
        }
    }



}
