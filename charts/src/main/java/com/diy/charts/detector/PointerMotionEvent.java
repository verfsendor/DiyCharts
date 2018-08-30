package com.diy.charts.detector;

/**
 * 多点触控event
 * Created by xuzhendong on 2018/8/30.
 */

public class PointerMotionEvent {
    public float x;
    public float y;
    public int actionIndex;
    public int action;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
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

    public int getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(int actionIndex) {
        this.actionIndex = actionIndex;
    }
}
