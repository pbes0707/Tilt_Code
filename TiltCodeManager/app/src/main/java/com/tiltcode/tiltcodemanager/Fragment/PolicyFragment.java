package com.tiltcode.tiltcodemanager.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiltcode.tiltcodemanager.Activity.SignupActivity;
import com.tiltcode.tiltcodemanager.R;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class PolicyFragment extends Fragment {

    //로그에 쓰일 tag
    public static final String TAG = PolicyFragment.class.getSimpleName();

    int layoutid;
    Context context;

    public PolicyFragment() {
        super();
        this.layoutid = R.layout.fragment_policy;
        this.context = SignupActivity.context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;

        if (v == null) {
            v = inflater.inflate(layoutid, null);

            init();

        }
        return v;
    }

    void init() {


    }
}
