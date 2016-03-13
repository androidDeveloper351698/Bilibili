package com.kaede.bilibilikaede.Domain.Search;

/**
 * Created by asus on 2016/2/29.
 */
public class SearchItemInfo {
    private String type;//special video
    private String id;//av号或专题号
    private String author;
    private String mid;
    private String spid;
    private String pic;
    private String thumb;
    private String ischeck;
    private String typename;
    private String typeurl;
    private String title;
    private String tag;
    private String description;
    private String pubdate;//上传时间
    private String postdate;
    private String lastupdate;
    private String click;
    private String favourite;
    private String attention;
    private String count;
    private String bgmcount;
    private String spcount;
    private String season_id;
    private String is_bangumi;
    private String is_bangumi_end;
    private String arc_url;//视频或专题的网页URL

/*  -----------------------------------以下为Video类型专属属性--------------------------------------- */
    private String aid;
    private String arcrank; //视频状态和所需权限
    private String play;
    private String video_review;
    private String favorites;//收藏数
    private String review;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getSpid() {
        return spid;
    }

    public void setSpid(String spid) {
        this.spid = spid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getTypeurl() {
        return typeurl;
    }

    public void setTypeurl(String typeurl) {
        this.typeurl = typeurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getBgmcount() {
        return bgmcount;
    }

    public void setBgmcount(String bgmcount) {
        this.bgmcount = bgmcount;
    }

    public String getSpcount() {
        return spcount;
    }

    public void setSpcount(String spcount) {
        this.spcount = spcount;
    }

    public String getSeason_id() {
        return season_id;
    }

    public void setSeason_id(String season_id) {
        this.season_id = season_id;
    }

    public String getIs_bangumi() {
        return is_bangumi;
    }

    public void setIs_bangumi(String is_bangumi) {
        this.is_bangumi = is_bangumi;
    }

    public String getIs_bangumi_end() {
        return is_bangumi_end;
    }

    public void setIs_bangumi_end(String is_bangumi_end) {
        this.is_bangumi_end = is_bangumi_end;
    }

    public String getArc_url() {
        return arc_url;
    }

    public void setArc_url(String arc_url) {
        this.arc_url = arc_url;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getArcrank() {
        return arcrank;
    }

    public void setArcrank(String arcrank) {
        this.arcrank = arcrank;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public String getVideo_review() {
        return video_review;
    }

    public void setVideo_review(String video_review) {
        this.video_review = video_review;
    }

    public String getFavorites() {
        return favorites;
    }

    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
