package iss.workshop.adproject;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomCookieJar implements CookieJar {
    private static final String COOKIE_PREFS = "CookiePrefs";
    private SharedPreferences cookiePrefs;
    private HashSet<String> cookies;

    public CustomCookieJar(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        cookies = (HashSet<String>) cookiePrefs.getStringSet("cookies", new HashSet<>());
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            this.cookies.add(cookie.toString());
        }
        SharedPreferences.Editor editor = cookiePrefs.edit();
        editor.putStringSet("cookies", this.cookies);
        editor.apply();
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        ArrayList<Cookie> result = new ArrayList<>();
        for (String cookieString : cookies) {
            result.add(Cookie.parse(url, cookieString));
        }
        return result;
    }
}
