package com.diy.charts.diycharts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.diy.charts.beans.BarChartBean;
import com.diy.charts.beans.MutiBarChartBean;
import com.diy.charts.beans.MutiBean;
import com.diy.charts.view.BarChart;
import com.diy.charts.view.MutiBarChart;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by xuzhendong on 2018/8/31.
 */

public class MutiBarChartActivity extends AppCompatActivity {
    MutiBarChart chart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutibar_chart);
        chart = (MutiBarChart) findViewById(R.id.muti_chart);
        ArrayList<MutiBean> mutiBeans = new ArrayList<>();
        MutiBean mutiBean = new MutiBean();
        mutiBean.setName("北京");
        mutiBean.setColor(Color.parseColor("#EE4000"));
        mutiBeans.add(mutiBean);

        MutiBean mutiBean1 = new MutiBean();
        mutiBean1.setName("上海");
        mutiBean1.setColor(Color.parseColor("#B23AEE"));
        mutiBeans.add(mutiBean1);

        MutiBean mutiBean2 = new MutiBean();
        mutiBean2.setName("杭州");
        mutiBean2.setColor(Color.parseColor("#00CD66"));
        mutiBeans.add(mutiBean2);
        ArrayList<MutiBarChartBean> mutiBarChartBeans = new ArrayList<>();
        for(int i = 0; i < 5; i ++){
          MutiBarChartBean barChartBean = new MutiBarChartBean();
          ArrayList<Float> value = new ArrayList<>();
          value.add((float) new Random().nextInt(20));
          value.add((float) new Random().nextInt(20));
          value.add((float) new Random().nextInt(20));
          barChartBean.setValues(value);
          barChartBean.setName("名字" + i);
          mutiBarChartBeans.add(barChartBean);
        }
        chart.setData(mutiBarChartBeans, mutiBeans);
    }
}
