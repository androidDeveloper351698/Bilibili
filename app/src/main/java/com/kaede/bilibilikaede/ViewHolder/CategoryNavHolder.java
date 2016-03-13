package com.kaede.bilibilikaede.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.UI.FanJuActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by asus on 2016/2/4.
 */
public class CategoryNavHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.icon1) ImageView icon1;
    @InjectView(R.id.name1) TextView name1;
    @InjectView(R.id.container1) LinearLayout ll1;
    @InjectView(R.id.icon2) ImageView icon2;
    @InjectView(R.id.name2) TextView name2;
    @InjectView(R.id.container2) LinearLayout ll2;
    @InjectView(R.id.icon3) ImageView icon3;
    @InjectView(R.id.name3) TextView name3;
    @InjectView(R.id.container3) LinearLayout ll3;

    private Context mContext;

    public CategoryNavHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.inject(this,itemView);
        mContext = context;
    }

    public void init(int position){
        //TODO 设置监听 跳转页面
        switch (position){
            case 0:
                icon1.setImageResource(R.mipmap.ic_category_t13);
                name1.setText("番 剧");
                ll1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, FanJuActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                icon2.setImageResource(R.mipmap.ic_category_t1);
                name2.setText("动 画");
                icon3.setImageResource(R.mipmap.ic_category_t3);
                name3.setText("音 乐");
                break;
            case 1:
                icon1.setImageResource(R.mipmap.ic_category_t129);
                name1.setText("舞 蹈");
                icon2.setImageResource(R.mipmap.ic_category_t36);
                name2.setText("科 技");
                icon3.setImageResource(R.mipmap.ic_category_t4);
                name3.setText("游 戏");
                break;
            case 2:
                icon1.setImageResource(R.mipmap.ic_category_t5);
                name1.setText("娱 乐");
                icon2.setImageResource(R.mipmap.ic_category_t119);
                name2.setText("鬼 畜");
                icon3.setImageResource(R.mipmap.ic_category_t23);
                name3.setText("电 影");
                break;
            case 3:
                icon1.setImageResource(R.mipmap.ic_category_t11);
                name1.setText("电视剧");
                icon2.setVisibility(View.INVISIBLE);
                name2.setVisibility(View.INVISIBLE);
                icon3.setVisibility(View.INVISIBLE);
                name3.setVisibility(View.INVISIBLE);
                break;
            default:
                throw new IllegalArgumentException("can not be position "+position);
        }
    }
}
