package com.tiltcode.tiltcodemanager.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import retrofit.http.FieldMap;

/**
 * Created by JSpiner on 2015. 7. 8..
 */
public class Analytic {


    @SerializedName("count")
    public String count;

    @SerializedName("sex")
    public int[] sex;

    @SerializedName("age")
    public int[] age;

    @SerializedName("model")
    public Map<String,String> model;

}
