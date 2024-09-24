package com.app.yourrecipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.models.Category;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class AdapterHomeCategory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Category> items;
    private SharedPref sharedPref;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Category obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterHomeCategory(Context context, List<Category> items) {
        this.items = items;
        this.context = context;
        this.sharedPref = new SharedPref(context);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView category_name;
        public TextView recipe_count;
        public ImageView category_image;
        public LinearLayout lyt_parent;
        public View view_margin;

        public OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            recipe_count = v.findViewById(R.id.video_count);
            category_image = v.findViewById(R.id.category_image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            view_margin = v.findViewById(R.id.view_margin);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_category, parent, false);
        return new OriginalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final Category c = items.get(position);
        final OriginalViewHolder vItem = (OriginalViewHolder) holder;

        vItem.category_name.setText(c.category_name);

        if (AppConfig.ENABLE_RECIPE_COUNT_ON_CATEGORY) {
            vItem.recipe_count.setVisibility(View.VISIBLE);
            vItem.recipe_count.setText(c.recipes_count + " " + context.getResources().getString(R.string.recipes_count_text));
        } else {
            vItem.recipe_count.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(sharedPref.getApiUrl() + "/upload/category/" + c.category_image)
                .placeholder(R.drawable.ic_thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(vItem.category_image);

        if (position == 0) {
            vItem.view_margin.setVisibility(View.VISIBLE);
        } else {
            vItem.view_margin.setVisibility(View.GONE);
        }

        vItem.lyt_parent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, c, position);
            }
        });
    }

    public void setListData(List<Category> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}