package com.diy.charts.detector;
import android.view.MotionEvent;
import java.util.ArrayList;

/**
 * 自定义手势监听器-可以监听 单独 X或者y方向的缩放
 * Created by xuzhendong on 2018/8/30.
 */

public class DirectionGestureDector {
    private float scaleX = 1;
    private float scaleY = 1;
    private float fousX;
    private float fousY;
    private long num;

    private DirectionGestureDectorListenr listenr;
    /**
     * 存储两个手指的触控点坐标， 0位置存储手指index，1，2位置存储开始点坐标。 3，4位置存储当前点坐标
     */
    private ArrayList<PointerMotionEvent> finger1;
    private ArrayList<PointerMotionEvent> finger2;

    public DirectionGestureDector(DirectionGestureDectorListenr listenr){
        this.listenr = listenr;
        finger1 = new ArrayList<>();
        finger2 = new ArrayList<>();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            if(event.getPointerCount() == 2){
                if(event.getX(1) != 0 && event.getY(1) != 0){
                   PointerMotionEvent pointerMotionEvent = new PointerMotionEvent();
                   pointerMotionEvent.setX(event.getX(1));
                   pointerMotionEvent.setY(event.getY(1));
                   pointerMotionEvent.setActionIndex(1);
                   pointerMotionEvent.setAction(event.getAction());
                    analysisMontionEvent(pointerMotionEvent);
                }
                if(event.getX(0) != 0 && event.getY(0) != 0){
                    PointerMotionEvent pointerMotionEvent = new PointerMotionEvent();
                    pointerMotionEvent.setX(event.getX(0));
                    pointerMotionEvent.setY(event.getY(0));
                    pointerMotionEvent.setActionIndex(0);
                    pointerMotionEvent.setAction(event.getAction());
                    analysisMontionEvent(pointerMotionEvent);
                }
            }
        }else {
            PointerMotionEvent pointerMotionEvent = new PointerMotionEvent();
            pointerMotionEvent.setX(event.getX());
            pointerMotionEvent.setY(event.getY());
            pointerMotionEvent.setActionIndex(event.getActionIndex());
            pointerMotionEvent.setAction(event.getAction());
            analysisMontionEvent(pointerMotionEvent);
        }
        return true;
    }


    public void analysisMontionEvent(PointerMotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_UP){
            fousX = 0;
            fousY = 0;
            scaleY = 1;
            scaleX = 1;
            num = 0;
            finger1.clear();
            finger2.clear();
            return;
        }
        num ++;

        if(event.getActionIndex() == 0){
            if(finger2.size() > 0 && event.getX() == finger2.get(0).getX() && event.getY() == finger2.get(0).getY()){
                return;
            }
            if(finger1.size() == 0){
                finger1.add(event);
            }else {
                if(finger1.size() > 1){
                    finger1.remove(1);
                }
                finger1.add(event);
            }
        }

        if(event.getActionIndex() == 1){
            if(finger1.size() > 0 && event.getX() == finger1.get(0).getX() && event.getY() == finger1.get(0).getY()){
                return;
            }
            if(finger2.size() == 0){
                finger2.add(event);
            }else {
                if(finger2.size() > 1){
                    finger2.remove(1);
                }
                finger2.add(event);
            }
        }
        if(num % 1 != 0){
            return;
        }
        if(finger1.size() == 2 && finger2.size() == 2){
            fousX = (finger1.get(0).getX() + finger2.get(0).getX()) / 2;
            fousY = (finger1.get(0).getY() + finger2.get(0).getY()) / 2;
            boolean driectionXHorintal1 = false;
            boolean driectionXHorintal2 = false;
            /**
             * 两根手指的起点终点坐标的xy差值
             */
            float finger1x = finger1.get(1).getX() - finger1.get(0).getX();
            float finger1y = finger1.get(1).getY() - finger1.get(0).getY();

            float finger2x = finger2.get(1).getX() - finger2.get(0).getX();
            float finger2y = finger2.get(1).getY() - finger2.get(0).getY();
            /**
             * 判断两个手指，如果两个坐标的做差后异号，即方向相反，此时可以认定手势做的是缩放手势，再进行下一步处理
             */
            if(finger1x * finger2x >= 0){
                return;
            }
            if(Math.abs(finger1x) > Math.abs(finger1y)){
                driectionXHorintal1 = true;
            }
            if(Math.abs(finger2x) > Math.abs(finger2y)){
                driectionXHorintal2 = true;
            }
            /**
             *  如果两个都是水平方向，则认定是水平缩放,否则为竖直缩放，并根据距离变化判断缩放种类和缩放比例
             */
            if(driectionXHorintal1 && driectionXHorintal2){
                float distance1 = Math.abs(finger1.get(0).getX() - finger2.get(0).getX());
                float distance2 = Math.abs(finger1.get(1).getX() - finger2.get(1).getX());
                if(distance2 > distance1) {
                    scaleX = 1 + (Math.abs(finger1x) + Math.abs(finger2x)) / 3000;
                }else {
                    scaleX = 1 - (Math.abs(finger1x) + Math.abs(finger2x)) / 3000;
                }
                scaleY = 1;
            }else {
                float distance3 = Math.abs(finger1.get(0).getY() - finger2.get(0).getY());
                float distance4 = Math.abs(finger1.get(1).getY() - finger2.get(1).getY());
                if(distance3 > distance4) {
                    scaleY = 1 + (Math.abs(finger1x) + Math.abs(finger2x)) / 3000;
                }else {
                    scaleY = 1 - (Math.abs(finger1x) + Math.abs(finger2x)) / 3000;
                }
                scaleX = 1;
            }
            GestureData data = new GestureData(scaleX, scaleY, fousX, fousY);
            listenr.onScale(data);
        }
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
