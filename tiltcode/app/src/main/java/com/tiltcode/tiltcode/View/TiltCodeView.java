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
    Bitmap radorImage;

    Paint p;

    public TiltCodeView(Context context) {
        super(context);

        Log.d(TAG,"context");

        init();
    }

    public TiltCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.d(TAG,"context attrs");


        init();
    }

    public TiltCodeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Log.d(TAG,"context attrs defStyle");


        init();
    }

    void init(){

        if(tiltImage==null) {

            BitmapFactory.Options o=new BitmapFactory.Options();
            o.inSampleSize = 1;
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inDither=false;                     //Disable Dithering mode
            o.inPurgeable=true;

            tiltImage = BitmapFactory.decodeResource(getResources(), R.drawable.tilt,o);
            tiltpImage = BitmapFactory.decodeResource(getResources(), R.drawable.tiltp,o);
            radorImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rador,o),
                    450, 450, true);
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
        rTilt = ((int)(rTilt/10))*10;

        canvas.rotate(rTilt,width/2,height/2);
        canvas.drawBitmap(radorImage, null, new Rect(width / 2 - 450, height / 2 - 450, width / 2 + 450, height / 2 + 450), null);

        canvas.rotate(-rTilt,width/2,height/2);
        if(Math.abs(rTilt)>=40 && Math.abs(rTilt)<=50){
            canvas.drawBitmap(tiltpImage,null, new Rect(width/2-150,height/2-270,width/2+150,height/2+270),null);
        }
        else {
            canvas.drawBitmap(tiltImage, null, new Rect(width / 2 - 150, height / 2 - 270, width / 2 + 150, height / 2 + 270), null);
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
