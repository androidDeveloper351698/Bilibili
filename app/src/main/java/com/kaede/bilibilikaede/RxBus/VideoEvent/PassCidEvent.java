package com.kaede.bilibilikaede.RxBus.VideoEvent;

/**
 * Created by kaede31416 on 16-2-14.
 * 传递Cid的Event 将会触发Activity初始化播放新的视频
 */
public class PassCidEvent {
    private String cid;
    private boolean isPlayImmediate;
    public PassCidEvent(String cid,boolean isPlayImmediate){
        this.cid = cid;
        this.isPlayImmediate = isPlayImmediate;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public boolean isPlayImmediate() {
        return isPlayImmediate;
    }

    public void setPlayImmediate(boolean playImmediate) {
        isPlayImmediate = playImmediate;
    }
}
