package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.tiltcode.tiltcode.R;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class NotificationActivity extends Activity {


    //로그에 쓰일 tag
    public static final String TAG = NotificationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notify);

        ((WebView)findViewById(R.id.wv_setting_notify)).loadUrl("http://hercu1es.tistory.com/category/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD");
    }
}
