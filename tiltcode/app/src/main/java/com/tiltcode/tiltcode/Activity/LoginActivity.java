package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.tv.TvInputService;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tiltcode.tiltcode.Model.LoginToken;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 17..
 * Contact : jspiner@naver.com
 */
public class LoginActivity extends Activity {

    //로그에 쓰일 tag
    public static final String TAG = LoginActivity.class.getSimpleName();

    EditText edt_login_id;
    EditText edt_login_pw;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getBaseContext());
//        FacebookSdk.setIsDebugEnabled(true);
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

//        AccessToken token = AccessToken.getCurrentAccessToken();

        init();


    }

    void init(){


        ((Button)findViewById(R.id.btn_login_proc)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(edt_login_id.getText().toString().length()<1){
                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_no_id),Toast.LENGTH_LONG).show();
                    return;
                }
                else if(edt_login_pw.getText().toString().length()<8){
                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_passwd_short),Toast.LENGTH_LONG).show();
                    return;
                }

                procLogin(edt_login_id.getText().toString(),edt_login_pw.getText().toString());

            }
        });

        ((TextView)findViewById(R.id.tv_login_signup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        edt_login_id = (EditText)findViewById(R.id.edt_login_id);
        edt_login_pw = (EditText)findViewById(R.id.edt_login_pw);



    }

    void procLogin(String id, String pw){

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setTitle("로드중");
        dialog.setMessage("데이터를 불러오는중입니다..");
        dialog.show();

        Util.getEndPoint().setPort("40001");
        Util.getHttpSerivce().login(Util.encrypt(id),
                Util.encrypt(pw)
                , new Callback<com.tiltcode.tiltcode.Model.LoginResult>() {
            @Override
            public void success(com.tiltcode.tiltcode.Model.LoginResult loginResult, Response response) {
                Log.d(TAG,"login success / code : "+loginResult.code);
                if (loginResult.code.equals("1")) { //성공
                    Log.d(TAG,"token : "+loginResult.info.session);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    Util.getAccessToken()
                            .setName(loginResult.info.name)
                            .setToken(loginResult.info.session)
                            .setUserId(loginResult.info.id)
                            .setPhone(loginResult.info.phone)
                            .setSex(loginResult.info.sex)
                            .setBirthday(loginResult.info.birth)
                            .setIsSkipedUser(false)
                            .setLoginType(LoginToken.LoginType.TiltCode);
                    Util.getAccessToken().saveToken();

                } else if (loginResult.code.equals("-1")) { //누락된게있음
                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
                } else if (loginResult.code.equals("-2")) { //아이디비번일치하지않음
                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_not_match_account),Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG,"login failure : "+error.getMessage());
                Toast.makeText(getBaseContext(),getResources().getText(R.string.message_network_error),Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }
        });
    }

}
