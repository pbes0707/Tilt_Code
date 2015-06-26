package com.tiltcode.tiltcode.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class LoginResult {

    @SerializedName("code")
    public String code;

    @SerializedName("message")
    public String message;

    @SerializedName("info")
    public Session info;

}
