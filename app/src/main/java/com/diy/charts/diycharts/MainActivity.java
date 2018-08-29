package com.diy.charts.diycharts;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.diy.charts.charts.AttributeChart;
import com.diy.charts.charts.beans.AttributeChartData;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    AttributeChart chart;
    ArrayList<AttributeChartData> data = new ArrayList<>();
    AttributeChartData data6 = new AttributeChartData();
    int i = 2;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(i < 20) {
                i++;
            }
            data.remove(4);
            data6.setValue(i);
            data.add(4,data6);
            chart.setData(data);
            handler.sendMessageDelayed(new Message(), 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = (AttributeChart)findViewById(R.id.chart);
        AttributeChartData data1 = new AttributeChartData();
        data1.setName("A");
        data1.setValue(19);
        data.add(data1);
        AttributeChartData data2 = new AttributeChartData();
        data2.setName("B");
        data2.setValue(12);
        data.add(data2);

        AttributeChartData data3 = new AttributeChartData();
        data3.setName("C");
        data3.setValue(16);
        data.add(data3);

        AttributeChartData data4 = new AttributeChartData();
        data4.setName("D");
        data4.setValue(22);
        data.add(data4);

        data6.setName("E");
        data6.setValue(i);
        data.add(data6);

        AttributeChartData data5 = new AttributeChartData();
        data5.setName("F");
        data5.setValue(11);
        data.add(data5);

        AttributeChartData data7 = new AttributeChartData();
        data7.setName("F");
        data7.setValue(11);
        data.add(data7);

        for(int i = 0; i < 2; i++){
            AttributeChartData data8 = new AttributeChartData();
            data8.setName("F");
            data8.setValue(4 + new Random().nextInt(10));
            data.add(data8);
        }

        AttributeChartData data8 = new AttributeChartData();
        data8.setName("F");
        data8.setValue(11);
        data.add(data8);
        chart.setData(data);
        handler.sendMessage(new Message());


    }
}
