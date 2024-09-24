package com.app.yourrecipeapp.config;

public class AppConfig {

    //your Server Key obtained from the admin panel
    public static final String SERVER_KEY = "WVVoU01HTkViM1pNTW5oMldUSkdjMkZIT1hwa1F6bG9Xa2N4Y0dKc09YZFpWelZzWWtNNU5XSXpWbmxZTTBwc1dUSnNkMXBZVG1aWldFSjNXREpHZDJOSGVIQlpNa1l3WVZjNWRWTlhVbVpaTWpsMFRHMUdkMk5ETlRWaU0xWjVZMjFXYW1GWVFteFpXRUoz";

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
    public static final int DELAY_SPLASH_SCREEN = 1500;

}