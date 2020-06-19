package com.example.myapp.utils;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

public class Utils {
    public static int TOAST_UI_QUEUE = 1;
    public static int TOAST_THREAD_QUEUE = 2;

    /**
     * 把提示框显示在屏幕中央
     * @param context 上下文
     * @param msg 消息
     * @param type 1：UI线程，2：非UI线程（需要先加入UI消息队列）
     */
    public static void showToastInCenter(Context context, String msg, int type) {
        if(type == TOAST_UI_QUEUE) {
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if(type == TOAST_THREAD_QUEUE) {
            Looper.prepare();
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Looper.loop();
        }
    }
}
