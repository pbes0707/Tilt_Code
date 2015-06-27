package com.tiltcode.tiltcodemanager.View;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.tiltcode.tiltcodemanager.R;


/**
 * Created by JSpiner on 2015. 6. 27..
 */
public class ActionActivity extends Activity {

    boolean enableBack;

    ImageButton backButton;

    public void initActionBar(){
        backButton = (ImageButton)findViewById(R.id.btn_actionbar_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionActivity.this.onBackPressed();
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
}
