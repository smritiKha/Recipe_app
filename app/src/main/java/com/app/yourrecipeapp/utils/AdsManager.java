package com.app.yourrecipeapp.utils;

import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.IRONSOURCE;

import android.app.Activity;
import android.view.View;

import com.app.yourrecipeapp.BuildConfig;
import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.AdsPref;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.models.Ads;
import com.app.yourrecipeapp.models.Placement;
import com.solodroid.ads.sdk.format.AdNetwork;
import com.solodroid.ads.sdk.format.AppOpenAd;
import com.solodroid.ads.sdk.format.BannerAd;
import com.solodroid.ads.sdk.format.InterstitialAd;
import com.solodroid.ads.sdk.format.NativeAd;
import com.solodroid.ads.sdk.format.NativeAdView;
import com.solodroid.ads.sdk.gdpr.GDPR;
import com.solodroid.ads.sdk.gdpr.LegacyGDPR;
import com.solodroid.ads.sdk.util.OnShowAdCompleteListener;

public class AdsManager {

    Activity activity;
    AdNetwork.Initialize adNetwork;
    AppOpenAd.Builder appOpenAd;
    BannerAd.Builder bannerAd;
    InterstitialAd.Builder interstitialAd;
    NativeAd.Builder nativeAd;
    NativeAdView.Builder nativeAdView;
    SharedPref sharedPref;
    AdsPref adsPref;
    LegacyGDPR legacyGDPR;
    GDPR gdpr;

    public AdsManager(Activity activity) {
        this.activity = activity;
        this.sharedPref = new SharedPref(activity);
        this.adsPref = new AdsPref(activity);
        this.legacyGDPR = new LegacyGDPR(activity);
        this.gdpr = new GDPR(activity);
        adNetwork = new AdNetwork.Initialize(activity);
        appOpenAd = new AppOpenAd.Builder(activity);
        bannerAd = new BannerAd.Builder(activity);
        interstitialAd = new InterstitialAd.Builder(activity);
        nativeAd = new NativeAd.Builder(activity);
        nativeAdView = new NativeAdView.Builder(activity);
    }

    public void initializeAd() {
        adNetwork.setAdStatus(adsPref.getAdStatus())
                .setAdNetwork(adsPref.getMainAds())
                .setBackupAdNetwork(adsPref.getBackupAds())
                .setStartappAppId(adsPref.getStartappAppId())
                .setUnityGameId(adsPref.getUnityGameId())
                .setIronSourceAppKey(adsPref.getIronSourceAppKey())
                .setWortiseAppId(adsPref.getWortiseAppId())
                .setDebug(BuildConfig.DEBUG)
                .build();
    }

