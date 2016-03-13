package com.kaede.bilibilikaede.Fragment.FanJu;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaede.bilibilikaede.Adapter.TuiJianAdapter;
import com.kaede.bilibilikaede.Fragment.BaseFragment;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.RxBus.PagerEvent.PassFetchEvent;
import com.kaede.bilibilikaede.RxBus.PagerEvent.PassRefreshEvent;
import com.kaede.bilibilikaede.UI.FanJuActivity;
import com.kaede.bilibilikaede.Utils.Constant;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;

/**
 * Created by asus on 2016/2/5.
 *
 */
public class FanJuTuiJianFragment extends BaseFragment {
    @InjectView(R.id.tui_jian_recycler) RecyclerView recyclerView;
    @InjectView(R.id.fan_ju_refresh) SwipeRefreshLayout refreshLayout;

    private TuiJianAdapter mAdapter;

    @Override
    protected View initContentView(LayoutInflater inflater, ViewGroup container) {
        View contentView = inflater.inflate(R.layout.fragment_fanju_tuijian, container, false);
        ButterKnife.inject(this,contentView);
        return contentView;
    }

    @Override
    protected void initData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        String[] navChildTitles = getResources().getStringArray(R.array.fan_ju_nav);
        int[] navImages = {R.mipmap.ic_category_t33,R.mipmap.ic_category_t32,R.mipmap.ic_category_t153,R.mipmap.ic_category_t51,R.mipmap.ic_category_t152};
        mAdapter = new TuiJianAdapter(getActivity(),navChildTitles,navImages, Constant.FAN_JU);
        recyclerView.setAdapter(mAdapter);
        onRxBusEvent();
    }

    private void onRxBusEvent(){
        if(getActivity() instanceof FanJuActivity){
            ((FanJuActivity) getActivity()).getRxBusSingleton().toObservable().subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    if(o instanceof PassRefreshEvent){
                        mAdapter.refreshList();
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    protected void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getActivity() instanceof FanJuActivity){
                    ((FanJuActivity) getActivity()).getRxBusSingleton().send(new PassFetchEvent());
                    if(refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }
}
