package com.tiltcode.tiltcode.Model;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public interface HttpService {


    /*

    모든 REST API들은 이곳에 기입됨.

     */

    @FormUrlEncoded
    @POST("/signUp")
    void signUp(@Field("id") String id,
                @Field("passwd") String pw,
                @Field("name") String name,
                @Field("birth") String birthday,
                @Field("sex") String sex,
                @Field("uuid") String uuid,
                @Field("model") String model,
                Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/login")
    void login(@Field("id") String id,
               @Field("passwd") String pw,
               Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/signFacebook")
    void signFacebook(@Field("id") String id,
                @Field("name") String name,
                @Field("birth") String birthday,
                @Field("sex") String sex,
                @Field("uuid") String uuid,
                @Field("model") String model,
                Callback<LoginResult> ret);

    @GET("/validateSession")
    void validateSession(@Query("session") String session,
                      Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/logOut")
    void logOut(@Field("session") String session,
                      Callback<LoginResult> ret);

    @GET("/couponGet")
    void couponGet(@Query("session") String session,
                         Callback<CouponResult> ret);

    @FormUrlEncoded
    @POST("/changeName")
    void changeName(@Field("session") String session,
                    @Field("name") String name,
                    Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/couponManageDelete")
    void couponManageDelete(@Field("session") String session,
                    @Field("id") String id,
                    Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/changePasswd")
    void changePasswd(@Field("session") String session,
                      @Field("currP") String currP,
                      @Field("changeP") String changeP,
                      Callback<LoginResult> ret);

    @GET("/backgroundCouponGetList")
    void backgroundCouponGetList(@Query("session") String session,
                                 @Query("lat") String lat,
                                 @Query("lng") String lng,
                                 @Query("tilt") String tilt,
                                 Callback<CouponResult> ret);

    @GET("/couponGetFile")
    retrofit.client.Response getFile(@Query("session") String session,
                 @Query("id") String id);

    @GET("/checkVersion")
    void checkVersion(@Query("session") String session,
                      Callback<VersionResult> ret);

    @FormUrlEncoded
    @POST("/couponAdd")
    void couponAdd(@Field("session") String session,
                      @Field("id") String id,
                      Callback<LoginResult> ret);

}
