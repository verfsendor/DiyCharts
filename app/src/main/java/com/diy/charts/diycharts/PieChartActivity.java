package com.diy.charts.diycharts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.diy.charts.beans.PiechartBean;
import com.diy.charts.view.PieChart;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by xuzhendong on 2018/8/31.
 */

public class PieChartActivity extends AppCompatActivity {
    PieChart chart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        chart = (PieChart)findViewById(R.id.chart);
        ArrayList<PiechartBean> piechartBeans = new ArrayList<>();
        PiechartBean bean1 = new PiechartBean();
        bean1.setValue(20 + new Random().nextInt(100));
        bean1.setColor(Color.parseColor("#EE2C2C"));
        bean1.setName("杭州");
        piechartBeans.add(bean1);

        PiechartBean bean2 = new PiechartBean();
        bean2.setValue(20 + new Random().nextInt(100));
        bean2.setColor(Color.parseColor("#8A2BE2"));
        bean2.setName("上海");
        piechartBeans.add(bean2);

        PiechartBean bean3 = new PiechartBean();
        bean3.setValue(25 + new Random().nextInt(100));
        bean3.setColor(Color.parseColor("#43CD80"));
        bean3.setName("北京");
        piechartBeans.add(bean3);

        PiechartBean bean4 = new PiechartBean();
        bean4.setValue(13 + new Random().nextInt(100));
        bean4.setColor(Color.parseColor("#A52A2A"));
        bean4.setName("广州");
        piechartBeans.add(bean4);
        chart.setData(piechartBeans);
    }
}
