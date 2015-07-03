package com.tiltcode.tiltcode.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tiltcode.tiltcode.Activity.MainActivity;
import com.tiltcode.tiltcode.Activity.NotificationActivity;
import com.tiltcode.tiltcode.Activity.SettingAccountActivity;
import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.Adapter.SettingListAdapter;
import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.Model.VersionResult;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;
import com.tiltcode.tiltcode.View.BackFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class SettingFragment extends BackFragment {


    //로그에 쓰일 tag
    public static final String TAG = SettingFragment.class.getSimpleName();

    private String[] strArray = {"계정설정","공지사항","버전정보"};
    private Class [] clsArray = {SettingAccountActivity.class, NotificationActivity.class,null};

    int layoutid;
    Context context;

    ListView listView;
    SettingListAdapter listAdapter;

    View v = null;

    ProgressDialog dialog;

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
                if(i==2){
                    checkUpdate();
                    return;
                }
                Intent intent = new Intent(context, clsArray[i]);
                startActivity(intent);
            }
        });

    }

    void checkUpdate(){
        dialog = new ProgressDialog(context);
        dialog.setTitle("로드중");
        dialog.setMessage("데이터를 불러오는중입니다..");
        dialog.show();

        Util.getEndPoint().setPort("40004");
        Util.getHttpSerivce().checkVersion("",
                new Callback<VersionResult>() {
                    @Override
                    public void success(VersionResult versionResult, Response response) {
                        Log.d(TAG,"versioncheck success / code : "+versionResult.code);
                        if (versionResult.code.equals("1")) { //성공

                            float nowVer = Float.valueOf(getResources().getString(R.string.version));
                            float newVer = Float.valueOf(versionResult.version);

                            if(nowVer<newVer){
                                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                                ab.setTitle("새로운버전");
                                ab.setMessage("새로운 버전이 출시되었습니다. 마켓으로 이동하시겠습니까?");
                                ab.setCancelable(false);

                                ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                                        marketLaunch.setData(Uri.parse("market://search?q=com.tiltcode.tiltcode"));
                                        startActivity(marketLaunch);

                                        arg0.dismiss();
                                    }
                                });

                                ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                    }
                                });
                                ab.create().show();
                            }
                            else{
                                Toast.makeText(context,getResources().getString(R.string.message_already_new_version),Toast.LENGTH_LONG).show();
                            }

                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "versioncheck failure : " + error.getMessage());
                        Toast.makeText(context, getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                }
        );
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
