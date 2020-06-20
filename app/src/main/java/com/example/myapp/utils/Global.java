package com.example.myapp.utils;

public class Global {
    public final static String server_addr = "http://121.199.36.33/";
    public final static String server_addr_static_profile = server_addr+"images/profiles/";
    public final static String server_addr_static_picture = server_addr+"images/pictures/";
    public final static int port = 443;
    public final static String defaultImg = "default.jpg";
    private static String sessionId = "H6gIffhWb1iwXAa8";


    static public void setSessionId(String s) {
        sessionId = s;
    }

    static public String getSessionId() {
        return sessionId;
    }
}
