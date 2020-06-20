package com.example.myapp.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.myapp.objects.Blog;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//ListViewAdapter的基类，负责公用的启动后台下载线程和初始化Handler类
public abstract class BaseListViewAdapter extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Handler handler = new myHandler();

    ViewGroup parentListView;

    protected void startDownloadThread(int position, ViewGroup parent, String profileName, File cacheFile, String prefix) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //把这两个变量保存起来，用于图片下载完更新UI
                    parentListView = parent;
                    HttpURLConnection conn;
                    //访问头像和博文图片是两个接口
                    if(prefix.equals("profile"))
                        conn = (HttpURLConnection) new URL(Global.server_addr_static_profile + profileName).openConnection();
                    else
                        conn = (HttpURLConnection) new URL(Global.server_addr_static_picture + profileName).openConnection();

                    conn.setRequestMethod("GET");
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //获取服务器响应头中的流
                        InputStream is = conn.getInputStream();

                        //读取服务器返回流里的数据，把数据写入到本地，缓冲起来
                        cacheFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(cacheFile);

                        byte[] b = new byte[1024];
                        int len = 0;
                        while ((len = is.read(b)) != -1) {
                            fos.write(b, 0, len);
                        }
                        fos.close();
                        is.close();

                        //从本地加载图片
                        System.out.println("写入图片:"+cacheFile.getAbsolutePath());
                        Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());

                        //通知UI线程更新
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("notifySingle", "ok");
                        bundle.putInt("position", position);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        //TODO
                        throw new Exception("HTTP request returns"+responseCode);
                    }
                } catch (Exception e) {
                    Utils.showToastInCenter(mInflater.getContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                }
            }
        }).start();

    }

    //调用一次getView使对应position位置的view得到更新，parent是容纳viewitem的ListView
    //这个不能写在BaseListViewAdapter里，不然getView会调用BaseAdapter的方法 and do nothing
    protected void updateSingleItem(int position, ViewGroup parent) {
        //getChildAt的参数index记录相对位置，即可见项的第一条为下标0开始，不管它实际是data的第几条。所以需要计算相对位置
        int startIndex = ((ListView) parent).getFirstVisiblePosition();
        View oldView = ((ListView) parent).getChildAt(position - startIndex);
        //这里调用getView，在getView中更新oldView对应的图片内容，达到更新视图的效果
        if (oldView != null)
            getView(position, oldView, parent);
    }


    class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String notifyAll = data.getString("notifyAll");
            if(notifyAll!= null && notifyAll.equals("ok")) {
//                loadingView.dismiss();
                notifyDataSetChanged();
            }
            String notifySingle = data.getString("notifySingle");
            if(notifySingle != null && notifySingle.equals("ok")) {
                int position = data.getInt("position");
                updateSingleItem(position, parentListView);
            }
        }
    }


}
