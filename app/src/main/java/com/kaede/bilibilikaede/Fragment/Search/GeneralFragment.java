package com.kaede.bilibilikaede.Fragment.Search;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaede.bilibilikaede.API.APIService;
import com.kaede.bilibilikaede.Domain.Search.SearchItemInfo;
import com.kaede.bilibilikaede.Domain.Search.SearchResult;
import com.kaede.bilibilikaede.Fragment.BaseFragment;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.RxBus.SearchEvent.PassSearchKeywordEvent;
import com.kaede.bilibilikaede.UI.PlayVideoActivity;
import com.kaede.bilibilikaede.UI.SearchActivity;
import com.kaede.bilibilikaede.Utils.Constant;
import com.kaede.bilibilikaede.Utils.VideoUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by asus on 2016/3/1.
 *
 */
public class GeneralFragment extends BaseFragment {
    @InjectView(R.id.recycler_search_general) RecyclerView recyclerView;

    private MyAdapter mAdapter;
    private RecyclerView.OnScrollListener mListener;
    private LinearLayoutManager mManager;
    private ArrayList<SearchItemInfo> specialList = new ArrayList<>();
    private ArrayList<SearchItemInfo> videoList = new ArrayList<>();

    private boolean isIdle = true;
    private boolean hasMore = false;

    private static final int PAGE_SIZE = 20;

    private int searchResultCount = 0;
    private int searchCurrentPage = 0;
    private int searchTotalPage = 0;
    private String currentKeyword = "";

    @Override
    protected View initContentView(LayoutInflater inflater, ViewGroup container) {
        View contentView = inflater.inflate(R.layout.fragment_general, container, false);
        ButterKnife.inject(this,contentView);
        return contentView;
    }

