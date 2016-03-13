package com.kaede.bilibilikaede.UI;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.kaede.bilibilikaede.API.APIService;
import com.kaede.bilibilikaede.Adapter.FanJuPagerAdapter;
import com.kaede.bilibilikaede.Database.VideoDao;
import com.kaede.bilibilikaede.Domain.Video.VideoListResult;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.RxBus.PagerEvent.PassFetchEvent;
import com.kaede.bilibilikaede.RxBus.PagerEvent.PassRefreshEvent;
import com.kaede.bilibilikaede.RxBus.RxBus;
import com.kaede.bilibilikaede.Utils.Constant;
import com.kaede.bilibilikaede.Utils.VideoUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FanJuActivity extends AppCompatActivity {
    @InjectView(R.id.fan_ju_back) ImageView back;
    @InjectView(R.id.tab_fan_ju) TabLayout tabs;
    @InjectView(R.id.fan_ju_viewpager) ViewPager pager;
    @InjectView(R.id.fan_ju_magnifier) ImageView search;
    @InjectView(R.id.fan_ju_download) ImageView download;

    private static final int PAGE_SIZE = 100;

    private CompositeSubscription compositeSubscription;
    private VideoDao videoDao;
    private FanJuPagerAdapter pagerAdapter;

    private RxBus rxBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_ju);
        ButterKnife.inject(this);

        initListener();
        pagerAdapter = new FanJuPagerAdapter(this, getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs.setTabGravity(TabLayout.MODE_SCROLLABLE);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(pager);
        compositeSubscription = new CompositeSubscription();
        videoDao = new VideoDao(this);
        onRxBusEvent();
        requestJson();
    }

    public RxBus getRxBusSingleton(){
        if(rxBus == null){
            synchronized (this){
                if(rxBus == null){
                    rxBus =new RxBus();
                }
            }
        }
        return rxBus;
    }

    private void requestJson(){
        compositeSubscription.add(generateObservable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).map(new Func1<ResponseBody, VideoListResult>() {
            @Override
            public VideoListResult call(ResponseBody body) {
                VideoListResult result = null;
                try {
                    String json = body.string();
                    int i = json.lastIndexOf(",");
                    json = json.substring(0,i)+"}}";
                    Log.i("json","FanJuActivity");
                    Log.i("json",json);
                    GsonBuilder builder = new GsonBuilder();
                    builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);//注意video_review这个情况
                    result = builder.create().fromJson(json, VideoListResult.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends VideoListResult>>() {
            @Override
            public Observable<? extends VideoListResult> call(Throwable throwable) {
                return Observable.just(null);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<VideoListResult>() {
            @Override
            public void onCompleted() {
                pagerAdapter.notifyDataSetChanged();
                getRxBusSingleton().send(new PassRefreshEvent());
            }

            @Override
            public void onError(Throwable e) {
                pagerAdapter.notifyDataSetChanged();
                try {
                    throw new RuntimeException(e);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onNext(VideoListResult result) {
                if(result == null){
                    throw new RuntimeException("videoListResult is null form FanJuActivity,please check!");
                }else {
                    videoDao.insertVideo(result,VideoDao.TYPE_RECENT, VideoUtils.getVideoTypeByName(result.getName()));
                }
            }
        }));
    }

    private Observable<ResponseBody> generateObservable(){
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(Constant.BASE_URL).build();
        APIService apiService = retrofit.create(APIService.class);
        //按新投稿排序
        ArrayList<Observable<ResponseBody>> oList = new ArrayList<>();
        Observable<ResponseBody> o1 = apiService.getVideoList(Constant.appkey, Constant.LIAN_ZAI_DONG_HUA + "", "1", PAGE_SIZE + "", "default");
        oList.add(o1);
        Observable<ResponseBody> o2 = apiService.getVideoList(Constant.appkey, Constant.WAN_JIE_DONG_HUA + "", "1", PAGE_SIZE + "", "default");
        oList.add(o2);
        Observable<ResponseBody> o3 = apiService.getVideoList(Constant.appkey, Constant.GUO_CHAN_DONG_HUA + "", "1", PAGE_SIZE + "", "default");
        oList.add(o3);
        Observable<ResponseBody> o4 = apiService.getVideoList(Constant.appkey, Constant.ZI_XUN + "", "1", PAGE_SIZE + "", "default");
        oList.add(o4);
        Observable<ResponseBody> o5= apiService.getVideoList(Constant.appkey, Constant.GUAN_FANG_YAN_SHEN + "", "1", PAGE_SIZE + "", "default");
        oList.add(o5);
        return Observable.merge(oList);


    }

    private void initListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onRxBusEvent(){
        getRxBusSingleton().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if(o instanceof PassFetchEvent){
                    requestJson();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
