package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.tiltcode.tiltcodemanager.BillingUtil.IabHelper;
import com.tiltcode.tiltcodemanager.BillingUtil.IabResult;
import com.tiltcode.tiltcodemanager.BillingUtil.Purchase;
import com.tiltcode.tiltcodemanager.Model.LoginResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;
import com.tiltcode.tiltcodemanager.View.ActionActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Secret on 2015. 6. 25..
 */
public class PurchaseActivity extends ActionActivity implements OnClickListener {

    //로그에 쓰일 tag
    public static final String TAG = PurchaseActivity.class.getSimpleName();

    IInAppBillingService mService;
    IabHelper mHelper;

    int selectedIndex = 0;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_purchase);

        initActionBar();
        setEnableBack(true);

        helperInit();

        Button btn_purchase = (Button) findViewById(R.id.btn_purchase);
        btn_purchase.setOnClickListener(this);

        Log.d(TAG,"point : "+Util.getAccessToken().getPoint());


        ((TextView)findViewById(R.id.tv_purchase_summoney)).setText(Util.getAccessToken().getPoint()+"P");
        ((TextView)findViewById(R.id.tv_purchase_nowmoney)).setText(Util.getAccessToken().getPoint()+"P");

        ((Spinner)findViewById(R.id.spinner_purchase_money)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void helperInit()
    {
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        // 구글에서 발급받은 바이너리키를 입력해줍니다
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyJ3Iv7GcGHex8ZfaNJzH4eu6tdUKUmeFcuF5NRJ8cfCM1eITYnPLmQBb1Qgs0UXzGVrkVHU0QEoOBF/GYr/iq7aGkHxUw6NGeNQFOWUAOY0ExXKSkZzy+/+vYEGheb2vu382h0Zp7CGfy2F0nUdZRik4x0s3EA8kunLiDD/uinyRdLbO4xFf/hYfupBty0YhTbfmV8k2hBIw+iD7Edx9vB8snKswMoUlCf0ThJnSjpl2/IIUKGrn07uRaK1NEjOigFa7J7jxMLDryP5rTqpWooz/w45m+wzIeXDaQkRaIhuY7fzJP0GGVX37lsxxmrVXHMaGMTF5yrU3BRowS0ALKwIDAQAB\n";

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // 구매오류처리 ( 토스트하나 띄우고 결제팝업 종료시키면 되겠습니다 )
                }

                // 구매목록을 초기화하는 메서드입니다.
                // v3으로 넘어오면서 구매기록이 모두 남게 되는데 재구매 가능한 상품( 게임에서는 코인같은아이템은 ) 구매후 삭제해주어야 합니다.
                // 이 메서드는 상품 구매전 혹은 후에 반드시 호출해야합니다. ( 재구매가 불가능한 1회성 아이템의경우 호출하면 안됩니다 )
                //AlreadyPurchaseItems();
            }
        });
    }
    public void AlreadyPurchaseItems()
    {
        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(),
                    "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> purchaseDataList = ownedItems
                        .getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                String[] tokens = new String[purchaseDataList.size()];

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = (String) purchaseDataList.get(i);
                    JSONObject jo = new JSONObject(purchaseData);
                    tokens[i] = jo.getString("purchaseToken");
                    // 여기서 tokens를 모두 컨슘 해주기
                    mService.consumePurchase(3, getPackageName(), tokens[i]);
                }
            }

            // 토큰을 모두 컨슘했으니 구매 메서드 처리
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Buy(String id_item) {
        // Var.ind_item = index;
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    id_item, "inapp", "서버에서 발급한 영수증 키");
            PendingIntent pendingIntent = buyIntentBundle
                    .getParcelable("BUY_INTENT");
            //mHelper.launchPurchaseFlow(this, id_item, 1001, mPurchaseFinishedListener);

            if (pendingIntent != null) {
                startIntentSenderForResult(pendingIntent.getIntentSender(),
                        1001, new Intent(), Integer.valueOf(0),
                        Integer.valueOf(0), Integer.valueOf(0));

            } else {
                Toast.makeText(this, "결제가 막혔습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // 여기서 아이템 추가 해주시면 됩니다.
            // 만약 서버로 영수증 체크후에 아이템 추가한다면, 서버로 purchase.getOriginalJson() ,
            // purchase.getSignature() 2개 보내시면 됩니다.
            if(result.isSuccess())
            {
                AlreadyPurchaseItems();

                Util.getEndPoint().setPort("40001");
                Util.getHttpSerivce().pointCharge(Util.getAccessToken().getToken(),
                        String.valueOf(selectedIndex),
                        new Callback<LoginResult>() {
                            @Override
                            public void success(LoginResult loginResult, Response response) {
                                if (loginResult.code.equals("1")) {
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_success_purchase), Toast.LENGTH_LONG).show();
                                    finish();
                                } else if (loginResult.code.equals("-1")) {
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-3")) {
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_session_invalid), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                Log.e(TAG, "login failure : " + error.getMessage());
                                Toast.makeText(getBaseContext(), getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                            }
                        });

            }

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data))
            {
                super.onActivityResult(requestCode, resultCode, data);

                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                AlreadyPurchaseItems();
            }
            else
            {
                Log.d("PurchaseActivity", "onActivityResult handled by IABUtil.");
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_purchase:
                Buy("product_"+selectedIndex);
                break;

        }

    }
    public void onDestroy(){
        super.onDestroy();
        if(mServiceConn != null){
            unbindService(mServiceConn);
        }
    }


}
