package com.example.myapp.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class UserInfo {

    public String realName;
    public boolean isMale;
    public String signature;
    public String school;
    public String interest;
    public String nickName;
    public boolean isTeacher;
    public int userId;
    public String department;
    public String experience;
    public int age;

    public UserInfo(){}

    public static UserInfo fromJson(JSONObject json)
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
}
