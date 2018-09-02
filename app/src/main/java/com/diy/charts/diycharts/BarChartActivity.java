package com.diy.charts.diycharts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.diy.charts.beans.BarChartBean;
import com.diy.charts.beans.PiechartBean;
import com.diy.charts.view.BarChart;
import com.diy.charts.view.PieChart;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by xuzhendong on 2018/8/31.
 */

public class BarChartActivity extends AppCompatActivity {
    BarChart chart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        chart = (BarChart)findViewById(R.id.chart);
        ArrayList<BarChartBean> piechartBeans = new ArrayList<>();
        BarChartBean bean1 = new BarChartBean();
        bean1.setValue(20 + new Random().nextInt(100));
        bean1.setColor(Color.parseColor("#EE2C2C"));
        bean1.setName("杭州");
        piechartBeans.add(bean1);

        BarChartBean bean2 = new BarChartBean();
        bean2.setValue(20 + new Random().nextInt(100));
        bean2.setColor(Color.parseColor("#8A2BE2"));
        bean2.setName("上海");
        piechartBeans.add(bean2);

        BarChartBean bean3 = new BarChartBean();
        bean3.setValue(25 + new Random().nextInt(100));
        bean3.setColor(Color.parseColor("#43CD80"));
        bean3.setName("北京");
        piechartBeans.add(bean3);

        BarChartBean bean4 = new BarChartBean();
        bean4.setValue(13 + new Random().nextInt(100));
        bean4.setColor(Color.parseColor("#A52A2A"));
        bean4.setName("广州");
        piechartBeans.add(bean4);

        for(int i = 0; i < 10; i ++){
            BarChartBean bean5 = new BarChartBean();
            bean5.setValue(13 + new Random().nextInt(100));
            bean5.setColor(Color.parseColor("#" + i + "5" + i + "f2A"));
            bean5.setName("广州" + i);
            piechartBeans.add(bean5);
        }
        chart.setData(piechartBeans);
    }
}
