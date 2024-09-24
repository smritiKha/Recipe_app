package com.app.yourrecipeapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.app.yourrecipeapp.BuildConfig;
import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.activities.ActivityNotificationDetail;
import com.app.yourrecipeapp.activities.ActivityWebView;
import com.app.yourrecipeapp.activities.MainActivity;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.solodroid.push.sdk.provider.OneSignalPush;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {

    public static void getTheme(Context context) {
        SharedPref sharedPref = new SharedPref(context);
        if (sharedPref.getIsDarkTheme()) {
            context.setTheme(R.style.AppDarkTheme);
        } else {
            context.setTheme(R.style.AppTheme);
        }
    }

    public static void setNavigation(Activity activity) {
        SharedPref sharedPref = new SharedPref(activity);
        if (sharedPref.getIsDarkTheme()) {
            Tools.darkNavigation(activity);
        } else {
            Tools.lightNavigation(activity);
        }
        setLayoutDirection(activity);
    }

    public static void setLayoutDirection(Activity activity) {
        if (AppConfig.ENABLE_RTL_MODE) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public static void darkNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_dark_status_bar));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_dark_status_bar));
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }

    public static void lightNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_light_bottom_navigation));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_light_status_bar));
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    public static void dialogStatusBarNavigationColor(Activity activity, boolean isDarkTheme) {
        if (isDarkTheme) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_dialog_navigation_bar_dark));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_dialog_status_bar_dark));
        } else {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_dialog_navigation_bar_light));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_dialog_status_bar_light));
        }
    }

    public static void setupToolbar(AppCompatActivity activity, Toolbar toolbar, String title, boolean backButton) {
        activity.setSupportActionBar(toolbar);
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            if (backButton) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setHomeButtonEnabled(true);
            }
            activity.getSupportActionBar().setTitle(Html.fromHtml(title));
        }
        SharedPref sharedPref = new SharedPref(activity);
        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(activity.getResources().getColor(R.color.color_dark_toolbar));
        } else {
            toolbar.setBackgroundColor(activity.getResources().getColor(R.color.color_light_primary));
        }
    }

    public static void notificationOpenHandler(Context context, Intent getIntent) {
        String id = getIntent.getStringExtra(OneSignalPush.EXTRA_ID);
        String title = getIntent.getStringExtra(OneSignalPush.EXTRA_TITLE);
        String message = getIntent.getStringExtra(OneSignalPush.EXTRA_MESSAGE);
        String bigImage = getIntent.getStringExtra(OneSignalPush.EXTRA_IMAGE);
        String launchUrl = getIntent.getStringExtra(OneSignalPush.EXTRA_LAUNCH_URL);
        String link = getIntent.getStringExtra(OneSignalPush.EXTRA_LINK);
        String uniqueId = getIntent.getStringExtra(OneSignalPush.EXTRA_UNIQUE_ID);

        SharedPref sharedPref = new SharedPref(context);
        String postId;
        if (sharedPref.getPushNotificationProvider().equals("onesignal")) {
            postId = OneSignalPush.AdditionalData.postId;
        } else {
            postId = getIntent.getStringExtra(OneSignalPush.EXTRA_POST_ID);
        }

        long _postId;
        if (postId != null && !postId.equals("")) {
            _postId = Long.parseLong(postId);
        } else {
            _postId = 0;
        }

        if (getIntent.hasExtra(OneSignalPush.EXTRA_UNIQUE_ID)) {
            if (_postId > 0) {
                Intent intent = new Intent(context, ActivityNotificationDetail.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
                OneSignalPush.AdditionalData.postId = "0";
            }

            if (launchUrl != null && !launchUrl.equals("")) {
                Tools.openActivityLaunchUrl(context, title, launchUrl);
            } else {
                if (link != null && !link.equals("")) {
                    if (!link.equals("0")) {
                        Tools.openActivityLaunchUrl(context, title, link);
                    }
                }
            }

        }
    }

    public static void openActivityLaunchUrl(Context context, String title, String url) {
        if (url.contains("play.google.com") || url.contains("?target=external")) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            Intent intent = new Intent(context, ActivityWebView.class);
            intent.putExtra("title", title);
            intent.putExtra("url", url);
            context.startActivity(intent);
        }
    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c", count / Math.pow(1000, exp), "KMGTPE".charAt(exp - 1));
    }

    public static long timeStringtoMilis(String time) {
        long milis = 0;
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sd.parse(time);
            milis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return milis;
    }

    public static int getGridSpanCount(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.recycler_item_size);
        return Math.round(screenWidth / cellWidth);
    }

    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @SuppressWarnings("deprecation")
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivity.getAllNetworks();
        NetworkInfo networkInfo;
        for (Network mNetwork : networks) {
            networkInfo = connectivity.getNetworkInfo(mNetwork);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                return true;
            }
        }
        return false;
    }

    public static String decode(String code) {
        return decodeBase64(decodeBase64(decodeBase64(code)));
    }

    public static String decodeBase64(String code) {
        byte[] valueDecoded = Base64.decode(code.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    @SuppressWarnings("deprecation")
    public static boolean isConnect(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnected() || activeNetworkInfo.isConnectedOrConnecting();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String getJSONString(String url) {
        String jsonString = null;
        HttpURLConnection linkConnection = null;
        try {
            URL linkurl = new URL(url);
            linkConnection = (HttpURLConnection) linkurl.openConnection();
            int responseCode = linkConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream linkinStream = linkConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int j = 0;
                while ((j = linkinStream.read()) != -1) {
                    baos.write(j);
                }
                byte[] data = baos.toByteArray();
                jsonString = new String(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (linkConnection != null) {
                linkConnection.disconnect();
            }
        }
        return jsonString;
    }

    public static String getFormatedDateSimple(String date_str) {
        if (date_str != null && !date_str.trim().equals("")) {
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
            try {
                String newStr = newFormat.format(oldFormat.parse(date_str));
                return newStr;
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void displayPostDescription(Activity activity, WebView webView, String htmlData) {
        SharedPref sharedPref = new SharedPref(activity);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);

        webView.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = webView.getSettings();
        if (sharedPref.getFontSize() == 0) {
            webSettings.setDefaultFontSize(Constant.FONT_SIZE_XSMALL);
        } else if (sharedPref.getFontSize() == 1) {
            webSettings.setDefaultFontSize(Constant.FONT_SIZE_SMALL);
        } else if (sharedPref.getFontSize() == 2) {
            webSettings.setDefaultFontSize(Constant.FONT_SIZE_MEDIUM);
        } else if (sharedPref.getFontSize() == 3) {
            webSettings.setDefaultFontSize(Constant.FONT_SIZE_LARGE);
        } else if (sharedPref.getFontSize() == 4) {
            webSettings.setDefaultFontSize(Constant.FONT_SIZE_XLARGE);
        } else {
            webSettings.setDefaultFontSize(Constant.FONT_SIZE_MEDIUM);
        }

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";

        String bg_paragraph;
        if (sharedPref.getIsDarkTheme()) {
            bg_paragraph = "<style type=\"text/css\">body{color: #eeeeee;} a{color:#ffffff; font-weight:bold;}";
        } else {
            bg_paragraph = "<style type=\"text/css\">body{color: #000000;} a{color:#1e88e5; font-weight:bold;}";
        }

        String font_style_default = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/custom_font.ttf\")}body {font-family: MyFont; font-size: medium; overflow-wrap: break-word; word-wrap: break-word; -ms-word-break: break-all; word-break: break-all; word-break: break-word; -ms-hyphens: auto; -moz-hyphens: auto; -webkit-hyphens: auto; hyphens: auto;}</style>";

        String text_default = "<html><head>"
                + font_style_default
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bg_paragraph
                + "</style></head>"
                + "<body>"
                + htmlData
                + "</body></html>";

        String text_rtl = "<html dir='rtl'><head>"
                + font_style_default
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bg_paragraph
                + "</style></head>"
                + "<body>"
                + htmlData
                + "</body></html>";

        if (AppConfig.ENABLE_RTL_MODE) {
            webView.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            webView.loadDataWithBaseURL(null, text_default, mimeType, encoding, null);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (AppConfig.OPEN_LINK_INSIDE_APP) {
                    if (url.startsWith("http://")) {
                        Intent intent = new Intent(activity, ActivityWebView.class);
                        intent.putExtra("url", url);
                        activity.startActivity(intent);
                    }
                    if (url.startsWith("https://")) {
                        Intent intent = new Intent(activity, ActivityWebView.class);
                        intent.putExtra("url", url);
                        activity.startActivity(intent);
                    }
                    if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);
                    }
                    if (url.endsWith(".pdf")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);
                    }
                    if (url.contains("?target=external")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    activity.startActivity(intent);
                }
                return true;
            }
        });

    }

    public static void shareApp(Activity activity, String title) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    public static void getCategoryPosition(Activity activity, Intent intent) {

        if (intent.hasExtra("category_position")) {
            String select = intent.getStringExtra("category_position");
            if (select != null) {
                if (select.equals("category_position")) {
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).selectFragmentCategory();
                    }
                }
            }
        }

    }

    public static void getRecipesPosition(Activity activity, Intent intent) {

        if (intent.hasExtra("recipes_position")) {
            String select = intent.getStringExtra("recipes_position");
            if (select != null) {
                if (select.equals("recipes_position")) {
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).selectFragmentRecipe();
                    }
                }
            }
        }

    }

    public static void postDelayed(OnCompleteListener onCompleteListener, int millisecond) {
        new Handler(Looper.getMainLooper()).postDelayed(onCompleteListener::onComplete, millisecond);
    }

    public static void nestedScrollTo(final NestedScrollView nested, final View targetView) {
        //nested.post(() -> nested.scrollTo(500, targetView.getBottom()));
    }

    public static void setPadding(View view, int padding) {
        view.setPadding(padding, padding, padding, padding);
    }

    public static void setNativeAdStyle(Activity activity, LinearLayout nativeAdView, String style) {
        switch (style) {
            case "small":
            case "radio":
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_radio, null));
                break;
            case "news":
            case "medium":
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_news, null));
                break;
            default:
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_medium, null));
                break;
        }
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

}
