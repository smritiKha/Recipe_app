package com.app.yourrecipeapp.rests;

import com.app.yourrecipeapp.callbacks.CallbackCategories;
import com.app.yourrecipeapp.callbacks.CallbackCategoryDetail;
import com.app.yourrecipeapp.callbacks.CallbackConfig;
import com.app.yourrecipeapp.callbacks.CallbackHome;
import com.app.yourrecipeapp.callbacks.CallbackRecipeDetail;
import com.app.yourrecipeapp.callbacks.CallbackRecipes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiInterface {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "Data-Agent: Your Recipes App";

    @Headers({CACHE, AGENT})
    @GET("api.php?get_home")
    Call<CallbackHome> getHome(
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api.php?get_recent_recipes")
    Call<CallbackRecipes> getRecipesList(
            @Query("page") int page,
            @Query("count") int count,
            @Query("filter") String filter,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api.php?get_recipe_detail")
    Call<CallbackRecipeDetail> getRecipeDetail(
            @Query("id") String id
    );

    @Headers({CACHE, AGENT})
    @GET("api.php?get_category_index")
    Call<CallbackCategories> getAllCategories(
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api.php?get_category_posts")
    Call<CallbackCategoryDetail> getRecipesByCategory(
            @Query("id") int id,
            @Query("page") int page,
            @Query("count") int count,
            @Query("filter") String filter,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api.php?get_search_results")
    Call<CallbackRecipes> getSearch(
            @Query("search") String search,
            @Query("page") int page,
            @Query("count") int count,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api.php?get_search_results_rtl")
    Call<CallbackRecipes> getSearchRTL(
            @Query("search") String search,
            @Query("page") int page,
            @Query("count") int count,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api.php?settings")
    Call<CallbackConfig> getConfig(
            @Query("package_name") String package_name,
            @Query("api_key") String api_key
    );

}
