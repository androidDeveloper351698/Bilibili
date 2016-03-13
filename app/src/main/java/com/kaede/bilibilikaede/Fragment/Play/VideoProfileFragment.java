package com.kaede.bilibilikaede.Fragment.Play;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaede.bilibilikaede.API.APIService;
import com.kaede.bilibilikaede.Domain.Video.VideoProfile;
import com.kaede.bilibilikaede.Fragment.BaseFragment;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.RxBus.RxBus;
import com.kaede.bilibilikaede.RxBus.VideoEvent.PassCidEvent;
import com.kaede.bilibilikaede.UI.PlayVideoActivity;
import com.kaede.bilibilikaede.Utils.Constant;
import com.kaede.bilibilikaede.Utils.SizeUtils;
import com.kaede.bilibilikaede.Utils.VideoUtils;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by asus on 2016/2/11.
 *
 */
public class VideoProfileFragment extends BaseFragment {
    @InjectView(R.id.recycler_video_profile) RecyclerView recyclerView;

    private CompositeSubscription compositeSubscription;

    @Override
    protected View initContentView(LayoutInflater inflater, ViewGroup container) {
        View contentView = inflater.inflate(R.layout.fragment_video_profile, container, false);
        ButterKnife.inject(this,contentView);
        return contentView;
    }

