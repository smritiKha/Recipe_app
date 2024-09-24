package com.app.yourrecipeapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.activities.ActivityCategoryDetail;
import com.app.yourrecipeapp.activities.ActivityRecipeDetail;
import com.app.yourrecipeapp.activities.ActivityRecipeList;
import com.app.yourrecipeapp.activities.MainActivity;
import com.app.yourrecipeapp.adapters.AdapterFeatured;
import com.app.yourrecipeapp.adapters.AdapterHomeCategory;
import com.app.yourrecipeapp.adapters.AdapterHomeRecipes;
import com.app.yourrecipeapp.callbacks.CallbackHome;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.AdsPref;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.models.Category;
import com.app.yourrecipeapp.models.Recipe;
import com.app.yourrecipeapp.rests.RestAdapter;
import com.app.yourrecipeapp.utils.AdsManager;
import com.app.yourrecipeapp.utils.Constant;
import com.app.yourrecipeapp.utils.RtlViewPager;
import com.app.yourrecipeapp.utils.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment {

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackHome> callbackCall = null;
    private View lytMainContent;
    private Runnable runnableCode = null;
    Handler handler = new Handler();
    private ViewPager viewPagerFeatured;
    private RtlViewPager viewPagerFeaturedRtl;
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";
    private ShimmerFrameLayout lytShimmer;
    SharedPref sharedPref;
    AdsPref adsPref;
    AdsManager adsManager;
    Activity activity;
    ImageView imgArrowCategory;
    ImageView imgArrowRecent;
    ImageView imgArrowVideos;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (AppConfig.ENABLE_RTL_MODE) {
            rootView = inflater.inflate(R.layout.fragment_home_rtl, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }

        sharedPref = new SharedPref(activity);
        adsPref = new AdsPref(activity);
        adsManager = new AdsManager(activity);
        initAds();

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_light_primary);
        lytMainContent = rootView.findViewById(R.id.lyt_home_content);
        lytShimmer = rootView.findViewById(R.id.shimmer_view_container);

        imgArrowCategory = rootView.findViewById(R.id.img_arrow_category);
        imgArrowRecent = rootView.findViewById(R.id.img_arrow_recent);
        imgArrowVideos = rootView.findViewById(R.id.img_arrow_videos);

        if (sharedPref.getIsDarkTheme()) {
            imgArrowCategory.setColorFilter(ContextCompat.getColor(activity, R.color.color_dark_icon));
            imgArrowRecent.setColorFilter(ContextCompat.getColor(activity, R.color.color_dark_icon));
            imgArrowVideos.setColorFilter(ContextCompat.getColor(activity, R.color.color_dark_icon));
        } else {
            imgArrowCategory.setColorFilter(ContextCompat.getColor(activity, R.color.color_light_icon));
            imgArrowRecent.setColorFilter(ContextCompat.getColor(activity, R.color.color_light_icon));
            imgArrowVideos.setColorFilter(ContextCompat.getColor(activity, R.color.color_light_icon));
        }

        swipeRefreshLayout.setOnRefreshListener(this::requestAction);

        requestAction();

        return rootView;
    }

    private void initAds() {
        LinearLayout nativeAdView = rootView.findViewById(R.id.native_ad_view);
        Tools.setNativeAdStyle(activity, nativeAdView, Constant.NATIVE_AD_STYLE_RECIPES_HOME);
        adsManager.loadNativeAdView(rootView, adsPref.getIsNativeAdHome());
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler().postDelayed(this::requestHomeData, Constant.DELAY_TIME);
    }

    private void requestHomeData() {
        this.callbackCall = RestAdapter.createAPI(sharedPref.getApiUrl()).getHome(AppConfig.REST_API_KEY);
        this.callbackCall.enqueue(new Callback<CallbackHome>() {
            public void onResponse(@NonNull Call<CallbackHome> call, @NonNull Response<CallbackHome> response) {
                CallbackHome responseHome = response.body();
                if (responseHome == null || !responseHome.status.equals("ok")) {
                    onFailRequest();
                    return;
                }
                displayData(responseHome);
                swipeProgress(false);
                lytMainContent.setVisibility(View.VISIBLE);
            }

            public void onFailure(@NonNull Call<CallbackHome> call, @NonNull Throwable th) {
                Log.e("onFailure", th.getMessage());
                if (!call.isCanceled()) {
                    onFailRequest();
                }
            }
        });
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (Tools.isConnect(activity)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = rootView.findViewById(R.id.lyt_failed_home);
        ((TextView) rootView.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_failed.setVisibility(View.VISIBLE);
            lytMainContent.setVisibility(View.GONE);
        } else {
            lyt_failed.setVisibility(View.GONE);
            lytMainContent.setVisibility(View.VISIBLE);
        }
        rootView.findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction());
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            lytShimmer.setVisibility(View.GONE);
            lytShimmer.stopShimmer();
            lytMainContent.setVisibility(View.VISIBLE);

            return;
        }
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(show);
            lytShimmer.setVisibility(View.VISIBLE);
            lytShimmer.startShimmer();
            lytMainContent.setVisibility(View.GONE);
        });
    }

    private void startAutoSlider(final int position) {

        if (AppConfig.ENABLE_RTL_MODE) {
            if (this.runnableCode != null) {
                this.handler.removeCallbacks(this.runnableCode);
            }
            this.runnableCode = () -> {
                int currentItem = viewPagerFeaturedRtl.getCurrentItem() + 1;
                if (currentItem >= position) {
                    currentItem = 0;
                }
                viewPagerFeaturedRtl.setCurrentItem(currentItem);
                handler.postDelayed(FragmentHome.this.runnableCode, AppConfig.AUTO_SLIDER_DURATION);
            };
            handler.postDelayed(this.runnableCode, AppConfig.AUTO_SLIDER_DURATION);
        } else {
            if (this.runnableCode != null) {
                this.handler.removeCallbacks(this.runnableCode);
            }
            this.runnableCode = () -> {
                int currentItem = viewPagerFeatured.getCurrentItem() + 1;
                if (currentItem >= position) {
                    currentItem = 0;
                }
                viewPagerFeatured.setCurrentItem(currentItem);
                handler.postDelayed(FragmentHome.this.runnableCode, AppConfig.AUTO_SLIDER_DURATION);
            };
            handler.postDelayed(this.runnableCode, AppConfig.AUTO_SLIDER_DURATION);
        }

    }

    private void displayData(CallbackHome responseHome) {
        displayFeatured(responseHome.featured);
        displayCategory(responseHome.category);
        displayRecent(responseHome.recent);
        displayVideos(responseHome.videos);
        displayData();
    }

    private void displayData() {

        ((TextView) rootView.findViewById(R.id.txt_title_category)).setText(getResources().getString(R.string.home_title_category));
        ((TextView) rootView.findViewById(R.id.txt_title_recent)).setText(getResources().getString(R.string.home_title_recent));
        ((TextView) rootView.findViewById(R.id.txt_title_videos)).setText(getResources().getString(R.string.home_title_videos));

        ((ImageView) rootView.findViewById(R.id.img_arrow_category)).setImageResource(R.drawable.ic_arrow_next);
        ((ImageView) rootView.findViewById(R.id.img_arrow_recent)).setImageResource(R.drawable.ic_arrow_next);
        ((ImageView) rootView.findViewById(R.id.img_arrow_videos)).setImageResource(R.drawable.ic_arrow_next);

        rootView.findViewById(R.id.ripple_more_category).setOnClickListener(view -> {
            ((MainActivity) activity).selectFragmentCategory();
        });

        rootView.findViewById(R.id.ripple_recent_more).setOnClickListener(view -> {
            Intent intent = new Intent(activity, ActivityRecipeList.class);
            intent.putExtra("title", getResources().getString(R.string.home_title_recent));
            intent.putExtra("filter", 1);
            startActivity(intent);
            ((MainActivity) activity).destroyBannerAd();
        });

        rootView.findViewById(R.id.ripple_videos_more).setOnClickListener(view -> {
            Intent intent = new Intent(activity, ActivityRecipeList.class);
            intent.putExtra("title", getResources().getString(R.string.home_title_videos));
            intent.putExtra("filter", 2);
            startActivity(intent);
            ((MainActivity) activity).destroyBannerAd();
        });

    }

    private void displayFeatured(final List<Recipe> list) {

        if (AppConfig.ENABLE_RTL_MODE) {
            viewPagerFeaturedRtl = rootView.findViewById(R.id.view_pager_featured_rtl);
            final AdapterFeatured adapter = new AdapterFeatured(activity, list);
            final LinearLayout lyt_featured = rootView.findViewById(R.id.lyt_featured);
            viewPagerFeaturedRtl.setAdapter(adapter);
            viewPagerFeaturedRtl.setOffscreenPageLimit(4);

            if (list.size() > 0) {
                lyt_featured.setVisibility(View.VISIBLE);
            } else {
                lyt_featured.setVisibility(View.GONE);
            }

            viewPagerFeaturedRtl.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    if (position < list.size()) {

                    }
                }
            });
            adapter.setOnItemClickListener((view, obj) -> {
                Intent intent = new Intent(activity, ActivityRecipeDetail.class);
                intent.putExtra(Constant.EXTRA_OBJC, obj);
                startActivity(intent);
                ((MainActivity) activity).showInterstitialAd();
                ((MainActivity) activity).destroyBannerAd();
            });

            TabLayout tabLayout = rootView.findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(viewPagerFeaturedRtl, true);

            startAutoSlider(list.size());
        } else {
            viewPagerFeatured = rootView.findViewById(R.id.view_pager_featured);
            final AdapterFeatured adapter = new AdapterFeatured(activity, list);
            final LinearLayout lyt_featured = rootView.findViewById(R.id.lyt_featured);
            viewPagerFeatured.setAdapter(adapter);
            viewPagerFeatured.setOffscreenPageLimit(4);

            if (list.size() > 0) {
                lyt_featured.setVisibility(View.VISIBLE);
            } else {
                lyt_featured.setVisibility(View.GONE);
            }

            viewPagerFeatured.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    if (position < list.size()) {

                    }
                }
            });
            adapter.setOnItemClickListener((view, obj) -> {
                Intent intent = new Intent(activity, ActivityRecipeDetail.class);
                intent.putExtra(Constant.EXTRA_OBJC, obj);
                startActivity(intent);
                ((MainActivity) activity).showInterstitialAd();
                ((MainActivity) activity).destroyBannerAd();
            });

            TabLayout tabLayout = rootView.findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(viewPagerFeatured, true);

            startAutoSlider(list.size());
        }

    }

    private void displayCategory(List<Category> list) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        AdapterHomeCategory adapterHomeCategory = new AdapterHomeCategory(activity, list);
        recyclerView.setAdapter(adapterHomeCategory);
        recyclerView.setNestedScrollingEnabled(false);
        adapterHomeCategory.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(activity, ActivityCategoryDetail.class);
            intent.putExtra(EXTRA_OBJC, obj);
            startActivity(intent);
            ((MainActivity) activity).showInterstitialAd();
            ((MainActivity) activity).destroyBannerAd();
        });
        LinearLayout lyt_category = rootView.findViewById(R.id.lyt_category);
        if (list.size() > 0) {
            lyt_category.setVisibility(View.VISIBLE);
        } else {
            lyt_category.setVisibility(View.GONE);
        }
    }

    private void displayRecent(List<Recipe> list) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_recent);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        AdapterHomeRecipes adapterNews = new AdapterHomeRecipes(activity, recyclerView, list);
        recyclerView.setAdapter(adapterNews);
        recyclerView.setNestedScrollingEnabled(false);
        adapterNews.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(activity, ActivityRecipeDetail.class);
            intent.putExtra(Constant.EXTRA_OBJC, obj);
            startActivity(intent);
            ((MainActivity) activity).showInterstitialAd();
            ((MainActivity) activity).destroyBannerAd();
        });

        LinearLayout lyt_recipes = rootView.findViewById(R.id.lyt_recipes);
        if (list.size() > 0) {
            lyt_recipes.setVisibility(View.VISIBLE);
        } else {
            lyt_recipes.setVisibility(View.GONE);
        }
    }

    private void displayVideos(List<Recipe> list) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_videos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        AdapterHomeRecipes adapterNews = new AdapterHomeRecipes(activity, recyclerView, list);
        recyclerView.setAdapter(adapterNews);
        recyclerView.setNestedScrollingEnabled(false);
        adapterNews.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(activity, ActivityRecipeDetail.class);
            intent.putExtra(Constant.EXTRA_OBJC, obj);
            startActivity(intent);
            ((MainActivity) activity).showInterstitialAd();
            ((MainActivity) activity).destroyBannerAd();
        });

        LinearLayout lyt_videos = rootView.findViewById(R.id.lyt_videos);
        if (list.size() > 0) {
            lyt_videos.setVisibility(View.VISIBLE);
        } else {
            lyt_videos.setVisibility(View.GONE);
        }
    }

    public void onDestroy() {
        if (!(callbackCall == null || callbackCall.isCanceled())) {
            this.callbackCall.cancel();
        }
        lytShimmer.stopShimmer();
        super.onDestroy();
    }

}

