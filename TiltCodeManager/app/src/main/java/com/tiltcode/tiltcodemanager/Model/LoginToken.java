package com.tiltcode.tiltcodemanager.Model;

import com.tiltcode.tiltcodemanager.Util;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class LoginToken {

    public enum LoginType {Facebook,TiltCode};

    LoginType loginType; //페이스북로그인인지 일반로그인인지
    boolean isSkipedUser; //로그인정보 없이 스킵한 유저
    String token; //엑세스토큰, 페이스북 일반로그인 둘다 이 변수로 등록
    String userId; //페이스북 로그인시 생기는 유저 고유값, 일반이면 그냥 아이디
    String name; //이름
    String sex; //성별
    String birthday; //생년월일
    String phone; //전화번호
    String uuid; //기기 고유값
    String model; // 기기 모델명
    int point; // 관리자 포인트(관리자용 앱에만 있음)


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

    public int getPoint() { return point; }

    public LoginToken setPoint(int point){
        this.point = point;
        return this;
    }

    //token의 값은 수정후 반드시 saveToken이 호출되야한다.
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
        Util.putInt("point",point);
    }

    //loadToken후 반드시 session valid검사해야함.
    //내부에서 토큰이 없을시 false반환
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
        point = Util.getInt("point",0);

        return token.length()>1?true:false;
    }

    //token destroy
    public void destroyToken(){
        Util.destroyToken();
    }
}
