package com.avi.in.earthquakemeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebView extends AppCompatActivity {

    public WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        savedInstanceState = getIntent().getExtras();

        String receivedURl = savedInstanceState.getString("passedURL");

        webView = findViewById(R.id.web_view);

        webView.loadUrl(receivedURl);
        webView.setWebViewClient(new WebViewClient());
        webView.setHorizontalScrollBarEnabled(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();

        else
            super.onBackPressed();

    }

}