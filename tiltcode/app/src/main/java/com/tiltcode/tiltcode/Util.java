package com.tiltcode.tiltcode;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.tiltcode.tiltcode.Model.HttpService;
import com.tiltcode.tiltcode.Model.LoginToken;


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

    //context
    public static Context context;

    //aes 암호 seed
    private static String seed = "ZCXpveXjFTRA83Yh73hgACFq";

    //설정값 및 각종 정보 저장용 sharedpreference
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    //static으로된 token <- 이 변수로 모든 유저정보를 관리
    public static LoginToken accessToken;

    //앱내에서 모든 http 통신은 httpservice로 동작
    private static HttpService httpService;

    //http통신시 endPoint를 이 변수로 변경, ex) endPoint.setPort("80");
    public static FooEndPoint endPoint;
    
    /*
    sharedpreference로 데이터 저장및 불러옴
     */

    public static String getString(String key,String defValue){
        return getSharedPreferences().getString(key, defValue);
    }

    public static void putString(String key, String value){
        getEditor().putString(key, value);
        getEditor().commit();
    }

    public static boolean getBoolean(String key, boolean defValue){
        return getSharedPreferences().getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value){
        getEditor().putBoolean(key, value);
        getEditor().commit();
    }

    public static int getInt(String key, int defValue){
        return getSharedPreferences().getInt(key, defValue);
    }

    public static void putInt(String key, int value){
        getEditor().putInt(key, value);
        getEditor().commit();
    }

    /*
    저장된 모든 데이터 삭제
     */
    public static void destroyToken(){
        getEditor().clear().commit();
    }

    //signleton sharedpreference
    private static SharedPreferences getSharedPreferences(){
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences("tiltcode",Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    //singleton editor
    private static SharedPreferences.Editor getEditor(){
        if(editor==null){
            editor = getSharedPreferences().edit();
        }
        return editor;
    }

    //singleton LoginToken
    //loginToken은 변경후 saveToken을 반드시 호출하여야한다.
    public static LoginToken getAccessToken(){
        if(accessToken==null){
            accessToken = new LoginToken();
        }
        return accessToken;
    }

    //HttpService의 EndPoint
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

    //Singleton Endpoint
    public static FooEndPoint getEndPoint(){
        if(endPoint==null){
            endPoint = new FooEndPoint();
        }
        return endPoint;
    }

    //Singleton HttpService
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


    /**
     * Encrypts the text.
     * @param clearText The text you want to encrypt
     * @return Encrypted data if successful, or null if unsucessful
     */
    public static String encrypt(String clearText) {
        byte[] encryptedText = null;
        try {
            byte[] keyData = seed.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, ks);
            encryptedText = c.doFinal(clearText.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedText, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decrypts the text
     * @param encryptedText The text you want to encrypt
     * @return Decrypted data if successful, or null if unsucessful
     */
    public static String decrypt (String encryptedText) {
        byte[] clearText = null;
        try {
            byte[] keyData = seed.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, ks);
            clearText = c.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            return new String(clearText, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    //getContentResolver -> contentResolver
    public static String getRealPathFromURI(ContentResolver contentResolver, Uri contentURI) {
        String result;
        Cursor cursor = contentResolver.query(contentURI, null, null, null, null);
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
