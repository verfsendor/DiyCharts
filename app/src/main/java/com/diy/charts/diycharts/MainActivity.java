package com.diy.charts.diycharts;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.diy.charts.beans.SlikLineChartBean;
import com.diy.charts.beans.SlikLineChartPoint;
import com.diy.charts.view.AttributeChart;
import com.diy.charts.beans.AttributeChartData;
import com.diy.charts.view.SlikLineChart;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    SlikLineChart chart;
    AttributeChart chart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = (SlikLineChart)findViewById(R.id.chart);
        ArrayList<SlikLineChartBean> beans =  new ArrayList<>();
        Random random = new Random();
        SlikLineChartBean bean1 =
                new SlikLineChartBean()
                .setLineColor(Color.parseColor("#9932CC"))
                .setShowCircle(true)
                .setCircleRadius(5)
                .setName("语文")
                .setShowNum(true);
        ArrayList<SlikLineChartPoint> points = new ArrayList<>();
        for(int i = 0; i < 30; i ++){
            SlikLineChartPoint point = new SlikLineChartPoint();
            point.setValue(random.nextInt(100));
            points.add(point);
        }
        bean1.setData(points);
        beans.add(bean1);

        SlikLineChartBean bean2 =
                new SlikLineChartBean()
                        .setLineColor(Color.parseColor("#7CFC00"))
                        .setShowCircle(true)
                        .setCircleRadius(5)
                        .setName("数学")
                        .setShowNum(true);
        ArrayList<SlikLineChartPoint> points2 = new ArrayList<>();
        for(int i = 0; i < 30; i ++){
            SlikLineChartPoint point = new SlikLineChartPoint();
            point.setValue(random.nextInt(70));
            points2.add(point);
        }
        bean1.setData(points2);
        beans.add(bean2);

        SlikLineChartBean bean3 =
                new SlikLineChartBean()
                        .setLineColor(Color.parseColor("#8B1A1A"))
                        .setShowCircle(true)
                        .setCircleRadius(5)
                        .setName("英语")
                        .setShowNum(true);
        ArrayList<SlikLineChartPoint> points3 = new ArrayList<>();
        for(int i = 0; i < 30; i ++){
            SlikLineChartPoint point = new SlikLineChartPoint();
            point.setValue(random.nextInt(70));
            points3.add(point);
        }
        bean1.setData(points3);
        beans.add(bean3);
        chart.setData(beans);
    }

    public void initChart2(){
        chart2 = (AttributeChart)findViewById(R.id.chart2);
        ArrayList<AttributeChartData> data = new ArrayList<>();
        for (int i = 0; i < 5; i ++){
            AttributeChartData data1 = new AttributeChartData();
            data1.setValue(new Random().nextInt(20));
            data1.setName("属性" + (i+1));
            data.add(data1);
        }
        chart2.setData(data);
    }

}
