package org.pix.healthcode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends Activity {

    public static Intent createIntent(Context context, int titleId, String url) {
        return createIntent(context, context.getString(titleId), url);
    }

    public static Intent createIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        return intent;
    }

    public static void startActivity(Context context, int titleId, String url) {
        startActivity(context, context.getString(titleId), url);
    }

    public static void startActivity(Context context, String title, String url) {
        Intent intent = createIntent(context, title, url);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        setContentView(R.layout.activity_webview);
        setTitle(title);
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl(url);
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("market:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    intent.setPackage("com.android.vending");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(WebViewActivity.this, R.string.playstore_not_installed, Toast.LENGTH_LONG).show();
                        String newUrl = url.replace("market://", "https://play.google.com/store/apps/");
                        view.loadUrl(newUrl);
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
    }
}
