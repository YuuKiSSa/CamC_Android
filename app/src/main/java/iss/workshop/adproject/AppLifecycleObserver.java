package iss.workshop.adproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class AppLifecycleObserver implements DefaultLifecycleObserver {
    private static final String TAG = "AppLifecycleObserver";
    private Context context;
    private SharedPreferences sharedPreferences;

    public AppLifecycleObserver(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }
    @Override
    public void onStart(LifecycleOwner owner) {
        // 应用程序启动
        Log.d(TAG, "App is starting");
        logout();
    }

    private void logout() {
        // 本地登出操作
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("username");
        editor.apply();

        // 可以在这里发送网络请求以登出服务器
    }
}
