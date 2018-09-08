package com.diy.charts.utils;
import android.util.Log;

import java.math.BigDecimal;

/**
 * Created by xuzhendong on 2018/9/1.
 */

public class ChartUtil {
    /**
     * 根据坐标，获取与Y轴反方向夹角的角度值（在0-360度之间）
     *
     * @param x 对边
     * @param y 邻边
     * @return
     */
    public static double getTanDegreeInverseY(float x, float y) {
        double result = 0;
        if (y <= 0) {
            result = 180 + Math.toDegrees(Math.atan(x / y));
        } else if (x >= 0) {
            result = Math.toDegrees(Math.atan(x / y));
        } else if (x < 0) {
            result = 360 + Math.toDegrees(Math.atan(x / y));
        }
        return result;
    }

    /**
     * 根据坐标，获取与X轴正方向夹角的角度值（在0-360度之间）
     *
     * @param x 对边
     * @param y 邻边
     * @return
     */
    public static double getTanDegreeX(float x, float y) {
        double degree = getTanDegreeInverseY(x, y);
        degree = 360 - ((270 + degree) % 360);
        return degree;
    }


    public static float getFloatDecimal(float value,int n) {
        float result = value;
        BigDecimal b = new BigDecimal(result);
        result =b.setScale(n, BigDecimal.ROUND_HALF_UP).floatValue();
        return result;
    }
}
