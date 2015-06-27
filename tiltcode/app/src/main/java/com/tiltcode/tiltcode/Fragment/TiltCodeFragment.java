package com.tiltcode.tiltcode.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.R;


/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class TiltCodeFragment extends Fragment {

    //로그에 쓰일 tag
    public static final String TAG = TiltCodeFragment.class.getSimpleName();

    int layoutid;
    Context context;

    public TiltCodeFragment() {
        super();
        this.layoutid = R.layout.fragment_tiltcode;
        this.context = MainPagerAdapter.context;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;

        if (v == null) {
//            v = inflater.inflate(layoutid, null);
            v = inflater.inflate(layoutid, container, false);
            init();
        }
        return v;
    }

    void init() {


    }


}
