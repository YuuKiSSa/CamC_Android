package iss.workshop.adproject;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class ChartActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        webView= findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl("http://10.0.2.2:3000/"); // React.js 应用 URL
    }
}
