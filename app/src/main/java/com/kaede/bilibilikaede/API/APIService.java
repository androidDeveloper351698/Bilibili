package com.kaede.bilibilikaede.API;

import com.kaede.bilibilikaede.Domain.Comment.CommentResult;
import com.kaede.bilibilikaede.Domain.Search.SearchResult;
import com.kaede.bilibilikaede.Domain.Video.VideoProfile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by asus on 2016/1/24.
 *
 */
public interface APIService {

    @GET("list")
    Observable<ResponseBody> getVideoList(@Query("appkey")String appkey, @Query("tid")String tid, @Query("page")String page,@Query("pagesize")String pagesize, @Query("order")String order);

    @GET("view")
    Call<VideoProfile> getVideoProfile(@Query("appkey")String appkey, @Query("id")String avId, @Query("page")String page, @Query("fav")String fav);

    @GET("view")
    Observable<ResponseBody> getVideoProfileObservable(@Query("appkey")String appkey, @Query("id")String avId, @Query("page")String page, @Query("fav")String fav);

    @GET("playurl")
    Call<ResponseBody> getVideoSource(@Query("appkey")String appkey,@Query("cid")String cid);

    @GET("feedback")
    Call<CommentResult> getComment(@Query("aid")String aid,@Query("page")String page,@Query("pageSize")String pageSize,@Query("ver")String ver,@Query("order")String order);

    @GET("search")
    Call<SearchResult> getSearch(@Query("appkey")String appkey,@Query("keyword")String keyword,@Query("order")String order,@Query("page")String page,@Query("pagesize")String pagesize,@Query("sign")String sign);

}
