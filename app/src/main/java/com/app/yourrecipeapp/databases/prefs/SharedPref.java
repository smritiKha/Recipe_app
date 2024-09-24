package com.app.yourrecipeapp.databases.prefs;

import static com.app.yourrecipeapp.utils.Constant.RECIPES_GRID_2_COLUMN;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsDarkTheme() {
        return sharedPreferences.getBoolean("theme", false);
    }

    public void setIsDarkTheme(Boolean isDarkTheme) {
        editor.putBoolean("theme", isDarkTheme);
        editor.apply();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setDefaultFilterRecipes(int i) {
        editor.putInt("filter", i);
        editor.apply();
    }

    public Integer getCurrentFilterRecipes() {
        return sharedPreferences.getInt("filter", 0);
    }

    public void updateFilterRecipes(int position) {
        editor.putInt("filter", position);
        editor.apply();
    }

    public Integer getRecipesViewType() {
        return sharedPreferences.getInt("recipes_list", RECIPES_GRID_2_COLUMN);
    }

    public void updateRecipesViewType(int position) {
        editor.putInt("recipes_list", position);
        editor.apply();
    }

    public void setYoutubeApiKey() {
        editor.putInt("youtube_api_key", 0);
        editor.apply();
    }

    public void saveConfig(String api_url, String application_id) {
        editor.putString("api_url", api_url);
        editor.putString("application_id", application_id);
        editor.apply();
    }

    public String getApiUrl() {
        return sharedPreferences.getString("api_url", "http://example.com");
    }

    public String getApplicationId() {
        return sharedPreferences.getString("application_id", "com.app.yourrecipeapp");
    }

    public void saveCredentials(String youtube_api_key, String privacy_policy, String more_apps_url, String redirect_url, String pushNotificationProvider) {
        editor.putString("youtube_api_key", youtube_api_key);
        editor.putString("privacy_policy", privacy_policy);
        editor.putString("more_apps_url", more_apps_url);
        editor.putString("redirect_url", redirect_url);
        editor.putString("providers", pushNotificationProvider);
        editor.apply();
    }

    public String getYoutubeAPIKey() {
        return sharedPreferences.getString("youtube_api_key", "0");
    }

    public String getPrivacyPolicy() {
        return sharedPreferences.getString("privacy_policy", "");
    }

    public String getMoreAppsUrl() {
        return sharedPreferences.getString("more_apps_url", "https://play.google.com/store/apps/developer?id=Solodroid");
    }

    public String getRedirectUrl() {
        return sharedPreferences.getString("redirect_url", "");
    }

    public Integer getInAppReviewToken() {
        return sharedPreferences.getInt("in_app_review_token", 0);
    }

    public void updateInAppReviewToken(int value) {
        editor.putInt("in_app_review_token", value);
        editor.apply();
    }

    public Integer getFontSize() {
        return sharedPreferences.getInt("font_size", 2);
    }

    public void updateFontSize(int font_size) {
        editor.putInt("font_size", font_size);
        editor.apply();
    }

    public String getPushNotificationProvider() {
        return sharedPreferences.getString("providers", "");
    }

}
