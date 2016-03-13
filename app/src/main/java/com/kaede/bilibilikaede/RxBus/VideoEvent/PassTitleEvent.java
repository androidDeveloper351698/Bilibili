package com.kaede.bilibilikaede.RxBus.VideoEvent;

/**
 * Created by asus on 2016/2/27.
 */
public class PassTitleEvent {
    private String title;
    public PassTitleEvent(String newTitle){
        title = newTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
