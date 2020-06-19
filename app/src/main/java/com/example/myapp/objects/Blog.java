package com.example.myapp.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.jar.JarException;

public class Blog {

    public String nickName;
    public String profile;
    public Date createTime;
    public boolean isTeacher;
    public int userId;
    public int blogId;
    public String content;
    public ArrayList<String> pictures = null;

    public Blog() {

    }

    public static Blog fromJson(JSONObject json)
            throws ParseException, JSONException {
        Blog blog = new Blog();
        blog.createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(json.getString("createTime"));
        blog.nickName = json.getString("nickName");
        blog.profile = json.getString("profile");
        blog.isTeacher = json.getBoolean("isTeacher");
        blog.userId = json.getInt("userId");
        blog.blogId = json.getInt("blogId");
        blog.content = json.getString("content");
        if(json.has("pictures")) {
            blog.pictures = new ArrayList<>();
            String pictures = json.getString("pictures");
            String[] picList = pictures.split(" ");
            blog.pictures.addAll(Arrays.asList(picList));
        }
        return blog;
    }

}
