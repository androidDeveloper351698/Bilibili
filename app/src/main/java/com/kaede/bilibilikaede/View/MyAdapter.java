package com.kaede.bilibilikaede.View;

import android.widget.TextView;

/**
 * Created by asus on 2016/2/10.
 */
public abstract class MyAdapter{
    public abstract TextView getTextView(int position);
    public abstract int getTextViewCount();
}
