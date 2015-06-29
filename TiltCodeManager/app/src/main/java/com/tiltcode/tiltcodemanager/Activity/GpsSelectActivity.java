package com.tiltcode.tiltcodemanager.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tiltcode.tiltcodemanager.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by JSpiner on 2015. 6. 29..
 */
public class GpsSelectActivity extends FragmentActivity {

    //로그에 쓰일 tag
    public static final String TAG = GpsSelectActivity.class.getSimpleName();

    //구글맵 뷰
    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsselect);

        init();
    }

    void init(){

        setUpMapIfNeeded();

        ((Button)findViewById(R.id.btn_gpsselect_proc)).setOnClickListener(new View.OnClickListener() {
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

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }

            Log.d(TAG,"dd");

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Log.d(TAG,"zoom : "+mMap.getCameraPosition().zoom);
                    Log.d(TAG,"lat : "+mMap.getCameraPosition().target.latitude+" lng : "+mMap.getCameraPosition().target.longitude);
                }
            });
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }

            Log.d(TAG,"dd");

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Log.d(TAG,"zoom : "+mMap.getCameraPosition().zoom);
                    Log.d(TAG,"lat : "+mMap.getCameraPosition().target.latitude+" lng : "+mMap.getCameraPosition().target.longitude);

                    if(mMap.getCameraPosition().zoom>10) {

                        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                        Address address;
                        String result = null;
                        List<Address> list = null;
                        try {
                            list = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
                            address = list.get(0);
                            result = address.getAddressLine(0) + ", " + address.getLocality();

                            ((TextView) findViewById(R.id.tv_gpsselect_locale)).setText(result);
                            Log.d(TAG, "location : " + result);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "error : " + e.getMessage());
                        }
                    }
                }
            });
        }
    }

    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

}
