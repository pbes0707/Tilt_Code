package com.tiltcode.tiltcodemanager.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tiltcode.tiltcodemanager.Activity.SignupActivity;
import com.tiltcode.tiltcodemanager.Exception.DataTypeException;
import com.tiltcode.tiltcodemanager.Exception.DisMatchException;
import com.tiltcode.tiltcodemanager.Exception.NoDataException;
import com.tiltcode.tiltcodemanager.R;

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
    EditText edt_sex;
    EditText edt_birth;
    EditText edt_phone;
    EditText edt_company;

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
            edt_sex = ((EditText)v.findViewById(R.id.edt_signup_sex));
            edt_birth = ((EditText)v.findViewById(R.id.edt_signup_birthday));
            edt_phone = ((EditText)v.findViewById(R.id.edt_signup_phone));
            edt_company = ((EditText)v.findViewById(R.id.edt_signup_company));

            init();

        }
        return v;
    }

    void init() {


    }

    public String getUserId() throws NoDataException {
        if(edt_id.getText().toString().length()<1){
            throw new NoDataException("user isn't exist");
        }
        return edt_id.getText().toString();
    }

    public String getUserPw() throws DisMatchException, DataTypeException {
        if(!edt_pw.getText().toString().equals(edt_pw_confirm.getText().toString())){
            throw new DisMatchException("password not match");
        }
        else if(edt_pw.getText().toString().length()<8){
            throw new DataTypeException("password too short");
        }
        return edt_pw.getText().toString();
    }

    public String getName() throws NoDataException {
        if(edt_name.getText().toString().length()<1){
            throw new NoDataException("name isn't exist");
        }
        return edt_name.getText().toString();
    }

    public String getSex() throws NoDataException {
        if(edt_sex.getText().toString().length()<1){
            throw new NoDataException("sex isn't exist");
        }
        return edt_sex.getText().toString();
    }

    public String getBirthday() throws NoDataException {
        if(edt_birth.getText().toString().length()<1){
            throw new NoDataException("birthday isn't exist");
        }
        return edt_birth.getText().toString();
    }

    public String getPhone() throws NoDataException {
        if(edt_phone.getText().toString().length()<1){
            throw new NoDataException("phone isn't exist");
        }
        return edt_phone.getText().toString();
    }

    public String getCompany() throws NoDataException {
        if(edt_company.getText().toString().length()<1){
            throw new NoDataException("company isn't exist");
        }
        return edt_company.getText().toString();
    }
}
