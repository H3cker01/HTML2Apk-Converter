package com.html2apk.webview;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.window.OnBackInvokedDispatcher;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // Performance
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMediaPlaybackRequiresUserGesture(false);

        if (getBoolRes("enable_zoom", true)) {
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
        }

        String bgColor = getStrRes("app_theme_color", null);
        if (bgColor != null) {
            try {
                webView.setBackgroundColor(Color.parseColor(bgColor));
            } catch (IllegalArgumentException ignored) { }
        }

        String title = getStrRes("app_title", null);
        if (title != null) setTitle(title);

        boolean showLoading = getBoolRes("show_loading", true);

        FrameLayout root = new FrameLayout(this);
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        if (showLoading) {
            progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
            FrameLayout.LayoutParams pp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            pp.gravity = Gravity.CENTER;
            root.addView(progressBar, pp);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });

        // Blob download listener — converts blob: URLs to base64 data URIs
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                    String contentDisposition, String mimeType, long contentLength) {
                if (url.startsWith("blob:")) {
                    String filename = "download";
                    if (contentDisposition != null) {
                        java.util.regex.Matcher m = java.util.regex.Pattern
                            .compile("filename=\"?([^\"\\s;]+)\"?")
                            .matcher(contentDisposition);
                        if (m.find()) filename = m.group(1);
                    }
                    final String fname = filename;
                    String js = "javascript:(function(){" +
                        "var x=new XMLHttpRequest();" +
                        "x.open('GET','" + url + "',true);" +
                        "x.responseType='blob';" +
                        "x.onload=function(){" +
                            "var r=new FileReader();" +
                            "r.onloadend=function(){" +
                                "var a=document.createElement('a');" +
                                "a.href=r.result;" +
                                "a.download='" + fname + "';" +
                                "document.body.appendChild(a);" +
                                "a.click();" +
                                "document.body.removeChild(a);" +
                            "};" +
                            "r.readAsDataURL(x.response);" +
                        "};" +
                        "x.send();" +
                    "})();";
                    webView.loadUrl(js);
                    return;
                }
                android.content.Intent intent = new android.content.Intent(
                    android.content.Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(url));
                startActivity(intent);
            }
        });

        setContentView(root);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    this::handleBackNavigation);
        }

        String url = getStrRes("app_url", "about:blank");
        webView.loadUrl(url);
    }

    private void handleBackNavigation() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        handleBackNavigation();
    }

    private String getStrRes(String name, String fallback) {
        int id = getResources().getIdentifier(name, "string", getPackageName());
        return id != 0 ? getString(id) : fallback;
    }

    private boolean getBoolRes(String name, boolean fallback) {
        int id = getResources().getIdentifier(name, "bool", getPackageName());
        return id != 0 ? getResources().getBoolean(id) : fallback;
    }
}