    public void loadAppOpenAd(boolean placement, OnShowAdCompleteListener onShowAdCompleteListener) {
        if (placement) {
            appOpenAd = new AppOpenAd.Builder(activity)
                    .setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobAppOpenId(adsPref.getAdMobAppOpenAdId())
                    .setAdManagerAppOpenId(adsPref.getAdManagerAppOpenAdId())
                    .setApplovinAppOpenId(adsPref.getAppLovinAppOpenAdUnitId())
                    .setWortiseAppOpenId(adsPref.getWortiseAppOpenId())
                    .build(onShowAdCompleteListener);
        } else {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    public void loadAppOpenAd(boolean placement) {
        if (placement) {
            appOpenAd = new AppOpenAd.Builder(activity)
                    .setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobAppOpenId(adsPref.getAdMobAppOpenAdId())
                    .setAdManagerAppOpenId(adsPref.getAdManagerAppOpenAdId())
                    .setApplovinAppOpenId(adsPref.getAppLovinAppOpenAdUnitId())
                    .setWortiseAppOpenId(adsPref.getWortiseAppOpenId())
                    .build();
        }
    }

    public void showAppOpenAd(boolean placement) {
        if (placement) {
            appOpenAd.show();
        }
    }

    public void destroyAppOpenAd(boolean placement) {
        if (placement) {
            appOpenAd.destroyOpenAd();
        }
    }

    public void loadBannerAd(boolean placement) {
        if (placement) {
            bannerAd.setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobBannerId(adsPref.getAdMobBannerId())
                    .setGoogleAdManagerBannerId(adsPref.getAdManagerBannerId())
                    .setFanBannerId(adsPref.getFanBannerUnitId())
                    .setUnityBannerId(adsPref.getUnityBannerPlacementId())
                    .setAppLovinBannerId(adsPref.getAppLovinBannerAdUnitId())
                    .setAppLovinBannerZoneId(adsPref.getAppLovinBannerZoneId())
                    .setIronSourceBannerId(adsPref.getIronSourceBannerId())
                    .setWortiseBannerId(adsPref.getWortiseBannerId())
                    .setDarkTheme(sharedPref.getIsDarkTheme())
                    .setPlacementStatus(1)
                    .setLegacyGDPR(false)
                    .build();
        }
    }

    public void loadInterstitialAd(boolean placement, int interval) {
        if (placement) {
            interstitialAd.setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobInterstitialId(adsPref.getAdMobInterstitialId())
                    .setGoogleAdManagerInterstitialId(adsPref.getAdManagerInterstitialId())
                    .setFanInterstitialId(adsPref.getFanInterstitialUnitId())
                    .setUnityInterstitialId(adsPref.getUnityInterstitialPlacementId())
                    .setAppLovinInterstitialId(adsPref.getAppLovinInterstitialAdUnitId())
                    .setAppLovinInterstitialZoneId(adsPref.getAppLovinInterstitialZoneId())
                    .setIronSourceInterstitialId(adsPref.getIronSourceInterstitialId())
                    .setWortiseInterstitialId(adsPref.getWortiseInterstitialId())
                    .setInterval(interval)
                    .setPlacementStatus(1)
                    .setLegacyGDPR(false)
                    .build();
        }
    }

    public void loadNativeAd(boolean placement, String nativeAdStyle) {
        if (placement) {
            nativeAd.setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobNativeId(adsPref.getAdMobNativeId())
                    .setAdManagerNativeId(adsPref.getAdManagerNativeId())
                    .setFanNativeId(adsPref.getFanNativeUnitId())
                    .setAppLovinNativeId(adsPref.getAppLovinNativeAdManualUnitId())
                    .setAppLovinDiscoveryMrecZoneId(adsPref.getAppLovinBannerMrecZoneId())
                    .setWortiseNativeId(adsPref.getWortiseNativeId())
                    .setPlacementStatus(1)
                    .setDarkTheme(sharedPref.getIsDarkTheme())
                    .setLegacyGDPR(false)
                    .setNativeAdBackgroundColor(R.color.color_light_native_ad_background, R.color.color_dark_native_ad_background)
                    .setNativeAdStyle(nativeAdStyle)
                    .build();
        }
    }

    public void loadNativeAdView(View view, boolean placement) {
        if (placement) {
            nativeAdView.setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobNativeId(adsPref.getAdMobNativeId())
                    .setAdManagerNativeId(adsPref.getAdManagerNativeId())
                    .setFanNativeId(adsPref.getFanNativeUnitId())
                    .setAppLovinNativeId(adsPref.getAppLovinNativeAdManualUnitId())
                    .setAppLovinDiscoveryMrecZoneId(adsPref.getAppLovinBannerMrecZoneId())
                    .setWortiseNativeId(adsPref.getWortiseNativeId())
                    .setPlacementStatus(1)
                    .setDarkTheme(sharedPref.getIsDarkTheme())
                    .setLegacyGDPR(false)
                    .setView(view)
                    .setNativeAdBackgroundColor(R.color.color_light_native_ad_background, R.color.color_dark_native_ad_background)
                    .setNativeAdStyle(Constant.NATIVE_AD_STYLE_RECIPES_HOME)
                    .build();
        }
    }

    public void showInterstitialAd() {
        interstitialAd.show();
    }

    public void destroyBannerAd() {
        bannerAd.destroyAndDetachBanner();
    }

    public void resumeBannerAd(boolean placement) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && !adsPref.getIronSourceBannerId().equals("0")) {
            if (adsPref.getMainAds().equals(IRONSOURCE) || adsPref.getBackupAds().equals(IRONSOURCE)) {
                loadBannerAd(placement);
            }
        }
    }

    public void updateConsentStatus() {
        if (AppConfig.ENABLE_GDPR_EU_CONSENT) {
            gdpr.updateGDPRConsentStatus(adsPref.getMainAds(), false, false);
        }
    }

    public void saveAds(AdsPref adsPref, Ads ads) {
        adsPref.saveAds(
                ads.ad_status.replace("on", "1"),
                ads.ad_type,
                ads.backup_ads,
                ads.admob_publisher_id,
                ads.admob_banner_unit_id,
                ads.admob_interstitial_unit_id,
                ads.admob_native_unit_id,
                ads.admob_app_open_ad_unit_id,
                ads.ad_manager_banner_unit_id,
                ads.ad_manager_interstitial_unit_id,
                ads.ad_manager_native_unit_id,
                ads.ad_manager_app_open_ad_unit_id,
                ads.fan_banner_unit_id,
                ads.fan_interstitial_unit_id,
                ads.fan_native_unit_id,
                ads.startapp_app_id,
                ads.unity_game_id,
                ads.unity_banner_placement_id,
                ads.unity_interstitial_placement_id,
                ads.applovin_banner_ad_unit_id,
                ads.applovin_interstitial_ad_unit_id,
                ads.applovin_native_ad_manual_unit_id,
                ads.applovin_app_open_ad_unit_id,
                ads.applovin_banner_zone_id,
                ads.applovin_banner_mrec_zone_id,
                ads.applovin_interstitial_zone_id,
                ads.ironsource_app_key,
                ads.ironsource_banner_placement_name,
                ads.ironsource_interstitial_placement_name,
                ads.wortise_app_id,
                ads.wortise_app_open_unit_id,
                ads.wortise_banner_unit_id,
                ads.wortise_interstitial_unit_id,
                ads.wortise_native_unit_id,
                ads.interstitial_ad_interval,
                ads.native_ad_index
        );
    }

    public void saveAdsPlacement(AdsPref adsPref, Placement placement) {
        adsPref.setPlacement(
                placement.banner_home == 1,
                placement.banner_post_details == 1,
                placement.banner_category_details == 1,
                placement.banner_search == 1,
                placement.interstitial_post_list == 1,
                placement.interstitial_post_details == 1,
                placement.native_ad_home == 1,
                placement.native_ad_post_list == 1,
                placement.native_ad_post_details == 1,
                placement.native_ad_exit_dialog == 1,
                placement.app_open_ad_on_start == 1,
                placement.app_open_ad_on_resume == 1
        );
    }

}
