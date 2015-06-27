package com.tiltcode.tiltcodemanager.Model;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public interface HttpService {

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






    @Multipart
    @POST("/down")
    void fileSend(@Part("str") String str,
                  @Part("file") TypedFile file,
                  Callback<String> ret);

}
