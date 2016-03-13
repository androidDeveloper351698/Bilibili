package com.kaede.bilibilikaede.RxBus.SearchEvent;

/**
 * Created by asus on 2016/3/1.
 */
public class PassSearchKeywordEvent {
    private String keyword;
    public PassSearchKeywordEvent(String keyword){
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
