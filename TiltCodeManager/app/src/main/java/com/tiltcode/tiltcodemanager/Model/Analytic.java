package com.tiltcode.tiltcodemanager.Model;

import com.google.gson.annotations.SerializedName;

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
}
