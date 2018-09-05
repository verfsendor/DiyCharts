package com.diy.charts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.diy.charts.beans.MutiBarChartBean;
import com.diy.charts.beans.MutiBean;

import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/9/2.
 */

public class MutiBarChart extends BaseAxisChart<MutiBarChartBean> {
    public ArrayList<MutiBean> mutis;//分项目的种类

    public MutiBarChart(Context context) {
        super(context);
    }

    public MutiBarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MutiBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawDataVlaus(Canvas canvas) {
       for(int i = 0; i < mData.size(); i ++){
           for(int j = 0; j < mutis.size(); j ++){
               defaultPaint.setColor(mutis.get(j).getColor());
               float startX = sourcex + valueWidth * i + valueWidth/3 * j;
               float startY = getMeasuredHeight() - PADDING_BOTTOM;
               float txtWidth = mChartPaint.measureText("0.0");
               float txtheight = mChartPaint.measureText("0");
               String txt = "";
               if(mData.get(i).getValues().size() > j){
                   startY = sourcey - valueHeight * mData.get(i).getValues().get(j);
                   txtWidth = mChartPaint.measureText("" + mData.get(i).getValues().get(j));
                   txt = "" + mData.get(i).getValues().get(j);
               }
               startY = getMeasuredHeight() - PADDING_BOTTOM - animationValue *(getMeasuredHeight() - PADDING_BOTTOM - startY);
               if(j == 0){
                   canvas.drawRect(startX + 5, startY, startX + valueWidth/3, getMeasuredHeight()- PADDING_BOTTOM, defaultPaint);
               }else if(j == mutis.size() - 1){
                   canvas.drawRect(startX, startY, startX + valueWidth/3 - 5, getMeasuredHeight()- PADDING_BOTTOM, defaultPaint);
               }else {
                   canvas.drawRect(startX, startY, startX + valueWidth/3, getMeasuredHeight()- PADDING_BOTTOM, defaultPaint);
               }
//               canvas.drawRect(startX, startY, startX + valueWidth/3, getMeasuredHeight()- PADDING_BOTTOM, defaultPaint);
               canvas.drawText(txt,startX + valueWidth/6 - txtWidth/2, startY - txtheight, mChartPaint);
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
            canvas.drawText(mData.get(value - 1).getName(), x + valueWidth / 2 - valuetxtWidth / 2, getMeasuredHeight() - PADDING_BOTTOM + valuetxtheight * 2, mChartPaint);
        }
    }

    @Override
    public void drawYAxisTxt(Canvas canvas, int valu, float y) {

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
            for(int j = 0; j < mData.get(i).getValues().size(); j ++){
                if(mData.get(i).getValues().get(j) > result){
                    result = mData.get(i).getValues().get(j);
                }
            }
        }
        return result;
    }

    @Override
    public float getMaxSize() {
        return mData.size();
    }

    public void setData(ArrayList<MutiBarChartBean> values, ArrayList<MutiBean> mutis){
        this.mutis = mutis;
        setData(values);
    }
}
