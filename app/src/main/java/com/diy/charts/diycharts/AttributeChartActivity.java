package com.diy.charts.diycharts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.diy.charts.beans.AttributeChartBean;
import com.diy.charts.view.AttributeChart;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by xuzhendong on 2018/8/31.
 */

public class AttributeChartActivity extends AppCompatActivity {

    AttributeChart chart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute_chart);
        chart = (AttributeChart)findViewById(R.id.chart);
        ArrayList<AttributeChartBean> data = new ArrayList<>();
        for (int i = 0; i < 5; i ++){
            AttributeChartBean data1 = new AttributeChartBean();
            data1.setValue(new Random().nextInt(10) + 5);
            data1.setName("属性" + (i+1));
            data.add(data1);
        }
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        chart.setData(data);
    }
}
