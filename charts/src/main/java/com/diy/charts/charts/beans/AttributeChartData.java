package com.diy.charts.charts.beans;

import java.io.Serializable;

/**
 * Created by xuzhendong on 2018/8/28.
 */

public class AttributeChartData implements Serializable {
    private String name;
    private float value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
