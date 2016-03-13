package com.kaede.bilibilikaede.Domain.Search;

import java.util.ArrayList;

/**
 * Created by asus on 2016/2/29.
 *
 */
public class SearchResult {
    private String code;
    private String seid;
    private String pagesize;
    private String page;
    private Sengine sengine;
    private String total;
    private String numResults;
    private String numPages;
    private String suggest_keyword;
    private ArrayList<SearchItemInfo> result;
    private Cost cost;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSeid() {
        return seid;
    }

    public void setSeid(String seid) {
        this.seid = seid;
    }

    public String getPagesize() {
        return pagesize;
    }

    public void setPagesize(String pagesize) {
        this.pagesize = pagesize;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Sengine getSengine() {
        return sengine;
    }

    public void setSengine(Sengine sengine) {
        this.sengine = sengine;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getNumResults() {
        return numResults;
    }

    public void setNumResults(String numResults) {
        this.numResults = numResults;
    }

    public String getNumPages() {
        return numPages;
    }

    public void setNumPages(String numPages) {
        this.numPages = numPages;
    }

    public String getSuggest_keyword() {
        return suggest_keyword;
    }

    public void setSuggest_keyword(String suggest_keyword) {
        this.suggest_keyword = suggest_keyword;
    }

    public ArrayList<SearchItemInfo> getResult() {
        return result;
    }

    public void setResult(ArrayList<SearchItemInfo> result) {
        this.result = result;
    }

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

}
