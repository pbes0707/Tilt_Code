package com.tiltcode.tiltcode.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tiltcode.tiltcode.Fragment.CouponListFragment;

/**
 * Created by JSpiner on 2015. 7. 3..
 */
public class DisableViewPager extends ViewPager {

    public boolean enableTouch = true;

    public DisableViewPager(Context context) {
        super(context);
    }

    public DisableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(enableTouch) {
            return super.onTouchEvent(ev);
        }
        else{
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(enableTouch) {
            return super.onInterceptTouchEvent(ev);
        }
        else{
            return CouponListFragment.interceptTouch.onTouch(null,ev);
//            return true;
        }
    }
}
