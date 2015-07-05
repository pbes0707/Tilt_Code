package com.tiltcode.tiltcodemanager.Activity;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.telephony.TelephonyManager;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.Toast;

        import com.tiltcode.tiltcodemanager.Exception.DataTypeException;
        import com.tiltcode.tiltcodemanager.Exception.DisMatchException;
        import com.tiltcode.tiltcodemanager.Exception.NoDataException;
        import com.tiltcode.tiltcodemanager.Fragment.CouponListDetailFragment;
        import com.tiltcode.tiltcodemanager.Fragment.CouponListFragment;
        import com.tiltcode.tiltcodemanager.Fragment.PolicyFragment;
        import com.tiltcode.tiltcodemanager.Fragment.SignupFragment;
        import com.tiltcode.tiltcodemanager.Model.LoginResult;
        import com.tiltcode.tiltcodemanager.R;
        import com.tiltcode.tiltcodemanager.Util;
        import com.tiltcode.tiltcodemanager.View.ActionActivity;
        import com.tiltcode.tiltcodemanager.View.ActionFragmentActivity;

        import retrofit.Callback;
        import retrofit.RetrofitError;
        import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class CouponListActivity extends ActionFragmentActivity{


    //로그에 쓰일 tag
    public static final String TAG = CouponListActivity.class.getSimpleName();

    Fragment fragment1; //couponlist fragment
    Fragment fragment2; //couponlistdetail fragment

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

        setPage(1);

    }

    @Override
    public void onBackPressed() {
        if (nowPage == 2) {
            setPage(1);
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

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.layout_couponlist_fragment, fr);
        fragmentTransaction.commit();
    }

}
