package com.diy.charts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.diy.charts.beans.MutiBarChartBean;
import com.diy.charts.beans.MutiBean;

import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/9/2.
 */

public class StackBarChart extends BaseAxisChart<MutiBarChartBean> {
    public ArrayList<MutiBean> mutis;//分项目的种类

    public StackBarChart(Context context) {
        super(context);
    }

    public StackBarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StackBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        PADDING_LEFT = 80;
    }

    @Override
    protected void drawDataVlaus(Canvas canvas) {
       for(int i = 0; i < mData.size(); i ++){
           float startX = sourcex;
           float startY = sourcey - (i+1) * valueHeight;
           float endX = 0;
           float endY = 0;
           for(int j = 0; j < mutis.size(); j ++) {
               defaultPaint.setColor(mutis.get(j).getColor());
               if (mData.get(i).getValues().size() > j) {
                   endX = startX + valueWidth * mData.get(i).getValues().get(j) * animationValue;
                   endY = startY + valueHeight;
               }
               canvas.drawRect(startX, startY + 5, endX, endY, defaultPaint);
               float txtWidth = mChartPaint.measureText("" + mData.get(i).getValues().get(j));
               float txtheight = mChartPaint.measureText("X");
               canvas.drawText("" + mData.get(i).getValues().get(j),endX - txtWidth - 5, endY - valueHeight/2 + txtheight/2,mChartPaint);
               startX = endX;
               startY = endY - valueHeight;
           }
       }
    }

    @Override
    public boolean drawValueNumber() {
        return true;
    }

    @Override
    public boolean isYshowValue() {
        return false;
    }

    @Override
    public void drawXAxisTxt(Canvas canvas, int value, float x) {

    }

    @Override
    public void drawYAxisTxt(Canvas canvas, int value, float y) {
        if(value <= mData.size()) {
            float valuetxtWidth = mChartPaint.measureText(mData.get(value - 1).getName());
            float valuetxtheight = mChartPaint.measureText("X");
            canvas.drawText(mData.get(value - 1).getName(), PADDING_LEFT - valuetxtWidth - 5, y - valueHeight/2 + valuetxtheight/2, mChartPaint);
        }
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

    public void setData(ArrayList<MutiBarChartBean> values, ArrayList<MutiBean> mutis){
        this.mutis = mutis;
        setData(values);
    }
}
