package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.tiltcode.tiltcodemanager.Model.GCMRegister;
import com.tiltcode.tiltcodemanager.Model.LoginResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;
import com.tiltcode.tiltcodemanager.View.ActionActivity;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class RegisterActivity extends ActionActivity {

    //로그에 쓰일 tag
    public static final String TAG = RegisterActivity.class.getSimpleName();

    ImageButton btnFile;
    ImageButton btnImage;

    TypedFile imgType;
    TypedFile fileType;

    double gpsLat;
    double gpsLng;
    String gpsLocale;

    ProgressDialog dialog;

    int couponTypeIndex;
    int couponPickIndex;

    int coutponType=0; //0:null 1:gps 2:time

    String tiltHour;
    String tiltMinute;
    String tiltValue;

    String dateTime; //yyyy-mm-dd

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initActionBar();
        setEnableBack(true);

        init();

    }


    void init(){

        btnFile = ((ImageButton)findViewById(R.id.imv_register_file));
        btnImage =  ((ImageButton)findViewById(R.id.imv_register_image));

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                // Configure initial directory by specifying a String.
                // You could specify a String like "/storage/emulated/0/", but that can
                // dangerous. Always use Android's API calls to get paths to the SD-card or
                // internal memory.
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                startActivityForResult(i, 1123);
                /*
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1123);*/
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1124);
            }
        });

        ((Spinner)findViewById(R.id.spinner_register_couponType)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                couponTypeIndex = i;

                switch (i){
                    case 1:

                        break;
                    case 2:

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                int i = ((Spinner)findViewById(R.id.spinner_register_couponType)).getSelectedItemPosition();

                couponTypeIndex = i;

                switch (i){
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }
        });

        ((Spinner)findViewById(R.id.spinner_register_couponPickType)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                couponPickIndex = i;

                switch (i){
                    case 1:
                        Intent intent = new Intent(RegisterActivity.this, GpsSelectActivity.class);
                        startActivityForResult(intent, 1125);

                        break;
                    case 2:
                        Calendar calendar = Calendar.getInstance();

                        new DatePickerDialog(RegisterActivity.this, dateSetListener,calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                int i = ((Spinner)findViewById(R.id.spinner_register_couponType)).getSelectedItemPosition();

                couponPickIndex = i;

                switch (i){
                    case 1:
                        Intent intent = new Intent(RegisterActivity.this, GpsSelectActivity.class);
                        startActivityForResult(intent, 1125);

                        break;
                    case 2:
                        Calendar calendar = Calendar.getInstance();
                        new DatePickerDialog(RegisterActivity.this, dateSetListener,calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                        break;
                }
            }
        });

        ((Button)findViewById(R.id.btn_register_proc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(couponPickIndex==1) {
                    procRegisterGPS();
                }
                else{
                    procRegisterTime();
                }
            }
        });
        ((Button)findViewById(R.id.btn_register_tilt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, TiltSelectActivity.class);
                startActivityForResult(intent,1126);
            }
        });

    }

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
//            String msg = String.format("%d / %d / %d", year, hourOfDay, minute);
            tiltHour = String.valueOf(hourOfDay);
            tiltMinute = String.valueOf(minute);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(tiltHour));
            calendar.set(Calendar.MINUTE,Integer.valueOf(tiltMinute));

            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
            String beginT = format1.format(calendar.getTime());
            calendar.add(Calendar.MINUTE,30);
            String endT = format1.format(calendar.getTime());

            ((TextView)findViewById(R.id.tv_register_gps_locale)).setText(dateTime+"  "+tiltHour+":"+tiltMinute+"~"+beginT+":"+endT);
            ((TextView)findViewById(R.id.tv_register_gps_locale)).setVisibility(View.VISIBLE);

        }
    };

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            dateTime = String.format("%04d",datePicker.getYear())+"-"+
                    String.format("%02d",(datePicker.getMonth()+1))+"-"+
                            String.format("%02d",datePicker.getDayOfMonth());
            new TimePickerDialog(RegisterActivity.this, timeSetListener,0,0,false).show();
        }

    };

    void procRegisterGPS(){

        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setTitle("로드중");
        dialog.setMessage("데이터를 불러오는중입니다..");
        dialog.show();

        Util.getEndPoint().setPort("40002");
        Util.getHttpSerivce().couponRegisterGPS(Util.getAccessToken().getToken(),
                getResources().getStringArray(R.array.couponTypeKey)[couponTypeIndex],
                ((EditText)findViewById(R.id.edt_register_title)).getText().toString(),
                ((EditText)findViewById(R.id.edt_register_desc)).getText().toString(),
                "link",                                     //TODO : 링크
                String.valueOf(gpsLat),
                String.valueOf(gpsLng),
                tiltValue,
                fileType,
                imgType,
                new Callback<LoginResult>() {
                    @Override
                    public void success(LoginResult loginResult, Response response) {

                        Log.d(TAG,"register success / code : "+loginResult.code);
                        if (loginResult.code.equals("1")) { //성공
                            Toast.makeText(getBaseContext(),getResources().getString(R.string.message_success_register),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, CouponListActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (loginResult.code.equals("-1")) { //누락된게있음
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-2")) { //링크가 빠짐
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_register_no_link),Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-3")) { //파일이 없음
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_register_no_file),Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-4")) { //썸네일 이미지가 없음
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_register_no_image),Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-5")) { //Tilt가 올바르지 않음
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_register_error_tilt),Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-6")) { //세션이 유효하지 않음
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_session_invalid),Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-7")) { //정의되지 않은 type
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_register_error_type),Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-8")) { //포인트가 부족함
                            Toast.makeText(getBaseContext(),getResources().getText(R.string.message_register_low_point),Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.e(TAG,"register failure : "+error.getMessage());
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });

    }

    void procRegisterTime(){

        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setTitle("로드중");
        dialog.setMessage("데이터를 불러오는중입니다..");
        dialog.show();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(tiltHour));
        calendar.set(Calendar.MINUTE,Integer.valueOf(tiltMinute));

        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
        String beginT = format1.format(calendar.getTime());
        calendar.add(Calendar.MINUTE,30);
        String endT = format1.format(calendar.getTime());

