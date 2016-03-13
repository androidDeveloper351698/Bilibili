package com.kaede.bilibilikaede.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.kaede.bilibilikaede.Domain.Video.VideoDetail;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.View.VideoCardView;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by asus on 2016/2/29.
 *
 */
public class DongTaiContentHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.card1_item_group) VideoCardView card1;
    @InjectView(R.id.card2_item_group) VideoCardView card2;

    private Context mContext;

    public DongTaiContentHolder(View itemView,Context context) {
        super(itemView);
        ButterKnife.inject(this,itemView);
        mContext = context;
    }

    public void initData(VideoDetail detail1,VideoDetail detail2){
        card1.setTitle(detail1.getTitle());
        card1.setPlayCount(detail1.getPlay());
        card1.setDanmaCount(detail1.getVideoReview());
        card1.setStartPlayActivityListener(detail1.getAid(),detail1.getPic());
        String imagePath = detail1.getPic();
        Picasso.with(mContext).load(imagePath).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.bili_default_image_tv).into(card1.getImageView());

        if(detail2 !=null){
            card2.setTitle(detail2.getTitle());
            card2.setPlayCount(detail2.getPlay());
            card2.setDanmaCount(detail2.getVideoReview());
            card2.setStartPlayActivityListener(detail2.getAid(),detail2.getPic());
            String imagePath2 = detail2.getPic();
            Picasso.with(mContext).load(imagePath2).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.bili_default_image_tv).into(card2.getImageView());
        }
    }
}
