package com.example.myapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.objects.Blog;
import com.example.myapp.objects.User;
import com.example.myapp.objects.UserInfo;
import com.example.myapp.ui.personal.WatchHomepageActivity;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//用户列表（头像+昵称+简介+关注与否）的适配器。可以被关注粉丝列表复用
public class BaseListViewUserAdapter extends BaseListViewAdapter {

    private ArrayList<UserInfo> users;

    public BaseListViewUserAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        users = new ArrayList<>();
    }


    @Override
    public int getCount() {
        if(users == null)
            return 0;
        else
            return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserInfo user = (UserInfo) getItem(position);
        String profileName = user.profile;  //头像文件名
        //convertView为null，说明首次创建，新建一个view
        View view = mInflater.inflate(R.layout.listitem_user, parent, false);

        //文字
        ((TextView) view.findViewById(R.id.user_username)).setText(user.nickName);
        ((TextView) view.findViewById(R.id.user_signature)).setText(user.signature);

        //关注按钮
        //关注列表：显示取消关注按钮
        TextView btnFollow = view.findViewById(R.id.user_btn_follow);
        TextView btnUnfollow = view.findViewById(R.id.user_btn_unfollow);
        //点击，切换关注和取消关注
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String url = Global.server_addr + "follow?sessionId=" + Global.getSessionId() + "&targetId=" + user.userId;
                            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                            connection.setRequestMethod("GET");
                            connection.connect();
                            int responseCode = connection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                if (user.hasFollowed == 1) {
                                    btnFollow.setVisibility(View.VISIBLE);
                                    btnUnfollow.setVisibility(View.GONE);
                                    Utils.showToastInCenter(mInflater.getContext(), "关注成功", Utils.TOAST_THREAD_QUEUE);
                                    user.hasFollowed = 0;
                                } else {
                                    btnFollow.setVisibility(View.GONE);
                                    btnUnfollow.setVisibility(View.VISIBLE);
                                    Utils.showToastInCenter(mInflater.getContext(), "取消关注成功", Utils.TOAST_THREAD_QUEUE);
                                    user.hasFollowed = 1;
                                }
                            } else {
                                InputStream inputStream = connection.getInputStream();
                                String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                                System.out.println("error!" + msg);
                            }
                        } catch (Exception e) {
                            Utils.showToastInCenter(mInflater.getContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                        }

                    }
                }).start();
            }
        };
        btnFollow.setOnClickListener(listener);
        btnUnfollow.setOnClickListener(listener);
        if(user.hasFollowed == 1) {
            btnFollow.setVisibility(View.GONE);
            btnUnfollow.setVisibility(View.VISIBLE);
        }
        //粉丝列表：显示关注按钮
        else {
            btnFollow.setVisibility(View.VISIBLE);
            btnUnfollow.setVisibility(View.GONE);
        }

        //头像
        //先查看本地有没有，如果有的话就直接从本地读
        File cacheDir = mInflater.getContext().getApplicationContext().getCacheDir();
        File cacheFile = new File(cacheDir, profileName);
        if (cacheFile.exists()) {
            System.out.println("从缓存读取的");
            Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            ((CircleImageView) view.findViewById(R.id.user_header_image)).setImageBitmap(bm);
        } else {
            //如果没有的话，从服务器下载
            startDownloadThread(position, parent, profileName, cacheFile, "profile");
        }
        //点击事件：弹出浏览主页
        ((CircleImageView) view.findViewById(R.id.user_header_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInflater.getContext(), WatchHomepageActivity.class);
                intent.putExtra("id", user.userId);
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
                ((CircleImageView) convertView.findViewById(R.id.user_header_image)).setImageBitmap(bm);
            }
        }

        return view;
    }

    public void setData(ArrayList<UserInfo> userArrayList) {
        users = userArrayList;
        //不能使用notifyDataSetChanged()，因为此时是在非UI线程里
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString("notifyAll", "ok");
        message.setData(data);
        handler.sendMessage(message);
    }

}
