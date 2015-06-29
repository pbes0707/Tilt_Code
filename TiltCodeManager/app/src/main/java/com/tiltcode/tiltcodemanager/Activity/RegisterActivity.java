package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tiltcode.tiltcodemanager.Model.GCMRegister;
import com.tiltcode.tiltcodemanager.Model.LoginResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class RegisterActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

    }


    void init(){

        btnFile = ((ImageButton)findViewById(R.id.imv_register_file));
        btnImage =  ((ImageButton)findViewById(R.id.imv_register_image));

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, 1123);
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ((Button)findViewById(R.id.btn_register_proc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procRegisterGPS();
            }
        });

    }

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
                "tilt",                                     //TODO : 틸트값 intent 만들기
                fileType,
                imgType,
                new Callback<LoginResult>() {
                    @Override
                    public void success(LoginResult loginResult, Response response) {

                        Log.d(TAG,"register success / code : "+loginResult.code);
                        if (loginResult.code.equals("1")) { //성공
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if(resultCode!=RESULT_OK) return;


        switch(requestCode){

            /*
            1123 : 파일 선택
            1124 : 이미지선택
            1125 : gps 선택

             */
            case 1123:
                String filePath = data.getData().getPath();
                ((TextView)findViewById(R.id.tv_register_file_uri)).setText(filePath);
                ((TextView)findViewById(R.id.tv_register_file_uri)).setVisibility(View.VISIBLE);
                fileType = new TypedFile("multipart/form-data", new File(filePath));
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
                    btnImage.setImageBitmap(bitmap);

                    imgType = new TypedFile("multipart/form-data",new File(getRealPathFromURI(data.getData())));
                }
                catch(Exception e){
                    Log.e(TAG,"error : "+e.getMessage());
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

        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}
