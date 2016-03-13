package com.kaede.bilibilikaede.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.View.ImageViewPager;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by asus on 2016/1/21.
 *
 */
public class PagerHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.image_pager_recommend)
    ImageViewPager imageViewPager;

    public PagerHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this,itemView);
    }

    public void initData(int[] res){
        imageViewPager.setupWithImageResource(res);
    }
}
