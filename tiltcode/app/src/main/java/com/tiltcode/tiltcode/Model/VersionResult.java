package com.tiltcode.tiltcode.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class VersionResult {

    @SerializedName("code")
    public String code;

    @SerializedName("message")
    public String message;

    @SerializedName("version")
    public String version;

}
