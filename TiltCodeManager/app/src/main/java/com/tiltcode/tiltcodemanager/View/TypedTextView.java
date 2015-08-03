package com.tiltcode.tiltcodemanager.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by JSpiner on 2015. 7. 2..
 */
public class TypedTextView extends TextView {

    //로그에 쓰일 tag
    public static final String TAG = TypedTextView.class.getSimpleName();

    public TypedTextView(Context context) {
        super(context);
//        setFont(context);
    }

    public TypedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context, attrs);
    }

    public TypedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TypedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFont(context, attrs);
    }

    void setFont(Context context, AttributeSet attrs){
        String packageName = "http://schemas.android.com/apk/res-auto";
        String font = attrs.getAttributeValue(packageName,"fonttype");
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(),""+font+".ttf");
        setTypeface(typeFace);
    }
}
