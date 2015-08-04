package com.tiltcode.tiltcode.View;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;

/**
 * Created by JSpiner on 2015. 6. 27..
 */
public class ActionFragmentActivity extends FragmentActivity {

    boolean enableBack;

    ImageButton backButton;

    public void initActionBar(){
        backButton = (ImageButton)findViewById(R.id.btn_actionbar_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionFragmentActivity.this.onBackPressed();
            }
        });
    }

    public void setEnableBack(boolean enable){
        this.enableBack = enable;

        if(enable){
            backButton.setVisibility(View.VISIBLE);
        }
        else{
            backButton.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {

        Util.recursiveRecycle(getWindow().getDecorView());
        System.gc();

        super.onDestroy();

    }
}
