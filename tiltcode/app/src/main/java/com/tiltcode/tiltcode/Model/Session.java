package com.tiltcode.tiltcode.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JSpiner on 2015. 6. 23..
 */
public class Session {

    @SerializedName("_id")
    public String _id;

    @SerializedName("id")
    public String id;

    @SerializedName("session")
    public String session;

    @SerializedName("name")
    public String name;

    @SerializedName("birth")
    public String birth;

    @SerializedName("phone")
    public String phone;

    @SerializedName("sex")
    public String sex;

    @SerializedName("company")
    public String company;

    @SerializedName("point")
    public String point;


}
