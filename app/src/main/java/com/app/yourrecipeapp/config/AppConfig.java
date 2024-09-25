package com.app.yourrecipeapp.config;

public class AppConfig {

    //your Server Key obtained from the admin panel
    public static final String SERVER_KEY = "WVVoU01HTkViM1pNZWtVMVRXazBlRTVxWjNWTlZGbDFUVlJCZUV3elFtOWpSamw1V2xkT2NHTkhWa0pqU0VGMlpWYzVNV05zT1hsYVYwNXdZMGRXZWxneVJuZGpRMVY1VFVSS1psbFlRbmRpUjJ4cVdWaFNjR0l5TlVwYVJqbHFZakl3ZFZsWVFuZE1ibXgyWkZoS2VWcFhUbkJqUjFab1kwaEJQUT09";

    //your Rest API Key obtained from the admin panel
    public static final String REST_API_KEY = "cda11Uib7PLEA8pjKehSVfY0vdHsXI269J3MlqcGatWZBmxOgR";

    //layout customization
    public static final boolean ENABLE_RECIPE_COUNT_ON_CATEGORY = true;
    public static final boolean FORCE_VIDEO_PLAYER_TO_LANDSCAPE = false;
    public static final boolean ENABLE_RECIPES_VIEW_COUNT = true;

    //if you use RTL Language e.g : Arabic Language or other, set true
    public static final boolean ENABLE_RTL_MODE = false;

    //load more for next list videos
    public static final int POST_PER_PAGE = 12;

    //GDPR EU Consent

    public static final boolean ENABLE_GDPR_EU_CONSENT = true;

    //Url handler for recipe details
    public static final boolean OPEN_LINK_INSIDE_APP = false;

    //show exit dialog confirmation
    public static final boolean ENABLE_EXIT_DIALOG = true;

    //auto image slider duration on featured recipes
    public static final int AUTO_SLIDER_DURATION = 6000;

    //Enable it with true value if want to the app will force to display open ads first before start the main menu
//Longer duration to start the app may occur depending on internet connection or open ad response time itself
    public static final boolean FORCE_TO_SHOW_APP_OPEN_AD_ON_START = false;

    //splash screen delay time
    public static final int DELAY_SPLASH_SCREEN = 2500;

}