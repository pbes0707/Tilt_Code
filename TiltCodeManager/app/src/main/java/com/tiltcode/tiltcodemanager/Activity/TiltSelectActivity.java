package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tiltcode.tiltcodemanager.Adapter.TiltSelectPagerAdapter;
import com.tiltcode.tiltcodemanager.R;

/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class TiltSelectActivity extends Activity {

    //로그에 쓰일 tag
    public static final String TAG = TiltSelectActivity.class.getSimpleName();

    ViewPager pager;

    int[] selectResource = {R.drawable.thumb_select_1,R.drawable.thumb_select_2,R.drawable.thumb_select_3,R.drawable.thumb_select_4,R.drawable.thumb_select_5,R.drawable.thumb_select_6,R.drawable.thumb_select_7,R.drawable.thumb_select_8,R.drawable.thumb_select_9,R.drawable.thumb_select_10,R.drawable.thumb_select_11,R.drawable.thumb_select_12,R.drawable.thumb_select_13,R.drawable.thumb_select_14};
    int[] selectedResource = {R.drawable.thumb_unselect_1,R.drawable.thumb_unselect_2,R.drawable.thumb_unselect_3,R.drawable.thumb_unselect_4,R.drawable.thumb_unselect_5,R.drawable.thumb_unselect_6,R.drawable.thumb_unselect_7,R.drawable.thumb_unselect_8,R.drawable.thumb_unselect_9,R.drawable.thumb_unselect_10,R.drawable.thumb_unselect_11,R.drawable.thumb_unselect_12,R.drawable.thumb_unselect_13,R.drawable.thumb_unselect_14};

    int nowPage=0;

    ImageView imv[] = new ImageView[14];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiltselect);

        init();
    }

    void init(){
        //pager 셋팅
        pager = (ViewPager)findViewById(R.id.pager_tiltselect);
        TiltSelectPagerAdapter adapter = new TiltSelectPagerAdapter(TiltSelectActivity.this);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowPage = position;
                Log.d(TAG, "position : " + position);
                for (int i = 0; i < 14; i++) {
                    imv[i].setImageResource(selectedResource[i]);
                }

                imv[position].setImageResource(selectResource[position]);
                ((HorizontalScrollView)findViewById(R.id.sv_tiltselect)).smoothScrollTo(imv[position].getRight()-imv[position].getWidth(),0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //선택된 tilt값을 result에 넣어서 리턴함
        ((LinearLayout)findViewById(R.id.layout_tiltselect_proc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("tiltValue", (nowPage + 1));

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

        for(int i=0;i<14;i++){
            imv[i] = new ImageView(getBaseContext());
            imv[i].setImageResource(selectedResource[i]);
            imv[i].setLayoutParams(new LinearLayout.LayoutParams(width,height));//LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            final int finalI = i;
            imv[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int j=0;j<14;j++){
                        imv[j].setImageResource(selectedResource[j]);
                    }
                    ((ImageView)imv[finalI]).setImageResource(selectResource[finalI]);
                    pager.setCurrentItem(finalI);
                }
            });

            ((LinearLayout) findViewById(R.id.sv_tiltselect_thumbnail)).addView(imv[i]);

        }
        imv[0].setImageResource(selectResource[0]);
    }
}
