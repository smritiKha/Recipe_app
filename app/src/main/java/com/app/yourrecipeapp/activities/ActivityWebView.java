package com.app.yourrecipeapp.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.utils.Tools;

public class ActivityWebView extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    Button btnFailedRetry;
    View lytFailed;
    String strUrl, title;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        sharedPref = new SharedPref(this);
        setContentView(R.layout.activity_webview);
        Tools.setNavigation(this);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        btnFailedRetry = findViewById(R.id.failed_retry);
        lytFailed = findViewById(R.id.lyt_failed);

        Intent intent = getIntent();
        if (null != intent) {
            title = intent.getStringExtra("title");
            strUrl = intent.getStringExtra("url");
        }

        displayData();

        btnFailedRetry.setOnClickListener(view -> {
            lytFailed.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            displayData();
        });

        setupToolbar();

    }

    public void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.color_dark_toolbar));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.color_light_primary));
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(title);
        }
    }

    public void displayData() {
        Handler handler = new Handler();
        handler.postDelayed(this::loadData, 1000);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void loadData() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new PQClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(strUrl);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //start intent for "tel:" links
            if (url != null && url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                view.reload();
                return true;
            }

            //start intent for "sms:" links
            if (url != null && url.startsWith("sms:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(intent);
                view.reload();
                return true;
            }

            //start intent for "sms:" links
            if (url != null && url.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(intent);
                view.reload();
                return true;
            }

            if (url != null && url.startsWith("http://pin.bbm.com/")) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setPackage("com.bbm");
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    i.setPackage(null);
                    startActivity(i);
                }
                view.reload();
                return true;
            }


            if (url != null && url.startsWith("https://api.whatsapp.com/")) {

                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.reload();

                return true;
            }

            if (url != null && url.startsWith("https://www.instagram.com/")) {

                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    i.setPackage("com.instagram.android");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.reload();

                return true;
            }

            if (url != null && url.startsWith("instagram://")) {

                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    i.setPackage("com.instagram.android");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.reload();

                return true;
            }

            if (url != null && url.startsWith("https://maps.google.com/")) {

                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    i.setPackage("com.google.android.apps.maps");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.reload();

                return true;
            }

            if (url != null && url.startsWith("file:///android_asset/[external]http")) {
                url = url.replace("file:///android_asset/[external]", "");
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {
                view.loadUrl(url);
            }

            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_text), Toast.LENGTH_LONG).show();
            view.loadUrl("about:blank");
        }
    }

    @SuppressWarnings("deprecation")
    public class PQClient extends WebViewClient {
        ProgressDialog progressDialog;

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // If url contains mailto link then open Mail Intent
            if (url.contains("mailto:")) {

                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                // Here we can open new activity

                return true;

            } else {

                // Stay within this webview and load url
                view.loadUrl(url);
                return true;
            }
        }

        //Show loader on url load
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // Then show progress  Dialog
            // in standard case YourActivity.this
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setMessage("Loading...");
                progressDialog.hide();
            }
        }

        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            webView.loadUrl("javascript:(function(){ " +
                    "document.getElementById('android-app').style.display='none';})()");

            try {
                // Close progressDialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.open_in_browser) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl)));

            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
