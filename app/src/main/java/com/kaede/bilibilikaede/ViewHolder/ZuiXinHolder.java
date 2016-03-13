package com.kaede.bilibilikaede.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaede.bilibilikaede.Database.VideoDao;
import com.kaede.bilibilikaede.Domain.Video.VideoDetail;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.Utils.Constant;
import com.kaede.bilibilikaede.View.VideoCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by asus on 2016/2/5.
 */
public class ZuiXinHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.container_title_item_group) RelativeLayout titleContainer;
    @InjectView(R.id.title_item_group) TextView titleText;
    @InjectView(R.id.enter_image_item_group) TextView enterText;
    @InjectView(R.id.card1_item_group) VideoCardView card1;
    @InjectView(R.id.card2_item_group) VideoCardView card2;
    @InjectView(R.id.card3_item_group) VideoCardView card3;
    @InjectView(R.id.card4_item_group) VideoCardView card4;

    private Context mContext;
    private int mCategory;
    private ArrayList<VideoDetail> videoDetails = new ArrayList<>();

    public ZuiXinHolder(View itemView, Context context, int category) {
        super(itemView);
        ButterKnife.inject(this,itemView);
        mContext = context;
        mCategory = category;
    }

    public void initData(){
        switch (mCategory){
            case Constant.FAN_JU:
                titleText.setText("最新视频");
                titleText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_header_new,0,0,0);
                enterText.setText("进去看看");
                enterText.setBackgroundResource(R.drawable.item_group_right_more_mask);
                titleContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到排行榜页面
                    }
                });
                changeItem();
                break;
            default:
                break;
        }
    }

    public void changeItem(){
        VideoDao dao = new VideoDao(mContext);
        videoDetails.clear();
        ArrayList<VideoDetail> list = dao.queryVideoByCategory(VideoDao.TYPE_RECENT, mCategory, VideoDao.FLAG_TOP_FOUR_TABLE_RECENT);
        videoDetails.addAll(list);
        if(list.size()>=4) {
            VideoDetail detail1 = this.videoDetails.get(0);
            card1.setTitle(detail1.getTitle());
            card1.setPlayCount(detail1.getPlay());
            card1.setDanmaCount(detail1.getComment());
            card1.setStartPlayActivityListener(detail1.getAid(), detail1.getPic());
            if (detail1.getPic() != null && !"".equals(detail1.getPic())) {
                Picasso.with(mContext).load(detail1.getPic()).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.bili_default_image_tv).into(card1.getImageView());
            }
            VideoDetail detail2 = this.videoDetails.get(1);
            card2.setTitle(detail2.getTitle());
            card2.setPlayCount(detail2.getPlay());
            card2.setDanmaCount(detail2.getComment());
            card2.setStartPlayActivityListener(detail2.getAid(), detail2.getPic());
            if (detail2.getPic() != null && !"".equals(detail2.getPic())) {
                Picasso.with(mContext).load(detail2.getPic()).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.bili_default_image_tv).into(card2.getImageView());
            }
            VideoDetail detail3 = this.videoDetails.get(2);
            card3.setTitle(detail3.getTitle());
            card3.setPlayCount(detail3.getPlay());
            card3.setDanmaCount(detail3.getComment());
            card3.setStartPlayActivityListener(detail3.getAid(), detail3.getPic());
            if (detail3.getPic() != null && !"".equals(detail3.getPic())) {
                Picasso.with(mContext).load(detail3.getPic()).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.bili_default_image_tv).into(card3.getImageView());
            }
            VideoDetail detail4 = this.videoDetails.get(3);
            card4.setTitle(detail4.getTitle());
            card4.setPlayCount(detail4.getPlay());
            card4.setDanmaCount(detail4.getComment());
            card4.setStartPlayActivityListener(detail4.getAid(), detail4.getPic());
            if (detail4.getPic() != null && !"".equals(detail4.getPic())) {
                Picasso.with(mContext).load(detail4.getPic()).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.bili_default_image_tv).into(card4.getImageView());
            }
        }
    }
}
