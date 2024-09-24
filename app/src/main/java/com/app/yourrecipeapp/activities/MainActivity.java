package com.app.yourrecipeapp.activities;

import static com.app.yourrecipeapp.config.AppConfig.ENABLE_RTL_MODE;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import com.app.yourrecipeapp.BuildConfig;
import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.AdsPref;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.fragments.FragmentCategory;
import com.app.yourrecipeapp.fragments.FragmentFavorite;
import com.app.yourrecipeapp.fragments.FragmentHome;
import com.app.yourrecipeapp.fragments.FragmentRecipes;
import com.app.yourrecipeapp.utils.AdsManager;
import com.app.yourrecipeapp.utils.AppBarLayoutBehavior;
import com.app.yourrecipeapp.utils.Constant;
import com.app.yourrecipeapp.utils.RtlViewPager;
import com.app.yourrecipeapp.utils.Tools;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.solodroid.ads.sdk.format.AppOpenAd;
import com.solodroid.push.sdk.provider.OneSignalPush;

public class MainActivity extends AppCompatActivity implements DefaultLifecycleObserver {

    private long exitTime = 0;
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private RtlViewPager viewPagerRTL;
    private TextView title_toolbar;
    MenuItem prevMenuItem;
    int pagerNumber = 4;
    ImageButton btnSearch;
    SharedPref sharedPref;
    CoordinatorLayout coordinatorLayout;
    ImageButton btnSettings;
    public ImageButton btnFilter;
    AdsPref adsPref;
    AdsManager adsManager;
    private AppUpdateManager appUpdateManager;
    View lytDialogExit;
    LinearLayout lytPanelView;
    LinearLayout lytPanelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        adsPref = new AdsPref(this);
        if (ENABLE_RTL_MODE) {
            setContentView(R.layout.activity_main_rtl);
        } else {
            setContentView(R.layout.activity_main);
        }

        sharedPref = new SharedPref(this);
        Tools.setNavigation(this);

        if (AppConfig.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        }

