package com.app.yourrecipeapp.adapters;

import static com.app.yourrecipeapp.config.AppConfig.POST_PER_PAGE;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_LIST_BIG;
import static com.app.yourrecipeapp.utils.Constant.RECIPES_LIST_SMALL;
import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_DISCOVERY;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.FACEBOOK;
import static com.solodroid.ads.sdk.util.Constant.FAN;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.STARTAPP;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.databases.prefs.AdsPref;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.models.Recipe;
import com.app.yourrecipeapp.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.solodroid.ads.sdk.format.NativeAdViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecipes extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_PROG = 0;
    private final int VIEW_ITEM = 1;
    private final int VIEW_AD = 2;

    private List<Recipe> items;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    Context context;
    private OnItemClickListener mOnItemClickListener;
    boolean scrolling = false;

    SharedPref sharedPref;
    AdsPref adsPref;

    public interface OnItemClickListener {
        void onItemClick(View view, Recipe obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterRecipes(Context context, RecyclerView view, List<Recipe> items) {
        this.items = items;
        this.context = context;
        this.sharedPref = new SharedPref(context);
        this.adsPref = new AdsPref(context);
        lastItemViewDetector(view);
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    scrolling = true;
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrolling = false;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public TextView recipe_title;
        public ImageView recipe_image;
        public ImageView thumbnail_video;
        public LinearLayout lyt_parent;

        OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            recipe_title = v.findViewById(R.id.recipe_title);
            recipe_image = v.findViewById(R.id.recipe_image);
            thumbnail_video = v.findViewById(R.id.thumbnail_video);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.load_more);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            SharedPref sharedPref = new SharedPref(context);
            if (sharedPref.getRecipesViewType() == RECIPES_LIST_SMALL) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_list_small, parent, false);
                vh = new OriginalViewHolder(v);
            } else if (sharedPref.getRecipesViewType() == RECIPES_LIST_BIG) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_list_big, parent, false);
                vh = new OriginalViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_grid, parent, false);
                vh = new OriginalViewHolder(v);
            }
        } else if (viewType == VIEW_AD) {
            View v = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_medium, parent, false);
            vh = new NativeAdViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Recipe p = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.category_name.setText(p.category_name);
            vItem.recipe_title.setText(p.recipe_title);

            if (p.content_type != null && p.content_type.equals("youtube")) {
                Glide.with(context)
                        .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK_MQ)
                        .placeholder(R.drawable.ic_thumbnail)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(vItem.recipe_image);
            } else {
                Glide.with(context)
                        .load(sharedPref.getApiUrl() + "/upload/" + p.recipe_image)
                        .placeholder(R.drawable.ic_thumbnail)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(vItem.recipe_image);
            }

            if (p.content_type != null && p.content_type.equals("Post")) {
                vItem.thumbnail_video.setVisibility(View.GONE);
            } else {
                vItem.thumbnail_video.setVisibility(View.VISIBLE);
            }

            vItem.lyt_parent.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, p, position);
                }
            });

        } else if (holder instanceof NativeAdViewHolder) {

            final NativeAdViewHolder vItem = (NativeAdViewHolder) holder;
            if (adsPref.getAdStatus().equals(AD_STATUS_ON) && adsPref.getIsNativeAdPostList()) {
                vItem.loadNativeAd(context,
                        adsPref.getAdStatus(),
                        1,
                        adsPref.getMainAds(),
                        adsPref.getBackupAds(),
                        adsPref.getAdMobNativeId(),
                        adsPref.getAdManagerNativeId(),
                        adsPref.getFanNativeUnitId(),
                        adsPref.getAppLovinNativeAdManualUnitId(),
                        adsPref.getAppLovinBannerMrecZoneId(),
                        adsPref.getWortiseNativeId(),
                        sharedPref.getIsDarkTheme(),
                        false,
                        Constant.NATIVE_AD_STYLE_RECIPES_LIST,
                        android.R.color.transparent,
                        android.R.color.transparent
                );
            }

            if (sharedPref.getIsDarkTheme()) {
                vItem.setNativeAdBackgroundResource(R.drawable.bg_native_dark);
            } else {
                vItem.setNativeAdBackgroundResource(R.drawable.bg_native_light);
            }

            if (sharedPref.getRecipesViewType() == RECIPES_LIST_SMALL || sharedPref.getRecipesViewType() == RECIPES_LIST_BIG) {
                vItem.setNativeAdMargin(
                        context.getResources().getDimensionPixelOffset(R.dimen.spacing_middle),
                        context.getResources().getDimensionPixelOffset(R.dimen.spacing_middle),
                        context.getResources().getDimensionPixelOffset(R.dimen.spacing_middle),
                        context.getResources().getDimensionPixelOffset(R.dimen.spacing_middle)
                );
            } else {
                vItem.setNativeAdMargin(
                        context.getResources().getDimensionPixelOffset(R.dimen.spacing_small),
                        context.getResources().getDimensionPixelOffset(R.dimen.spacing_small),
                        context.getResources().getDimensionPixelOffset(R.dimen.spacing_small),
                        context.getResources().getDimensionPixelOffset(R.dimen.spacing_middle)
                );
            }

            vItem.setNativeAdPadding(
                    context.getResources().getDimensionPixelOffset(R.dimen.no_margin),
                    context.getResources().getDimensionPixelOffset(R.dimen.spacing_small),
                    context.getResources().getDimensionPixelOffset(R.dimen.no_margin),
                    context.getResources().getDimensionPixelOffset(R.dimen.spacing_small)
            );

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

        if (getItemViewType(position) == VIEW_PROG || getItemViewType(position) == VIEW_AD) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        } else {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(false);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Recipe recipe = items.get(position);
        if (recipe != null) {
            if (recipe.recipe_title == null || recipe.recipe_title.equals("")) {
                return VIEW_AD;
            }
            return VIEW_ITEM;
        } else {
            return VIEW_PROG;
        }
    }

    public void insertDataWithNativeAd(List<Recipe> items) {
        setLoaded();
        int positionStart = getItemCount();
        if (items.size() >= adsPref.getNativeAdIndex())
            items.add(adsPref.getNativeAdIndex(), new Recipe());
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void insertData(List<Recipe> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {

        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = getLastVisibleItem(layoutManager.findLastVisibleItemPositions(null));
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        AdsPref adsPref = new AdsPref(context);
                        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && adsPref.getIsNativeAdPostList()) {
                            switch (adsPref.getMainAds()) {
                                case ADMOB:
                                case GOOGLE_AD_MANAGER:
                                case FAN:
                                case FACEBOOK:
                                case STARTAPP:
                                case APPLOVIN:
                                case APPLOVIN_MAX:
                                case APPLOVIN_DISCOVERY:
                                case WORTISE: {
                                    int current_page = getItemCount() / (POST_PER_PAGE + 1); //posts per page plus 1 Ad
                                    onLoadMoreListener.onLoadMore(current_page);
                                    break;
                                }
                                default: {
                                    int current_page = getItemCount() / (POST_PER_PAGE);
                                    onLoadMoreListener.onLoadMore(current_page);
                                    break;
                                }
                            }
                        } else {
                            int current_page = getItemCount() / (POST_PER_PAGE);
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    private int getLastVisibleItem(int[] into) {
        int last_idx = into[0];
        for (int i : into) {
            if (last_idx < i) last_idx = i;
        }
        return last_idx;
    }

}