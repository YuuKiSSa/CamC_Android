package iss.workshop.adproject;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChartFragment extends Fragment {
    private WebView webView1;
    private WebView webView2;
    private static final String ARG_CAMERA_ID = "CAMERA_ID";

    public static ChartFragment newInstance(Long cameraId) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CAMERA_ID, cameraId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        webView1 = view.findViewById(R.id.webview1);
        webView2 = view.findViewById(R.id.webview2);

        WebSettings webSettings = webView1.getSettings();
        WebSettings webSettings2 = webView2.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings2.setJavaScriptEnabled(true);
        webSettings2.setDomStorageEnabled(true);  // 启用 DOM 存储
        webSettings2.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView2.setWebContentsDebuggingEnabled(true);


        webView1.setWebViewClient(new WebViewClient());
        webView2.setWebViewClient(new WebViewClient());

        Bundle args = getArguments();
        Long cameraId = (args != null) ? args.getLong(ARG_CAMERA_ID, 0) : 0;
        String url = "http://172.20.10.12:3000/?cameraId=" + cameraId;
        String url1 = "http://172.20.10.12:3001/?cameraId=" + cameraId;

        webView1.loadUrl(url);
        webView2.loadUrl(url1);

        return view;
    }
}
