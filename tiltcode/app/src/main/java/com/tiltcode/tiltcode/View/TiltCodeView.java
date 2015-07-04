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

    public float tiltX;

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

        tiltImage = BitmapFactory.decodeResource(getResources(), R.drawable.tilt);

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
//        canvas.rotate(0,0,0,0);
        float rTilt;
        rTilt = -tiltX*(45f/10f);
//        rTilt = ((int)(rTilt/10))*10;
        canvas.rotate(rTilt,width/2,height/2);
        canvas.drawBitmap(tiltImage,null, new Rect(width/2-500,height/2-500,width/2+500,height/2+500),null);
        //super.onDraw(canvas);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"ontouch");
        return super.onTouchEvent(event);
    }
}
