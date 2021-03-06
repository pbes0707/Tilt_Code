package com.tiltcode.tiltcodemanager.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.squareup.picasso.Callback;
import com.tiltcode.tiltcodemanager.Activity.CouponListActivity;
import com.tiltcode.tiltcodemanager.Activity.GpsSelectActivity;
import com.tiltcode.tiltcodemanager.Activity.MainActivity;
import com.tiltcode.tiltcodemanager.Activity.SignupActivity;
import com.tiltcode.tiltcodemanager.Activity.TiltSelectActivity;
import com.tiltcode.tiltcodemanager.Model.Coupon;
import com.tiltcode.tiltcodemanager.Model.GCMRegister;
import com.tiltcode.tiltcodemanager.Model.LoginResult;
import com.tiltcode.tiltcodemanager.Model.PointResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class CouponListEditFragment extends Fragment {


    //각도별 이미지 14개
    int[] resources = {R.drawable.tilt_1,R.drawable.tilt_2,R.drawable.tilt_3,R.drawable.tilt_4,R.drawable.tilt_5,R.drawable.tilt_6,R.drawable.tilt_7,R.drawable.tilt_8,R.drawable.tilt_9,R.drawable.tilt_10,R.drawable.tilt_11,R.drawable.tilt_12,R.drawable.tilt_13,R.drawable.tilt_14};


    //로그에 쓰일 tag
    public static final String TAG = CouponListEditFragment.class.getSimpleName();

    int layoutid;
    Context context;
    public static Coupon coupon;

    public static TextView fileUrl;

    public static TypedFile imgType;
    public static TypedFile fileType;

    Spinner couponType;
    Spinner couponPickType;
    EditText descText;
    EditText titleText;

    public static ImageView tiltImage;

    public static String tilt;

    Button registButton;
    public static ImageButton btnImage;
    ImageButton btnFile;
    ImageButton btnFileDown;

    public static TextView gpsText;

    TextView gpsLocale;

    ProgressDialog dialog;

    public static String lat;
    public static String lng;

    public static String beginT;
    public static String endT;

    int couponPickIndex;
    int couponTypeIndex;

    String tiltHour;
    String tiltMinute;

    String dateTime;

    public CouponListEditFragment() {
        super();
        this.layoutid = R.layout.fragment_edit;
        this.context = CouponListActivity.context;
        this.coupon = CouponListFragment.coupon;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;
        this.coupon = CouponListFragment.coupon;

        if (v == null) {
            v = inflater.inflate(layoutid, null);

            couponType = ((Spinner)v.findViewById(R.id.spinner_register_couponType));
            couponPickType = ((Spinner)v.findViewById(R.id.spinner_register_couponPickType));
            descText = ((EditText)v.findViewById(R.id.edt_register_desc));
            titleText = ((EditText)v.findViewById(R.id.edt_register_title));
            registButton = ((Button)v.findViewById(R.id.btn_register_proc));
            btnImage = ((ImageButton)v.findViewById(R.id.imv_register_image));
            btnFile = ((ImageButton)v.findViewById(R.id.imv_register_file));
            btnFileDown = ((ImageButton)v.findViewById(R.id.imv_register_file_down));
            fileUrl = ((TextView)v.findViewById(R.id.tv_register_file_uri));
            gpsText = ((TextView)v.findViewById(R.id.tv_register_gps_locale));

            ((Button)v.findViewById(R.id.btn_register_tilt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //tilt선택시 해당 activity로 result activity시작
                    Intent intent = new Intent(context, TiltSelectActivity.class);
                    getActivity().startActivityForResult(intent, 1126);
                }
            });

            tiltImage = ((ImageView)v.findViewById(R.id.img_coupon_detail_tilt));

            init();

        }
        return v;
    }

    //timedialog 선택시 이벤트
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
            beginT = format1.format(calendar.getTime());
            calendar.add(Calendar.MINUTE,30);
            endT = format1.format(calendar.getTime());

            gpsText.setText(dateTime + "  " + tiltHour + ":" + tiltMinute + "~" + endT);
            gpsText.setVisibility(View.VISIBLE);

        }
    };

    //date선택시 이벤트
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            dateTime = String.format("%04d",datePicker.getYear())+"-"+
                    String.format("%02d",(datePicker.getMonth()+1))+"-"+
                    String.format("%02d",datePicker.getDayOfMonth());
            new TimePickerDialog(context, timeSetListener,0,0,false).show();
        }

    };


    private String getMimeType(String url)
    {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
    public String setFilePath(String title){
        return title.replace(' ','_');
    }

    byte[] streamToBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (stream != null) {
            byte[] buf = new byte[1024];
            int r;
            while ((r = stream.read(buf)) != -1) {
                baos.write(buf, 0, r);
            }
        }
        return baos.toByteArray();
    }

    void init() {

        /*
        기존 쿠폰 정보를 불러옴
         */
        switch (coupon.type){
            case "link":
                couponType.setSelection(1);
                break;
            case "file":
                couponType.setSelection(2);
                break;
            case "image":
                couponType.setSelection(3);
                break;
        }


        if(coupon.beginT==null){
            //gps
            couponPickType.setSelection(1);

            //gps 좌표를 기준으로 주소값을 가져옴
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            Address address;
            String result = null;
            List<Address> list = null;
            try {
                Log.d(TAG, "lat : " + coupon.lat+" lng : "+coupon.lng);
                list = geocoder.getFromLocation(Double.valueOf(coupon.lat),Double.valueOf(coupon.lng), 1);
                address = list.get(0);
                result = address.getAddressLine(0) + ", " + address.getLocality();

                gpsText.setText(result);
                Log.d(TAG, "location : " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "error : " + e.getMessage());
            }
            gpsText.setVisibility(View.VISIBLE);
        }
        else{
            //time
            couponPickType.setSelection(2);
            gpsText.setText(coupon.beginT+"~"+coupon.endT);
            gpsText.setVisibility(View.VISIBLE);
        }

        tiltImage.setImageResource(resources[Integer.valueOf(coupon.tilt)-1]);
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/" + setFilePath(coupon.title) + "." + coupon.fileEx);

        if(pdfFile.exists()){
            fileUrl.setText(pdfFile.getPath());
            fileUrl.setVisibility(View.VISIBLE);
        }

        btnFileDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/" + setFilePath(coupon.title) + "." + coupon.fileEx);
                if(pdfFile.exists()){

                    try {
/*                                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                                myIntent.setData(Uri.fromFile(pdfFile));
                                Intent j = Intent.createChooser(myIntent, "이 파일을 열 어플리케이션을 선택해주세요.");
                                context.startActivity(j);
                                /**/
                                /*
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(pdfFile));
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                context.startActivity(intent);*/

                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(pdfFile), getMimeType(pdfFile.getAbsolutePath()));
                        context.startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(context, "파일을 열수 없습니다. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else {

                    dialog = new ProgressDialog(context);
                    dialog.setTitle("로드중");
                    dialog.setMessage("데이터를 불러오는중입니다..");
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Util.getEndPoint().setPort("40002");
                            retrofit.client.Response response = Util.getHttpSerivce().getFile(Util.getAccessToken().getToken(), coupon.id + "." + coupon.fileEx);
//                                        byte[] bytes = FileHelper.getBytesFromStream(response.getBody().in());
                            try {

                                InputStream stream = (response.getBody().in());

                                byte[] fileBytes = streamToBytes(stream);

                                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/" + setFilePath(coupon.title) + "." + coupon.fileEx);
                                File filePath = new File(Environment.getExternalStorageDirectory() + "/Download/");
                                filePath.mkdir();
                                Log.d(TAG, "file : " + pdfFile.getAbsolutePath() + " name : " + pdfFile.getName() + " size : " + fileBytes.length);

                                FileOutputStream output = null;
                                output = new FileOutputStream(pdfFile);
                                output.write(fileBytes);
                                output.flush();
                                output.close();
//                                            org.apache.commons.io.IOUtils.write(fileBytes, output);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "file error : " + e.getMessage());
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, context.getResources().getText(R.string.message_download_coupon_fail), Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(context, context.getResources().getText(R.string.message_download_coupon_success), Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        //type 선택 리스너
        couponPickType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                couponPickIndex = i;

                switch (i) {
                    case 1:
                        //gps선택시 해당액티비티 실행
                        Intent intent = new Intent(context, GpsSelectActivity.class);
                        getActivity().startActivityForResult(intent, 1125);

                        break;
                    case 2:
                        //시간 선택시 해당 액티비티 실행
                        Calendar calendar = Calendar.getInstance();
                        new DatePickerDialog(context, dateSetListener,calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                int i = couponPickType.getSelectedItemPosition();

                couponPickIndex = i;

                switch (i) {
                    case 1:
                        Intent intent = new Intent(context, GpsSelectActivity.class);
                        getActivity().startActivityForResult(intent, 1125);

                        break;
                    case 2:
                        Calendar calendar = Calendar.getInstance();
                        new DatePickerDialog(context, dateSetListener,calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                        break;
                }
            }
        });
        couponType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                couponTypeIndex = i;

                switch (i) {
                    case 1:

                        break;
                    case 2:

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                int i = couponType.getSelectedItemPosition();

                couponTypeIndex = i;

                switch (i) {
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }
        });

        descText.setText(coupon.desc);
        titleText.setText(coupon.title);

        tilt = coupon.tilt;

        lat = coupon.lat;
        lng = coupon.lng;

        beginT = coupon.beginT;
        endT = coupon.endT;

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //파일선택 다이얼로그
                Intent i = new Intent(context, FilePickerActivity.class);
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

                getActivity().startActivityForResult(i, 1123);
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
                //이미지선택
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, 1124);
            }
        });


        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(context);
                dialog.setTitle("로드중");
                dialog.setMessage("데이터를 불러오는중입니다..");
                dialog.show();

                //선택한 쿠폰등록타입에 맞게 요청을 실행
                if(couponPickType.getSelectedItemPosition()==1){
                    updateGpsCoupon();
                }
                else{
                    updateTimeCoupon();
                }

            }
        });
    }

    void updateGpsCoupon(){
        //gps타입의 쿠폰 수정
        Util.getEndPoint().setPort("40002");
        Util.getHttpSerivce().couponManageModifyGPS(Util.getAccessToken().getToken(),
                coupon.id,
                titleText.getText().toString(),
                descText.getText().toString(),
                lat,
                lng,
                tilt,
                new retrofit.Callback<LoginResult>() {
                    @Override
                    public void success(LoginResult loginResult, Response response) {
                        Log.d(TAG,"access success / code : "+loginResult.code);
                        if (loginResult.code.equals("1")) { //성공
                            Toast.makeText(context,getResources().getText(R.string.message_success_register),Toast.LENGTH_LONG).show();
                            ((CouponListActivity)context).setPage(1);
                        } else if (loginResult.code.equals("-1")) { //누락된게있음
                            Toast.makeText(context, getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-2")) { //세션이 유효하지않음
                            Toast.makeText(context,getResources().getText(R.string.message_session_invalid),Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG,"login failure : "+error.getMessage());
                        Toast.makeText(context,getResources().getText(R.string.message_network_error),Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });
    }

    void updateTimeCoupon(){
        //시간형태의 쿠폰 수정
        Log.d(TAG,"beginT : "+beginT+" endT : "+endT);
        Log.d(TAG,"session : "+Util.getAccessToken().getToken()+" id : "+coupon.id);
        Log.d(TAG,"tilt : "+tilt);
        Util.getEndPoint().setPort("40002");
        Util.getHttpSerivce().couponManageModifyTime(Util.getAccessToken().getToken(),
                coupon.id,
                titleText.getText().toString(),
                descText.getText().toString(),
                beginT,
                endT,
                dateTime,
                dateTime,
                tilt,
                new retrofit.Callback<LoginResult>() {
                    @Override
                    public void success(LoginResult loginResult, Response response) {
                        Log.d(TAG, "access success / code : " + loginResult.code);
                        if (loginResult.code.equals("1")) { //성공
                            Toast.makeText(context, getResources().getText(R.string.message_success_register), Toast.LENGTH_LONG).show();
                            ((CouponListActivity)context).setPage(1);
                        } else if (loginResult.code.equals("-1")) { //누락된게있음
                            Toast.makeText(context, getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-2")) { //세션이 유효하지않음
                            Toast.makeText(context, getResources().getText(R.string.message_session_invalid), Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "login failure : " + error.getMessage());
                        Toast.makeText(context, getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });
    }
}
