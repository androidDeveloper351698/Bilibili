package com.kaede.bilibilikaede.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kaede.bilibilikaede.Database.VideoDao;
import com.kaede.bilibilikaede.Domain.Video.VideoDetail;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.ViewHolder.CategoryChildNavHolder;
import com.kaede.bilibilikaede.ViewHolder.DongTaiContentHolder;
import com.kaede.bilibilikaede.ViewHolder.DongTaiTitleHolder;
import com.kaede.bilibilikaede.ViewHolder.PagerHolder;
import com.kaede.bilibilikaede.ViewHolder.ReMenHolder;
import com.kaede.bilibilikaede.ViewHolder.ZuiXinHolder;

import java.util.ArrayList;

/**
 * Created by asus on 2016/2/5.
 *
 */
public class TuiJianAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_IMAGE_PAGER = 0;
    private static final int TYPE_NAV = 1;
    private static final int TYPE_RE_MEN = 2;
    private static final int TYPE_ZUI_XIN = 3;
    private static final int TYPE_DONG_TAI_TITLE = 4;
    private static final int TYPE_DONG_TAI_CONTENT = 5;

    private Context mContext;
    private String[] mNavTitle;
    private int[] mNavImage;
    private int mCategory;

    private VideoDao mDao;
    private ArrayList<VideoDetail> mList = new ArrayList<>();

    public TuiJianAdapter(Context context,String[] navTitle,int[] navImage,int category){
        mContext = context;
        mNavTitle = navTitle;
        mNavImage = navImage;
        mCategory = category;
        mDao = new VideoDao(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return TYPE_IMAGE_PAGER;
            case 1:
                return TYPE_NAV;
            case 2:
                return TYPE_RE_MEN;
            case 3:
                return TYPE_ZUI_XIN;
            case 4:
                return TYPE_DONG_TAI_TITLE;
            default:
                return TYPE_DONG_TAI_CONTENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_IMAGE_PAGER){
            return new PagerHolder(LayoutInflater.from(mContext).inflate(R.layout.item__imagepager,parent,false));
        }else if(viewType == TYPE_NAV){
            return new CategoryChildNavHolder(LayoutInflater.from(mContext).inflate(R.layout.item_category_child_nav,parent,false),mContext);
        }else if(viewType == TYPE_RE_MEN){
            return new ReMenHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recommend_itemgroup,parent,false),mContext,mCategory);
        }else if(viewType == TYPE_ZUI_XIN){
            return new ZuiXinHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recommend_itemgroup,parent,false),mContext,mCategory);
        }else if(viewType == TYPE_DONG_TAI_TITLE){
            return new DongTaiTitleHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dong_tai_title,parent,false),mContext,mCategory);
        }else if(viewType == TYPE_DONG_TAI_CONTENT){
            return new DongTaiContentHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dong_tai_content,parent,false),mContext);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if(itemViewType == TYPE_IMAGE_PAGER){
            ((PagerHolder)holder).initData(new int[]{R.drawable.lunbo_01,R.drawable.lunbo_02,R.drawable.lunbo_03});
        }else if(itemViewType == TYPE_NAV){
            ((CategoryChildNavHolder)holder).initData(mNavTitle,mNavImage);
        }else if(itemViewType == TYPE_RE_MEN){
            ((ReMenHolder)holder).initData();
        }else if(itemViewType == TYPE_ZUI_XIN){
            ((ZuiXinHolder)holder).initData();
        }else if(itemViewType == TYPE_DONG_TAI_TITLE){
            ((DongTaiTitleHolder)holder).initData();
        }else if(itemViewType == TYPE_DONG_TAI_CONTENT){
            int realPosition = Math.max(0, position - 5);
            Log.i("json","position:"+position);
            Log.i("json","realPosition"+realPosition);
            VideoDetail detail1 = mList.get(realPosition*2);
            VideoDetail detail2 = null;
            if(realPosition*2+1<mList.size()-1) {
                detail2 = mList.get(realPosition * 2+1);
            }
            ((DongTaiContentHolder)holder).initData(detail1,detail2);
        }
    }

    public void refreshList(){
        ArrayList<VideoDetail> videoDetails = mDao.queryVideoByCategory(VideoDao.TYPE_RECENT, mCategory, VideoDao.FLAG_LAST_BUT_TOP_FOUR);
        mList.clear();
        mList.addAll(videoDetails);
    }

    @Override
    public int getItemCount() {
        Log.i("json","itemCount:"+(5+(mList.size()%2 == 1?mList.size()+1:mList.size())));
        return 5+(mList.size()%2 == 1?mList.size()+1:mList.size()/2);
    }
}
