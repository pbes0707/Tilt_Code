package com.tiltcode.tiltcode.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tiltcode.tiltcode.R;

/**
 * Created by JSpiner on 2015. 6. 25..
 */
public class TiltCodeView extends View {

    //로그에 쓰일 tag
    public static final String TAG = TiltCodeView.class.getSimpleName();

    private int width;
    private int height;

    Bitmap tiltImage;

    public TiltCodeView(Context context) {
        super(context);

        init();
    }

    public TiltCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TiltCodeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    void init(){

        tiltImage = BitmapFactory.decodeResource(getResources(), R.drawable.back);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d(TAG,"width : "+w+" height : "+h);

        this.width = w;
        this.height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG,"ondraw");

        //canvas.drawBitmap(tiltImage,null, new Rect(0,0,250,250),null);
        //super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"ontouch");
        return super.onTouchEvent(event);
    }
}
