package com.diy.charts.diycharts;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.diy.charts.charts.AttributeChart;
import com.diy.charts.charts.beans.AttributeChartData;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    AttributeChart chart;
    ArrayList<AttributeChartData> data = new ArrayList<>();
    AttributeChartData data6 = new AttributeChartData();
    int i = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = (AttributeChart)findViewById(R.id.chart);
        chart.setEnabled(true);
        final AttributeChartData data1 = new AttributeChartData();
        data1.setName("属Add");
        data1.setValue(19);
        data.add(data1);
        AttributeChartData data2 = new AttributeChartData();
        data2.setName("属Bdd");
        data2.setValue(12);
        data.add(data2);

        AttributeChartData data3 = new AttributeChartData();
        data3.setName("属Cg");
        data3.setValue(16);
        data.add(data3);

        AttributeChartData data4 = new AttributeChartData();
        data4.setName("属D");
        data4.setValue(25);
        data.add(data4);

        data6.setName("属dsggE");
        data6.setValue(i);
        data.add(data6);

        AttributeChartData data5 = new AttributeChartData();
        data5.setName("属dggddgF");
        data5.setValue(11);
        data.add(data5);

        AttributeChartData data7 = new AttributeChartData();
        data7.setName("属dggG");
        data7.setValue(11);
        data.add(data7);

        AttributeChartData data8 = new AttributeChartData();
        data8.setName("属sggH");
        data8.setValue(11);
        data.add(data8);

        for(int i = 0; i < 2; i ++) {
            AttributeChartData data9 = new AttributeChartData();
            data9.setName("属sssssH");
            data9.setValue(11);
            data.add(data9);
        }
        chart.setData(data);

        findViewById(R.id.textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttributeChartData data9 = new AttributeChartData();
                data9.setName("徐镇东aa");
                data9.setValue(11);
                data.add(data9);
                chart.notifyDataChanged(data);
            }
        });
    }
}
