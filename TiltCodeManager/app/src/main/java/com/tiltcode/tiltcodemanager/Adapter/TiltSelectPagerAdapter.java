package com.tiltcode.tiltcodemanager.Adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.tiltcode.tiltcodemanager.R;

/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class TiltSelectPagerAdapter extends PagerAdapter {

    int[] resources = {R.drawable.tilt_1,R.drawable.tilt_2,R.drawable.tilt_3,R.drawable.tilt_4,R.drawable.tilt_5,R.drawable.tilt_6,R.drawable.tilt_7,R.drawable.tilt_8,R.drawable.tilt_9,R.drawable.tilt_10,R.drawable.tilt_11,R.drawable.tilt_12,R.drawable.tilt_13,R.drawable.tilt_14};

    private LayoutInflater mInflater;

    public TiltSelectPagerAdapter(Context c){
        super();
        mInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return 14;
    }

    @Override
    public Object instantiateItem(View pager, int position) {
        View v = null;
        v = mInflater.inflate(R.layout.item_tiltselect_row, null);
        ImageView imv = ((ImageView)v.findViewById(R.id.imv_tiltselect_row));

        imv.setImageResource(resources[position]);

        ((ViewPager) pager).addView(v, 0);

        return v;
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager)pager).removeView((View)view);
    }

    @Override
    public boolean isViewFromObject(View pager, Object obj) {
        return pager == obj;
    }

    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
    @Override public Parcelable saveState() { return null; }
    @Override public void startUpdate(View arg0) {}
    @Override public void finishUpdate(View arg0) {}
}
