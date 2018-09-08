package com.diy.charts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.diy.charts.beans.MutiBarChartBean;
import com.diy.charts.beans.MutiBean;
import com.diy.charts.listener.OnChartItemClickListener;

import java.util.ArrayList;

/**
 * 垂直堆积图
 * Created by xuzhendong on 2018/9/2.
 */

public class VerticalStackBarChart extends BaseAxisChart<MutiBarChartBean> {
    public ArrayList<MutiBean> mutis;//分项目的种类
    private int clikPostion = -1;
    private OnChartItemClickListener listener;

    public VerticalStackBarChart(Context context) {
        super(context);
    }

    public VerticalStackBarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalStackBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        PADDING_LEFT = 80;
        axisWidth = 100;
    }

    @Override
    protected void drawDataVlaus(Canvas canvas) {
        for(int i = 0; i < mData.size(); i ++){
            float startX = sourcex + i * valueWidth;
            float startY = sourcey;
            float endX = startX + valueWidth;
            float endY = startY;
            mData.get(i).setX(startX);
            for(int j = 0; j < mutis.size(); j ++){
                defaultPaint.setColor(mutis.get(j).getColor());
                if (mData.get(i).getValues().size() > j) {
                    endY = endY - valueHeight * mData.get(i).getValues().get(j) * animationValue;
                }
                if(clikPostion == i){
                    defaultPaint.setColor(Color.BLACK);
                }
                canvas.drawRect(startX, startY, endX - 5, endY, defaultPaint);
                float txtWidth = mChartPaint.measureText("" + mData.get(i).getValues().get(j));
                float txtheight = mChartPaint.measureText("X");
                if(clikPostion == i){
                    defaultPaint.setColor(Color.WHITE);
                }
                if(mData.get(i).getValues().get(j) != 0) {
                    canvas.drawText("" + mData.get(i).getValues().get(j), startX + valueWidth / 2 - txtWidth / 2, endY + txtheight + 5, mChartPaint);
                }
                startY = endY;
            }
        }
    }

    @Override
    public boolean drawValueNumber() {
        return true;
    }

    @Override
    public boolean isYshowValue() {
        return true;
    }

    @Override
    public void drawXAxisTxt(Canvas canvas, int value, float x) {
        if(value <= mData.size()) {
            float valuetxtWidth = mChartPaint.measureText(mData.get(value - 1).getName());
            float valuetxtheight = mChartPaint.measureText("X");
            canvas.drawText(mData.get(value - 1).getName(),  x - valuetxtWidth/2,getMeasuredHeight() - PADDING_BOTTOM + valuetxtheight + 10, mChartPaint);
        }
    }

    @Override
    public void drawYAxisTxt(Canvas canvas, int value, float y) {

    }

    @Override
    protected boolean isdrawBottomTxt() {
        return true;
    }

    @Override
    protected void drawBottomTxt(Canvas canvas) {
        float startx = PADDING_LEFT;
        float starty = getMeasuredHeight() - PADDING_BOTTOM + 80;
        float txtHeight = mTextPaint.measureText("图");
        for (int i = 0; i < mutis.size(); i++) {
            if (mutis.get(i).getColor() != 0) {
                defaultPaint.setColor(mutis.get(i).getColor());
            }
            float width = mTextPaint.measureText(mutis.get(i).getName());
            canvas.drawText(mutis.get(i).getName(), startx, starty + txtHeight, mTextPaint);
            startx = startx + width + 20;
            canvas.drawRect(startx, starty, startx + 50, starty + txtHeight, defaultPaint);
            startx = startx + 50 + 30;
        }
    }

    @Override
    public float getMaxValue() {
        float result = 0;
        for(int i =0; i < mData.size(); i ++){
            float num = 0;
            for(int j = 0; j < mData.get(i).getValues().size(); j ++){
               num = num + mData.get(i).getValues().get(j);
            }
            if(num > result){
                result = num;
            }
        }
        return result;
    }

    @Override
    public float getMaxSize() {
        return mData.size() + 0.5f;
    }

    @Override
    public boolean showTotalData() {
        return false;
    }

    public void setData(ArrayList<MutiBarChartBean> values, ArrayList<MutiBean> mutis){
        this.mutis = mutis;
        setData(values);
    }

    @Override
    public void onSingleTap(MotionEvent event) {
        super.onSingleTap(event);
        clikPostion = -1;
        for(int i = 0; i < mData.size(); i ++){
            if(event.getX() <= mData.get(i).getX() + valueWidth && event.getX() > mData.get(i).getX()){
                clikPostion = i;
                if(listener != null){
                    listener.onChartItemClick(i);
                }
            }
        }
        invalidate();
    }

    public void setOnChartItemClickListener(OnChartItemClickListener listener){
        this.listener = listener;
    }
}
