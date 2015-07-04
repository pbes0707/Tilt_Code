package com.tiltcode.tiltcode.Activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.tiltcode.tiltcode.R;
import android.os.Handler;

/**
 * Created by JSpiner on 2015. 7. 1..
 */
public class LockScreenActivity extends Activity {

    SeekBar sb;
    int value;
    ValueAnimator anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onAttachedToWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_lockscreen);

        init();
    }

    void init(){

        sb = ((SeekBar)findViewById(R.id.myseek));


        ((SeekBar)findViewById(R.id.myseek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar.getProgress() > 95) {
                    seekBar.setProgress(seekBar.getMax());

                } else {

                    seekBar.setThumb(getResources().getDrawable(R.drawable.back));

                    value = sb.getProgress();
                    ValueAnimator anim = ValueAnimator.ofInt(value,
                            0);
                    anim.setDuration((long)(500*((float)value/sb.getMax())));
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(
                                ValueAnimator animation) {
                            value = (Integer) animation
                                    .getAnimatedValue();
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


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress > 95) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_setting));
                }

            }
        });

    }

    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }
}