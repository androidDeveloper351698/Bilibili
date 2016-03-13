package com.kaede.bilibilikaede.Fragment.Play;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaede.bilibilikaede.API.APIService;
import com.kaede.bilibilikaede.Domain.Comment.CommentReply;
import com.kaede.bilibilikaede.Domain.Comment.CommentResult;
import com.kaede.bilibilikaede.Domain.Comment.UserCommentInfo;
import com.kaede.bilibilikaede.Fragment.BaseFragment;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.RxBus.VideoEvent.PassTitleEvent;
import com.kaede.bilibilikaede.UI.PlayVideoActivity;
import com.kaede.bilibilikaede.Utils.Constant;
import com.kaede.bilibilikaede.Utils.SizeUtils;
import com.kaede.bilibilikaede.Utils.VideoUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by asus on 2016/2/11.
 *
 */
public class VideoCommentFragment extends BaseFragment {
    @InjectView(R.id.recycler_comment) RecyclerView recyclerView;

    private CommentFragmentRecyclerAdapter mAdapter;
    private RecyclerView.OnScrollListener mScrollListener;
    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<UserCommentInfo> hotList = new ArrayList<>();
    private ArrayList<UserCommentInfo> normalList = new ArrayList<>();

    private String mAid;
    private int mTotalPage;
    private int mCurrentPage;
    private static final String PAGE_SIZE = "25";//每次读取25条评论

    private boolean mHasMore = false;
    private boolean isIdle = true;

    @Override
    protected View initContentView(LayoutInflater inflater, ViewGroup container) {
        View contentView = inflater.inflate(R.layout.fragment_comment, container, false);
        ButterKnife.inject(this,contentView);
        return contentView;
    }

