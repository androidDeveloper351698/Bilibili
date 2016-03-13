package com.kaede.bilibilikaede;

import android.app.Application;
import android.os.Handler;

import com.kaede.bilibilikaede.Database.VideoDao;

/**
 * Created by asus on 2016/1/21.
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    private static Handler mHandler; //全局Handler

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mHandler = new Handler();
    }

    public static MyApplication getInstance(){
        return instance;
    }

    public static Handler getMainHandler(){
        return mHandler;
    }
}
