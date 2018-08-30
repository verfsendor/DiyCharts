package com.diy.charts.detector;

/**
 * Created by xuzhendong on 2018/8/30.
 */

public class GestureData {
    private float scaleX;
    private float scaleY;
    private float fousX;
    private float fousY;
    public GestureData(float scaleX, float scaleY, float fousX, float fousY){
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.fousX = fousX;
        this.fousY = fousY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getFousX() {
        return fousX;
    }

    public void setFousX(float fousX) {
        this.fousX = fousX;
    }

    public float getFousY() {
        return fousY;
    }

    public void setFousY(float fousY) {
        this.fousY = fousY;
    }
}
