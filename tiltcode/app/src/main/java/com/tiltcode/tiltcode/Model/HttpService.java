package com.tiltcode.tiltcode.Model;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public interface HttpService {

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




    @Multipart
    @POST("/down")
    void fileSend(@Part("str") String str,
                  @Part("file")TypedFile file,
                  Callback<String> ret);

}
