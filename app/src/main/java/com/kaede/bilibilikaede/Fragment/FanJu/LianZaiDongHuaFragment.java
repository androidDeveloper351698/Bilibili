package com.kaede.bilibilikaede.Fragment.FanJu;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kaede.bilibilikaede.Fragment.BaseFragment;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.View.AutoSizeMultiLinearLayout;
import com.kaede.bilibilikaede.View.MyAdapter;
import com.kaede.bilibilikaede.Utils.SizeUtils;

import org.apmem.tools.layouts.FlowLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by asus on 2016/2/5.
 */
public class LianZaiDongHuaFragment extends BaseFragment {
    @InjectView(R.id.scroller_container)
    LinearLayout scroller_container;
    @InjectView(R.id.scroller)
    HorizontalScrollView scrollerHorizontal;
    @InjectView(R.id.ll_scroller)
    LinearLayout ll_scroller;
    @InjectView(R.id.arrowDown)
    ImageView arrowDown;

    private View popContent;
    private PopupWindow dropDown;
    private FlowLayout flowLayout;

    private String[] dummyTags = {"全部", "BILIBILI正版", "OVA·OAD", "剧场版", "特典", "TV动画", "物语系列", "BILIBILI独家正版", "福山润", "竹达彩奈", "百合", "小松未可子", "杉田智和", "野良神", "野良神ARAGOTO", "泡面番", "重装武器", "K", "小野大辅", "K RETURN OF KINGS", "FATE/STAYNIGHT-UBW-", "宫野真守", "立花慎之介", "传颂之物 虚伪的假面", "GORAxGOHANDS", "神谷浩史", "樱井孝宏"};

    @Override
    protected View initContentView(LayoutInflater inflater, ViewGroup container) {
        View contentView = inflater.inflate(R.layout.fragment_lianzai, container, false);
        popContent = inflater.inflate(R.layout.item_drop_down, container, false);
        ButterKnife.inject(this, contentView);
        return contentView;
    }

    @Override
    protected void initData() {
        initScrollerHorizontal(dummyTags);
        initPopupWindow();
    }

    @Override
    protected void initListener() {
        arrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dropDown.isShowing()) {
                    dropDown.showAtLocation(scroller_container, Gravity.TOP | Gravity.LEFT, 0, SizeUtils.dp2px(getActivity(), 116));
                }
            }
        });
    }

    private void initScrollerHorizontal(String[] tags) {
        for (int i = 0; i < tags.length; i++) {
            TextView tv = new TextView(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5));
            tv.setLayoutParams(lp);
            tv.setPadding(SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5));
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setBackgroundResource(R.drawable.text_bg_selected);
            tv.setText(tags[i]);
            tv.setSelected(false);
            ll_scroller.addView(tv);
        }
        TextView tv = (TextView) ll_scroller.getChildAt(0);
        tv.setSelected(true);
        tv.setTextColor(Color.parseColor("#fb7299"));
    }

    private void initPopupWindow() {
        flowLayout = (FlowLayout) popContent.findViewById(R.id.flowLayout);
        initFlow(dummyTags);
        initBottom(popContent);
        dropDown = new PopupWindow(popContent, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        dropDown.setBackgroundDrawable(new BitmapDrawable());
        dropDown.setOutsideTouchable(true);
    }

    private void initFlow(final String[] tags) {
        for(int i=0;i<tags.length;i++){
            TextView tv = new TextView(getActivity());
            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setPadding(SizeUtils.dp2px(getActivity(), 5), 0, SizeUtils.dp2px(getActivity(), 5), 0);//5dp 左右padding
            lp.setMargins(SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 5));//5dp 全margin
            tv.setLayoutParams(lp);
            tv.setText(tags[i]);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setTextSize(16);
            tv.setBackgroundResource(R.drawable.text_bg_selector);
            flowLayout.addView(tv);
        }

        TextView firstTv = (TextView)flowLayout.getChildAt(0);
        firstTv.setTextColor(Color.parseColor("#FF4081"));
        firstTv.setSelected(true);

        for (int i = 1; i < flowLayout.getChildCount(); i++) {
            TextView textView = (TextView) flowLayout.getChildAt(i);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setSelected(false);
        }
    }

    private void initBottom(View contentView) {
        TextView tv_default = (TextView) contentView.findViewById(R.id.tv_default);
        TextView tv_play = (TextView) contentView.findViewById(R.id.tv_play);
        TextView tv_bullet = (TextView) contentView.findViewById(R.id.tv_bullet);
        TextView tv_comment = (TextView) contentView.findViewById(R.id.tv_comment);
        TextView tv_tv_fav = (TextView) contentView.findViewById(R.id.tv_fav);
        tv_default.setSelected(true);
        tv_default.setTextColor(Color.parseColor("#FFFFFF"));
    }
}
