package com.diy.charts.beans;

import java.text.DecimalFormat;

/**
 * Created by xuzhendong on 2018/8/31.
 */

public class PiechartBean extends SimpleEntry {
   private float percent;
   private float startAngel;
   private float sweepAngel;


    public float getPercent() {
        return percent;
    }

    public String getPercenttxt() {
        float x = percent * 100;
        DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String result = decimalFormat.format(x);//format 返回的是字符串
        return result + "%";
    }

    public float getStartAngel() {
        return startAngel % 360;
    }

    public void setStartAngel(float startAngel) {
        this.startAngel = startAngel;
    }

    public float getSweepAngel() {
        return sweepAngel;
    }

    public void setSweepAngel(float sweepAngel) {
        this.sweepAngel = sweepAngel;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }


}
