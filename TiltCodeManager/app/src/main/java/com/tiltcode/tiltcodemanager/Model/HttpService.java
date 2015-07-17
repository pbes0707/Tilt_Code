package com.tiltcode.tiltcodemanager.Model;

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

    /*

    모든 REST API들은 이곳에 기입됨.

     */

    @FormUrlEncoded
    @POST("/signUpManager")
    void signUpManager(@Field("id") String id,
                @Field("passwd") String pw,
                @Field("name") String name,
                @Field("birth") String birthday,
                @Field("sex") String sex,
                @Field("phone") String phone,
                @Field("company") String company,
                Callback<LoginResult> ret);


    @GET("/validateSession")
    void validateSession(@Query("session") String session,
                         Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/login")
    void login(@Field("id") String id,
               @Field("passwd") String pw,
               Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/validSession")
    void validSession(@Field("session") String session,
                      Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/logOut")
    void logOut(@Field("session") String session,
                Callback<LoginResult> ret);

    @GET("/couponGet")
    void couponGet(@Query("session") String session,
                   Callback<CouponResult> ret);

    @FormUrlEncoded
    @POST("/register")
    void register(@Field("regId") String regId,
                  @Field("id") String id,
                  Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/pointCharge")
    void pointCharge(@Field("session") String session,
                  @Field("point") String point,
                  Callback<LoginResult> ret);

    @Multipart
    @POST("/couponRegisterGPS")
    void couponRegisterGPS(@Part("session") String session,
                           @Part("type") String type,
                           @Part("title") String title,
                           @Part("desc") String desc,
                           @Part("link") String link,
                           @Part("lat") String lat,
                           @Part("lng") String lng,
                           @Part("tilt") String tilt,
                           @Part("file") TypedFile file,
                           @Part("image") TypedFile image,
                           Callback<LoginResult> ret);

    @Multipart
    @POST("/couponRegisterTime")
    void couponRegisterTime(@Part("session") String session,
                           @Part("type") String type,
                           @Part("title") String title,
                           @Part("desc") String desc,
                           @Part("link") String link,
                           @Part("beginT") String beginT,
                           @Part("endT") String endT,
                           @Part("beginD") String beginD,
                           @Part("endD") String endD,
                           @Part("tilt") String tilt,
                           @Part("file") TypedFile file,
                           @Part("image") TypedFile image,
                           Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/changePasswd")
    void changePasswd(@Field("session") String session,
                      @Field("currP") String currP,
                      @Field("changeP") String changeP,
                      Callback<LoginResult> ret);

    @GET("/getCouponAnalytics")
    void getCouponAnalytics(@Query("session") String session,
                            @Query("id") String id,
                   Callback<AnalyticResult> ret);

    @GET("/pointCheck")
    void pointCheck(@Query("session") String session,
                            Callback<PointResult> ret);

    @FormUrlEncoded
    @POST("/couponManageModifyGPS")
    void couponManageModifyGPS(@Field("session") String session,
                               @Field("id") String id,
                               @Field("title") String title,
                               @Field("desc") String desc,
                               @Field("lat") String lat,
                               @Field("lng") String lng,
                               @Field("tilt") String tilt,
                    Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/couponManageModifyTime")
    void couponManageModifyTime(@Field("session") String session,
                                @Field("id") String id,
                                @Field("title") String title,
                                @Field("desc") String desc,
                                @Field("beginT") String beginT,
                                @Field("endT") String endT,
                                @Field("beginD") String beginD,
                                @Field("endD") String endD,
                                @Field("tilt") String tilt,
                    Callback<LoginResult> ret);

    @FormUrlEncoded
    @POST("/couponImageModify")
    void couponImageModify(@Part("session") String session,
                  @Part("id") String id,
                  @Part("image") TypedFile image,
                  Callback<LoginResult> ret);

    @Multipart
    @POST("/couponFileModify")
    void couponFileModify(@Part("session") String session,
                  @Part("id") String id,
                  @Part("image") TypedFile image,
                  Callback<LoginResult> ret);

    @Multipart
    @POST("/down")
    void fileSend(@Part("str") String str,
                  @Part("file") TypedFile file,
                  Callback<String> ret);

}