    @Override
    protected void initData() {
        mAdapter = new CommentFragmentRecyclerAdapter();
        recyclerView.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        mAid = getArguments().getString("aid", "");
        if(!TextUtils.isEmpty(mAid)) {
            Retrofit.Builder builder = new Retrofit.Builder();
            Call<CommentResult> call = builder.addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.BASE_URL).build().create(APIService.class).getComment(mAid, "1", PAGE_SIZE, "3", "default");
            call.enqueue(new Callback<CommentResult>() {
                @Override
                public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                    CommentResult body = response.body();
                    int commentCount;
                    try {
                       commentCount  = Integer.parseInt(body.getResults());
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        commentCount = 0;
                    }
                    if(getActivity() instanceof PlayVideoActivity){
                        ((PlayVideoActivity) getActivity()).getRxBusSingleton().send(new PassTitleEvent("评论("+commentCount+")"));
                    }
                    mTotalPage = Integer.parseInt(body.getPages());
                    mCurrentPage = Integer.parseInt(body.getPage());
                    ArrayList<UserCommentInfo> hot = body.getHotList();
                    hotList.clear();
                    hotList.addAll(hot);

                    ArrayList<UserCommentInfo> list = body.getList();
                    normalList.clear();
                    normalList.addAll(list);

                    if (mTotalPage>mCurrentPage) {
                        mHasMore = true;
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<CommentResult> call, Throwable t) {
                    Toast.makeText(getActivity(),"网络异常，拉取评论列表失败",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void initListener() {
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    isIdle = true;
                    if(mLinearLayoutManager.findLastVisibleItemPosition() ==mAdapter.getItemCount()-1&&mHasMore){//position别忘了减去1
                        loadMore(++mCurrentPage);
                    }
                    mAdapter.notifyDataSetChanged();
                }else {
                    isIdle = false;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
        recyclerView.addOnScrollListener(mScrollListener);
    }

    private void loadMore(int page){
        Retrofit.Builder builder = new Retrofit.Builder();
        Call<CommentResult> call = builder.addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.BASE_URL).build().create(APIService.class).getComment(mAid, page + "", PAGE_SIZE, "3", "default");
        call.enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                CommentResult result = response.body();
                int page = Integer.parseInt(result.getPage());
                if(page!=mCurrentPage){
                    throw new IllegalStateException("Page info from server does not match current one!");
                }
                ArrayList<UserCommentInfo> list = result.getList();
                normalList.addAll(list);
                mHasMore = mTotalPage>mCurrentPage;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CommentResult> call, Throwable t) {

            }
        });
    }

    class CommentFragmentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        final int TYPE_HOT = 1;
        final int TYPE_NORMAL = 4;
        final int TYPE_FOOTER_LOADING = 10;
        final int TYPE_FOOTER_COMPLETE = 11;
        final int TYPE_DIVIDER_NORMAL =100;
        final int TYPE_DIVIDER_SPECIAL = 101;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case TYPE_HOT:
                case TYPE_NORMAL:
                    View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.item_comment, parent, false);
                    return new CommentViewHolder(contentView);
                case TYPE_DIVIDER_NORMAL:
                    View divider = LayoutInflater.from(getActivity()).inflate(R.layout.item_divider, parent, false);
                    return new CommentDividerHolder(divider);
                case TYPE_DIVIDER_SPECIAL:
                    View dividerSpc = LayoutInflater.from(getActivity()).inflate(R.layout.item_divider_special, parent, false);
                    return new CommentDividerSpecialHolder(dividerSpc);
                case TYPE_FOOTER_LOADING:
                    View footerLoading = LayoutInflater.from(getActivity()).inflate(R.layout.item_footer_loading,parent,false);
                    return new CommentFooterLoadingHolder(footerLoading);
                case TYPE_FOOTER_COMPLETE:
                    View footerComplete = LayoutInflater.from(getActivity()).inflate(R.layout.item_footer_complete,parent,false);
                    return new CommentFooterCompleteHolder(footerComplete);
                default:
                    return null;
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof CommentViewHolder){
                final UserCommentInfo info;
                int itemViewType = getItemViewType(position);
                if(itemViewType == TYPE_HOT){
                    Log.i("json","position:"+position);
                    Log.i("json","itemCount:"+getItemCount());
                    info = hotList.get(position/2);
                }else{
                    int hotCount = Math.min(3,hotList.size());
                    info = normalList.get((position-hotCount*2)/2);
                }
                if(info !=null) {
                    ((CommentViewHolder) holder).userNickname.setText(info.getNick());
                    if(info.getSex().equals("男")){
                        Drawable maleDrawable = getActivity().getResources().getDrawable(R.drawable.ic_user_male);
                        if (maleDrawable != null) {
                            maleDrawable.setBounds(0,0, SizeUtils.dp2px(getActivity(),12),SizeUtils.dp2px(getActivity(),12));
                        ((CommentViewHolder) holder).userNickname.setCompoundDrawables(null,null,maleDrawable,null);
                        ((CommentViewHolder) holder).userNickname.setCompoundDrawablePadding(SizeUtils.dp2px(getActivity(),5));
                        }
                    }else if(info.getSex().equals("女")){
                        Drawable femaleDrawable = getActivity().getResources().getDrawable(R.drawable.ic_user_female);
                        if (femaleDrawable != null) {
                            femaleDrawable.setBounds(0,0, SizeUtils.dp2px(getActivity(),12),SizeUtils.dp2px(getActivity(),12));
                        ((CommentViewHolder) holder).userNickname.setCompoundDrawables(null,null,femaleDrawable,null);
                        ((CommentViewHolder) holder).userNickname.setCompoundDrawablePadding(SizeUtils.dp2px(getActivity(),5));
                        }
                    }
                    String temp = "#" + info.getLv();
                    ((CommentViewHolder) holder).userFloor.setText(temp);
                    ((CommentViewHolder) holder).userGood.setText(info.getGood());
                    ((CommentViewHolder) holder).userReply.setText(info.getReply_count());
                    ((CommentViewHolder) holder).userMsg.setText(info.getMsg());
                    ((CommentViewHolder) holder).userCreateTime.setText(VideoUtils.getStringTimeDisplay(info.getCreate()));
                    int level = Integer.parseInt(info.getLevel_info().getCurrent_level());
                    switch (level){
                        case 0:
                            ((CommentViewHolder) holder).level.setImageResource(R.drawable.ic_lv0);
                            break;
                        case 1:
                            ((CommentViewHolder) holder).level.setImageResource(R.drawable.ic_lv1);
                            break;
                        case 2:
                            ((CommentViewHolder) holder).level.setImageResource(R.drawable.ic_lv2);
                            break;
                        case 3:
                            ((CommentViewHolder) holder).level.setImageResource(R.drawable.ic_lv3);
                            break;
                        case 4:
                            ((CommentViewHolder) holder).level.setImageResource(R.drawable.ic_lv4);
                            break;
                        case 5:
                            ((CommentViewHolder) holder).level.setImageResource(R.drawable.ic_lv5);
                            break;
                        case 6:
                            ((CommentViewHolder) holder).level.setImageResource(R.drawable.ic_lv6);
                            break;
                        default:
                            break;
                    }
                    ((CommentViewHolder) holder).replyContainer.removeAllViews();
                    if(itemViewType == TYPE_NORMAL&&info.getReply()!=null) {//热评不显示回复
                        ArrayList<CommentReply> reply = info.getReply();
                        int replyShown = Math.min(5, reply.size());//最多显示5条回复
                        for (int i = 0; i < replyShown; i++) {
                            CommentReply cr = reply.get(i);
                            View replyView = LayoutInflater.from(getActivity()).inflate(R.layout.item_comment_reply, ((CommentViewHolder) holder).replyContainer, false);
                            TextView nickname = (TextView) replyView.findViewById(R.id.reply_nickname);
                            TextView time = (TextView) replyView.findViewById(R.id.reply_time);
                            TextView msg = (TextView) replyView.findViewById(R.id.reply_msg);
                            nickname.setText(cr.getNick());
                            time.setText(VideoUtils.getStringTimeDisplay(cr.getCreate()));
                            msg.setText(cr.getMsg());
                            ((CommentViewHolder) holder).replyContainer.addView(replyView);
                        }
                    }
                    if (isIdle) {
                        Picasso.with(getActivity()).load(info.getFace()).placeholder(R.drawable.bili_default_avatar).error(R.drawable.bili_default_avatar).into(((CommentViewHolder) holder).avatar);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            int hotCount = Math.min(3, hotList.size());
            return (hotCount+normalList.size())*2+1;//最后一个是footer
        }

        @Override
        public int getItemViewType(int position) {
            if(getItemCount() == 1){
                return TYPE_FOOTER_COMPLETE;
            }

            int hotCount = Math.min(3,hotList.size());//最多显示3条最热
            if(position%2 == 1){//分割线
                if(position == hotCount*2-1){
                    return TYPE_DIVIDER_SPECIAL;
                }else {
                    return TYPE_DIVIDER_NORMAL;
                }
            }else if (position<hotCount*2){
                return TYPE_HOT;
            }else if(position <= getItemCount()-2){
                return TYPE_NORMAL;
            }else if(mHasMore){
                return TYPE_FOOTER_LOADING;
            }else {
                return TYPE_FOOTER_COMPLETE;
            }
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.user_avatar) CircleImageView avatar;
        @InjectView(R.id.user_level) ImageView level;
        @InjectView(R.id.user_nickname) TextView userNickname;
        @InjectView(R.id.user_good) TextView userGood;
        @InjectView(R.id.user_reply) TextView userReply;
        @InjectView(R.id.user_floor) TextView userFloor;
        @InjectView(R.id.user_create_time) TextView userCreateTime;
        @InjectView(R.id.user_msg) TextView userMsg;
        @InjectView(R.id.reply_container) LinearLayout replyContainer;
        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    class CommentDividerHolder extends RecyclerView.ViewHolder{
        public CommentDividerHolder(View itemView) {
            super(itemView);
        }
    }

    class CommentDividerSpecialHolder extends RecyclerView.ViewHolder{
        public CommentDividerSpecialHolder(View itemView) {
            super(itemView);
        }
    }

    class CommentFooterLoadingHolder extends RecyclerView.ViewHolder{
        public CommentFooterLoadingHolder(View itemView) {
            super(itemView);
        }
    }

    class CommentFooterCompleteHolder extends RecyclerView.ViewHolder{
        public CommentFooterCompleteHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.removeOnScrollListener(mScrollListener);
    }
}
