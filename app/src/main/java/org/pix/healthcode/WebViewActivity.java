package org.pix.healthcode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("market:")) {
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                    marketIntent.setData(Uri.parse(url));
                    marketIntent.setPackage("com.android.vending");
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(marketIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(WebViewActivity.this, R.string.playstore_not_installed, Toast.LENGTH_LONG).show();
                        String newUrl = url.replace("market://", "https://play.google.com/store/apps/");
                        view.loadUrl(newUrl);
                    }
                } else if (url.startsWith("mailto:")) {
                    String email = url.substring(7);
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    String[] emailReciver = new String[] { email };
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, emailReciver);
                    PackageManager pm = getPackageManager();
                    try {
                        PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.menu_about)+" "+pi.applicationInfo.loadLabel(pm));
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(emailIntent, ""));
                    } catch (PackageManager.NameNotFoundException e) {

                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
    }
}
