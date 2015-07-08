package com.tiltcode.tiltcodemanager.Activity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.tiltcode.tiltcodemanager.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by JSpiner on 2015. 6. 29..
 */
public class GpsSelectActivity extends FragmentActivity {

    //로그에 쓰일 tag
    public static final String TAG = GpsSelectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsselect);

        Log.d(TAG,"onCreate");

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MapFragment fragment = new MapFragment();
        transaction.add(R.id.ll_fragment, fragment);
        transaction.commit();

        new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                ((FrameLayout)findViewById(R.id.ll_fragment)).performClick();
                ((FrameLayout)findViewById(R.id.ll_fragment)).callOnClick();
            }
        }.sendEmptyMessageDelayed(0,1000);

//        init();
//        moveMapToMyLocation();
    }

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        Log.d(TAG,"onCreateView");
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
//        getSupportFragmentManager().popBackStack();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
//        init();
        super.onResume();
    }


    class MapFragment extends Fragment{


        //구글맵 뷰
        private GoogleMap mMap; // Might be null if Google Play services APK is not available.

        View v;

        MapView mapView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.fragment_gpsselect, container,
                    false);

            GooglePlayServicesUtil.isGooglePlayServicesAvailable(GpsSelectActivity.this);

            mapView = ((MapView)v.findViewById(R.id.mapView));
            mapView.onCreate(savedInstanceState);

            mapView.onResume();// needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG,"map init error : "+e.getMessage());
            }

            mapView.invalidate();

            mMap = mapView.getMap();


            init();

            new android.os.Handler(){
                @Override
                public void handleMessage(Message msg) {
                    mapView.invalidate();
                    mapView.postInvalidate();

                    long downTime = SystemClock.uptimeMillis();
                    long eventTime = SystemClock.uptimeMillis() + 100;
                    float x = 0.0f;
                    float y = 0.0f;
// List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
                    int metaState = 0;
                    MotionEvent motionEvent = MotionEvent.obtain(
                            downTime,
                            eventTime,
                            MotionEvent.ACTION_DOWN,
                            x,
                            y,
                            metaState
                    );

                    mapView.dispatchTouchEvent(motionEvent);
                    super.handleMessage(msg);
                }
            }.sendEmptyMessageDelayed(0,1000);
            return v;
        }

        @Override
        public void onResume() {
            mapView.onResume();
            super.onResume();
        }

        LocationManager mLocationManager;;

        private Location getLastKnownLocation() {
            mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            return bestLocation;
        }

        private void moveMapToMyLocation() {


            LocationManager locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            Criteria crit = new Criteria();

            Location loc = getLastKnownLocation();//mMap.getMyLocation();//locMan.getLastKnownLocation(locMan.getBestProvider(crit, false));




            CameraPosition camPos;

            if(loc==null){

                camPos = new CameraPosition.Builder()

                        .target(new LatLng(37.444917, 127.138868))

                        .zoom(10f)

                        .build();
            }
            else {
                camPos = new CameraPosition.Builder()

                        .target(new LatLng(loc.getLatitude(), loc.getLongitude()))

                        .zoom(16f)

                        .build();
            }

            CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);

            mMap.moveCamera(camUpdate);

            mapView.invalidate();

        }



        void init(){

            setUpMapIfNeeded();

            ((Button)v.findViewById(R.id.btn_gpsselect_proc)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mMap.getCameraPosition().zoom>16){

                        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                        Address address;
                        String result = null;
                        List<Address> list = null;
                        try {
                            list = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
                            address = list.get(0);
                            result = address.getAddressLine(0) + ", " + address.getLocality();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "error : " + e.getMessage());
                        }

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("lat",mMap.getCameraPosition().target.latitude);
                        resultIntent.putExtra("lng",mMap.getCameraPosition().target.longitude);
                        resultIntent.putExtra("locale",result);

                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                    else{
                        Toast.makeText(getBaseContext(),getResources().getText(R.string.message_gps_zoom_low_level),Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG,"zoom : "+mMap.getCameraPosition().zoom);
                    Log.d(TAG,"lat : "+mMap.getCameraPosition().target.latitude+" lng : "+mMap.getCameraPosition().target.longitude);
                }
            });

            moveMapToMyLocation();
        }

        private void setUpMapIfNeeded() {
                mMap.clear();

                Log.d(TAG,"dd");

                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        Log.d(TAG,"zoom : "+mMap.getCameraPosition().zoom);
                        Log.d(TAG,"lat : "+mMap.getCameraPosition().target.latitude+" lng : "+mMap.getCameraPosition().target.longitude);
                    }
                });
                mMap.setMyLocationEnabled(true);

                Log.d(TAG,"dd");



                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {

                        Log.d(TAG,"zoom : "+mMap.getCameraPosition().zoom);
                        Log.d(TAG,"lat : "+mMap.getCameraPosition().target.latitude+" lng : "+mMap.getCameraPosition().target.longitude);

                        if(mMap.getCameraPosition().zoom>14) {

                            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                            Address address;
                            String result = null;
                            List<Address> list = null;
                            try {
                                list = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
                                address = list.get(0);
                                result = address.getAddressLine(0) + ", " + address.getLocality();

                                ((TextView) v.findViewById(R.id.tv_gpsselect_locale)).setText(result);
                                Log.d(TAG, "location : " + result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "error : " + e.getMessage());
                            }
                        }
                    }
                });

//            }
        }

        private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//            setUpMapIfNeeded();
        }

    }

}
