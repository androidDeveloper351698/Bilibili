package com.kaede.bilibilikaede.Domain.Comment;

import java.util.ArrayList;

/**
 * Created by asus on 2016/2/25.
 */
public class CommentResult {
    private String results;
    private String page;//当前页数
    private String pages;//总共页数
    private String isAdmin;
    private String needCode;
    private String owner;
    private ArrayList<UserCommentInfo> hotList;
    private ArrayList<UserCommentInfo> list;

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getNeedCode() {
        return needCode;
    }

    public void setNeedCode(String needCode) {
        this.needCode = needCode;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<UserCommentInfo> getHotList() {
        return hotList;
    }

    public void setHotList(ArrayList<UserCommentInfo> hotList) {
        this.hotList = hotList;
    }

    public ArrayList<UserCommentInfo> getList() {
        return list;
    }

    public void setList(ArrayList<UserCommentInfo> list) {
        this.list = list;
    }
}
