package com.kaede.bilibilikaede.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.Utils.SizeUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by asus on 2016/2/5.
 */
public class CategoryChildNavHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.container) LinearLayout container;

    private int mColumn;
    private int mRow;
    private Context mContext;

    public CategoryChildNavHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.inject(this,itemView);
        mContext = context;
    }

    public void initData(String[] navTitle,int[] navImage){
        if(navTitle.length == 0 ){
            return;
        }
        container.removeAllViews();
        mColumn = navTitle.length < 4 ? navTitle.length:4;
        mRow = navTitle.length % mColumn ==0?navTitle.length/ mColumn :(navTitle.length/ mColumn)+1;

        int count = 0;
        for(int i=0;i<mRow;i++) {
            LinearLayout rowContainer = new LinearLayout(mContext);
            rowContainer.setOrientation(LinearLayout.HORIZONTAL);
            rowContainer.setWeightSum(mColumn);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,SizeUtils.dp2px(mContext,10),0,0);
            rowContainer.setLayoutParams(lp);
            container.addView(rowContainer);
            for (int j = 0; j < mColumn; j++) {
                LinearLayout ll = new LinearLayout(mContext);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dp2px(mContext, 0), ViewGroup.LayoutParams.WRAP_CONTENT, 1));//weight为1
                ll.setGravity(Gravity.CENTER);
                ImageView iv = new ImageView(mContext);
                iv.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dp2px(mContext, 36), SizeUtils.dp2px(mContext, 36)));
                iv.setImageResource(navImage[count]);
                ll.addView(iv);
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setTextSize(10);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.parseColor("#000000")); //黑色
                tv.setText(navTitle[count]);
                ll.addView(tv);
                rowContainer.addView(ll);
                count++;
                if (count == navTitle.length) {
                    return;
                }
            }
        }

    }
}
