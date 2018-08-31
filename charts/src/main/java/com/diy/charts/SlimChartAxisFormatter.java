package com.diy.charts;

/**
 * Created by xuzhendong on 2018/8/28.
 */

public class SlimChartAxisFormatter implements AxisFormatter {
    @Override
    public String getCoordinate(int value) {
        return "" + value;
    }
}
