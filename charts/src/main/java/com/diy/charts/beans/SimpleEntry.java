package com.diy.charts.beans;

/**
 * Created by xuzhendong on 2018/8/30.
 */

public class SimpleEntry extends BaseEntry {
    private int color;
    private float x;
    private float y;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
