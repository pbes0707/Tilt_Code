package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tiltcode.tiltcodemanager.Model.AnalyticResult;
import com.tiltcode.tiltcodemanager.Model.LoginResult;
import com.tiltcode.tiltcodemanager.Model.PointResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class MainActivity extends Activity {

    public static Context context;

    //로그에 쓰일 tag
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    void init(){

        this.context = MainActivity.this;

        ((TextView) findViewById(R.id.tv_main_name)).setText(Util.getAccessToken().getName() + "님");
        ((TextView)findViewById(R.id.tv_main_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog().show();
            }
        });
        ((TextView)findViewById(R.id.tv_main_point)).setText("현재 잔금 : " + Util.getAccessToken().getPoint() + "P");

        Util.getEndPoint().setPort("40001");
        Util.getHttpSerivce().pointCheck(Util.getAccessToken().getToken(),
                new Callback<PointResult>() {
                    @Override
                    public void success(PointResult pointResult, Response response) {

                        ((TextView)findViewById(R.id.tv_main_point)).setText("현재 잔금 : "+pointResult.point+"P");
                        Util.getAccessToken().setPoint(Integer.parseInt(pointResult.point))
                                .saveToken();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG,"get point check failure");
                    }
                });

        ((ImageButton)findViewById(R.id.btn_main_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        ((ImageButton)findViewById(R.id.btn_main_list)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CouponListActivity.class);
                startActivity(intent);
            }
        });
        ((ImageButton)findViewById(R.id.btn_main_purchase)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PurchaseActivity.class);
                startActivity(intent);
            }
        });
        ((ImageButton)findViewById(R.id.btn_main_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("로그아웃");
        ab.setMessage("로그아웃하시겠습니까?");
        ab.setCancelable(false);

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                setDismiss(mDialog);
                arg0.dismiss();
                procLogout();
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                setDismiss(mDialog);
                arg0.dismiss();
            }
        });

        return ab.create();
    }

    void procLogout(){

        Util.getEndPoint().setPort("40001");
        Util.getHttpSerivce().logOut(Util.getAccessToken().getToken(),
                new Callback<LoginResult>() {
                    @Override
                    public void success(LoginResult loginResult, Response response) {
                        //Log.d(TAG,"logout success");

                        if (loginResult.code.equals("-1")) {
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-2")) {
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_session_invalid), Toast.LENGTH_LONG).show();
                        } else {

                            Util.getAccessToken().destroyToken();

                            Intent mStartActivity = new Intent(getBaseContext(), SplashActivity.class);
                            int mPendingIntentId = 1111;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(getBaseContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "Logout failure : " + error.getMessage());
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
