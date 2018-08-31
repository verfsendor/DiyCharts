package com.diy.charts.beans;

import java.io.Serializable;

/**
 * Created by xuzhendong on 2018/8/31.
 */

public class PointBean implements Serializable {
    private float x;
    private float y;
    public PointBean(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
