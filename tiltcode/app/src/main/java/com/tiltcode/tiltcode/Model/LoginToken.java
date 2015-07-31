package com.tiltcode.tiltcode.Model;

import com.tiltcode.tiltcode.Util;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class LoginToken {

    public enum LoginType {Facebook,TiltCode};

    LoginType loginType; //페이스북로그인인지 일반로그인인지
    boolean isSkipedUser; //로그인정보 없이 스킵한 유저
    String token; //엑세스토큰, 페이스북 일반로그인 둘다 이 변수로 등록
    String userId; //페이스북 로그인시 생기는 유저 고유값
    String name; //이름
    String sex; //성별
    String birthday; //생년월일
    String phone; //전화번호
    String uuid; //기기 고유값
    String model; // 기기 모델명

    public LoginToken(){
        loadToken();
    }


    public LoginType getLoginType() {
        return loginType;
    }

    public LoginToken setLoginType(LoginType loginType) {
        this.loginType = loginType;
        return this;
    }

    public boolean isSkipedUser() {
        return isSkipedUser;
    }

    public LoginToken setIsSkipedUser(boolean isSkipedUser) {
        this.isSkipedUser = isSkipedUser;
        return this;
    }

    public String getToken() {
        return token;
    }

    public LoginToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public LoginToken setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public LoginToken setName(String name) {
        this.name = name;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public LoginToken setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public LoginToken setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public LoginToken setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public LoginToken setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getModel() {
        return model;
    }

    public LoginToken setModel(String model) {
        this.model = model;
        return this;
    }

    public void saveToken(){
        Util.putBoolean("isSkipedUser",isSkipedUser);
        Util.putString("token",token);
        Util.putString("userId",userId);
        Util.putString("name",name);
        Util.putString("sex",sex);
        Util.putString("birthday",birthday);
        Util.putString("phone",phone);
        Util.putString("uuid",uuid);
        Util.putString("model",model);
    }

    public boolean loadToken(){

        isSkipedUser = Util.getBoolean("isSkipedUser",false);
        token = Util.getString("token","");
        userId = Util.getString("userId","");
        name = Util.getString("name","");
        sex = Util.getString("sex","");
        birthday = Util.getString("birthday","");
        phone = Util.getString("phone","");
        uuid = Util.getString("uuid","");
        model = Util.getString("model","");

        return token.length()>1?true:false;
    }

    public void destroyToken(){
        Util.destroyToken();
    }
}
