package com.tiltcode.tiltcode.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tiltcode.tiltcode.Activity.MainActivity;
import com.tiltcode.tiltcode.Activity.SignupActivity;
import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.Exception.DataTypeException;
import com.tiltcode.tiltcode.Exception.DisMatchException;
import com.tiltcode.tiltcode.Exception.NoDataException;
import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.Model.LoginToken;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class SignupFragment  extends Fragment {

    //로그에 쓰일 tag
    public static final String TAG = SignupFragment.class.getSimpleName();

    int layoutid;
    Context context;

    EditText edt_id;
    EditText edt_pw;
    EditText edt_pw_confirm;
    EditText edt_name;
    //EditText edt_sex;
    EditText edt_birth;
    RadioGroup radio_sex;

    Button signup_proc;

    int type_sex; // 0 : null 1 : man 2 : women

    ProgressDialog dialog;


    public SignupFragment() {
        super();
        this.layoutid = R.layout.fragment_signup;
        this.context = SignupActivity.context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;

        if (v == null) {
            v = inflater.inflate(layoutid, null);

            edt_id = ((EditText)v.findViewById(R.id.edt_signup_id));
            edt_pw = ((EditText)v.findViewById(R.id.edt_signup_pw));
            edt_pw_confirm = ((EditText)v.findViewById(R.id.edt_signup_pw_confirm));
            edt_name = ((EditText)v.findViewById(R.id.edt_signup_name));
            //edt_sex = ((EditText)v.findViewById(R.id.edt_signup_sex));
            edt_birth = ((EditText)v.findViewById(R.id.edt_signup_birthday));
            radio_sex = ((RadioGroup)v.findViewById(R.id.radiogroup_signup_sex));
            signup_proc = ((Button)v.findViewById(R.id.btn_signup_proc));

            init();

        }
        return v;
    }

    void init() {
        radio_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                type_sex = i+1;
            }
        });
        signup_proc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!checkForm()) return;

                dialog = new ProgressDialog(context);
                dialog.setTitle("로드중");
                dialog.setMessage("데이터를 불러오는중입니다..");
                dialog.show();

                TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                String uuid = tManager.getDeviceId();
                String model = Build.BRAND + " " + Build.DEVICE;

                Util.getEndPoint().setPort("40001");
                try {
                    Util.getHttpSerivce().signUp(Util.encrypt(getUserId()),
                            Util.encrypt(getUserPw()),
                            getName(),
                            getBirthday(),
                            getSex(),
                            uuid,
                            model,
                            new Callback<LoginResult>() {
                                @Override
                                public void success(LoginResult loginResult, Response response) {

                                    Log.d(TAG, "access success / code : " + loginResult.code);
                                    if (loginResult.code.equals("1")) { //성공

                                        try{
                                            Util.getAccessToken()
                                                    .setToken(loginResult.info.session)
                                                    .setUserId(getUserId())
                                                    .setPhone("")
                                                    .setSex(getSex())
                                                    .setBirthday(getBirthday())
                                                    .setIsSkipedUser(false)
                                                    .setLoginType(LoginToken.LoginType.TiltCode);
                                        }catch (Exception e){

                                        }

                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                        ((Activity)context).finish();
                                    } else if (loginResult.code.equals("-1")) { //누락된게있음
                                        Toast.makeText(context, getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                                    } else if (loginResult.code.equals("-2")) { //중복 id
                                        Toast.makeText(context,getResources().getText(R.string.message_repeated_id),Toast.LENGTH_LONG).show();
                                    } else if (loginResult.code.equals("-3")) { //pw 너무 짧음
                                        Toast.makeText(context,getResources().getText(R.string.message_passwd_short),Toast.LENGTH_LONG).show();
                                    }

                                    dialog.dismiss();
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    Log.e(TAG, "signup failure : " + error.getMessage());
                                    Toast.makeText(context,getResources().getText(R.string.message_network_error),Toast.LENGTH_LONG).show();

                                    dialog.dismiss();
                                }
                            });
                }catch (Exception e){
                    //여기는 로직상 올수가 없음
                }

            }
        });

    }

    public boolean checkForm(){

        try {
            getUserId();
            getUserPw();
            getName();
            getSex();
            getBirthday();
        } catch (NoDataException e) {
            e.printStackTrace();
            return false;
        } catch (DataTypeException e) {
            e.printStackTrace();
            return false;
        } catch (DisMatchException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public String getUserId() throws NoDataException {
        if(edt_id.getText().toString().length()<1){
            Toast.makeText(context,getResources().getText(R.string.message_no_id),Toast.LENGTH_LONG).show();
            throw new NoDataException("user isn't exist");
        }
        return edt_id.getText().toString();
    }

    public String getUserPw() throws DisMatchException, DataTypeException {
        if(!edt_pw.getText().toString().equals(edt_pw_confirm.getText().toString())){
            Toast.makeText(context,getResources().getText(R.string.message_diff_passwd),Toast.LENGTH_LONG).show();
            throw new DisMatchException("password not match");
        }
        else if(edt_pw.getText().toString().length()<8){
            Toast.makeText(context,getResources().getText(R.string.message_passwd_short),Toast.LENGTH_LONG).show();
            throw new DataTypeException("password too short");
        }
        return edt_pw.getText().toString();
    }

    public String getName() throws NoDataException {
        if(edt_name.getText().toString().length()<1){
            Toast.makeText(context,getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
            throw new NoDataException("name isn't exist");
        }
        return edt_name.getText().toString();
    }

    public String getSex() throws NoDataException {
        if(type_sex==0){
            Toast.makeText(context,getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
            throw new NoDataException("sex isn't exist");
        }
        return type_sex==1?"m":"w";

    }

    public String getBirthday() throws NoDataException {
        if(edt_birth.getText().toString().length()<1){
            Toast.makeText(context,getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
            throw new NoDataException("birthday isn't exist");
        }
        return edt_birth.getText().toString();
    }
}
