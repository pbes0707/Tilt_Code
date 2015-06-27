package com.tiltcode.tiltcode.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.tiltcode.tiltcode.Fragment.CouponListFragment;
import com.tiltcode.tiltcode.Fragment.SettingFragment;
import com.tiltcode.tiltcode.Fragment.TiltCodeFragment;

/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    //로그에 쓰일 tag
    public static final String TAG = MainPagerAdapter.class.getSimpleName();


    public Fragment[] FRAGMENTS;
    FragmentManager fm;

    public static Context context;

    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.context = context;
        FRAGMENTS = new Fragment[3];
        FRAGMENTS[0] = new CouponListFragment();
        FRAGMENTS[1] = new TiltCodeFragment();
        FRAGMENTS[2] = new SettingFragment();
    }

    @Override
    public Fragment getItem(int position) {
        return FRAGMENTS[position];
    }

    @Override
    public int getCount() {
        return FRAGMENTS.length;
    }

    public void replace(Fragment fragment){
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fm.getFragments().get(0));

        ft.add(fragment, "");
        notifyDataSetChanged();

    }

}