//        ((TextView)findViewById(R.id.tv_register_gps_locale)).setText(beginT+"~"+endT);
//        ((TextView)findViewById(R.id.tv_register_gps_locale)).setVisibility(View.VISIBLE);

        Util.getEndPoint().setPort("40002");
        Util.getHttpSerivce().couponRegisterTime(Util.getAccessToken().getToken(),
                getResources().getStringArray(R.array.couponTypeKey)[couponTypeIndex],
                ((EditText) findViewById(R.id.edt_register_title)).getText().toString(),
                ((EditText) findViewById(R.id.edt_register_desc)).getText().toString(),
                "link",                                     //TODO : 링크
                beginT,
                endT,
                dateTime,
                dateTime,
                tiltValue,
                fileType,
                imgType,
                new Callback<LoginResult>() {
                    @Override
                    public void success(LoginResult loginResult, Response response) {

                        Log.d(TAG, "register success / code : " + loginResult.code);
                        if (loginResult.code.equals("1")) { //성공
                        } else if (loginResult.code.equals("-1")) { //누락된게있음
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-2")) { //링크가 빠짐
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_register_no_link), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-3")) { //파일이 없음
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_register_no_file), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-4")) { //썸네일 이미지가 없음
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_register_no_image), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-5")) { //Tilt가 올바르지 않음
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_register_error_tilt), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-6")) { //세션이 유효하지 않음
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_session_invalid), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-7")) { //정의되지 않은 type
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_register_error_type), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-8")) { //포인트가 부족함
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.message_register_low_point), Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.e(TAG, "register failure : " + error.getMessage());
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if(resultCode!=RESULT_OK) return;


        switch(requestCode){

            /*
            1123 : 파일 선택
            1124 : 이미지선택
            1125 : gps 선택
            1126 : tilt 선택
             */
            case 1123:
                if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                    // For JellyBean and above
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ClipData clip = data.getClipData();

                        if (clip != null) {
                            for (int i = 0; i < clip.getItemCount(); i++) {
                                Uri uri = clip.getItemAt(i).getUri();
                                // Do something with the URI
                                String filePath = uri.getPath();
                                Log.d(TAG,"filepath : "+filePath);

                                ((TextView) findViewById(R.id.tv_register_file_uri)).setText(filePath);
                                ((TextView)findViewById(R.id.tv_register_file_uri)).setVisibility(View.VISIBLE);
                                fileType = new TypedFile("multipart/form-data", new File(filePath));
                                return;
                            }
                        }
                        // For Ice Cream Sandwich
                    } else {
                        ArrayList<String> paths = data.getStringArrayListExtra
                                (FilePickerActivity.EXTRA_PATHS);

                        if (paths != null) {
                            for (String path: paths) {
                                Uri uri = Uri.parse(path);
                                // Do something with the URI
                                String filePath = uri.getPath();
                                Log.d(TAG,"filepath : "+filePath);

                                ((TextView) findViewById(R.id.tv_register_file_uri)).setText(filePath);
                                ((TextView)findViewById(R.id.tv_register_file_uri)).setVisibility(View.VISIBLE);
                                fileType = new TypedFile("multipart/form-data", new File(filePath));
                                return;
                            }
                        }
                    }

                } else {
                    Uri uri = data.getData();
                    // Do something with the URI

                    String filePath = uri.getPath();
                    Log.d(TAG,"filepath : "+filePath);

                    ((TextView) findViewById(R.id.tv_register_file_uri)).setText(filePath);
                    ((TextView)findViewById(R.id.tv_register_file_uri)).setVisibility(View.VISIBLE);
                    fileType = new TypedFile("multipart/form-data", new File(filePath));
                }

                /*
                String filePath = data.getData().getPath();
                Log.d(TAG,"filepath : "+filePath);

                ((TextView) findViewById(R.id.tv_register_file_uri)).setText(filePath);
                ((TextView)findViewById(R.id.tv_register_file_uri)).setVisibility(View.VISIBLE);
                fileType = new TypedFile("multipart/form-data", new File(filePath));*/
                break;
            case 1124:
                try {
                    Bitmap bitmap;

                    InputStream stream = getContentResolver().openInputStream(
                            data.getData());

                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inSampleSize = 4;

                    bitmap = BitmapFactory.decodeStream(stream,null,option);

                    stream.close();
                    imgType = new TypedFile("multipart/form-data",new File(Util.getRealPathFromURI(getContentResolver(), data.getData())));
                    btnImage.setImageBitmap(bitmap);

                }
                catch(Exception e){
                    Log.e(TAG,"file load error : "+e.getMessage());
                }
                break;
            case 1125:

                gpsLat = data.getDoubleExtra("lat",0f);
                gpsLng = data.getDoubleExtra("lng",0f);
                gpsLocale = data.getStringExtra("locale");

                Log.d(TAG,"lat : "+gpsLat+" lng : "+gpsLng + "locale : "+gpsLocale);

                ((TextView)findViewById(R.id.tv_register_gps_locale)).setText(gpsLocale);
                ((TextView)findViewById(R.id.tv_register_gps_locale)).setVisibility(View.VISIBLE);

                break;
            case 1126:

                tiltValue = String.valueOf(data.getIntExtra("tiltValue",1));
                Log.d(TAG, "tiltValue : "+tiltValue);

                break;

        }
    }

}
