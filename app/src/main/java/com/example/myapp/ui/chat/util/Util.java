package com.example.myapp.ui.chat.util;

import android.content.Context;
import android.widget.Toast;

public class Util {
    public static final String ws = "http://121.199.36.33:80/websocket/";

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
