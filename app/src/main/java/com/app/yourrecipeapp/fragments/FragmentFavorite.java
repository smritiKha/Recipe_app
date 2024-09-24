package com.app.yourrecipeapp.fragments;

import static com.app.yourrecipeapp.utils.Constant.RECIPES_GRID_2_COLUMN;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_GRID_3_COLUMN;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_LIST_BIG;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_LIST_SMALL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.activities.ActivityRecipeDetail;
import com.app.yourrecipeapp.activities.ActivityRecipeDetailOffline;
import com.app.yourrecipeapp.activities.MainActivity;
import com.app.yourrecipeapp.adapters.AdapterFavorite;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.databases.sqlite.DbHandler;
import com.app.yourrecipeapp.models.Recipe;
import com.app.yourrecipeapp.utils.Constant;
import com.app.yourrecipeapp.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

    List<Recipe> recipes = new ArrayList<>();
    View rootView;
    AdapterFavorite adapterFavorite;
    DbHandler dbHandler;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    SharedPref sharedPref;
    Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        sharedPref = new SharedPref(activity);
        dbHandler = new DbHandler(activity);

        linearLayout = rootView.findViewById(R.id.lyt_no_favorite);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        if (sharedPref.getRecipesViewType() == RECIPES_LIST_SMALL || sharedPref.getRecipesViewType() == RECIPES_LIST_BIG) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            int padding = activity.getResources().getDimensionPixelSize(R.dimen.grid_space_recipes);
            recyclerView.setPadding(0, padding, 0, padding);
        } else if (sharedPref.getRecipesViewType() == RECIPES_GRID_3_COLUMN) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            Tools.setPadding(recyclerView, activity.getResources().getDimensionPixelSize(R.dimen.grid_space_recipes));
        } else if (sharedPref.getRecipesViewType() == RECIPES_GRID_2_COLUMN) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            Tools.setPadding(recyclerView, activity.getResources().getDimensionPixelSize(R.dimen.grid_space_recipes));
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            Tools.setPadding(recyclerView, activity.getResources().getDimensionPixelSize(R.dimen.grid_space_recipes));
        }

        adapterFavorite = new AdapterFavorite(activity, recyclerView, recipes);
        recyclerView.setAdapter(adapterFavorite);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayData(dbHandler.getAllData());
    }

    private void displayData(List<Recipe> recipes) {
        List<Recipe> favorites = new ArrayList<>();
        if (recipes != null && recipes.size() > 0) {
            favorites.addAll(recipes);
        }
        adapterFavorite.resetListData();
        adapterFavorite.insertData(favorites);

        showNoItemView(favorites.size() == 0);

        adapterFavorite.setOnItemClickListener((view, obj, position) -> {
            if (Tools.isConnect(activity)) {
                Intent intent = new Intent(activity, ActivityRecipeDetail.class);
                intent.putExtra(Constant.EXTRA_OBJC, obj);
                startActivity(intent);
                ((MainActivity) activity).showInterstitialAd();
                ((MainActivity) activity).destroyBannerAd();
            } else {
                Intent intent = new Intent(activity, ActivityRecipeDetailOffline.class);
                intent.putExtra(Constant.EXTRA_OBJC, obj);
                startActivity(intent);
            }
        });
    }

    private void showNoItemView(boolean show) {
        if (show) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

}
