package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;
import com.tiltcode.tiltcode.View.ActionActivity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 25..
 */
public class ChangePasswdActivity extends ActionActivity {

    //로그에 쓰일 태그
    public static final String TAG = ChangePasswdActivity.class.getSimpleName();

    EditText edt_passwd;
    EditText edt_passwd_retype;
    EditText edt_passwd_current;

    TextView tv_same;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepasswd);

        super.initActionBar();
        setEnableBack(true);

        init();

    }

    void init(){

        edt_passwd = ((EditText)findViewById(R.id.edt_setting_account_passwd));
        edt_passwd_retype = ((EditText)findViewById(R.id.edt_setting_account_passwd_retype));
        edt_passwd_current = ((EditText)findViewById(R.id.edt_setting_account_passwd_current));

        tv_same = ((TextView)findViewById(R.id.tv_setting_account_pw_correct));

        edt_passwd_retype.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "before");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "chagne");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "after");

                tv_same.setVisibility(View.VISIBLE);

                if (edt_passwd.getText().toString().equals(edt_passwd_retype.getText().toString())) {
                    tv_same.setText(getResources().getText(R.string.message_same_passwd));
                    tv_same.setTextColor(Color.rgb(179, 255, 255));
                } else {
                    tv_same.setText(getResources().getText(R.string.message_diff_passwd));
                    tv_same.setTextColor(Color.rgb(255, 0, 0));
                }

            }
        });

        ((Button)findViewById(R.id.btn_setting_account_passwd_proc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(ChangePasswdActivity.this);
                dialog.setTitle("로드중");
                dialog.setMessage("데이터를 불러오는중입니다..");
                dialog.show();

                Util.getEndPoint().setPort("40001");
                Util.getHttpSerivce().changePasswd(Util.getAccessToken().getToken(),
                        Util.encrypt(edt_passwd_current.getText().toString()),
                        Util.encrypt(edt_passwd.getText().toString()),
                        new Callback<LoginResult>() {
                            @Override
                            public void success(LoginResult loginResult, Response response) {
                                Log.d(TAG, "change success / code : " + loginResult.code);
                                if (loginResult.code.equals("1")) { //성공
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_success_change_passwd), Toast.LENGTH_LONG).show();
                                    finish();
                                } else if (loginResult.code.equals("-1")) { //누락된게있음
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-2")) { //세션이 유효하지않음
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_session_invalid), Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-3")) { //현재 비밀번호가 다름
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_diff_current_passwd), Toast.LENGTH_LONG).show();
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

    }
}
