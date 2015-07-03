package com.tiltcode.tiltcodemanager.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import com.db.chart.model.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.tiltcode.tiltcodemanager.Activity.SignupActivity;
import com.tiltcode.tiltcodemanager.R;

import java.text.DecimalFormat;

/**
 * Created by JSpiner on 2015. 6. 27..
 */
public class CouponListDetailFragment extends Fragment {

    //로그에 쓰일 tag
    public static final String TAG = CouponListDetailFragment.class.getSimpleName();

    int layoutid;
    Context context;

    BarChartView sexChart;
    LineChartView ageChart;

    public CouponListDetailFragment() {
        super();
        this.layoutid = R.layout.fragment_coupondetail;
        this.context = SignupActivity.context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;

        if (v == null) {
            v = inflater.inflate(layoutid, null);

            sexChart = (BarChartView)v.findViewById(R.id.chart_coupondetail_sex);
            ageChart = (LineChartView)v.findViewById(R.id.chart_coupondetail_age);

            init();

        }
        return v;
    }

    void init() {


        BarSet barSet = new BarSet();

        Bar bar1 = new Bar("Man",100);
        Bar bar2 = new Bar("Women",80);
        Bar bar3 = new Bar("Unkown",120);

        bar1.setColor(Color.CYAN);
        bar2.setColor(Color.BLUE);
        bar3.setColor(Color.MAGENTA);

        barSet.addBar(bar1);
        barSet.addBar(bar2);
        barSet.addBar(bar3);


        sexChart.addData(barSet);

        sexChart.setSetSpacing(100);
        sexChart.setAxisBorderValues(0,140,20)
                .setBorderSpacing(50)
                .setYAxis(true)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYLabels(YController.LabelPosition.OUTSIDE);

        sexChart.show();

        LineSet lineSet = new LineSet();

        for(int i=0;i<10;i++){
            Point point = new Point("dd",(int)(Math.random()*100));
            lineSet.addPoint(point);
        }

        ageChart.addData(lineSet);
        ageChart.setBorderSpacing(50)
//                .setGrid(LineChartView.GridType.HORIZONTAL, mLineGridPaint)
                .setXAxis(true)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(true)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(0, 100, 30);
        ageChart.show();


    }
}
