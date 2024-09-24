package com.app.yourrecipeapp.activities;

import static com.app.yourrecipeapp.config.AppConfig.ENABLE_RTL_MODE;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_GRID_2_COLUMN;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_GRID_3_COLUMN;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_LIST_BIG;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_LIST_SMALL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.adapters.AdapterRecipes;
import com.app.yourrecipeapp.adapters.AdapterSearch;
import com.app.yourrecipeapp.callbacks.CallbackRecipes;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.AdsPref;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.rests.ApiInterface;
import com.app.yourrecipeapp.rests.RestAdapter;
import com.app.yourrecipeapp.utils.AdsManager;
import com.app.yourrecipeapp.utils.Constant;
import com.app.yourrecipeapp.utils.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySearch extends AppCompatActivity {

    private EditText edtSearch;
    private RecyclerView recyclerView;
    RecyclerView recyclerViewSuggestion;
    private AdapterRecipes adapterRecipes;
    private AdapterSearch adapterSearch;
    private LinearLayout lytSuggestion;
    private ImageButton btnClear;
    Call<CallbackRecipes> callbackCall = null;
    SharedPref sharedPref;
    private ShimmerFrameLayout lytShimmer;
    AdsPref adsPref;
    AdsManager adsManager;
    LinearLayout lytBannerAd;
    private int postTotal = 0;
    private int failedPage = 0;
    CoordinatorLayout parentView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_search);
        Tools.setNavigation(this);

        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);

        adsManager = new AdsManager(this);
        adsManager.loadBannerAd(adsPref.getIsBannerSearch());
        adsManager.loadInterstitialAd(adsPref.getIsInterstitialPostList(), adsPref.getInterstitialAdInterval());

        parentView = findViewById(R.id.parent_view);
        lytBannerAd = findViewById(R.id.lyt_banner_ad);
        edtSearch = findViewById(R.id.et_search);
        btnClear = findViewById(R.id.bt_clear);
        btnClear.setVisibility(View.GONE);
        lytShimmer = findViewById(R.id.shimmer_view_container);
        swipeProgress(false);

        recyclerView = findViewById(R.id.recyclerView);
        if (sharedPref.getRecipesViewType() == RECIPES_LIST_SMALL || sharedPref.getRecipesViewType() == RECIPES_LIST_BIG) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            int padding = getResources().getDimensionPixelSize(R.dimen.grid_space_recipes);
            recyclerView.setPadding(0, padding, 0, padding);
        } else if (sharedPref.getRecipesViewType() == RECIPES_GRID_3_COLUMN) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            Tools.setPadding(recyclerView, getResources().getDimensionPixelSize(R.dimen.grid_space_recipes));
        } else if (sharedPref.getRecipesViewType() == RECIPES_GRID_2_COLUMN) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            Tools.setPadding(recyclerView, getResources().getDimensionPixelSize(R.dimen.grid_space_recipes));
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            Tools.setPadding(recyclerView, getResources().getDimensionPixelSize(R.dimen.grid_space_recipes));
        }

        edtSearch.addTextChangedListener(textWatcher);

        adapterRecipes = new AdapterRecipes(this, recyclerView, new ArrayList<>());
        recyclerView.setAdapter(adapterRecipes);

        lytSuggestion = findViewById(R.id.lyt_suggestion);
        recyclerViewSuggestion = findViewById(R.id.recycler_view_suggestion);
        recyclerViewSuggestion.setLayoutManager(new LinearLayoutManager(this));

        lytSuggestion = findViewById(R.id.lyt_suggestion);
        if (sharedPref.getIsDarkTheme()) {
            lytSuggestion.setBackgroundColor(getResources().getColor(R.color.color_dark_background));
        } else {
            lytSuggestion.setBackgroundColor(getResources().getColor(R.color.color_light_background));
        }

        adapterSearch = new AdapterSearch(this);
        recyclerViewSuggestion.setAdapter(adapterSearch);
        showSuggestionSearch();
        adapterSearch.setOnItemClickListener((view, viewModel, pos) -> {
            edtSearch.setText(viewModel);
            edtSearch.setSelection(viewModel.length());
            lytSuggestion.setVisibility(View.GONE);
            adapterRecipes.resetListData();
            hideKeyboard();
            searchAction(1);
        });

        adapterSearch.setOnItemActionClickListener((view, viewModel, pos) -> {
            edtSearch.setText(viewModel);
            edtSearch.setSelection(viewModel.length());
        });

        btnClear.setOnClickListener(view -> edtSearch.setText(""));

        adapterRecipes.setOnItemClickListener((v, obj, position) -> {
            Intent intent = new Intent(getApplicationContext(), ActivityRecipeDetail.class);
            intent.putExtra(Constant.EXTRA_OBJC, obj);
            startActivity(intent);
            adsManager.showInterstitialAd();
            adsManager.destroyBannerAd();
        });

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                searchAction(1);
                return true;
            }
            return false;
        });

        edtSearch.setOnTouchListener((view, motionEvent) -> {
            if (adapterSearch.getItemCount() > 0) {
                showSuggestionSearch();
            } else {
                lytSuggestion.setVisibility(View.GONE);
                showEmptySearch();
            }
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return false;
        });

        if (adapterSearch.getItemCount() <= 0) {
            lytSuggestion.setVisibility(View.GONE);
            showEmptySearch();
        }

        adapterRecipes.setOnLoadMoreListener(this::setLoadMore);

        setupToolbar();
        initShimmerLayout();

    }

    public void setLoadMore(int current_page) {
        Log.d("page", "currentPage: " + current_page);
        // Assuming final total items equal to real post items plus the ad
        int totalItemBeforeAds = (adapterRecipes.getItemCount() - current_page);
        if (postTotal > totalItemBeforeAds && current_page != 0) {
            int next_page = current_page + 1;
            searchAction(next_page);
        } else {
            adapterRecipes.setLoaded();
        }
    }

    public void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        Tools.setupToolbar(this, toolbar, "", true);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            if (c.toString().trim().length() == 0) {
                btnClear.setVisibility(View.GONE);
            } else {
                btnClear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void requestSearchApi(final String query, final int page_no) {
        ApiInterface apiInterface = RestAdapter.createAPI(sharedPref.getApiUrl());
        if (ENABLE_RTL_MODE) {
            callbackCall = apiInterface.getSearchRTL(query, page_no, AppConfig.POST_PER_PAGE, AppConfig.REST_API_KEY);
        } else {
            callbackCall = apiInterface.getSearch(query, page_no, AppConfig.POST_PER_PAGE, AppConfig.REST_API_KEY);
        }
        callbackCall.enqueue(new Callback<CallbackRecipes>() {
            @Override
            public void onResponse(@NonNull Call<CallbackRecipes> call, @NonNull Response<CallbackRecipes> response) {
                CallbackRecipes resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    postTotal = resp.count_total;
                    adapterRecipes.insertDataWithNativeAd(resp.posts);
                    lytBannerAd.setVisibility(View.VISIBLE);
                    if (resp.posts.size() == 0) {
                        showNotFoundView(true);
                    }
                    ;
                } else {
                    onFailRequest(page_no);
                }
                swipeProgress(false);
            }

            @Override
            public void onFailure(@NonNull Call<CallbackRecipes> call, @NonNull Throwable t) {
                onFailRequest(page_no);
                swipeProgress(false);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failedPage = page_no;
        adapterRecipes.setLoaded();
        if (Tools.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void searchAction(final int page_no) {
        lytSuggestion.setVisibility(View.GONE);
        showFailedView(false, "");
        showNotFoundView(false);
        final String query = edtSearch.getText().toString().trim();
        if (!query.equals("")) {
            if (page_no == 1) {
                adapterSearch.addSearchHistory(query);
                adapterRecipes.resetListData();
                swipeProgress(true);
            } else {
                adapterRecipes.setLoading();
            }
            new Handler().postDelayed(() -> requestSearchApi(query, page_no), Constant.DELAY_TIME);
        } else {
            Snackbar.make(parentView, getString(R.string.msg_search_input), Snackbar.LENGTH_SHORT).show();
            showEmptySearch();
            swipeProgress(false);
        }
    }

    private void showSuggestionSearch() {
        adapterSearch.refreshItems();
        lytSuggestion.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(view -> searchAction(failedPage));
    }

    private void showNotFoundView(boolean show) {
        View lyt_no_item = findViewById(R.id.lyt_no_item);
        ((TextView) findViewById(R.id.no_item_message)).setText(R.string.no_post_found);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void showEmptySearch() {
        View lytNotFound = findViewById(R.id.lyt_no_item);
        ((TextView) findViewById(R.id.no_item_title)).setText(getString(R.string.search));
        ((TextView) findViewById(R.id.no_item_message)).setText(getString(R.string.msg_search_input));
        lytNotFound.setVisibility(View.VISIBLE);
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            lytShimmer.setVisibility(View.GONE);
            lytShimmer.stopShimmer();
            return;
        } else {
            lytShimmer.setVisibility(View.VISIBLE);
            lytShimmer.startShimmer();
        }
    }

    private void initShimmerLayout() {
        View lyt_shimmer_recipes_list_small = findViewById(R.id.lyt_shimmer_recipes_list_small);
        View lyt_shimmer_recipes_list_big = findViewById(R.id.lyt_shimmer_recipes_list_big);
        View lyt_shimmer_recipes_grid2 = findViewById(R.id.lyt_shimmer_recipes_grid2);
        View lyt_shimmer_recipes_grid3 = findViewById(R.id.lyt_shimmer_recipes_grid3);
        if (sharedPref.getRecipesViewType() == RECIPES_LIST_SMALL) {
            lyt_shimmer_recipes_list_small.setVisibility(View.VISIBLE);
            lyt_shimmer_recipes_list_big.setVisibility(View.GONE);
            lyt_shimmer_recipes_grid2.setVisibility(View.GONE);
            lyt_shimmer_recipes_grid3.setVisibility(View.GONE);
        } else if (sharedPref.getRecipesViewType() == RECIPES_LIST_BIG) {
            lyt_shimmer_recipes_list_small.setVisibility(View.GONE);
            lyt_shimmer_recipes_list_big.setVisibility(View.VISIBLE);
            lyt_shimmer_recipes_grid2.setVisibility(View.GONE);
            lyt_shimmer_recipes_grid3.setVisibility(View.GONE);
        } else if (sharedPref.getRecipesViewType() == RECIPES_GRID_2_COLUMN) {
            lyt_shimmer_recipes_list_small.setVisibility(View.GONE);
            lyt_shimmer_recipes_list_big.setVisibility(View.GONE);
            lyt_shimmer_recipes_grid2.setVisibility(View.VISIBLE);
            lyt_shimmer_recipes_grid3.setVisibility(View.GONE);
        } else if (sharedPref.getRecipesViewType() == RECIPES_GRID_3_COLUMN) {
            lyt_shimmer_recipes_list_small.setVisibility(View.GONE);
            lyt_shimmer_recipes_list_big.setVisibility(View.GONE);
            lyt_shimmer_recipes_grid2.setVisibility(View.GONE);
            lyt_shimmer_recipes_grid3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (edtSearch.length() > 0) {
            edtSearch.setText("");
        } else {
            super.onBackPressed();
            adsManager.destroyBannerAd();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adsManager.resumeBannerAd(adsPref.getIsBannerSearch());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adsManager.destroyBannerAd();
        ;
    }

}
