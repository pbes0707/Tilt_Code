package com.tiltcode.tiltcodemanager.View;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

/**
 * Created by JSpiner on 2015. 6. 27..
 */
public class ActionActivity extends Activity {

    /*

    상단 액션바에 Back버튼 구현을 위해 Activity를 상속받은
    ActionActivity와 ActionFragmentActivity를 만들었습니다.
    액션바가 들어간 모든 액티비티는 두 클래스중 하나를 상속받아
    initActionBar();
    setEnableBack();
    해주고 작업하시면됩니다.
    추후 액션바에 다른기능 추가시 이 코드를 수정하여 하길 권장

     */

    boolean enableBack;

    ImageButton backButton;

    //actionbar init
    public void initActionBar(){
        backButton = (ImageButton)findViewById(R.id.btn_actionbar_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionActivity.this.onBackPressed();
            }
        });
    }

    //backbutton enable
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
