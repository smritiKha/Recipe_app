package com.app.yourrecipeapp.adapters;

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
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.models.Recipe;
import com.app.yourrecipeapp.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class AdapterHomeRecipes extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Recipe> items;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    boolean scrolling = false;

    private SharedPref sharedPref;

    public interface OnItemClickListener {
        void onItemClick(View view, Recipe obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterHomeRecipes(Context context, RecyclerView view, List<Recipe> items) {
        this.items = items;
        this.context = context;
        this.sharedPref = new SharedPref(context);
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
        public View view_margin;

        public OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            recipe_title = v.findViewById(R.id.recipe_title);
            recipe_image = v.findViewById(R.id.recipe_image);
            thumbnail_video = v.findViewById(R.id.thumbnail_video);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            view_margin = v.findViewById(R.id.view_margin);
        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.load_more);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recipe, parent, false);
            vh = new OriginalViewHolder(v);
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

            if (position == 0) {
                vItem.view_margin.setVisibility(View.VISIBLE);
            } else {
                vItem.view_margin.setVisibility(View.GONE);
            }

            vItem.lyt_parent.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, p, position);
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

        if (getItemViewType(position) == VIEW_PROG) {
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
        if (items.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROG;
        }
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
                        int current_page = getItemCount() / AppConfig.POST_PER_PAGE;
                        onLoadMoreListener.onLoadMore(current_page);
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