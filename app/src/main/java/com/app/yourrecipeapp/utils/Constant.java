package com.app.yourrecipeapp.utils;

public class Constant {

    public static final String NATIVE_AD_STYLE_RECIPES_HOME = "large";
    public static final String NATIVE_AD_STYLE_RECIPES_LIST = "large";
    public static final String NATIVE_AD_STYLE_RECIPES_DETAILS = "large";
    public static final String NATIVE_AD_STYLE_EXIT_DIALOG = "medium";

    //font size for content description
    public static final int FONT_SIZE_XSMALL = 12;
    public static final int FONT_SIZE_SMALL = 14;
    public static final int FONT_SIZE_MEDIUM = 16;
    public static final int FONT_SIZE_LARGE = 18;
    public static final int FONT_SIZE_XLARGE = 20;

    //global variables
    public static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";
    public static final String KEY_VIDEO_ID = "video_id";
    public static final String YOUTUBE_IMAGE_FRONT = "http://img.youtube.com/vi/";
    public static final String YOUTUBE_IMAGE_BACK_MQ = "/mqdefault.jpg";
    public static final long DELAY_TIME = 100;
    public static final int MAX_SEARCH_RESULT = 100;
    public static final String FILTER_SHOW_ALL_RECIPES = "n.content_type != 'Null' ";
    public static final String FILTER_SHOW_ONLY_RECIPES_POSTS = "n.content_type = 'Post'";
    public static final String FILTER_SHOW_ONLY_RECIPES_VIDEOS = "n.content_type != 'Post'";
    public static final int RECIPES_LIST_SMALL = 0;
    public static final int RECIPES_LIST_BIG = 1;
    public static final int RECIPES_GRID_2_COLUMN = 2;
    public static final int RECIPES_GRID_3_COLUMN = 3;
    public static int selectedRecipesPosition;
    public static boolean isAppOpen = false;

    //Local IP Address
    public static final String LOCALHOST_ADDRESS = "http://10.0.2.2";

}