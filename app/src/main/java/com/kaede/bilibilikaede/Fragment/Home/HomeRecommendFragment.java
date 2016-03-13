package com.kaede.bilibilikaede.Fragment.Home;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.kaede.bilibilikaede.API.APIService;
import com.kaede.bilibilikaede.Adapter.RecommendRecyclerAdapter;
import com.kaede.bilibilikaede.Database.VideoDao;
import com.kaede.bilibilikaede.Domain.Video.VideoListResult;
import com.kaede.bilibilikaede.Fragment.BaseFragment;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.Utils.Constant;
import com.kaede.bilibilikaede.Utils.VideoUtils;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

/**
 * Created by asus on 2016/1/21.
 */
public class HomeRecommendFragment extends BaseFragment {
    @InjectView(R.id.refresh_home_base)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.recycler_home_base)
    RecyclerView recyclerView;

    private RecommendRecyclerAdapter recommendRecyclerAdapter; //根adapter

    private VideoDao videoDao;
    private static final int PAGE_SIZE = 20;//最多查询20条视频就够了 用来在专区首页展示 并且能够切换
    
    private int threadCount = Runtime.getRuntime().availableProcessors()+1;

    @Override
    protected View initContentView(LayoutInflater inflater, ViewGroup container) {
        View content = inflater.inflate(R.layout.fragment_recommend, container,false);
        ButterKnife.inject(this, content);
        return content;
    }

    @Override
    protected void initData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recommendRecyclerAdapter = new RecommendRecyclerAdapter(getActivity());
        recyclerView.setAdapter(recommendRecyclerAdapter);
        videoDao = new VideoDao(getActivity());

        requestJson(PAGE_SIZE);
    }

    @Override
    protected void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestJson(PAGE_SIZE);
            }
        });
    }


    private Observable<ResponseBody> queryAPi(int typeId, int page, int pageSize, String order) {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(Constant.BASE_URL).build();
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getVideoList(Constant.appkey, typeId + "", page + "", pageSize + "", order);
    }

    private Observable<ResponseBody> queryAPi(int typeId, int pageSize) {
        return queryAPi(typeId, 1, pageSize, "hot");
    }



    private void requestJson(int pageSize){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(!refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(true);
                }
            }
        });

        final AtomicInteger atom = new AtomicInteger();
        generateObservable(pageSize).groupBy(new Func1<ResponseBody, Integer>() {
            @Override
            public Integer call(ResponseBody body) {
                return atom.getAndIncrement()&threadCount;
            }
        }).flatMap(new Func1<GroupedObservable<Integer, ResponseBody>, Observable<?>>() {
            @Override
            public Observable<?> call(GroupedObservable<Integer, ResponseBody> o) {
                return o.observeOn(Schedulers.from(Executors.newFixedThreadPool(threadCount))).map(new Func1<ResponseBody, VideoListResult>() {
                    @Override
                    public VideoListResult call(ResponseBody body) {
                        VideoListResult videoListResult = null;
                        try {
                            String json = body.string();
                            int i = json.lastIndexOf(",");
                            json = json.substring(0,i)+"}}";
                            Log.i("json",Thread.currentThread().toString());
                            Log.i("json",json);
                            GsonBuilder builder = new GsonBuilder();
                            builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                            videoListResult = builder.create().fromJson(json, VideoListResult.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return videoListResult;
                    }
                });
            }
        }).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                recommendRecyclerAdapter.notifyDataSetChanged();
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if(refreshLayout.isRefreshing()){
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                recommendRecyclerAdapter.notifyDataSetChanged();
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if(refreshLayout.isRefreshing()){
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
                try {
                    throw new RuntimeException(e);//抓一下
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onNext(Object o) {
                if(o == null){
                    throw new RuntimeException("videoListResult is null!");
                }else if(o instanceof VideoListResult){
                    videoDao.insertVideo((VideoListResult)o,VideoDao.TYPE_MAIN_HOT, VideoUtils.getVideoTypeByName(((VideoListResult)o).getName()));
                }
            }
        });
    }

    private Observable<ResponseBody> generateObservable(int pageSize) {
        ArrayList<Observable<ResponseBody>> list = new ArrayList<>();

        list.add(queryAPi(Constant.FAN_JU, pageSize));
        list.add(queryAPi(Constant.DONG_HUA, pageSize));
        list.add(queryAPi(Constant.YIN_YUE, pageSize));
        list.add(queryAPi(Constant.WU_DAO, pageSize));
        list.add(queryAPi(Constant.YOU_XI, pageSize));
        list.add(queryAPi(Constant.KE_JI, pageSize));
        list.add(queryAPi(Constant.YU_LE, pageSize));
        list.add(queryAPi(Constant.GUI_CHU, pageSize));

        return Observable.merge(list);
    }



}
