package com.tiltcode.tiltcodemanager.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tiltcode.tiltcodemanager.Exception.DataTypeException;
import com.tiltcode.tiltcodemanager.Exception.DisMatchException;
import com.tiltcode.tiltcodemanager.Exception.NoDataException;
import com.tiltcode.tiltcodemanager.Fragment.PolicyFragment;
import com.tiltcode.tiltcodemanager.Fragment.SignupFragment;
import com.tiltcode.tiltcodemanager.Model.LoginResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 17..
 * Contact : jspiner@naver.com
 */
public class SignupActivity extends FragmentActivity {

    //로그에 쓰일 tag
    public static final String TAG = SignupActivity.class.getSimpleName();

    Fragment fragment1; //policy fragment
    Fragment fragment2; //signup fragment

    int nowPage;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /*
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent,1);*/
        init();
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String filePath = data.getData().getPath();
                    Log.d("asdf","file path : "+filePath);
        //                    textFile.setText(FilePath);
                    upload(filePath);
                }
                break;

        }
    }
*/

    /*
    void upload(String url){

        TypedFile typedFile = new TypedFile("multipart/form-data", new File(url));
        String description = "hello, this is description speaking";

        Util.endPoint.setPort("40001");
        Util.getHttpSerivce().fileSend("asdf", typedFile, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.e("Upload", "success");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Upload", "error" + error.getMessage());
            }
        });
    }
*/
    void init(){

        context = getBaseContext();

        fragment1 = new PolicyFragment();
        fragment2 = new SignupFragment();

        setPage(1);

        ((LinearLayout)findViewById(R.id.btn_signup_prev)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nowPage==1){
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                }
                else if(nowPage==2){
                    setPage(1);
                }
            }
        });

        ((LinearLayout)findViewById(R.id.btn_signup_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(nowPage==1){
                    setPage(2);
                }
                else if(nowPage==2){

                    TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

                    try {
                        String id = ((SignupFragment)fragment2).getUserId();
                        String pw = ((SignupFragment)fragment2).getUserPw();
                        String name = ((SignupFragment)fragment2).getName();
                        String sex = ((SignupFragment)fragment2).getSex();
                        String birthday = ((SignupFragment)fragment2).getBirthday();
                        String phone = ((SignupFragment)fragment2).getPhone();
                        String company = ((SignupFragment)fragment2).getCompany();

                        procSignup(id,pw,name,birthday,sex,phone,company);
                    } catch (DisMatchException e) {
                        e.printStackTrace();
                    } catch (DataTypeException e) {
                        e.printStackTrace();
                    } catch (NoDataException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    boolean procSignup(final String id, final String pw,String name, String birthday, String sex, String phone, String company){

        Util.getEndPoint().setPort("40001");
        Util.getHttpSerivce().signUpManager(id, pw, name, birthday, sex, phone, company
                , new Callback<LoginResult>() {

            @Override
            public void success(LoginResult loginResult, Response response) {
                Log.d(TAG, "procSignup success / code : " + loginResult.code);

                if (loginResult.code.equals("1")) { //성공
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    Util.getAccessToken().setToken(loginResult.info.session);


                } else if (loginResult.code.equals("-1")) { //비번 길이 짧음
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_passwd_short), Toast.LENGTH_LONG).show();
                } else if (loginResult.code.equals("-2")) { //중복id
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_repeated_id), Toast.LENGTH_LONG).show();
                } else if (loginResult.code.equals("-3")) { //누락된게있음
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "procSignup error : " + error.getMessage());

            }
        });

        return false;
    }


    void setPage(int page){
        nowPage = page;

        Fragment fr = null;

        if(nowPage==1){
            fr = fragment1;
        }
        else if(nowPage==2){
            fr = fragment2;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.layout_signup, fr);
        fragmentTransaction.commit();
    }
}
