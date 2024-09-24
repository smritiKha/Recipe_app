package com.app.yourrecipeapp.activities;

import static com.app.yourrecipeapp.utils.Constant.RECIPES_GRID_2_COLUMN;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_GRID_3_COLUMN;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_LIST_BIG;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_LIST_SMALL;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.yourrecipeapp.BuildConfig;
import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.adapters.AdapterSearch;
import com.app.yourrecipeapp.adapters.AdapterViewType;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.utils.Constant;
import com.app.yourrecipeapp.utils.Tools;
import com.app.yourrecipeapp.utils.ViewAnimation;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ActivitySettings extends AppCompatActivity {

    private static final String TAG = "Settings";
    MaterialSwitch switchTheme;
    LinearLayout btnSwitchTheme;
    private String singleChoiceSelected;
    TextView txtCacheSize;
    SharedPref sharedPref;
    private LinearLayout btnToggleGeneral;
    private LinearLayout btnToggleCache;
    private LinearLayout btnTogglePrivacy;
    private LinearLayout btnToggleAbout;
    private View lytExpandGeneral;
    private View lytExpandCache;
    private View lytExpandDisplay;
    private View lytExpandAbout;
    NestedScrollView nestedScrollView;
    ImageButton btnArrowGeneral;
    ImageButton btnArrowCache;
    ImageButton btnArrowDisplay;
    ImageButton btnArrowAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_settings);
        Tools.setNavigation(this);
        sharedPref = new SharedPref(this);
        initView();
        initToggleView();
        setupToolbar();
    }

    public void setupToolbar() {
        Tools.setupToolbar(this, findViewById(R.id.toolbar), getString(R.string.title_settings), true);
    }

    private void initView() {
        switchTheme = findViewById(R.id.switch_theme);
        switchTheme.setChecked(sharedPref.getIsDarkTheme());
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.e("INFO", "" + isChecked);
            sharedPref.setIsDarkTheme(isChecked);
            Tools.postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, 350);
        });

        btnSwitchTheme = findViewById(R.id.btn_switch_theme);
        btnSwitchTheme.setOnClickListener(v -> {
            if (switchTheme.isChecked()) {
                sharedPref.setIsDarkTheme(false);
                switchTheme.setChecked(false);
            } else {
                sharedPref.setIsDarkTheme(true);
                switchTheme.setChecked(true);
            }
            Tools.postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, 350);
        });

        final TextView txt_recipes_view = findViewById(R.id.txt_recipes_columns);
        if (sharedPref.getRecipesViewType() == RECIPES_LIST_SMALL) {
            txt_recipes_view.setText(getResources().getString(R.string.single_choice_list_small));
        } else if (sharedPref.getRecipesViewType() == RECIPES_LIST_BIG) {
            txt_recipes_view.setText(getResources().getString(R.string.single_choice_list_big));
        } else if (sharedPref.getRecipesViewType() == RECIPES_GRID_2_COLUMN) {
            txt_recipes_view.setText(getResources().getString(R.string.single_choice_grid_2));
        } else if (sharedPref.getRecipesViewType() == RECIPES_GRID_3_COLUMN) {
            txt_recipes_view.setText(getResources().getString(R.string.single_choice_grid_3));
        }
        findViewById(R.id.btn_recipes_columns).setOnClickListener(v -> {
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setView(R.layout.dialog_view_type)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                        if (sharedPref.getRecipesViewType() != Constant.selectedRecipesPosition) {
                            sharedPref.updateRecipesViewType(Constant.selectedRecipesPosition);
                            Tools.postDelayed(() -> {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("recipes_position", "recipes_position");
                                startActivity(intent);
                            }, 10);
                        }
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss())
                    .show();

            TextView title = alertDialog.findViewById(R.id.dialog_title);
            assert title != null;
            title.setText(getString(R.string.title_setting_recipes));

            String[] list = new String[]{
                    getString(R.string.single_choice_list_small),
                    getString(R.string.single_choice_list_big),
                    getString(R.string.single_choice_grid_2),
                    getString(R.string.single_choice_grid_3)
            };
            RecyclerView recyclerView = alertDialog.findViewById(R.id.recycler_view_type);
            assert recyclerView != null;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            AdapterViewType adapterMenu = new AdapterViewType(this, new ArrayList<>());
            adapterMenu.setListData(Arrays.asList(list));
            recyclerView.setAdapter(adapterMenu);
            recyclerView.postDelayed(() -> Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(sharedPref.getRecipesViewType())).itemView.performClick(), 0);
            Tools.postDelayed(() -> recyclerView.setVisibility(View.VISIBLE), 250);
        });

        findViewById(R.id.btn_text_size).setOnClickListener(v -> {
            String[] items = getResources().getStringArray(R.array.dialog_font_size);
            singleChoiceSelected = items[sharedPref.getFontSize()];
            int itemSelected = sharedPref.getFontSize();
            new MaterialAlertDialogBuilder(ActivitySettings.this)
                    .setTitle(getString(R.string.title_dialog_font_size))
                    .setSingleChoiceItems(items, itemSelected, (dialogInterface, i) -> singleChoiceSelected = items[i])
                    .setPositiveButton(getString(R.string.dialog_ok), (dialogInterface, i) -> {
                        if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_xsmall))) {
                            sharedPref.updateFontSize(0);
                        } else if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_small))) {
                            sharedPref.updateFontSize(1);
                        } else if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_medium))) {
                            sharedPref.updateFontSize(2);
                        } else if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_large))) {
                            sharedPref.updateFontSize(3);
                        } else if (singleChoiceSelected.equals(getResources().getString(R.string.font_size_xlarge))) {
                            sharedPref.updateFontSize(4);
                        } else {
                            sharedPref.updateFontSize(2);
                        }
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(getString(R.string.dialog_cancel), null)
                    .show();
        });

        findViewById(R.id.btn_notification).setOnClickListener(v -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID);
            } else {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", BuildConfig.APPLICATION_ID);
                intent.putExtra("app_uid", getApplicationInfo().uid);
            }
            startActivity(intent);
        });

        txtCacheSize = findViewById(R.id.txt_cache_size);
        initializeCache();

        findViewById(R.id.btn_clear_cache).setOnClickListener(v -> clearCache());

        findViewById(R.id.btn_clear_search_history).setOnClickListener(view -> {
            AdapterSearch adapterSearch = new AdapterSearch(this);
            if (adapterSearch.getItemCount() > 0) {
                adapterSearch.clearSearchHistory();
                new Handler().postDelayed(() -> {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.msg_search_history_cleared), Snackbar.LENGTH_SHORT).show();
                }, 200);
            } else {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.msg_search_history_empty), Snackbar.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_privacy_policy).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ActivityPrivacyPolicy.class));
        });

        findViewById(R.id.btn_rate).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))));

        findViewById(R.id.btn_share).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_share) + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            intent.setType("text/plain");
            startActivity(intent);
        });

        findViewById(R.id.btn_more_apps).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sharedPref.getMoreAppsUrl()))));

        findViewById(R.id.btn_about).setOnClickListener(v -> {
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setView(R.layout.custom_dialog_about)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_ok, (dialog, which) -> dialog.dismiss())
                    .show();
            TextView txtAppVersion = alertDialog.findViewById(R.id.txt_version);
            txtAppVersion.setText(getString(R.string.msg_about_version) + " " + BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")");

        });

    }

    private void initToggleView() {
        nestedScrollView = findViewById(R.id.nested_scroll_view);

        btnArrowGeneral = findViewById(R.id.btn_arrow_general);
        btnArrowCache = findViewById(R.id.btn_arrow_cache);
        btnArrowDisplay = findViewById(R.id.btn_arrow_display);
        btnArrowAbout = findViewById(R.id.btn_arrow_about);

        if (sharedPref.getIsDarkTheme()) {
            btnArrowGeneral.setColorFilter(ContextCompat.getColor(this, R.color.color_dark_icon));
            btnArrowCache.setColorFilter(ContextCompat.getColor(this, R.color.color_dark_icon));
            btnArrowDisplay.setColorFilter(ContextCompat.getColor(this, R.color.color_dark_icon));
            btnArrowAbout.setColorFilter(ContextCompat.getColor(this, R.color.color_dark_icon));
        } else {
            btnArrowGeneral.setColorFilter(ContextCompat.getColor(this, R.color.color_light_icon));
            btnArrowCache.setColorFilter(ContextCompat.getColor(this, R.color.color_light_icon));
            btnArrowDisplay.setColorFilter(ContextCompat.getColor(this, R.color.color_light_icon));
            btnArrowAbout.setColorFilter(ContextCompat.getColor(this, R.color.color_light_icon));
        }

        btnToggleGeneral = findViewById(R.id.btn_toggle_general);
        btnToggleCache = findViewById(R.id.btn_toggle_cache);
        btnTogglePrivacy = findViewById(R.id.btn_toggle_display);
        btnToggleAbout = findViewById(R.id.btn_toggle_about);

        lytExpandGeneral = findViewById(R.id.lyt_expand_general);
        lytExpandCache = findViewById(R.id.lyt_expand_cache);
        lytExpandDisplay = findViewById(R.id.lyt_expand_display);
        lytExpandAbout = findViewById(R.id.lyt_expand_about);

        btnToggleGeneral.setOnClickListener(view -> {
            toggleSection(btnToggleGeneral, lytExpandGeneral, btnArrowGeneral);
        });

        btnToggleCache.setOnClickListener(view -> {
            toggleSection(btnToggleCache, lytExpandCache, btnArrowCache);
        });

        btnTogglePrivacy.setOnClickListener(view -> {
            toggleSection(btnTogglePrivacy, lytExpandDisplay, btnArrowDisplay);
        });

        btnToggleAbout.setOnClickListener(view -> {
            toggleSection(btnToggleAbout, lytExpandAbout, btnArrowAbout);
        });

    }

    private void toggleSection(View view, View layout, ImageButton imageButton) {
        boolean show = toggleArrow(view, imageButton);
        viewAnimation(show, layout);
    }

    public boolean toggleArrow(View view, ImageButton imageButton) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(100).rotation((float) 0.001);
            imageButton.setImageResource(R.drawable.ic_arrow_expand);
            return true;
        } else {
            view.animate().setDuration(100).rotation(0);
            imageButton.setImageResource(R.drawable.ic_arrow_collapse);
            return false;
        }
    }

    public void viewAnimation(boolean show, View view) {
        if (show) {
            ViewAnimation.expand(view, () -> Tools.nestedScrollTo(nestedScrollView, view));
        } else {
            ViewAnimation.collapse(view);
        }
    }

    private void clearCache() {
        FileUtils.deleteQuietly(getCacheDir());
        FileUtils.deleteQuietly(getExternalCacheDir());
        txtCacheSize.setText(getString(R.string.sub_settings_clear_cache_start) + " 0 Bytes " + getString(R.string.sub_settings_clear_cache_end));
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.msg_cache_cleared), Snackbar.LENGTH_SHORT).show();
    }

    private void initializeCache() {
        txtCacheSize.setText(getString(R.string.sub_settings_clear_cache_start) + " " + readableFileSize((0 + getDirSize(getCacheDir())) + getDirSize(getExternalCacheDir())) + " " + getString(R.string.sub_settings_clear_cache_end));
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 Bytes";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10((double) size) / Math.log10(1024.0d));
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double d = (double) size;
        double pow = Math.pow(1024.0d, (double) digitGroups);
        Double.isNaN(d);
        stringBuilder.append(decimalFormat.format(d / pow));
        stringBuilder.append(" ");
        stringBuilder.append(units[digitGroups]);
        return stringBuilder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

}
