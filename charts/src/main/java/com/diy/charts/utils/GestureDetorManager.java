package com.diy.charts.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.diy.charts.detector.DirectionGestureDector;
import com.diy.charts.detector.DirectionGestureDectorListenr;
import com.diy.charts.detector.GestureData;
import com.diy.charts.listener.DetorListener;

/**
 * Created by xuzhendong on 2018/8/31.
 */
public class GestureDetorManager {
    private Context context;
    private final float MAX_SCALE = 7; //最大缩放比例
    /**
     * 缩放中心以及缩放比例
     */
    public float focusX;
    public float focusY;
    public float scaleValueX = 1f;
    public float scaleValueY = 1f;
    /**
     * 平移x y 距离
     */
    public float scrollDistanceX;
    public float scrollDistanceY;

    /**
     * 手势监听器
     */
    private DirectionGestureDector mDirectionGestureDector;
    private GestureDetector mGesturDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private DetorListener listener;


    public GestureDetorManager(Context context){
        this.context = context;
        mDirectionGestureDector = new DirectionGestureDector(new DirectionGestureDectorListenr() {
            @Override
            public boolean onScale(GestureData detector) {
                focusX = detector.getFousX();
                focusY = detector.getFousY();
                scaleValueX = scaleValueX * detector.getScaleX();
                scaleValueY = scaleValueY * detector.getScaleY();
                if(scaleValueY < 1){
                    scaleValueY = 1;
                    scrollDistanceY = 0;
                }
                if(scaleValueX < 1){
                    scaleValueX = 1;
                    scrollDistanceX = 0;
                }
                scaleValueX = scaleValueX > MAX_SCALE ? MAX_SCALE : scaleValueX;
                scaleValueY = scaleValueY > MAX_SCALE ? MAX_SCALE : scaleValueY;
                if(listener != null){
                    listener.refreshView();
                }
                return true;
            }
        });
        mScaleGestureDetector =  new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                scaleValueX = scaleValueX * detector.getScaleFactor();
                scaleValueY = scaleValueY * detector.getScaleFactor();
                if(scaleValueY < 1){
                    scaleValueY = 1;
                    scrollDistanceY = 0;
                }
                if(scaleValueX < 1){
                    scaleValueX = 1;
                    scrollDistanceX = 0;
                }
                scaleValueX = scaleValueX > MAX_SCALE ? MAX_SCALE : scaleValueX;
                scaleValueY = scaleValueY > MAX_SCALE ? MAX_SCALE : scaleValueY;
                if(listener != null){
                    listener.refreshView();
                }
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });
        mGesturDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                listener.onSingleTap(e);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1){
                    return true;
                }
                //只有当value>1,即图表处于放大状态时才可以拖动
                if(scaleValueX > 1) {
                    scrollDistanceX = scrollDistanceX - distanceX;
                    //不可以往
                }
                if(scaleValueY > 1) {
                    scrollDistanceY = scrollDistanceY - distanceY;
                }
                if(listener != null){
                    listener.refreshView();
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        mGesturDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                scaleValueX = scaleValueX * 1.05f;
                scaleValueY = scaleValueY * 1.05f;
                focusX = e.getX();
                focusY = e.getY();
                listener.refreshView();
                return false;
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event){
        mScaleGestureDetector.onTouchEvent(event);
        mGesturDetector.onTouchEvent(event);
       return true;
    }

    public void setDetorListener(DetorListener listener){
        this.listener = listener;
    }
}
