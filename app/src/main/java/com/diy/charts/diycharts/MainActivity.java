package com.diy.charts.diycharts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.diy.charts.view.AttributeChart;
import com.diy.charts.view.BarChart;
import com.diy.charts.view.SlikLineChart;
import com.diy.charts.view.StackBarChart;

public class MainActivity extends AppCompatActivity {
    SlikLineChart chart;
    AttributeChart chart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AttributeChartActivity.class));
            }
        });

        findViewById(R.id.b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SlikLineChartActivity.class));
            }
        });

        findViewById(R.id.c).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PieChartActivity.class));
            }
        });
        findViewById(R.id.d).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BarChartActivity.class));
            }
        });
        findViewById(R.id.e).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MutiBarChartActivity.class));
            }
        });

        findViewById(R.id.f).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,StackBarChartActivity.class));
            }
        });
    }


}
