package com.diy.charts.beans;

import java.io.Serializable;

/**
 * Created by xuzhendong on 2018/9/2.
 */

public class MutiBean implements Serializable {
    private String name;
    private int color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
