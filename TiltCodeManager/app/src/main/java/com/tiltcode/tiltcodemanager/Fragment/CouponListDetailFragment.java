package com.tiltcode.tiltcodemanager.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tiltcode.tiltcodemanager.Activity.CouponListActivity;
import com.tiltcode.tiltcodemanager.Activity.SignupActivity;
import com.tiltcode.tiltcodemanager.Model.AnalyticResult;
import com.tiltcode.tiltcodemanager.Model.Coupon;
import com.tiltcode.tiltcodemanager.Model.CouponResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 27..
 */
public class CouponListDetailFragment extends Fragment {

    //로그에 쓰일 tag
    public static final String TAG = CouponListDetailFragment.class.getSimpleName();

    int layoutid;
    Context context;

    BarChart sexChart;
    LineChart ageChart;
    PieChart deviceChart;


    public static Coupon coupon;

    TextView tvTitle;
    TextView tvDownload;
    TextView tvEarn;

    public CouponListDetailFragment() {
        super();
        this.layoutid = R.layout.fragment_coupondetail;
        this.context = CouponListActivity.context;
        this.coupon = CouponListFragment.coupon;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;
        this.coupon = CouponListFragment.coupon;

        if (v == null) {
            v = inflater.inflate(layoutid, null);

            sexChart = (BarChart)v.findViewById(R.id.chart_coupondetail_sex);
            ageChart = (LineChart)v.findViewById(R.id.chart_coupondetail_age);
            deviceChart = (PieChart)v.findViewById(R.id.chart_coupondetail_device);

            tvTitle = ((TextView)v.findViewById(R.id.tv_coupondetail_title));
            tvDownload = ((TextView)v.findViewById(R.id.tv_coupondetail_download));
            tvEarn = ((TextView)v.findViewById(R.id.tv_coupondetail_earn));


            init();

        }
        return v;
    }

    //최댓값 구함
    int getMax(int[] arr){
        int max = -1;
        for(int i=0;i<arr.length;i++){
            if(max<arr[i]) max=arr[i];
        }
        return max;
    }

    //그래프에서 최대값을 기준으로 정규화함
    int getLinear(int value){
        return ((value+15)/10)*10;
    }

