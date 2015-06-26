package com.tiltcode.tiltcode.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class CouponResult {

    @SerializedName("code")
    public String code;

    @SerializedName("message")
    public String message;

    @SerializedName("session")
    public String session;

    @SerializedName("coupon")
    public List<Coupon> coupon;

}
