package com.diy.charts.diycharts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.diy.charts.beans.SlikLineChartBean;
import com.diy.charts.beans.SlikLineChartPoint;
import com.diy.charts.view.SlikLineChart;

import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/31.
 */

public class SlikLineChartActivity extends AppCompatActivity {
    SlikLineChart chart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slikline_chart);
        chart = (SlikLineChart)findViewById(R.id.chart);
        ArrayList<SlikLineChartBean> beans =  new ArrayList<>();
        SlikLineChartBean bean3 =
                new SlikLineChartBean()
                        .setLineColor(Color.parseColor("#ffffff"))
                        .setShowCircle(true)
                        .setCircleRadius(7)
                        .setNumTextsize(20)
                        .setName("英语")
                        .setShowNum(true);
        ArrayList<SlikLineChartPoint> points3 = new ArrayList<>();
        SlikLineChartPoint a = new SlikLineChartPoint();
        a.setValue(4);
        points3.add(a);
        SlikLineChartPoint b = new SlikLineChartPoint();
        b.setValue(15);
        points3.add(b);
        SlikLineChartPoint c = new SlikLineChartPoint();
        c.setValue(8);
        points3.add(c);
        SlikLineChartPoint d = new SlikLineChartPoint();
        d.setValue(9);
        points3.add(d);
        SlikLineChartPoint e = new SlikLineChartPoint();
        e.setValue(6);
        points3.add(e);
        SlikLineChartPoint f = new SlikLineChartPoint();
        f.setValue(10);
        points3.add(f);
        SlikLineChartPoint g = new SlikLineChartPoint();
        g.setValue(15);
        points3.add(g);
        SlikLineChartPoint h = new SlikLineChartPoint();
        h.setValue(11);
        points3.add(h);

        bean3.setData(points3);
        beans.add(bean3);
        chart.setData(beans);
    }
}
