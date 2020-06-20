package com.example.myapp.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * 消息类：包括头像、标题、内容、发布时间
 */
public class Notification {

    public String nickName;
    public String profile;
    public Date createTime;
    public int userId;

    public static Notification fromJson(JSONObject json)
            throws ParseException, JSONException {
        Notification notification = new Notification();
        notification.createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(json.getString("createTime"));
        notification.nickName = json.getString("nickName");
        notification.profile = json.getString("profile");
        notification.userId = json.getInt("userId");
        return notification;
    }

}
