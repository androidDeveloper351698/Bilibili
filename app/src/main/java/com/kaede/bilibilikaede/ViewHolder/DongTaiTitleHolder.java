package com.kaede.bilibilikaede.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaede.bilibilikaede.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by asus on 2016/2/29.
 *
 */
public class DongTaiTitleHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.container_title_item_group) RelativeLayout title;

    private Context mContext;
    private int mCategory;

    public DongTaiTitleHolder(View itemView, Context context, int category) {
        super(itemView);
        ButterKnife.inject(this,itemView);
        mContext = context;
        mCategory = category;
    }

    public void initData(){
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Category is "+mCategory,Toast.LENGTH_LONG).show();
            }
        });
    }
}
