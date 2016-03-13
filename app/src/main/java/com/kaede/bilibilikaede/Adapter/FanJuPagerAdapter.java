package com.kaede.bilibilikaede.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kaede.bilibilikaede.Fragment.FanJu.FanJuTuiJianFragment;
import com.kaede.bilibilikaede.Fragment.FanJu.GuanFangYanShenFragment;
import com.kaede.bilibilikaede.Fragment.FanJu.GuoChanDongHuaFragment;
import com.kaede.bilibilikaede.Fragment.FanJu.LianZaiDongHuaFragment;
import com.kaede.bilibilikaede.Fragment.FanJu.WanJieDongHuaFragment;
import com.kaede.bilibilikaede.Fragment.FanJu.ZiXunFragment;
import com.kaede.bilibilikaede.R;

/**
 * Created by asus on 2016/2/5.
 */
public class FanJuPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public FanJuPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.fan_ju_tab_title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FanJuTuiJianFragment();
            case 1:
                return new LianZaiDongHuaFragment();
            case 2:
                return new WanJieDongHuaFragment();
            case 3:
                return new GuoChanDongHuaFragment();
            case 4:
                return new ZiXunFragment();
            case 5:
                return new GuanFangYanShenFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
