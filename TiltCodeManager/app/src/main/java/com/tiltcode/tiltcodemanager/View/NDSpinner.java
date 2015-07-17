package com.tiltcode.tiltcodemanager.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by JSpiner on 2015. 7. 12..
 */
public class NDSpinner extends Spinner {

    /*
    item 선택후 재선택시 호출이 안되는 버그때문에 만듬
     */

    OnItemSelectedListener listener;

    public NDSpinner(Context context) {
        super(context);
    }

    public NDSpinner(Context context, int mode) {
        super(context, mode);
    }

    public NDSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NDSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public NDSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    // 이부분이 재선택시 listener를 다시 호출해주는부분
    @Override
    public void setSelection(int position)
    {
        super.setSelection(position);

        if (position == getSelectedItemPosition())
        {
            if(listener!=null) listener.onItemSelected(null, null, position, 0);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener)
    {
        this.listener = listener;
    }

}