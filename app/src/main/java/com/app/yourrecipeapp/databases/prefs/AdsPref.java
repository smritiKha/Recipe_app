package com.app.yourrecipeapp.databases.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class AdsPref {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public AdsPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("ads_setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAds(String adStatus, String adType, String backupAds, String admobPublisherId, String admobBannerUnitId, String admobInterstitialUnitId, String admobNativeUnitId, String admobAppOpenAdUnitId, String adManagerBannerUnitId, String adManagerInterstitialUnitId, String adManagerNativeUnitId, String adManagerAppOpenAdUnitId, String fanBannerUnitId, String fanInterstitialUnitId, String fanNativeUnitId, String startappAppId, String unityGameId, String unityBannerPlacementId, String unityInterstitialPlacementId, String applovinBannerAdUnitId, String applovinInterstitialAdUnitId, String applovinNativeAdManualUnitId, String applovinAppOpenAdUnitId, String applovinBannerZoneId, String applovinBannerMrecZoneId, String applovinInterstitialZoneId, String ironSourceAppKey, String ironSourceBannerId, String ironSourceInterstitialId, String wortiseAppId, String wortiseAppOpenId, String wortiseBannerId, String wortiseInterstitialId, String wortiseNativeId, int interstitialAdInterval, int nativeAdIndex) {
        editor.putString("ad_status", adStatus);
        editor.putString("ad_type", adType);
        editor.putString("backup_ads", backupAds);
        editor.putString("admob_publisher_id", admobPublisherId);
        editor.putString("admob_banner_unit_id", admobBannerUnitId);
        editor.putString("admob_interstitial_unit_id", admobInterstitialUnitId);
        editor.putString("admob_native_unit_id", admobNativeUnitId);
        editor.putString("admob_app_open_ad_unit_id", admobAppOpenAdUnitId);
        editor.putString("ad_manager_banner_unit_id", adManagerBannerUnitId);
        editor.putString("ad_manager_interstitial_unit_id", adManagerInterstitialUnitId);
        editor.putString("ad_manager_native_unit_id", adManagerNativeUnitId);
        editor.putString("ad_manager_app_open_ad_unit_id", adManagerAppOpenAdUnitId);
        editor.putString("fan_banner_unit_id", fanBannerUnitId);
        editor.putString("fan_interstitial_unit_id", fanInterstitialUnitId);
        editor.putString("fan_native_unit_id", fanNativeUnitId);
        editor.putString("startapp_app_id", startappAppId);
        editor.putString("unity_game_id", unityGameId);
        editor.putString("unity_banner_placement_id", unityBannerPlacementId);
        editor.putString("unity_interstitial_placement_id", unityInterstitialPlacementId);
        editor.putString("applovin_banner_ad_unit_id", applovinBannerAdUnitId);
        editor.putString("applovin_interstitial_ad_unit_id", applovinInterstitialAdUnitId);
        editor.putString("applovin_native_ad_manual_unit_id", applovinNativeAdManualUnitId);
        editor.putString("applovin_app_open_ad_unit_id", applovinAppOpenAdUnitId);
        editor.putString("applovin_banner_zone_id", applovinBannerZoneId);
        editor.putString("applovin_banner_mrec_zone_id", applovinBannerMrecZoneId);
        editor.putString("applovin_interstitial_zone_id", applovinInterstitialZoneId);
        editor.putString("ironsource_app_key", ironSourceAppKey);
        editor.putString("ironsource_banner_id", ironSourceBannerId);
        editor.putString("ironsource_interstitial_id", ironSourceInterstitialId);
        editor.putString("wortise_app_id", wortiseAppId);
        editor.putString("wortise_app_open_id", wortiseAppOpenId);
        editor.putString("wortise_banner_id", wortiseBannerId);
        editor.putString("wortise_interstitial_id", wortiseInterstitialId);
        editor.putString("wortise_native_id", wortiseNativeId);
        editor.putInt("interstitial_ad_interval", interstitialAdInterval);
        editor.putInt("native_ad_index", nativeAdIndex);
        editor.apply();
    }

    public String getAdStatus() {
        return sharedPreferences.getString("ad_status", "0");
    }

    public String getMainAds() {
        return sharedPreferences.getString("ad_type", "0");
    }

    public String getBackupAds() {
        return sharedPreferences.getString("backup_ads", "none");
    }

    public String getAdMobPublisherId() {
        return sharedPreferences.getString("admob_publisher_id", "0");
    }

    public String getAdMobAppId() {
        return sharedPreferences.getString("admob_app_id", "0");
    }

    public String getAdMobBannerId() {
        return sharedPreferences.getString("admob_banner_unit_id", "0");
    }

    public String getAdMobInterstitialId() {
        return sharedPreferences.getString("admob_interstitial_unit_id", "0");
    }

    public String getAdMobNativeId() {
        return sharedPreferences.getString("admob_native_unit_id", "0");
    }

    public String getAdMobAppOpenAdId() {
        return sharedPreferences.getString("admob_app_open_ad_unit_id", "0");
    }

    public String getAdManagerBannerId() {
        return sharedPreferences.getString("ad_manager_banner_unit_id", "0");
    }

    public String getAdManagerInterstitialId() {
        return sharedPreferences.getString("ad_manager_interstitial_unit_id", "0");
    }

    public String getAdManagerNativeId() {
        return sharedPreferences.getString("ad_manager_native_unit_id", "0");
    }

    public String getAdManagerAppOpenAdId() {
        return sharedPreferences.getString("ad_manager_app_open_ad_unit_id", "0");
    }

    public String getFanBannerUnitId() {
        return sharedPreferences.getString("fan_banner_unit_id", "0");
    }

    public String getFanInterstitialUnitId() {
        return sharedPreferences.getString("fan_interstitial_unit_id", "0");
    }

    public String getFanNativeUnitId() {
        return sharedPreferences.getString("fan_native_unit_id", "0");
    }

    public String getStartappAppId() {
        return sharedPreferences.getString("startapp_app_id", "0");
    }

    public String getUnityGameId() {
        return sharedPreferences.getString("unity_game_id", "0");
    }

    public String getUnityBannerPlacementId() {
        return sharedPreferences.getString("unity_banner_placement_id", "banner");
    }

    public String getUnityInterstitialPlacementId() {
        return sharedPreferences.getString("unity_interstitial_placement_id", "video");
    }

    public String getAppLovinBannerAdUnitId() {
        return sharedPreferences.getString("applovin_banner_ad_unit_id", "0");
    }

    public String getAppLovinInterstitialAdUnitId() {
        return sharedPreferences.getString("applovin_interstitial_ad_unit_id", "0");
    }

    public String getAppLovinNativeAdManualUnitId() {
        return sharedPreferences.getString("applovin_native_ad_manual_unit_id", "0");
    }

    public String getAppLovinAppOpenAdUnitId() {
        return sharedPreferences.getString("applovin_app_open_ad_unit_id", "0");
    }

    public String getAppLovinBannerZoneId() {
        return sharedPreferences.getString("applovin_banner_zone_id", "0");
    }

    public String getAppLovinBannerMrecZoneId() {
        return sharedPreferences.getString("applovin_banner_mrec_zone_id", "0");
    }

    public String getAppLovinInterstitialZoneId() {
        return sharedPreferences.getString("applovin_interstitial_zone_id", "0");
    }

    public String getIronSourceAppKey() {
        return sharedPreferences.getString("ironsource_app_key", "0");
    }

    public String getIronSourceBannerId() {
        return sharedPreferences.getString("ironsource_banner_id", "0");
    }

    public String getIronSourceInterstitialId() {
        return sharedPreferences.getString("ironsource_interstitial_id", "0");
    }

    public String getWortiseAppId() {
        return sharedPreferences.getString("wortise_app_id", "0");
    }

    public String getWortiseAppOpenId() {
        return sharedPreferences.getString("wortise_app_open_id", "0");
    }

    public String getWortiseBannerId() {
        return sharedPreferences.getString("wortise_banner_id", "0");
    }

    public String getWortiseInterstitialId() {
        return sharedPreferences.getString("wortise_interstitial_id", "0");
    }

    public String getWortiseNativeId() {
        return sharedPreferences.getString("wortise_native_id", "0");
    }

    public int getInterstitialAdInterval() {
        return sharedPreferences.getInt("interstitial_ad_interval", 3);
    }

    public int getNativeAdIndex() {
        return sharedPreferences.getInt("native_ad_index", 6);
    }

    public void saveCounter(int counter) {
        editor.putInt("counter", counter);
        editor.apply();
    }

    public int getCounter() {
        return sharedPreferences.getInt("counter", 0);
    }

    //placement
    public void setPlacement(boolean isBannerHome, boolean isBannerPostDetails, boolean isBannerCategoryDetails, boolean isBannerSearch, boolean isInterstitialPostList, boolean isInterstitialPostDetails, boolean isNativeAdHome, boolean isNativeAdPostList, boolean isNativeAdPostDetails, boolean isNativeAdExitDialog, boolean isAppOpenAdOnStart, boolean isAppOpenAdOnResume) {
        editor.putBoolean("banner_home", isBannerHome);
        editor.putBoolean("banner_post_details", isBannerPostDetails);
        editor.putBoolean("banner_category_details", isBannerCategoryDetails);
        editor.putBoolean("banner_search", isBannerSearch);
        editor.putBoolean("interstitial_post_list", isInterstitialPostList);
        editor.putBoolean("interstitial_post_details", isInterstitialPostDetails);
        editor.putBoolean("native_ad_home", isNativeAdHome);
        editor.putBoolean("native_ad_post_list", isNativeAdPostList);
        editor.putBoolean("native_ad_post_details", isNativeAdPostDetails);
        editor.putBoolean("native_ad_exit_dialog", isNativeAdExitDialog);
        editor.putBoolean("app_open_ad_on_start", isAppOpenAdOnStart);
        editor.putBoolean("app_open_ad_on_resume", isAppOpenAdOnResume);
        editor.apply();
    }

    public boolean getIsBannerHome() {
        return sharedPreferences.getBoolean("banner_home", true);
    }

    public boolean getIsBannerPostDetails() {
        return sharedPreferences.getBoolean("banner_post_details", true);
    }

    public boolean getIsBannerCategoryDetails() {
        return sharedPreferences.getBoolean("banner_category_details", true);
    }

    public boolean getIsBannerSearch() {
        return sharedPreferences.getBoolean("banner_search", true);
    }

    public boolean getIsInterstitialPostList() {
        return sharedPreferences.getBoolean("interstitial_post_list", true);
    }

    public boolean getIsInterstitialPostDetails() {
        return sharedPreferences.getBoolean("interstitial_post_details", true);
    }

    public boolean getIsNativeAdHome() {
        return sharedPreferences.getBoolean("native_ad_home", true);
    }

    public boolean getIsNativeAdPostList() {
        return sharedPreferences.getBoolean("native_ad_post_list", true);
    }

    public boolean getIsNativeAdPostDetails() {
        return sharedPreferences.getBoolean("native_ad_post_details", true);
    }

    public boolean getIsNativeAdExitDialog() {
        return sharedPreferences.getBoolean("native_ad_exit_dialog", true);
    }

    public boolean getIsAppOpenAdOnStart() {
        return sharedPreferences.getBoolean("app_open_ad_on_start", true);
    }

    public boolean getIsAppOpenAdOnResume() {
        return sharedPreferences.getBoolean("app_open_ad_on_resume", true);
    }

    public boolean getIsOpenAd() {
        return sharedPreferences.getBoolean("open_ads", false);
    }

    public void setIsOpenAd(boolean isOpenAd) {
        editor.putBoolean("open_ads", isOpenAd);
        editor.apply();
    }

}
