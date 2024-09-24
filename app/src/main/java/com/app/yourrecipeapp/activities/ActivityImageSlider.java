package com.app.yourrecipeapp.activities;

import static com.app.yourrecipeapp.config.AppConfig.ENABLE_RTL_MODE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.adapters.AdapterImageSlider;
import com.app.yourrecipeapp.callbacks.CallbackRecipeDetail;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.models.Images;
import com.app.yourrecipeapp.rests.RestAdapter;
import com.app.yourrecipeapp.utils.Tools;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityImageSlider extends AppCompatActivity {

    private Call<CallbackRecipeDetail> callbackCall = null;
    ImageButton btnClose;
    TextView txtNumber;
    ViewPager viewPager;
    String recipeId;
    int position;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDarkTheme);
        setContentView(R.layout.activity_image_slider);
        Tools.darkNavigation(this);
        Tools.setLayoutDirection(this);
        sharedPref = new SharedPref(this);

        btnClose = findViewById(R.id.lyt_close);
        btnClose.setOnClickListener(view -> finish());

        txtNumber = findViewById(R.id.txt_number);

        Intent intent = getIntent();
        if (null != intent) {
            position = intent.getIntExtra("position", 0);
            recipeId = intent.getStringExtra("recipe_id");
        }

        requestAction();

    }

    private void requestAction() {
        showFailedView(false, "");
        requestPostData();
    }

    private void requestPostData() {
        this.callbackCall = RestAdapter.createAPI(sharedPref.getApiUrl()).getRecipeDetail(recipeId);
        this.callbackCall.enqueue(new Callback<CallbackRecipeDetail>() {
            public void onResponse(@NonNull Call<CallbackRecipeDetail> call, @NonNull Response<CallbackRecipeDetail> response) {
                CallbackRecipeDetail responseHome = response.body();
                if (responseHome == null || !responseHome.status.equals("ok")) {
                    onFailRequest();
                    return;
                }
                displayAllData(responseHome);
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
        if (Tools.isConnect(ActivityImageSlider.this)) {
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

    private void displayAllData(CallbackRecipeDetail responseHome) {
        displayImages(responseHome.images);
    }

    private void displayImages(final List<Images> list) {

        viewPager = findViewById(R.id.view_pager_image);
        final AdapterImageSlider adapter = new AdapterImageSlider(ActivityImageSlider.this, list);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            public void onPageSelected(final int position) {
                super.onPageSelected(position);
                txtNumber.setText((position + 1) + " of " + list.size());
            }

        });

        if (ENABLE_RTL_MODE) {
            viewPager.setRotationY(180);
        }

        txtNumber.setText((position + 1) + " of " + list.size());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onDestroy() {
        if (!(callbackCall == null || callbackCall.isCanceled())) {
            this.callbackCall.cancel();
        }
        super.onDestroy();
    }


}
