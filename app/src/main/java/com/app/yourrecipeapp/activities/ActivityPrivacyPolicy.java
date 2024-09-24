package com.app.yourrecipeapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.utils.Tools;

public class ActivityPrivacyPolicy extends AppCompatActivity {

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_privacy_policy);
        Tools.setNavigation(this);
        sharedPref = new SharedPref(this);
        setupToolbar();
        displayPostData();
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        Tools.setupToolbar(this, toolbar, getResources().getString(R.string.title_settings_privacy), true);
    }

    public void displayPostData() {
        WebView webView = findViewById(R.id.webview_privacy_policy);
        Tools.displayPostDescription(this, webView, sharedPref.getPrivacyPolicy());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