    @Override
    protected void initData() {
        mManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mManager);
        mAdapter = new MyAdapter();
        recyclerView.setAdapter(mAdapter);
        Intent intent = getActivity().getIntent();
        if(currentKeyword.equals("")) {
            currentKeyword = intent.getStringExtra("keyword");
        }
        getSearchResult(currentKeyword,"default","1",true);
    }

    @Override
    protected void initListener() {
        onRxBusEvent();
        mListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    isIdle = true;
                    mAdapter.notifyDataSetChanged();
                    if(mManager.findLastVisibleItemPosition()> mAdapter.getItemCount() - 2&&hasMore){//尝试去加载
                        getSearchResult(currentKeyword,"default",(searchCurrentPage+1)+"",false);
                    }
                }else {
                    isIdle = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
        recyclerView.addOnScrollListener(mListener);
    }

    private void onRxBusEvent(){
        if(getActivity() instanceof SearchActivity){
            ((SearchActivity) getActivity()).getRxBusSingleton().toObservable().subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    if(o instanceof PassSearchKeywordEvent){
                        currentKeyword = ((PassSearchKeywordEvent) o).getKeyword();
                        getSearchResult(currentKeyword,"default","1",true);
                    }
                }
            });
        }
    }

    private void getSearchResult(String keyword, String order, String page, final boolean isNewSearch){//使用新的appKey和appSecret才能进行搜索API的使用
        Retrofit.Builder builder = new Retrofit.Builder();
        HashMap<String,String> paramsMap = new HashMap<>();
        paramsMap.put("appkey",Constant.newAppkey);
        paramsMap.put("keyword",keyword);
        paramsMap.put("order",order);
        paramsMap.put("page",page);
        paramsMap.put("pagesize",PAGE_SIZE+"");
        String sign = VideoUtils.getSign(paramsMap);
        Log.i("json",sign);
        Call<SearchResult> call = builder.addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.BASE_URL).build().create(APIService.class).getSearch(Constant.newAppkey, keyword, order, page, PAGE_SIZE + "", sign);
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                SearchResult result = response.body();
                if(result.getNumResults()!=null) {
                    searchResultCount = Integer.parseInt(result.getNumResults());
                }else {
                    searchResultCount = 0;
                }
                if(result.getPage()!=null) {
                    searchTotalPage = Integer.parseInt(result.getPage());
                }else {
                    searchTotalPage = 0;
                }
                if(isNewSearch){
                    searchCurrentPage = 0;
                    specialList.clear();
                    videoList.clear();
                }
                searchCurrentPage++;
                hasMore = (searchCurrentPage<searchTotalPage);
                if(result.getResult()!=null&&result.getResult().size() !=0) {
                    Observable.from(result.getResult()).subscribe(new Subscriber<SearchItemInfo>() {
                        @Override
                        public void onCompleted() {
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "搜索过程中发生了错误", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(SearchItemInfo searchItemInfo) {
                            if (searchItemInfo.getType().equals(Constant.TYPE_SPEC)) {
                                specialList.add(searchItemInfo);
                            } else if (searchItemInfo.getType().equals(Constant.TYPE_VIDEO)) {
                                videoList.add(searchItemInfo);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.removeOnScrollListener(mListener);
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        final int TYPE_SPECIAL = 1;
        final int TYPE_DIVIDER = 2;
        final int TYPE_VIDEO = 3;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case TYPE_SPECIAL:
                    return new SpecialHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_search_special,parent,false));
                case TYPE_DIVIDER:
                    return new DividerTextHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_search_divider,parent,false));
                case TYPE_VIDEO:
                    return new VideoHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_search_video_card,parent,false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);
            switch (itemViewType){
                case TYPE_SPECIAL:
                    SearchItemInfo specialVideo = specialList.get(position);
                    ((SpecialHolder)holder).title.setText(specialVideo.getTitle());
                    String click = specialVideo.getClick();
                    click = "点击："+VideoUtils.getStringVideoDetailCount(click);
                    ((SpecialHolder)holder).clickCount.setText(click);
                    String favourite = specialVideo.getFavourite();
                    favourite = "订阅："+VideoUtils.getStringVideoDetailCount(favourite);
                    ((SpecialHolder)holder).subCount.setText(favourite);
                    ((SpecialHolder)holder).state.setText(VideoUtils.getStringVideoIsEnd(specialVideo.getIs_bangumi_end()));
                    ((SpecialHolder)holder).container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO 跳到专题页面
                            Toast.makeText(getActivity(),"完善中..",Toast.LENGTH_SHORT).show();
                        }
                    });
                    ((SpecialHolder) holder).pic.setBackgroundResource(R.drawable.bili_default_image_tv);
                    if(isIdle) {
                        Picasso.with(getActivity()).load(specialVideo.getPic()).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.bili_default_image_tv).into(((SpecialHolder) holder).pic);
                    }
                    break;
                case TYPE_DIVIDER:
                    String text = "相关视频("+searchResultCount+")";
                    ((DividerTextHolder)holder).videoCount.setText(text);
                    break;
                case TYPE_VIDEO:
                    int specialCount = Math.min(3,specialList.size());
                    final SearchItemInfo videoInfo = videoList.get(position - specialCount - 1);
                    ((VideoHolder)holder).review.setText(VideoUtils.getStringVideoDetailCount(videoInfo.getVideo_review()));
                    ((VideoHolder)holder).playCount.setText(VideoUtils.getStringVideoDetailCount(videoInfo.getPlay()));
                    ((VideoHolder)holder).title.setText(videoInfo.getTitle());
                    ((VideoHolder)holder).upper.setText(videoInfo.getAuthor());
                    ((VideoHolder)holder).container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
                            intent.putExtra("aid",videoInfo.getAid());
                            intent.putExtra("picPath",videoInfo.getPic());
                            startActivity(intent);
                        }
                    });
                    ((VideoHolder) holder).pic.setImageResource(R.drawable.bili_default_image_tv);
                    if(isIdle) {
                        Picasso.with(getActivity()).load(videoInfo.getPic()).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.bili_default_image_tv).into(((VideoHolder) holder).pic);
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            int specialCount = Math.min(3,specialList.size());
            if(videoList.size()!=0){
                return specialCount+videoList.size()+1;
            }else {
                return specialCount+videoList.size();
            }
        }

        @Override
        public int getItemViewType(int position) {
            int specialCount = Math.min(3,specialList.size());
            if(videoList.size()!=0){
                if(position<specialCount){
                    return TYPE_SPECIAL;
                }else if(position == specialCount){
                    return TYPE_DIVIDER;
                }else {
                    return TYPE_VIDEO;
                }
            }else {
                return TYPE_SPECIAL;
            }
        }
    }

    class SpecialHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.special_title) TextView title;
        @InjectView(R.id.special_pic) ImageView pic;
        @InjectView(R.id.special_click_count) TextView clickCount;
        @InjectView(R.id.special_sub_count) TextView subCount;
        @InjectView(R.id.special_state) TextView state;
        @InjectView(R.id.container) LinearLayout container;
        public SpecialHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    class DividerTextHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.videoCount) TextView videoCount;
        public DividerTextHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    class VideoHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.video_pic) ImageView pic;
        @InjectView(R.id.video_title) TextView title;
        @InjectView(R.id.video_upper) TextView upper;
        @InjectView(R.id.video_play) TextView playCount;
        @InjectView(R.id.video_container) LinearLayout container;
        @InjectView(R.id.video_review) TextView review;//弹幕数量
        public VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }
}
