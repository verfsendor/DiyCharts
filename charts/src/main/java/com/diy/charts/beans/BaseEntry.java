package com.diy.charts.beans;

import java.io.Serializable;

/**
 * Created by xuzhendong on 2018/8/30.
 */

public class BaseEntry implements Serializable {
    private float value;
    private String name;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
