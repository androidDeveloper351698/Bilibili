package com.kaede.bilibilikaede.Domain.Comment;

/**
 * Created by asus on 2016/2/25.
 */
public class LevelInfo {
    private String current_level;
    private String current_min;
    private String current_exp;
    private String next_exp;

    public String getCurrent_level() {
        return current_level;
    }

    public void setCurrent_level(String current_level) {
        this.current_level = current_level;
    }

    public String getCurrent_min() {
        return current_min;
    }

    public void setCurrent_min(String current_min) {
        this.current_min = current_min;
    }

    public String getCurrent_exp() {
        return current_exp;
    }

    public void setCurrent_exp(String current_exp) {
        this.current_exp = current_exp;
    }

    public String getNext_exp() {
        return next_exp;
    }

    public void setNext_exp(String next_exp) {
        this.next_exp = next_exp;
    }
}
