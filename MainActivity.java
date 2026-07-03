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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.window.OnBackInvokedDispatcher;

/**
 * Static, precompiled WebView Activity reused by EVERY app html2apk
 * generates. It never changes between builds, so it's compiled exactly
 * once into tools/classes.dex (see BUILD_TEMPLATE instructions) — the
 * per-app build pipeline only ever compiles resources/manifest with
 * aapt2, never recompiles Java.
 *
 * Because the same compiled class is reused across many different
 * generated app packages, it never references a generated app's R class
 * directly (those numeric resource IDs differ from build to build).
 * Instead it looks resources up by NAME at runtime via
 * getResources().getIdentifier(...), which works correctly regardless
 * of which package or numeric IDs aapt2 assigned for that particular
 * build.
 */
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
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);

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

        // targetSdk 35+ enforces edge-to-edge: content draws under the status/nav
        // bars by default. Pad the root view to the system bar insets ourselves
        // so the WebView isn't covered by them.
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

        setContentView(root);

        // Predictive back (API 33+): a registered OnBackInvokedCallback takes
        // priority over onBackPressed(), which is deprecated as of API 33.
        // onBackPressed() below remains the active path on API < 33.
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
