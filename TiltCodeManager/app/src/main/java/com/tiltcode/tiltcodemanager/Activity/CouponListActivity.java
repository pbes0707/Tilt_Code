package com.tiltcode.tiltcodemanager.Activity;

        import android.app.Activity;
        import android.content.ClipData;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.telephony.TelephonyManager;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.nononsenseapps.filepicker.FilePickerActivity;
        import com.tiltcode.tiltcodemanager.Adapter.CouponListAdapter;
        import com.tiltcode.tiltcodemanager.Exception.DataTypeException;
        import com.tiltcode.tiltcodemanager.Exception.DisMatchException;
        import com.tiltcode.tiltcodemanager.Exception.NoDataException;
        import com.tiltcode.tiltcodemanager.Fragment.CouponListDetailFragment;
        import com.tiltcode.tiltcodemanager.Fragment.CouponListEditFragment;
        import com.tiltcode.tiltcodemanager.Fragment.CouponListFragment;
        import com.tiltcode.tiltcodemanager.Fragment.PolicyFragment;
        import com.tiltcode.tiltcodemanager.Fragment.SignupFragment;
        import com.tiltcode.tiltcodemanager.Model.LoginResult;
        import com.tiltcode.tiltcodemanager.R;
        import com.tiltcode.tiltcodemanager.Util;
        import com.tiltcode.tiltcodemanager.View.ActionActivity;
        import com.tiltcode.tiltcodemanager.View.ActionFragmentActivity;

        import java.io.File;
        import java.io.InputStream;
        import java.util.ArrayList;

        import retrofit.Callback;
        import retrofit.RetrofitError;
        import retrofit.client.Response;
        import retrofit.mime.TypedFile;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class CouponListActivity extends ActionFragmentActivity{


    //로그에 쓰일 tag
    public static final String TAG = CouponListActivity.class.getSimpleName();

    Fragment fragment1; //couponlist fragment
    Fragment fragment2; //couponlistdetail fragment
    Fragment fragment3; //couponlistedit fragment

    int nowPage;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_couponlist);

        initActionBar();
        setEnableBack(true);

        init();
    }

    void init(){

        context = CouponListActivity.this;

        fragment1 = new CouponListFragment();
        fragment2 = new CouponListDetailFragment();
        fragment3 = new CouponListEditFragment();

        setPage(1);

    }

    @Override
    public void onBackPressed() {
        if (nowPage == 2) {
            setPage(1);
        } else if(nowPage==3){
            setPage(1);
        } else if(nowPage==1 && (CouponListFragment.mUnfoldableView.isUnfolded() || CouponListFragment.mUnfoldableView.isUnfolding())) {
            CouponListFragment.mUnfoldableView.foldBack();
        } else {
            super.onBackPressed();
        }
    }

    public void setPage(int page){
        nowPage = page;

        Fragment fr = null;

        if(nowPage==1){
            fr = fragment1;
        }
        else if(nowPage==2){
            fr = fragment2;
        }
        if(nowPage==3){
            fr = fragment3;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.layout_couponlist_fragment, fr);
        fragmentTransaction.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if(resultCode!=RESULT_OK) return;

        Log.d(TAG,"couponlistactivity onactivityresult code : "+requestCode);
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

                                CouponListEditFragment.fileUrl.setText(filePath);
                                CouponListEditFragment.fileUrl.setVisibility(View.VISIBLE);
                                CouponListEditFragment.fileType = new TypedFile("multipart/form-data", new File(filePath));
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

                                CouponListEditFragment.fileUrl.setText(filePath);
                                CouponListEditFragment.fileUrl.setVisibility(View.VISIBLE);
                                CouponListEditFragment.fileType = new TypedFile("multipart/form-data", new File(filePath));
                                return;
                            }
                        }
                    }

                } else {
                    Uri uri = data.getData();
                    // Do something with the URI

                    String filePath = uri.getPath();
                    Log.d(TAG,"filepath : "+filePath);

                    CouponListEditFragment.fileUrl.setText(filePath);
                    CouponListEditFragment.fileUrl.setVisibility(View.VISIBLE);
                    CouponListEditFragment.fileType = new TypedFile("multipart/form-data", new File(filePath));
                }
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
                    CouponListEditFragment.imgType = new TypedFile("multipart/form-data",new File(Util.getRealPathFromURI(getContentResolver(), data.getData())));
                    CouponListEditFragment.btnImage.setImageBitmap(bitmap);

                }
                catch(Exception e){
                    Log.e(TAG,"file load error : "+e.getMessage());
                }
                break;
            case 1125:

                CouponListEditFragment.lat = String.valueOf(data.getDoubleExtra("lat",0f));
                CouponListEditFragment.lng = String.valueOf(data.getDoubleExtra("lng",0f));
                String gpsLocale = data.getStringExtra("locale");

//                Log.d(TAG,"lat : "+gpsLat+" lng : "+gpsLng + "locale : "+gpsLocale);

                CouponListEditFragment.gpsText.setText(gpsLocale);
                CouponListEditFragment.gpsText.setVisibility(View.VISIBLE);

                break;
            case 1126:

                CouponListEditFragment.tilt = String.valueOf(data.getIntExtra("tiltValue",1));
//                Log.d(TAG, "tiltValue : "+tiltValue);

                break;

        }
    }

}
