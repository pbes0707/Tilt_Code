package com.tiltcode.tiltcode.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    Bitmap tiltpImage;

    Paint p;

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

        if(tiltImage==null) {
            tiltImage = BitmapFactory.decodeResource(getResources(), R.drawable.tilt);
            tiltpImage = BitmapFactory.decodeResource(getResources(), R.drawable.tiltp);
        }

        p = new Paint();
        p.setColor(Color.CYAN);
        p.setTextSize(140);
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
//        Log.d(TAG,"ondraw");
//        canvas.rotate(0,0,0,0);
        float rTilt;
        rTilt = tiltX*(90f/10f);
        canvas.drawText(((int)tiltX*(90/10))+"°", width/2-100,400,p);
        rTilt = ((int)(rTilt/6))*6;
        canvas.rotate(rTilt,width/2,height/2);
        if(Math.abs(rTilt)>=40 && Math.abs(rTilt)<=50){
            canvas.drawBitmap(tiltpImage,null, new Rect(width/2-250,height/2-450,width/2+250,height/2+450),null);
        }
        else {
            canvas.drawBitmap(tiltImage, null, new Rect(width / 2 - 250, height / 2 - 450, width / 2 + 250, height / 2 + 450), null);
        }
        //super.onDraw(canvas);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"ontouch");
        return super.onTouchEvent(event);
    }
}
