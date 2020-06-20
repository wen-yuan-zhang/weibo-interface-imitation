package com.example.myapp.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class UserInfo {

    public String realName;
    public boolean isMale;
    public String profile;
    public String signature;
    public String school;
    public String interest;
    public String nickName;
    public boolean isTeacher;
    public int userId;
    public String department;
    public String experience;
    public int age;
    public int hasFollowed; //0未关注，1已关注

    public UserInfo(){}

    //详细信息：用于个人主页面的展示
    public static UserInfo fromDetailedJson(JSONObject json)
            throws JSONException {
        UserInfo info = new UserInfo();
        info.realName = json.getString("realName");
        info.isMale = json.getBoolean("isMale");
        info.signature = json.getString("signature");
        info.school = json.getString("school");
        info.interest = json.getString("interest");
        info.nickName = json.getString("nickName");
        info.userId = json.getInt("id");
        info.isTeacher = json.getBoolean("isTeacher");
        info.department = json.getString("department");
        info.experience = json.getString("experience");
        info.age = json.getInt("age");
        return info;
    }

    //简略信息：用于关注粉丝列表
    public static UserInfo fromBriefJson(JSONObject json)
        throws JSONException {
        UserInfo info = new UserInfo();
        info.signature = json.getString("signature");
        info.nickName = json.getString("nickName");
        info.userId = json.getInt("id");
        info.profile = json.getString("profile");
        info.hasFollowed = json.getInt("hasFollowed");
        return info;
    }
}
