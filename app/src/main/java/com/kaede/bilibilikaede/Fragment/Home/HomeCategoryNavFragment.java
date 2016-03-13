package com.kaede.bilibilikaede.Fragment.Home;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaede.bilibilikaede.Adapter.CategoryNavAdapter;
import com.kaede.bilibilikaede.Fragment.BaseFragment;
import com.kaede.bilibilikaede.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by asus on 2016/2/4.
 *
 */
public class HomeCategoryNavFragment extends BaseFragment {
    @InjectView(R.id.recycler_category_nav) RecyclerView recyclerView;

    @Override
    protected View initContentView(LayoutInflater inflater, ViewGroup container) {
        View contentView = inflater.inflate(R.layout.fragment_category_nav, container, false);
        ButterKnife.inject(this,contentView);
        return contentView;
    }

    @Override
    protected void initData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CategoryNavAdapter(getActivity()));
    }

    @Override
    protected void initListener() {

    }
}
