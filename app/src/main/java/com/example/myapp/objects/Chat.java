package com.example.myapp.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 聊天类：用于展示
 */
public class Chat {
    public int targetId;
    public String lastMessage;
    public Date lastTime;
    public String profile = "";
    public String nickName = "";

    public Chat() {
    }

    public Chat(int targetId, Date lastTime) {
        this.targetId = targetId;
        this.lastTime = lastTime;
    }

    public static Chat fromJson(JSONObject json)
            throws JSONException, ParseException {
        Chat chat = new Chat();
        chat.targetId = json.getInt("fromId");
        chat.lastTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(json.getString("createTime"));
        chat.lastMessage = json.getString("message");
        return chat;
    }
}
