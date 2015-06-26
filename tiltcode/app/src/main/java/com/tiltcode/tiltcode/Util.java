package com.tiltcode.tiltcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tiltcode.tiltcode.Model.HttpService;
import com.tiltcode.tiltcode.Model.LoginToken;

import org.apache.commons.codec.binary.Hex;

import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import java.security.*;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class Util {

    //로그에 쓰일 tag
    public static final String TAG = Util.class.getSimpleName();

    public static Context context;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static LoginToken accessToken;

    private static HttpService httpService;

    public static FooEndPoint endPoint;

    public static String getString(String key,String defValue){
        if(sharedPreferences!=null){
            return sharedPreferences.getString(key,defValue);
        }
        else{
            sharedPreferences = context.getSharedPreferences("tiltcode",Context.MODE_PRIVATE);
            return getString(key,defValue);
        }
    }

    public static void putString(String key, String value){
        if(editor!=null){
            editor.putString(key,value);
            editor.commit();
        }
        else{
            editor = sharedPreferences.edit();
            putString(key,value);
        }
    }

    public static boolean getBoolean(String key, boolean defValue){
        if(sharedPreferences!=null){
            return sharedPreferences.getBoolean(key,defValue);
        }
        else{
            sharedPreferences = context.getSharedPreferences("tiltcode",Context.MODE_PRIVATE);
            return getBoolean(key,defValue);
        }
    }

    public static void putBoolean(String key, boolean value){
        if(editor!=null){
            editor.putBoolean(key,value);
            editor.commit();
        }
        else{
            editor = sharedPreferences.edit();
            putBoolean(key,value);
        }
    }

    public static LoginToken getAccessToken(){
        if(accessToken==null){
            accessToken = new LoginToken();
        }
        return accessToken;
    }

    public static void destroyToken(){
        editor.clear().commit();
    }

    public static class FooEndPoint implements Endpoint {
        private static final String BASE = context.getResources().getString(R.string.API_SERVER);

        private String url = BASE;

        public void setPort(String port) {
            url = BASE +":"+ port;
        }

        @Override public String getName() {
            return "default";
        }

        @Override public String getUrl() {
            Log.d(TAG,"url : "+url);
            if (url == null) setPort("80");
            return url;
        }
    }

    public static FooEndPoint getEndPoint(){
        if(endPoint==null){
            endPoint = new FooEndPoint();


        }
        return endPoint;
    }

    public static HttpService getHttpSerivce() {

        if(httpService==null) {

            RestAdapter restAdapter =
                    new RestAdapter.Builder()
                            .setEndpoint(getEndPoint())
                            .build();
            httpService = restAdapter.create(HttpService.class);
        }
        return httpService;
    }



    public static String encrypt192(String str){
        try {

            PBEKeySpec keySpec = new PBEKeySpec("1234".toCharArray());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBE");
            Key secureKey = new SecretKeySpec("1234".getBytes(),"AES");
            Cipher ciper = Cipher.getInstance("AES");
            ciper.init(Cipher.ENCRYPT_MODE,secureKey);
            byte[] encryptedData = ciper.doFinal("asdf".getBytes());



            /*
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            SecretKey sk;
            kg.init(192);
            sk = kg.generateKey("1234");*/
//            Log.d(TAG, "AES 192 - "+Hex.encodeHex(sk.getEncoded()));

            return encryptedData.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
