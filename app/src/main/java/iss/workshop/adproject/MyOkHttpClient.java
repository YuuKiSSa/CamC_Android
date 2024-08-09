package iss.workshop.adproject;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyOkHttpClient {
    private static OkHttpClient instance;

    public static OkHttpClient getInstance(Context context) {
        if (instance == null) {
            instance = new OkHttpClient.Builder()
                    .cookieJar(new CustomCookieJar(context))
                    .build();
        }
        return instance;
    }
}
