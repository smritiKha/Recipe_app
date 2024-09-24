package com.app.yourrecipeapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.app.yourrecipeapp.BuildConfig;
import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.databases.sqlite.DbHandler;
import com.app.yourrecipeapp.models.Recipe;
import com.app.yourrecipeapp.utils.AppBarLayoutBehavior;
import com.app.yourrecipeapp.utils.Constant;
import com.app.yourrecipeapp.utils.Tools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ActivityRecipeDetailOffline extends AppCompatActivity {

    private Recipe post;
    TextView txtRecipeTitle, txtCategory, txtRecipeTime, txtTotalViews;
    LinearLayout lytView;
    ImageView thumbnailVideo;
    ImageView recipeImage;
    private WebView webView;
    DbHandler databaseHandler;
    CoordinatorLayout parentView;
    SharedPref sharedPref;
    ImageButton btnFontSize, btnFavorite, btnShare;
    private String singleChoiceSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_recipe_detail_offline);

        sharedPref = new SharedPref(this);
        databaseHandler = new DbHandler(this);
        Tools.setNavigation(this);

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        parentView = findViewById(R.id.lyt_content);

        thumbnailVideo = findViewById(R.id.thumbnail_video);
        recipeImage = findViewById(R.id.recipe_image);
        txtRecipeTitle = findViewById(R.id.recipe_title);
        txtCategory = findViewById(R.id.category_name);
        txtRecipeTime = findViewById(R.id.recipe_time);
        webView = findViewById(R.id.recipe_description);
        txtTotalViews = findViewById(R.id.total_views);
        lytView = findViewById(R.id.lyt_view_count);

        btnFontSize = findViewById(R.id.btn_font_size);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnShare = findViewById(R.id.btn_share);

        post = (Recipe) getIntent().getSerializableExtra(Constant.EXTRA_OBJC);

        initToolbar();
        initFavorite();
        displayData();


    }

    public void displayData() {

        txtRecipeTitle.setText(post.recipe_title);
        txtCategory.setText(post.category_name);
        txtRecipeTime.setText(post.recipe_time);

        if (AppConfig.ENABLE_RECIPES_VIEW_COUNT) {
            txtTotalViews.setText(Tools.withSuffix(post.total_views) + " " + getResources().getString(R.string.views_count));
        } else {
            lytView.setVisibility(View.GONE);
        }

        if (post.content_type != null && post.content_type.equals("youtube")) {
            Glide.with(this)
                    .load(Constant.YOUTUBE_IMAGE_FRONT + post.video_id + Constant.YOUTUBE_IMAGE_BACK_MQ)
                    .placeholder(R.drawable.ic_thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(recipeImage);
        } else {
            Glide.with(this)
                    .load(sharedPref.getApiUrl() + "/upload/" + post.recipe_image)
                    .placeholder(R.drawable.ic_thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(recipeImage);
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
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(ActivityRecipeDetailOffline.this);
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

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        Tools.setupToolbar(this, toolbar, "", true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void initFavorite() {
        List<Recipe> data = databaseHandler.getFavRow(post.recipe_id);
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
            List<Recipe> data1 = databaseHandler.getFavRow(post.recipe_id);
            if (data1.size() == 0) {
                databaseHandler.AddtoFavorite(new Recipe(
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
                    databaseHandler.RemoveFav(new Recipe(post.recipe_id));
                    Snackbar.make(parentView, R.string.favorite_removed, Snackbar.LENGTH_SHORT).show();
                    btnFavorite.setImageResource(R.drawable.ic_fav_outline);
                }
            }
        });

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

}
