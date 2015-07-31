package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
 * Created by JSpiner on 2015. 7. 31..
 */
public class LoginSelectActivity extends Activity {

    //로그에 쓰일 tag
    public static final String TAG = LoginSelectActivity.class.getSimpleName();

    CallbackManager callbackManager;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getBaseContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginselect);

        init();
    }

    void init(){

        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.btn_login_proc_fb);
        List<String> listPermission = Arrays.asList("public_profile", "email", "user_birthday");
        fbLoginButton.setReadPermissions(listPermission);

        callbackManager = CallbackManager.Factory.create();


        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "onSuccess");


                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code


                                Log.v(TAG, response.toString());

                                try {
                                    Log.d(TAG, "token : "+ AccessToken.getCurrentAccessToken());
                                    Log.d(TAG, "id : "+object.getString("id"));
                                    Log.d(TAG, "email : "+object.getString("email"));
                                    Log.d(TAG, "gender : "+object.getString("gender"));
                                    Log.d(TAG, "birthday : "+object.getString("birthday"));

                                    Util.getAccessToken()
                                            .setUserId(object.getString("id"))
                                            .setPhone(object.getString("email"))
                                            .setSex(object.getString("gender"))
                                            .setBirthday(object.getString("birthday"))
                                            .setIsSkipedUser(false)
                                            .setLoginType(LoginToken.LoginType.Facebook);


                                    TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                                    String uuid = tManager.getDeviceId();
                                    String model = Build.BRAND + " " + Build.DEVICE;

                                    procFbLogin(object.getString("id"),
                                            object.getString("name"),
                                            object.getString("birthday"),
                                            object.getString("gender"),
                                            uuid,
                                            model);


                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    Log.e(TAG,"login failure : "+e.getMessage());
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG,"onCancel");

                Toast.makeText(getBaseContext(),getResources().getText(R.string.message_network_error),Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG,"onError"+exception.getMessage());

                Toast.makeText(getBaseContext(),getResources().getText(R.string.message_network_error),Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }
        });

        ((Button)findViewById(R.id.btn_loginselect_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSelectActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ((Button)findViewById(R.id.btn_loginselect_nologin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(LoginSelectActivity.this);
                dialog.setTitle("로드중");
                dialog.setMessage("데이터를 불러오는중입니다..");
                dialog.show();


                TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String uuid = tManager.getDeviceId();

                String model = Build.BRAND + " " + Build.DEVICE;

                Util.getEndPoint().setPort("40001");
                Util.getHttpSerivce().signFacebook(uuid, "null", "null", "null", uuid, model,        //비회원 로그인시에는 uuid를 통해 페이스북 로그인인것처럼 로그인한다.
                        new Callback<com.tiltcode.tiltcode.Model.LoginResult>() {
                            @Override
                            public void success(com.tiltcode.tiltcode.Model.LoginResult loginResult, Response response) {
                                Log.d(TAG, "login success / code : " + loginResult.code);
                                if (loginResult.code.equals("1") || loginResult.code.equals("2")) { //성공
                                    Log.d(TAG, "token : " + loginResult.info.session);
                                    Intent intent = new Intent(LoginSelectActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                    Util.getAccessToken().setToken(loginResult.info.session)
                                            .setIsSkipedUser(true);
                                    Util.getAccessToken().saveToken();

                                } else if (loginResult.code.equals("-1")) { //누락된게있음
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                                }

                                dialog.dismiss();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e(TAG, "login failure : " + error.getMessage());
                                Toast.makeText(getBaseContext(), getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                                dialog.dismiss();
                            }
                        });
            }
        });

        if(Util.getAccessToken().loadToken()){

            dialog = new ProgressDialog(LoginSelectActivity.this);
            dialog.setTitle("로드중");
            dialog.setMessage("데이터를 불러오는중입니다..");
            dialog.show();

            Util.getEndPoint().setPort("40001");
            Util.getHttpSerivce().validateSession(Util.getAccessToken().getToken(),
                    new Callback<com.tiltcode.tiltcode.Model.LoginResult>() {
                        @Override
                        public void success(com.tiltcode.tiltcode.Model.LoginResult loginResult, Response response) {
                            dialog.dismiss();
                            Log.d(TAG,"access success / code : "+loginResult.code);
                            if (loginResult.code.equals("1")) { //성공
                                Log.d(TAG,"token : "+Util.getAccessToken().getToken());

                                Intent intent = new Intent(LoginSelectActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (loginResult.code.equals("-1")) { //누락된게있음
                                Toast.makeText(getBaseContext(),getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
                            } else if (loginResult.code.equals("-2")) { //세션이 유효하지않음
                                Toast.makeText(getBaseContext(),getResources().getText(R.string.message_session_invalid),Toast.LENGTH_LONG).show();
                            }

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

    void procFbLogin(String id, String name, String birth, String sex, String uuid, String model){

        dialog = new ProgressDialog(LoginSelectActivity.this);
        dialog.setTitle("로드중");
        dialog.setMessage("데이터를 불러오는중입니다..");
        dialog.show();

        Util.getEndPoint().setPort("40001");
        Util.getHttpSerivce().signFacebook(id, name, birth, sex, uuid, model,
                new Callback<com.tiltcode.tiltcode.Model.LoginResult>() {
                    @Override
                    public void success(com.tiltcode.tiltcode.Model.LoginResult loginResult, Response response) {
                        Log.d(TAG,"login success / code : "+loginResult.code);
                        if (loginResult.code.equals("1") || loginResult.code.equals("2")) { //성공
                            Log.d(TAG,"token : "+loginResult.info.session);
                            Intent intent = new Intent(LoginSelectActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            Util.getAccessToken().setToken(loginResult.info.session);
                            Util.getAccessToken().saveToken();

                        } else if (loginResult.code.equals("-1")) { //누락된게있음
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
