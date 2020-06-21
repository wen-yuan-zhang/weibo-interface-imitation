package com.example.myapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.objects.Notification;
import com.example.myapp.objects.Notification;
import com.example.myapp.ui.personal.WatchHomepageActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseListViewNotificationAdapter extends BaseListViewAdapter {

    private ArrayList<Notification> notifications;

    public BaseListViewNotificationAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        notifications = new ArrayList<>();
    }


    @Override
    public int getCount() {
        if(notifications == null)
            return 0;
        else
            return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification notification = (Notification) getItem(position);
        String profileName = notification.profile;  //头像文件名
        //convertView为null，说明首次创建，新建一个view
        View view = mInflater.inflate(R.layout.listitem_notification, parent, false);

        //文字
        ((TextView) view.findViewById(R.id.tv_username)).setText(notification.nickName);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ((TextView) view.findViewById(R.id.tv_notification_deliverTime)).setText(sf.format(notification.createTime));

        //头像
        //先查看本地有没有，如果有的话就直接从本地读
        File cacheDir = mInflater.getContext().getApplicationContext().getCacheDir();
        File cacheFile = new File(cacheDir, profileName);
        if (cacheFile.exists()) {
            System.out.println("从缓存读取的");
            Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            ((CircleImageView) view.findViewById(R.id.header_image)).setImageBitmap(bm);
        } else {
            //如果没有的话，从服务器下载
            startDownloadThread(position, parent, profileName, cacheFile, "profile");
        }
        //点击事件：点击一整条都可以弹出浏览主页
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInflater.getContext(), WatchHomepageActivity.class);
                intent.putExtra("id", notification.userId);
                mInflater.getContext().startActivity(intent);
            }
        });

        //这里处理从BaseListViewAdapter的updateSingleItem传过来的刷新时间，用于更新当前convertView的图片
        //但不返回convertView，只需要在原来的对象上操作
        if (convertView != null) {
            //由于头像和博文图片只要加载任意一张就会来刷新，所以可能有图片还没写好。
            //有哪个文件就加载哪个文件，没有就跳过
            if (cacheFile.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
                ((CircleImageView) convertView.findViewById(R.id.header_image)).setImageBitmap(bm);
            }
        }

        return view;
    }

    public void setData(ArrayList<Notification> notificationArrayList) {
        notifications = notificationArrayList;
        //不能使用notifyDataSetChanged()，因为此时是在非UI线程里
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString("notifyAll", "ok");
        message.setData(data);
        handler.sendMessage(message);
    }

}
