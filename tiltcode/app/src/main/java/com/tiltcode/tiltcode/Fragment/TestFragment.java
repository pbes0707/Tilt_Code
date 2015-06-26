package com.tiltcode.tiltcode.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tiltcode.tiltcode.Activity.NotificationActivity;
import com.tiltcode.tiltcode.Activity.SettingAccountActivity;
import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.Adapter.SettingListAdapter;
import com.tiltcode.tiltcode.R;

import java.util.ArrayList;


/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class TestFragment extends Fragment {


    //로그에 쓰일 tag
    public static final String TAG = TestFragment.class.getSimpleName();

    int layoutid;
    Context context;

    View v = null;

    public TestFragment() {
        super();
        this.layoutid = R.layout.fragment_test;
        this.context = MainPagerAdapter.context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {
            v = inflater.inflate(layoutid, container,false);


            init();

        }
        return v;
    }

    void init() {


    }


}
