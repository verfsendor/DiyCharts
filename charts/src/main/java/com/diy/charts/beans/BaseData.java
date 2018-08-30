package com.diy.charts.beans;

import java.io.Serializable;

/**
 * Created by xuzhendong on 2018/8/30.
 */

public class BaseData implements Serializable {
    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
