package com.kaede.bilibilikaede.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaede.bilibilikaede.R;

/**
 * Created by asus on 2016/1/25.
 */
public class DummyFragment extends BaseFragment {
    @Override
    protected View initContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_dummy,container,false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
