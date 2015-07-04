package com.tiltcode.tiltcode.Activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.tiltcode.tiltcode.R;
import android.os.Handler;

/**
 * Created by JSpiner on 2015. 7. 1..
 */
public class LockScreenActivity extends Activity {

    //로그에 쓰일 tag
    public static final String TAG = LockScreenActivity.class.getSimpleName();

    SeekBar sb;
    int value;
    ValueAnimator anim;

    long stTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onAttachedToWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_lockscreen);

        stTime = System.currentTimeMillis();

        init();
    }

    void init(){

        sb = ((SeekBar)findViewById(R.id.myseek));


        ((SeekBar)findViewById(R.id.myseek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar.getProgress() > 95) {
                    seekBar.setProgress(seekBar.getMax());

                    Intent intent = new Intent(LockScreenActivity.this, CouponReceiveActivity.class);
                    startActivity(intent);
                    finish();;

                } else {

                    seekBar.setThumb(getResources().getDrawable(R.drawable.back));

                    value = sb.getProgress();
                    ValueAnimator anim = ValueAnimator.ofInt(value,
                            5);
                    anim.setDuration((long)(500*((float)value/sb.getMax())));
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(
                                ValueAnimator animation) {
                            value = (Integer) animation
                                    .getAnimatedValue();
                            if(value<0) value=0;
                            sb.setProgress(value);
                        }
                    });
                    anim.start();

                    /*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ValueAnimator anim = ValueAnimator.ofInt(0,
                                    sb.getMax());
                            anim.setDuration(1000);
                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(
                                        ValueAnimator animation) {
                                    value = (Integer) animation
                                            .getAnimatedValue();
                                    value = 100 - value;
                                    sb.setProgress(value);
                                }
                            });
                            anim.start();
                        }
                    }, 1200);*/
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stTime = System.currentTimeMillis();

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress > 95) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_setting));
                }

            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){
                    if(System.currentTimeMillis() - stTime >=1000*5){
                        Log.d(TAG,"close window");
                        mHandler.sendEmptyMessageDelayed(0,1);
                        break;
                    }
                }

            }
        }).start();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            finish();
            super.handleMessage(msg);
        }
    };

    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }
}
