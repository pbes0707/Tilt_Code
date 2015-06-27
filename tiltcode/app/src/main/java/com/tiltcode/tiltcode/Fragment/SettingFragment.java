package com.tiltcode.tiltcode.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tiltcode.tiltcode.Activity.MainActivity;
import com.tiltcode.tiltcode.Activity.NotificationActivity;
import com.tiltcode.tiltcode.Activity.SettingAccountActivity;
import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.Adapter.SettingListAdapter;
import com.tiltcode.tiltcode.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class SettingFragment extends Fragment {


    //로그에 쓰일 tag
    public static final String TAG = SettingFragment.class.getSimpleName();

    private String[] strArray = {"계정설정","공지사항","버전정보"};
    private Class [] clsArray = {SettingAccountActivity.class, NotificationActivity.class};

    int layoutid;
    Context context;

    ListView listView;
    SettingListAdapter listAdapter;

    View v = null;

    public SettingFragment() {
        super();
        this.layoutid = R.layout.fragment_setting;
        this.context = MainPagerAdapter.context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {
            v = inflater.inflate(layoutid, null);

            listView = (ListView)v.findViewById(R.id.lv_setting);

            init();

        }
        return v;
    }

    void init() {

        ArrayList<String> arr = new ArrayList<>();
        for(int i=0;i<strArray.length;i++){
            arr.add(strArray[i]);
        }

        listAdapter = new SettingListAdapter(context, arr);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, clsArray[i]);
                startActivity(intent);
            }
        });

    }

    /*
    void moveFragment(int position){

        FragmentTransaction trans = getFragmentManager().beginTransaction();
//                trans.hide(DetailSettingFragment.this);
        getFragmentManager().popBackStack();
        trans.replace(R.id.layout_setting_root, new DetailSettingModifyFragment());
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();
    }*/

}
