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
    private WebView webView;
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
        webView = view.findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        Bundle args = getArguments();
        Long cameraId = (args != null) ? args.getLong(ARG_CAMERA_ID, 0) : 0;
        String url = "http://192.168.0.201:3000/?cameraId=" + cameraId;
        webView.loadUrl(url);

        return view;
    }
}
