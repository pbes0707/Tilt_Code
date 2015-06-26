package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Service.ServiceMonitor;

/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class MainActivity extends FragmentActivity{

//    private ServiceMonitor serviceMonitor = ServiceMonitor.getInstance();
    //로그에 쓰일 tag
    public static final String TAG = MainActivity.class.getSimpleName();

    Context context;

    ViewPager mPager;

    public static View layout_main_tab;

    int lastPage=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = getBaseContext();

        init();
    }

    void init() {

        layout_main_tab = findViewById(R.id.layout_main_tab);

        MainPagerAdapter adapter = new MainPagerAdapter(context,getSupportFragmentManager() );
        mPager = (ViewPager) findViewById(R.id.pager_mainview);
        mPager.setAdapter(adapter);
        mPager.setOffscreenPageLimit(3);


        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                selectTab(arg0);
                lastPage = arg0;
            }     //페이지가 바뀔때 마다 탭의 상태를 바꿈


            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mPager.setCurrentItem(1);

    }



    // 현재 보이는 탭을 포커스처리함
    public void selectTab(int position) {


        switch (position) {
            case 0:
//                tabCouponList.setSelected(true);
                break;
            case 1:
//                tabTiltCode.setSelected(true);
                break;
            case 2:
 //               tabSetting.setSelected(true);
                break;
        }


        Animation animation;
        Log.d(TAG,"last : "+lastPage+" position : "+position);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, lastPage,
                Animation.RELATIVE_TO_SELF, position,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
//        animation.setInterpolator(new AccelerateInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(500);

        layout_main_tab.clearAnimation();
        layout_main_tab.setAnimation(animation);

//        mPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