        AppBarLayout appBarLayout = findViewById(R.id.appbarLayout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        adsManager = new AdsManager(this);
        adsManager.initializeAd();
        adsManager.updateConsentStatus();
        adsManager.loadBannerAd(adsPref.getIsBannerHome());
        adsManager.loadInterstitialAd(adsPref.getIsInterstitialPostList(), adsPref.getInterstitialAdInterval());
        initExitDialog();
        adsPref.setIsOpenAd(true);

        title_toolbar = findViewById(R.id.title_toolbar);
        btnFilter = findViewById(R.id.btn_filter);
        btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ActivitySettings.class)));

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        navigation = findViewById(R.id.navigation);
        navigation.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);
        if (sharedPref.getIsDarkTheme()) {
            navigation.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dark_bottom_navigation));
        } else {
            navigation.setBackgroundColor(ContextCompat.getColor(this, R.color.color_light_bottom_navigation));
        }

        if (ENABLE_RTL_MODE) {
            initRTLViewPager();
        } else {
            initViewPager();
        }

        Tools.notificationOpenHandler(this, getIntent());
        Tools.getCategoryPosition(this, getIntent());
        Tools.getRecipesPosition(this, getIntent());

        setupToolbar();

        if (!BuildConfig.DEBUG) {
            appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
            inAppUpdate();
            inAppReview();
        }

        new OneSignalPush.Builder(this).requestNotificationPermission();

    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        Tools.postDelayed(() -> {
            if (AppOpenAd.isAppOpenAdLoaded) {
                adsManager.showAppOpenAd(adsPref.getIsAppOpenAdOnResume());
            }
        }, 100);
    }

    public void showInterstitialAd() {
        adsManager.showInterstitialAd();
    }

    public void initViewPager() {
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(pagerNumber);
        navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.navigation_explore) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.navigation_category) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (itemId == R.id.navigation_favorite) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                if (viewPager.getCurrentItem() == 0) {
                    title_toolbar.setText(getResources().getString(R.string.app_name));
                    showFilter(false);
                } else if (viewPager.getCurrentItem() == 1) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_recent));
                    showFilter(true);
                } else if (viewPager.getCurrentItem() == 2) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_category));
                    showFilter(false);
                } else if (viewPager.getCurrentItem() == 3) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_favorite));
                    showFilter(false);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initRTLViewPager() {
        viewPagerRTL = findViewById(R.id.viewpager_rtl);
        viewPagerRTL.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPagerRTL.setOffscreenPageLimit(pagerNumber);
        navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                viewPagerRTL.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.navigation_explore) {
                viewPagerRTL.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.navigation_category) {
                viewPagerRTL.setCurrentItem(2);
                return true;
            } else if (itemId == R.id.navigation_favorite) {
                viewPagerRTL.setCurrentItem(3);
                return true;
            }
            return false;
        });

        viewPagerRTL.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                if (viewPagerRTL.getCurrentItem() == 0) {
                    title_toolbar.setText(getResources().getString(R.string.app_name));
                    showFilter(false);
                } else if (viewPagerRTL.getCurrentItem() == 1) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_recent));
                    showFilter(true);
                } else if (viewPagerRTL.getCurrentItem() == 2) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_category));
                    showFilter(false);
                } else if (viewPagerRTL.getCurrentItem() == 3) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_favorite));
                    showFilter(false);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void selectFragmentRecipe() {
        if (ENABLE_RTL_MODE) {
            viewPagerRTL.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(1);
        }
    }

    public void selectFragmentCategory() {
        if (ENABLE_RTL_MODE) {
            viewPagerRTL.setCurrentItem(2);
        } else {
            viewPager.setCurrentItem(2);
        }
    }

    public void showFilter(Boolean show) {
        if (show) {
            btnFilter.setVisibility(View.VISIBLE);
        } else {
            btnFilter.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("deprecation")
    public class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

       @SuppressWarnings("DataFlowIssue")
        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new FragmentHome();
                case 1:
                    return new FragmentRecipes();
                case 2:
                    return new FragmentCategory();
                case 3:
                    return new FragmentFavorite();
            }
            return null;
        }

        @Override
        public int getCount() {
            return pagerNumber;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        Tools.setupToolbar(this, toolbar, "", false);

        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(view -> new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), ActivitySearch.class));
            destroyBannerAd();
        }, 50));

    }

    @Override
    public void onBackPressed() {
        if (ENABLE_RTL_MODE) {
            if (viewPagerRTL.getCurrentItem() != 0) {
                viewPagerRTL.setCurrentItem((0), true);
            } else {
                exitApp();
            }
        } else {
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem((0), true);
            } else {
                exitApp();
            }
        }
    }

    public void exitApp() {
        if (AppConfig.ENABLE_EXIT_DIALOG) {
            if (lytDialogExit.getVisibility() != View.VISIBLE) {
                showDialog(true);
            }
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showSnackBar(getString(R.string.press_again_to_exit));
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                destroyBannerAd();
                destroyAppOpenAd();
            }
        }
    }

    public void initExitDialog() {

        lytDialogExit = findViewById(R.id.lyt_dialog_exit);
        lytPanelView = findViewById(R.id.lyt_panel_view);
        lytPanelDialog = findViewById(R.id.lyt_panel_dialog);

        if (sharedPref.getIsDarkTheme()) {
            lytPanelView.setBackgroundColor(getResources().getColor(R.color.color_dialog_background_dark_overlay));
            lytPanelDialog.setBackgroundResource(R.drawable.bg_dialog_dark);
        } else {
            lytPanelView.setBackgroundColor(getResources().getColor(R.color.color_dialog_background_light));
            lytPanelDialog.setBackgroundResource(R.drawable.bg_dialog_default);
        }

        lytPanelView.setOnClickListener(view -> {
            //empty state
        });

        LinearLayout nativeAdView = findViewById(R.id.native_ad_view);
        Tools.setNativeAdStyle(this, nativeAdView, Constant.NATIVE_AD_STYLE_EXIT_DIALOG);
        adsManager.loadNativeAd(adsPref.getIsNativeAdExitDialog(), Constant.NATIVE_AD_STYLE_EXIT_DIALOG);

        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnExit = findViewById(R.id.btn_exit);

        FloatingActionButton btnRate = findViewById(R.id.btn_rate);
        FloatingActionButton btnShare = findViewById(R.id.btn_share);

        btnCancel.setOnClickListener(view -> showDialog(false));

        btnExit.setOnClickListener(view -> {
            showDialog(false);
            Tools.postDelayed(() -> {
                finish();
                destroyBannerAd();
                destroyAppOpenAd();
            }, 300);
        });

        btnRate.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            showDialog(false);
        });

        btnShare.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_share) + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            intent.setType("text/plain");
            startActivity(intent);
            showDialog(false);
        });
    }

    private void showDialog(boolean show) {
        if (show) {
            lytDialogExit.setVisibility(View.VISIBLE);
            slideUp(lytPanelDialog);
            Tools.dialogStatusBarNavigationColor(this, sharedPref.getIsDarkTheme());
        } else {
            slideDown(lytPanelDialog);
            Tools.postDelayed(() -> {
                lytDialogExit.setVisibility(View.GONE);
                Tools.setNavigation(this);
            }, 300);
        }
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, findViewById(R.id.main_content).getHeight(), 0);
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, findViewById(R.id.main_content).getHeight());
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adsManager.resumeBannerAd(adsPref.getIsBannerHome());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyBannerAd();
        destroyAppOpenAd();
    }

    public void destroyBannerAd() {
        adsManager.destroyBannerAd();
    }

    public void destroyAppOpenAd() {
        if (AppConfig.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            adsManager.destroyAppOpenAd(adsPref.getIsAppOpenAdOnResume());
            ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);
        }
        Constant.isAppOpen = false;
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    private void inAppReview() {
        if (sharedPref.getInAppReviewToken() <= 3) {
            sharedPref.updateInAppReviewToken(sharedPref.getInAppReviewToken() + 1);
        } else {
            ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    manager.launchReviewFlow(MainActivity.this, reviewInfo).addOnFailureListener(e -> {
                    }).addOnCompleteListener(complete -> {
                            }
                    ).addOnFailureListener(failure -> {
                    });
                }
            }).addOnFailureListener(failure -> Log.d("In-App Review", "In-App Request Failed " + failure));
        }
    }

    private void inAppUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, Constant.IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                showSnackBar(getString(R.string.msg_cancel_update));
            } else if (resultCode == RESULT_OK) {
                showSnackBar(getString(R.string.msg_success_update));
            } else {
                showSnackBar(getString(R.string.msg_failed_update));
                inAppUpdate();
            }
        }
    }

}
