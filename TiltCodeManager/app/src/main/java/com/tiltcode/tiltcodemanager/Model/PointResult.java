package com.tiltcode.tiltcodemanager.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class PointResult {

    @SerializedName("code")
    public String code;

    @SerializedName("message")
    public String message;

    @SerializedName("session")
    public String session;

    @SerializedName("point")
    public String point;

}
