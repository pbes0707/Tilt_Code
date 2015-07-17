package com.tiltcode.tiltcodemanager.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * Created by JSpiner on 2015. 7. 3..
 */
public class DisableViewPager extends ViewPager {

    /*
    ViewPager 움직이는걸 막도록 ViewPager를 상속받아 만듦
     */

    //public
    //public으로된 enableTouch를 바꿔주면 됩니다.
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
            return true;
        }
    }
}