    @Override
    protected void initData() {
        compositeSubscription = new CompositeSubscription();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new VideoProfileRecyclerAdapter());
    }

    @Override
    protected void initListener() {

    }

    static public void abc(){

    }

    class VideoProfileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VideoProfileViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_video_profile_detail,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof VideoProfileViewHolder){
                ((VideoProfileViewHolder) holder).initData();
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    class VideoProfileViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.video_title) TextView videoTitle;
        @InjectView(R.id.video_play_count) TextView videoPlayCount;
        @InjectView(R.id.video_danma_count) TextView videoDanmaCount;
        @InjectView(R.id.video_detail) TextView videoDetail;
        @InjectView(R.id.video_share_count) TextView shareCount;
        @InjectView(R.id.video_coin_count) TextView coinCount;
        @InjectView(R.id.video_fav_count) TextView favCount;
        @InjectView(R.id.author_avatar) CircleImageView authorAvatar;
        @InjectView(R.id.author_name) TextView authorName;
        @InjectView(R.id.post_time) TextView postTime;
        @InjectView(R.id.like_author) TextView likeAuthor;//拥有Up主的mid 不难.....可惜没有权限
        @InjectView(R.id.tags_container) FlowLayout tagsContainer;
        @InjectView(R.id.episode_divider) View episodeDivider;
        @InjectView(R.id.episode_title) TextView episodeTitle;
        @InjectView(R.id.episode_container) LinearLayout episodeContainer;

        private final int FETCH_INTERVAL = 6;

        public VideoProfileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void initData(){
            String aid = getArguments().getString("aid", "");
            Retrofit.Builder builder = new Retrofit.Builder();
            Retrofit retrofit = builder.addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.BASE_URL).build();
            APIService apiService = retrofit.create(APIService.class);
            Call<VideoProfile> callback = apiService.getVideoProfile(Constant.appkey, aid, "1", "1");
            callback.enqueue(new Callback<VideoProfile>() {
                @Override
                public void onResponse(Call<VideoProfile> call, Response<VideoProfile> response) {
                    VideoProfile videoProfile = response.body();
                    final String cid = videoProfile.getCid();
                    if(!TextUtils.isEmpty(cid)) {
                        if (getActivity() instanceof PlayVideoActivity) {//将数据传递给Activity
                            RxBus bus = ((PlayVideoActivity) getActivity()).getRxBusSingleton();
                            bus.send(new PassCidEvent(cid,false));

                        }
                        videoTitle.setText(videoProfile.getTitle());
                        videoPlayCount.setText(VideoUtils.getStringVideoDetailCount(videoProfile.getPlay()));
                        videoDanmaCount.setText(VideoUtils.getStringVideoDetailCount(videoProfile.getVideo_review()));
                        videoDetail.setText(videoProfile.getDescription());
                        shareCount.setText(videoProfile.getFavorites());//不知道如何获取..
                        coinCount.setText(videoProfile.getCoins());
                        favCount.setText(videoProfile.getFavorites());
                        authorName.setText(videoProfile.getAuthor());
                        postTime.setText(videoProfile.getCreated_at());//TODO 做个小工具换算下
                        int page = Integer.parseInt(videoProfile.getPages());
                        if(page>1){//初始化分集View
                            episodeDivider.setVisibility(View.VISIBLE);
                            episodeTitle.setVisibility(View.VISIBLE);
                            String titleStr = "分集("+page+")";
                            episodeTitle.setText(titleStr);
                            episodeContainer.setVisibility(View.VISIBLE);
                            //添加第一个分集
                            final TextView tv = new TextView(getActivity());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            int dp10 = SizeUtils.dp2px(getActivity(), 10);
                            lp.setMargins(dp10,dp10,dp10,dp10);
                            lp.gravity = Gravity.CENTER;
                            tv.setLayoutParams(lp);
                            tv.setMaxWidth(SizeUtils.dp2px(getActivity(),150));//对宽度做一下限制
                            tv.setPadding(dp10,dp10,dp10,dp10);
                            tv.setBackgroundResource(R.drawable.drop_bottom_btn_selector);
                            tv.setText(videoProfile.getPartname());
                            tv.setTextSize(12);
                            tv.setTextColor(Color.parseColor("#fffb7299"));
                            tv.setGravity(Gravity.CENTER);
                            tv.setSelected(true);
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!tv.isSelected()){
                                        int childCount = episodeContainer.getChildCount();
                                        for(int i=0;i<childCount;i++){
                                            TextView otherChild = (TextView)episodeContainer.getChildAt(i);
                                            otherChild.setTextColor(Color.parseColor("#FF000000"));
                                            otherChild.setSelected(false);
                                        }
                                        tv.setSelected(true);
                                        tv.setTextColor(Color.parseColor("#fffb7299"));
                                        if(getActivity() instanceof PlayVideoActivity){
                                            recyclerView.scrollToPosition(0);//注意RecyclerView不支持scrollTo方法
                                            RxBus rxBus = ((PlayVideoActivity) getActivity()).getRxBusSingleton();
                                            rxBus.send(new PassCidEvent(cid,true));
                                        }
                                    }
                                }
                            });
                            episodeContainer.addView(tv);
                            addOtherChild(page);
                        }
                        if (!TextUtils.isEmpty(videoProfile.getFace())) {
                            Picasso.with(getActivity()).load(videoProfile.getFace()).placeholder(R.drawable.bili_default_avatar).error(R.drawable.bili_default_avatar).into(authorAvatar);
                        }
                        String[] tags = videoProfile.getTag().split(",");
                        for (String tag : tags) {
                            TextView tv = new TextView(getActivity());
                            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(SizeUtils.dp2px(getActivity(), 10), SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 10), SizeUtils.dp2px(getActivity(), 5));
                            tv.setLayoutParams(lp);
                            tv.setPadding(SizeUtils.dp2px(getActivity(), 10), SizeUtils.dp2px(getActivity(), 5), SizeUtils.dp2px(getActivity(), 10), SizeUtils.dp2px(getActivity(), 5));
                            tv.setBackgroundResource(R.drawable.text_bg_normal);
                            tv.setText(tag);
                            //TODO onclickListener
                            tagsContainer.addView(tv);
                        }
                    }else {
                        Toast.makeText(getActivity(),"对不起，当前没有权限查看该视频哦",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<VideoProfile> call, Throwable t) {
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_LONG).show();
                }
            });


        }

        private void addOtherChild(int page){//使用RxJava确保获取结果有序
            String aid = getArguments().getString("aid", "");
            int targetPage = Math.min(7,page);
            mergeRequestAndApply(page,2,targetPage,aid);
        }

        private void mergeRequestAndApply(final int totalPage, final int beginPage,final int targetPage,final String aid){
            ArrayList<Observable<ResponseBody>> list = new ArrayList<>();
            for(int i=beginPage;i<=targetPage;i++){
                Retrofit.Builder builder = new Retrofit.Builder();
                Observable<ResponseBody> observable = builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(Constant.BASE_URL).build().create(APIService.class).getVideoProfileObservable(Constant.appkey, aid, i + "", "1");
                list.add(observable);
            }
            compositeSubscription.add(Observable.merge(list).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {//保持分集TextView高度大小的一致
                    int count = episodeContainer.getChildCount();
                    int maxHeight = 0;
                    for(int i=0;i<count;i++){
                        TextView child = (TextView)episodeContainer.getChildAt(i);
                        maxHeight = Math.max(maxHeight,child.getHeight());
                    }

                    for (int i=0;i<count;i++){//不可以通过setHeight设置高度
                        TextView child = (TextView)episodeContainer.getChildAt(i);
                        child.getLayoutParams().height = maxHeight;
                    }
                    episodeContainer.requestLayout();

                    //加载下一轮
                    episodeContainer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(targetPage<totalPage){
                                int begin = targetPage+1;
                                int target = Math.min(totalPage,beginPage+FETCH_INTERVAL);
                                mergeRequestAndApply(totalPage,begin,target,aid);
                            }
                        }
                    },10000);//间隔10秒
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getActivity(),"获取视频信息失败",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(ResponseBody body) {
                    VideoProfile videoProfile = null;
                    try {
                        String json = body.string();
                        Log.i("json",json);
                        Gson gson = new GsonBuilder().create();
                        videoProfile = gson.fromJson(json,VideoProfile.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(videoProfile!=null&&!TextUtils.isEmpty(videoProfile.getCid())){
                        final String cid = videoProfile.getCid();
                        final TextView tv = new TextView(getActivity());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int dp10 = SizeUtils.dp2px(getActivity(), 10);
                        lp.setMargins(dp10,dp10,dp10,dp10);
                        tv.setLayoutParams(lp);
                        lp.gravity = Gravity.CENTER;
                        tv.setPadding(dp10,dp10,dp10,dp10);
                        tv.setMaxWidth(SizeUtils.dp2px(getActivity(),150));
                        tv.setBackgroundResource(R.drawable.drop_bottom_btn_selector);
                        tv.setText(videoProfile.getPartname());
                        tv.setTextColor(Color.parseColor("#FF000000"));
                        tv.setTextSize(12);
                        tv.setGravity(Gravity.CENTER);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!tv.isSelected()){
                                    int childCount = episodeContainer.getChildCount();
                                    for(int j=0;j<childCount;j++){
                                        TextView otherChild = (TextView)episodeContainer.getChildAt(j);
                                        otherChild.setTextColor(Color.parseColor("#FF000000"));
                                        otherChild.setSelected(false);
                                    }
                                    tv.setSelected(true);
                                    tv.setTextColor(Color.parseColor("#fffb7299"));
                                    if(getActivity() instanceof PlayVideoActivity){
                                        recyclerView.scrollToPosition(0);
                                        RxBus rxBus = ((PlayVideoActivity) getActivity()).getRxBusSingleton();
                                        rxBus.send(new PassCidEvent(cid,true));
                                    }
                                }
                            }
                        });
                        episodeContainer.addView(tv);
                    }
                }
            }));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
//        recyclerView.removeOnScrollListener(listener);
    }
}