    void init() {

        tvTitle.setText(coupon.title);
        tvEarn.setText(coupon.desc);

        //쿠폰의 통계정보를 받아옴
        Util.getEndPoint().setPort("40004");
        Util.getHttpSerivce().getCouponAnalytics(Util.getAccessToken().getToken(),
                coupon.id,
                new Callback<AnalyticResult>() {
                    @Override
                    public void success(AnalyticResult analyticResult, Response response) {

                        Log.d(TAG,"analytic code : "+analyticResult.code);

                        if(analyticResult.code.equals("1")){
                            tvDownload.setText("다운로드수 : " +analyticResult.data.count);

                            Log.d(TAG, "size : " + analyticResult.data.age.length);



                            initChart(analyticResult);
                            /*
                            BarSet barSet = new BarSet();

                            Bar bar1 = new Bar("Man",analyticResult.data.sex[0]);
                            Bar bar2 = new Bar("Women",analyticResult.data.sex[1]);
                            Bar bar3 = new Bar("Unkown",analyticResult.data.sex[2]);

                            bar1.setColor(Color.CYAN);
                            bar2.setColor(Color.BLUE);
                            bar3.setColor(Color.MAGENTA);

                            barSet.addBar(bar1);
                            barSet.addBar(bar2);
                            barSet.addBar(bar3);


                            sexChart.addData(barSet);

                            sexChart.setSetSpacing(100);
                            int max = getLinear(getMax(analyticResult.data.age));
                            sexChart.setAxisBorderValues(0,max,max/10)
                                    .setBorderSpacing(50)
                                    .setYAxis(true)
                                    .setXLabels(XController.LabelPosition.OUTSIDE)
                                    .setYLabels(YController.LabelPosition.OUTSIDE);

                            Animation ani = new Animation();
                            ani.setDuration(500);
                            ani.setEasing(new LinearEase());
                            sexChart.show(ani);

                            LineSet lineSet = new LineSet();

                            for(int i=0;i<7;i++){
                                Point point = new Point(i*10+"~"+(i+1)*10,analyticResult.data.age[i]);

                                lineSet.addPoint(point);


                            }
                            lineSet.setLineColor(Color.CYAN);

                            ageChart.addData(lineSet);
                            max = getMax(analyticResult.data.age);
                            ageChart.setBorderSpacing(50)
                                    .setXAxis(true)
                                    .setXLabels(XController.LabelPosition.OUTSIDE)
                                    .setYAxis(true)
                                    .setYLabels(YController.LabelPosition.OUTSIDE)
                                    .setAxisBorderValues(0, getLinear(max), getLinear(max)/10);

                            Animation ani2 = new Animation();
                            ani2.setDuration(500);
                            ani2.setEasing(new LinearEase());
                            ageChart.show(ani2);*/
                        }
                        else if(analyticResult.code.equals("2")){
                            tvDownload.setText("다운로드수 : 0");
                            Toast.makeText(context,"아무도 쿠폰을 다운받지 않았습니다.",Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG,"error : "+error.getMessage());

                        Toast.makeText(context, getResources().getString(R.string.message_network_error),Toast.LENGTH_LONG).show();
                    }
                });




    }

    void initChart(AnalyticResult analyticResult){
        initBarChart(analyticResult);
        initLineChart(analyticResult);
        initPieChart(analyticResult);
    }


    void initPieChart(AnalyticResult analyticResult){

        Log.d(TAG,"device : "+analyticResult.data.model);

        ArrayList<String> xVals = new ArrayList<String>();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        int i=0;

        for (String key : analyticResult.data.model.keySet()) {
            i++;
            xVals.add(key);
            Entry entry = new Entry(Float.valueOf(analyticResult.data.model.get(key)),i);
            entries.add(entry);
        }

        PieDataSet set = new PieDataSet(entries, "");
//        set.setLin(40f);
        set.setColors(ColorTemplate.COLORFUL_COLORS);



        PieData data = new PieData(xVals, set);
        data.setValueTextSize(10f);
        data.setDrawValues(true);

        /*deviceChart.getXAxis().removeAllLimitLines();
        deviceChart.getXAxis().setEnabled(false);
        deviceChart.getAxisLeft().setTextColor(Color.WHITE);
        deviceChart.getAxisRight().setEnabled(false);*/
        deviceChart.setData(data);
        deviceChart.invalidate();

    }

    void initLineChart(AnalyticResult analyticResult){

        ArrayList<String> xVals = new ArrayList<String>();

        ArrayList<Entry> entries = new ArrayList<Entry>();
        LineData data = null;

        for (int i = 0; i < analyticResult.data.age.length; i++) {
            if(analyticResult.data.age[i]==0) continue;
            xVals.add(i*10+"~"+(i+1)*10);
            Entry entry = new Entry((int)analyticResult.data.age[i],i);
            entries.add(entry);

            LineDataSet set = new LineDataSet(entries, xVals.get(i));
//        set.setLin(40f);
            if(i<5) {
                set.setColor(ColorTemplate.LIBERTY_COLORS[i]);
            }
            else{
                set.setColor(ColorTemplate.COLORFUL_COLORS[i%5]);
            }
            if(i==0){

                data = new LineData(xVals, set);
                data.setValueTextSize(10f);
                data.setDrawValues(true);
            }
            else{
                data.addDataSet(set);
            }
        }




        ageChart.getXAxis().removeAllLimitLines();
//        ageChart.getXAxis().setEnabled(false);
        ageChart.getAxisLeft().setTextColor(Color.WHITE);
        ageChart.getAxisRight().setEnabled(false);
        ageChart.setData(data);
        ageChart.invalidate();

    }

    void initBarChart(AnalyticResult analyticResult){
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("남성");
        xVals.add("여성");
        xVals.add("알수없음");

        BarData data = null;

        for (int i = 0; i < analyticResult.data.sex.length; i++) {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            BarEntry entry = new BarEntry(analyticResult.data.sex[i],i);
            entries.add(entry);

            BarDataSet set = new BarDataSet(entries, xVals.get(i));
            set.setBarSpacePercent(40f);

            set.setColor(ColorTemplate.COLORFUL_COLORS[i]);

            if(i==0){
                data = new BarData(xVals, set);
            }
            else{
                data.addDataSet(set);
            }
        }

        data.setValueTextSize(10f);
        data.setDrawValues(true);

        sexChart.getXAxis().removeAllLimitLines();
//        sexChart.getXAxis().setEnabled(false);

        sexChart.getAxisLeft().setTextColor(Color.WHITE);
        sexChart.getAxisRight().setEnabled(false);
        sexChart.setData(data);
        sexChart.invalidate();






        Log.d(TAG,"chart init");
    }
}
