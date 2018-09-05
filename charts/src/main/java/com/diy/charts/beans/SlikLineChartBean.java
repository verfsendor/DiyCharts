package com.diy.charts.beans;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/30.
 */

public class SlikLineChartBean implements Serializable {
    private String name;
    private ArrayList<SlikLineChartPoint> data;//存储
    private int linecolor;
    private int circlecolor;//数据圆点颜色
    private int circleRadius = 5;//数据圆点半径
    private int numTextsize = 12;
    private int decimalN = 1;
    private boolean showNum = false;//是否在线上显示数字
    private boolean showCircle = true;//是否展示数据圆点
    private boolean showSlik = true;//是否展示圆滑效果
    public SlikLineChartBean(){
        data = new ArrayList<>();
        linecolor = Color.parseColor("#333333");
        circlecolor = Color.parseColor("#ffffff");
    }

    public SlikLineChartBean setData(ArrayList<SlikLineChartPoint> points){
        this.data = points;
        return this;
    }

    public SlikLineChartBean setLineColor(int color){
        this.linecolor = color;
        return this;
    }

    public SlikLineChartBean setDecimalN(int n){
        this.decimalN = n;
        return this;
    }

    public SlikLineChartBean setCircleColor(int color){
        this.circlecolor = color;
        return this;
    }

    public SlikLineChartBean setCircleRadius(int radius){
        this.circleRadius = radius;
        return this;
    }

    public SlikLineChartBean setNumTextsize(int size){
        this.numTextsize = size;
        return this;
    }

    public SlikLineChartBean setShowSlik(boolean showSlik){
        this.showSlik = showSlik;
        return this;
    }

    public SlikLineChartBean setShowNum(boolean showNum){
        this.showNum = showNum;
        return this;
    }

    public SlikLineChartBean setShowCircle(boolean showCircle){
        this.showCircle = showCircle;
        return this;
    }

    public String getName() {
        return name;
    }

    public SlikLineChartBean setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<SlikLineChartPoint> getData() {
        return data;
    }

    public int getLinecolor() {
        return linecolor;
    }

    public void setLinecolor(int linecolor) {
        this.linecolor = linecolor;
    }

    public int getCirclecolor() {
        return circlecolor;
    }

    public void setCirclecolor(int circlecolor) {
        this.circlecolor = circlecolor;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public int getNumTextsize() {
        return numTextsize;
    }

    public boolean isShowCircle() {
        return showCircle;
    }

    public boolean isShowNum() {
        return showNum;
    }

    public boolean isShowSlik() {
        return showSlik;
    }

    public int getDecimalN() {
        return decimalN;
    }
}
