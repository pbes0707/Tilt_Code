package com.tiltcode.tiltcodemanager.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class Coupon {

    @SerializedName("_id")
    public String _id;

    @SerializedName("id")
    public String id;

    @SerializedName("category")
    public String category;

    @SerializedName("type")
    public String type;

    @SerializedName("title")
    public String title;

    @SerializedName("desc")
    public String desc;

    @SerializedName("create")
    public String create;

    @SerializedName("link")
    public String link;

    @SerializedName("lat")
    public String lat;

    @SerializedName("lng")
    public String lng;

    @SerializedName("tilt")
    public String tilt;

    @SerializedName("imageEx")
    public String imageEx;

    @SerializedName("fileEx")
    public String fileEx;

    @SerializedName("active")
    public String active;

    @SerializedName("beginT")
    public String beginT;

    @SerializedName("endT")
    public String endT;


}
