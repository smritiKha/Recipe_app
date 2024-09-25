package com.app.yourrecipeapp.activities;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.yourrecipeapp.BuildConfig;
import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.callbacks.CallbackConfig;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.AdsPref;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.models.Ads;
import com.app.yourrecipeapp.models.App;
import com.app.yourrecipeapp.models.Placement;
import com.app.yourrecipeapp.models.Settings;
import com.app.yourrecipeapp.rests.RestAdapter;
import com.app.yourrecipeapp.utils.AdsManager;
import com.app.yourrecipeapp.utils.Constant;
import com.app.yourrecipeapp.utils.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    public static final String TAG = "ActivitySplash";
    ProgressBar progressBar;
    ImageView imgSplash;
    SharedPref sharedPref;
    AdsPref adsPref;
    AdsManager adsManager;
    App app;
    Ads ads;
    Placement adsPlacement;
    Settings settings;
    Call<CallbackConfig> callbackCall = null;
    boolean isForceOpenAds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_splash);
        Tools.setNavigation(this);
        isForceOpenAds = AppConfig.FORCE_TO_SHOW_APP_OPEN_AD_ON_START;
        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        adsManager = new AdsManager(this);
        adsManager.initializeAd();

        imgSplash = findViewById(R.id.img_splash);
        if (sharedPref.getIsDarkTheme()) {
            imgSplash.setImageResource(R.drawable.logo);
        } else {
            imgSplash.setImageResource(R.drawable.splash);
        }

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Tools.postDelayed(this::requestConfig, AppConfig.DELAY_SPLASH_SCREEN);

    }

    @SuppressWarnings("ConstantConditions")
    private void requestConfig() {
        if (AppConfig.SERVER_KEY.contains("XXXXX")) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("App not configured")
                    .setMessage("Please put your Server Key and Rest API Key from settings menu in your admin panel to AppConfig, you can see the documentation for more detailed instructions.")
                    .setPositiveButton(getString(R.string.dialog_ok), (dialogInterface, i) -> startMainActivity())
                    .setCancelable(false)
                    .show();
        } else {

            String data = Tools.decode(AppConfig.SERVER_KEY);
            String[] results = data.split("_applicationId_");
            String apiUrl = results[0].replace("http://10.0.2.2", Constant.LOCALHOST_ADDRESS);
            String applicationId = results[1];
            sharedPref.saveConfig(apiUrl, applicationId);
            if (applicationId.equals(BuildConfig.APPLICATION_ID)) {
                if (Tools.isConnect(this)) {
                    requestAPI(apiUrl);
                } else {
                    showAppOpenAdIfAvailable(false);
                }
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("Whoops! invalid server key or applicationId, please check your configuration")
                        .setPositiveButton(getString(R.string.dialog_ok), (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            }
        }
    }

    private void requestAPI(String apiUrl) {
        this.callbackCall = RestAdapter.createAPI(apiUrl).getConfig(BuildConfig.APPLICATION_ID, AppConfig.REST_API_KEY);

        this.callbackCall.enqueue(new Callback<CallbackConfig>() {
            public void onResponse(@NonNull Call<CallbackConfig> call, @NonNull Response<CallbackConfig> response) {
                CallbackConfig resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    app = resp.app;
                    ads = resp.ads;
                    adsPlacement = resp.ads_placement;
                    settings = resp.settings;
                    adsManager.saveAds(adsPref, ads);
                    adsManager.saveAdsPlacement(adsPref, adsPlacement);
                    sharedPref.saveCredentials(
                            settings.youtube_api_key,
                            settings.privacy_policy,
                            settings.more_apps_url,
                            app.redirect_url,
                            settings.providers
                    );

                    if (app.status.equals("0")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityRedirect.class);
                        intent.putExtra("redirect_url", app.redirect_url);
                        startActivity(intent);
                        finish();
                        Log.d(TAG, "App status is suspended");
                    } else {
                        showAppOpenAdIfAvailable(true);
                        Log.d("Response", "Ads Data is saved");
                    }
                } else {
                    showAppOpenAdIfAvailable(false);
                }

            }

            public void onFailure(@NonNull Call<CallbackConfig> call, @NonNull Throwable th) {
                Log.e("onFailure", "onFailure: " + th.getMessage());
                showAppOpenAdIfAvailable(false);
            }
        });
    }

    private void showAppOpenAdIfAvailable(boolean show) {
        if (show) {
            if (isForceOpenAds) {
                if (adsPref.getIsOpenAd()) {
                    adsManager.loadAppOpenAd(adsPref.getIsAppOpenAdOnStart(), this::startMainActivity);
                } else {
                    startMainActivity();
                }
            } else {
                if (adsPref.getAdStatus().equals(AD_STATUS_ON) && adsPref.getIsAppOpenAdOnStart()) {
                    switch (adsPref.getMainAds()) {
                        case ADMOB:
                            if (!adsPref.getAdMobAppOpenAdId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                            } else {
                                startMainActivity();
                            }
                            break;
                        case GOOGLE_AD_MANAGER:
                            if (!adsPref.getAdManagerAppOpenAdId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                            } else {
                                startMainActivity();
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                            if (!adsPref.getAppLovinAppOpenAdUnitId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                            } else {
                                startMainActivity();
                            }
                            break;
                        case WORTISE:
                            if (!adsPref.getWortiseAppOpenId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                            } else {
                                startMainActivity();
                            }
                            break;
                        default:
                            startMainActivity();
                            break;
                    }
                } else {
                    startMainActivity();
                }
            }
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.isAppOpen = false;
    }

}
