package com.kaede.bilibilikaede.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.Utils.Constant;
import com.kaede.bilibilikaede.ViewHolder.CategoryNavHolder;

/**
 * Created by asus on 2016/2/4.
 */
public class CategoryNavAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;

    public CategoryNavAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryNavHolder){
            ((CategoryNavHolder) holder).init(position);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_category_nav, parent, false);
        return  new CategoryNavHolder(mContext,itemView);
    }
}
