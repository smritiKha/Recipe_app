package com.app.yourrecipeapp.activities;

import static com.app.yourrecipeapp.config.AppConfig.ENABLE_RTL_MODE;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.app.yourrecipeapp.BuildConfig;
import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.adapters.AdapterImage;
import com.app.yourrecipeapp.adapters.AdapterSuggested;
import com.app.yourrecipeapp.callbacks.CallbackRecipeDetail;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.AdsPref;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.databases.sqlite.DbHandler;
import com.app.yourrecipeapp.models.Images;
import com.app.yourrecipeapp.models.Recipe;
import com.app.yourrecipeapp.rests.RestAdapter;
import com.app.yourrecipeapp.utils.AdsManager;
import com.app.yourrecipeapp.utils.AppBarLayoutBehavior;
import com.app.yourrecipeapp.utils.Constant;
import com.app.yourrecipeapp.utils.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRecipeDetail extends AppCompatActivity {

    public static final String TAG = "RecipesDetail";
    private Call<CallbackRecipeDetail> callbackCall = null;
    private LinearLayout lytMainContent;
    private Recipe post;
    TextView txtRecipeTitle, txtCategory, txtRecipeTime, txtTotalViews;
    LinearLayout lytView;
    ImageView thumbnailVideo;
    private WebView webView;
    DbHandler dbHandler;
    CoordinatorLayout parentView;
    private ShimmerFrameLayout lytShimmer;
    RelativeLayout lytSuggested;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPref sharedPref;
    ImageButton btnFontSize, btnFavorite, btnShare;
    ViewPager viewPager;
    AdsPref adsPref;
    AdsManager adsManager;
    private String singleChoiceSelected;
    AdapterSuggested adapterSuggested;
    AdapterImage adapterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_recipe_detail);
        Tools.setNavigation(this);

        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        dbHandler = new DbHandler(this);

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        adsManager = new AdsManager(this);
        adsManager.loadBannerAd(adsPref.getIsBannerPostDetails());
        adsManager.loadInterstitialAd(adsPref.getIsInterstitialPostDetails(), 1);

        LinearLayout nativeAdView = findViewById(R.id.native_ad_view);
        Tools.setNativeAdStyle(this, nativeAdView, Constant.NATIVE_AD_STYLE_RECIPES_DETAILS);
        adsManager.loadNativeAd(adsPref.getIsNativeAdPostDetails(), Constant.NATIVE_AD_STYLE_RECIPES_DETAILS);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_light_primary);
        swipeRefreshLayout.setRefreshing(false);

        lytMainContent = findViewById(R.id.lyt_main_content);
        lytShimmer = findViewById(R.id.shimmer_view_container);
        parentView = findViewById(R.id.lyt_content);

        thumbnailVideo = findViewById(R.id.thumbnail_video);
        txtRecipeTitle = findViewById(R.id.recipe_title);
        txtCategory = findViewById(R.id.category_name);
        txtRecipeTime = findViewById(R.id.recipe_time);
        webView = findViewById(R.id.recipe_description);
        txtTotalViews = findViewById(R.id.total_views);
        lytView = findViewById(R.id.lyt_view_count);

        btnFontSize = findViewById(R.id.btn_font_size);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnShare = findViewById(R.id.btn_share);

        lytSuggested = findViewById(R.id.lyt_suggested);

        post = (Recipe) getIntent().getSerializableExtra(Constant.EXTRA_OBJC);

        requestAction();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            lytShimmer.setVisibility(View.VISIBLE);
            lytShimmer.startShimmer();
            lytMainContent.setVisibility(View.GONE);
            requestAction();
        });

        initToolbar();
        initFavorite();
        loadViewed();

    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler().postDelayed(this::requestPostData, 200);
    }

    private void requestPostData() {
        this.callbackCall = RestAdapter.createAPI(sharedPref.getApiUrl()).getRecipeDetail(post.recipe_id);
        this.callbackCall.enqueue(new Callback<CallbackRecipeDetail>() {
            public void onResponse(@NonNull Call<CallbackRecipeDetail> call, @NonNull Response<CallbackRecipeDetail> response) {
                CallbackRecipeDetail responseHome = response.body();
                if (responseHome == null || !responseHome.status.equals("ok")) {
                    onFailRequest();
                    return;
                }
                displayAllData(responseHome);
                swipeProgress(false);
                lytMainContent.setVisibility(View.VISIBLE);
            }

            public void onFailure(@NonNull Call<CallbackRecipeDetail> call, @NonNull Throwable th) {
                Log.e("onFailure", th.getMessage());
                if (!call.isCanceled()) {
                    onFailRequest();
                }
            }
        });
    }

    private void onFailRequest() {
        swipeProgress(false);
        lytMainContent.setVisibility(View.GONE);
        if (Tools.isConnect(ActivityRecipeDetail.this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed_home);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction());
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            lytShimmer.setVisibility(View.GONE);
            lytShimmer.stopShimmer();
            lytMainContent.setVisibility(View.VISIBLE);
            return;
        }
        lytMainContent.setVisibility(View.GONE);
    }

    private void displayAllData(CallbackRecipeDetail responseHome) {
        displayImages(responseHome.images);
        displayData(responseHome.post);
        displaySuggested(responseHome.related);
    }

    private void displayImages(final List<Images> list) {

        viewPager = findViewById(R.id.view_pager_image);
        adapterImage = new AdapterImage(this, list);
        viewPager.setAdapter(adapterImage);
        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position < list.size()) {

                }
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        if (list.size() > 1) {
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
        }

        if (ENABLE_RTL_MODE) {
            viewPager.setRotationY(180);
        }

        adapterImage.setOnItemClickListener((view, p, position) -> {
            switch (p.content_type) {
                case "youtube": {
                    Intent intent = new Intent(getApplicationContext(), ActivityYoutubePlayer.class);
                    intent.putExtra("video_id", post.video_id);
                    startActivity(intent);
                    break;
                }
                case "Url": {
                    Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                    intent.putExtra("video_url", post.video_url);
                    startActivity(intent);
                    break;
                }
                case "Upload": {
                    Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                    intent.putExtra("video_url", sharedPref.getApiUrl() + "/upload/video/" + post.video_url);
                    startActivity(intent);
                    break;
                }
                default: {
                    Intent intent = new Intent(getApplicationContext(), ActivityImageSlider.class);
                    intent.putExtra("position", position);
                    intent.putExtra("recipe_id", post.recipe_id);
                    startActivity(intent);
                    break;
                }
            }
            showInterstitialAd();
        });

    }

    public void displayData(final Recipe post) {

        txtRecipeTitle.setText(post.recipe_title);
        txtRecipeTime.setText(post.recipe_time);

        if (AppConfig.ENABLE_RECIPES_VIEW_COUNT) {
            txtTotalViews.setText(Tools.withSuffix(post.total_views) + " " + getResources().getString(R.string.views_count));
        } else {
            lytView.setVisibility(View.GONE);
        }

        if (post.content_type != null && post.content_type.equals("Post")) {
            thumbnailVideo.setVisibility(View.GONE);
        } else {
            thumbnailVideo.setVisibility(View.VISIBLE);
        }

        Tools.displayPostDescription(this, webView, post.recipe_description);

        btnShare.setOnClickListener(view -> {
            String share_title = android.text.Html.fromHtml(post.recipe_title).toString();
            String share_content = android.text.Html.fromHtml(getResources().getString(R.string.app_share)).toString();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" + share_content + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });

        btnFontSize.setOnClickListener(view -> {
            String[] items = getResources().getStringArray(R.array.dialog_font_size);
            singleChoiceSelected = items[sharedPref.getFontSize()];
            int itemSelected = sharedPref.getFontSize();
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(ActivityRecipeDetail.this);
            dialog.setTitle(getString(R.string.title_dialog_font_size));
            dialog.setSingleChoiceItems(items, itemSelected, (dialogInterface, i) -> singleChoiceSelected = items[i]);
            dialog.setPositiveButton(getString(R.string.dialog_ok), (dialogInterface, i) -> {
                WebSettings webSettings = webView.getSettings();
                if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_xsmall))) {
                    sharedPref.updateFontSize(0);
                    webSettings.setDefaultFontSize(Constant.FONT_SIZE_XSMALL);
                } else if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_small))) {
                    sharedPref.updateFontSize(1);
                    webSettings.setDefaultFontSize(Constant.FONT_SIZE_SMALL);
                } else if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_medium))) {
                    sharedPref.updateFontSize(2);
                    webSettings.setDefaultFontSize(Constant.FONT_SIZE_MEDIUM);
                } else if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_large))) {
                    sharedPref.updateFontSize(3);
                    webSettings.setDefaultFontSize(Constant.FONT_SIZE_LARGE);
                } else if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_xlarge))) {
                    sharedPref.updateFontSize(4);
                    webSettings.setDefaultFontSize(Constant.FONT_SIZE_XLARGE);
                } else {
                    sharedPref.updateFontSize(2);
                    webSettings.setDefaultFontSize(Constant.FONT_SIZE_MEDIUM);
                }
                dialogInterface.dismiss();
            });
            dialog.setNegativeButton(getString(R.string.dialog_cancel), null);
            dialog.show();
        });

        addToFavorite();
        new Handler().postDelayed(() -> lytSuggested.setVisibility(View.VISIBLE), 1000);

    }

    private void displaySuggested(List<Recipe> list) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_suggested);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        adapterSuggested = new AdapterSuggested(ActivityRecipeDetail.this, recyclerView, list);
        recyclerView.setAdapter(adapterSuggested);
        recyclerView.setNestedScrollingEnabled(false);
        adapterSuggested.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(getApplicationContext(), ActivityRecipeDetail.class);
            intent.putExtra(Constant.EXTRA_OBJC, obj);
            startActivity(intent);
            showInterstitialAd();
            adsManager.destroyBannerAd();
        });

        TextView txt_suggested = findViewById(R.id.txt_suggested);
        if (list.size() > 0) {
            txt_suggested.setText(getResources().getString(R.string.txt_suggested));
        } else {
            txt_suggested.setText("");
        }

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        Tools.setupToolbar(this, toolbar, "", true);
        txtCategory.setText(post.category_name);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void initFavorite() {
        List<Recipe> data = dbHandler.getFavRow(post.recipe_id);
        if (data.size() == 0) {
            btnFavorite.setImageResource(R.drawable.ic_fav_outline);
        } else {
            if (data.get(0).getRecipe_id().equals(post.recipe_id)) {
                btnFavorite.setImageResource(R.drawable.ic_fav);
            }
        }
    }

    public void addToFavorite() {

        btnFavorite.setOnClickListener(view -> {
            List<Recipe> data1 = dbHandler.getFavRow(post.recipe_id);
            if (data1.size() == 0) {
                dbHandler.AddtoFavorite(new Recipe(
                        post.category_name,
                        post.recipe_id,
                        post.recipe_title,
                        post.recipe_time,
                        post.recipe_image,
                        post.recipe_description,
                        post.video_url,
                        post.video_id,
                        post.content_type,
                        post.featured,
                        post.tags,
                        post.total_views
                ));
                Snackbar.make(parentView, R.string.favorite_added, Snackbar.LENGTH_SHORT).show();
                btnFavorite.setImageResource(R.drawable.ic_fav);
            } else {
                if (data1.get(0).getRecipe_id().equals(post.recipe_id)) {
                    dbHandler.RemoveFav(new Recipe(post.recipe_id));
                    Snackbar.make(parentView, R.string.favorite_removed, Snackbar.LENGTH_SHORT).show();
                    btnFavorite.setImageResource(R.drawable.ic_fav_outline);
                }
            }
        });

    }

    private void showInterstitialAd() {
        if (adsPref.getCounter() >= adsPref.getInterstitialAdInterval()) {
            Log.d(TAG, "reset and show interstitial");
            adsPref.saveCounter(1);
            adsManager.showInterstitialAd();
        } else {
            adsPref.saveCounter(adsPref.getCounter() + 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    private void loadViewed() {
        if (Tools.isConnect(this)) {
            new MyTask().execute(sharedPref.getApiUrl() + "/api/api.php?get_total_views&id=" + post.recipe_id);
        }
    }

    @SuppressWarnings("deprecation")
    private static class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return Tools.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                Log.d("TAG", "no data found!");
            } else {

                try {

                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray("result");
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!(callbackCall == null || callbackCall.isCanceled())) {
            this.callbackCall.cancel();
        }
        lytShimmer.stopShimmer();
        adsManager.destroyBannerAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adsManager.resumeBannerAd(adsPref.getIsBannerPostDetails());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        adsManager.destroyBannerAd();
    }

}
