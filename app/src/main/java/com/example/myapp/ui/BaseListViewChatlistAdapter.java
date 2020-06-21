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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.objects.Chat;
import com.example.myapp.objects.Notification;
import com.example.myapp.objects.UserInfo;
import com.example.myapp.ui.BaseListViewAdapter;
import com.example.myapp.ui.chat.ChatActivity;
import com.example.myapp.ui.personal.WatchHomepageActivity;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseListViewChatlistAdapter extends BaseListViewAdapter {

    private ArrayList<Chat> chats;
    private LayoutInflater mInflater;

    public BaseListViewChatlistAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        chats = new ArrayList<>();
    }


    @Override
    public int getCount() {
        if(chats == null)
            return 0;
        else
            return chats.size();
    }

    @Override
    public Object getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.listitem_chatbrief, parent, false);
        Chat chat = (Chat) getItem(position);


        //TODO
        String nickName = chat.nickName;  //头像文件名
        if (nickName.equals("")) {
            //nickName为空，说明getView第一次被调用：只设置时间，昵称和头像都从服务器请求一下
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ((TextView) view.findViewById(R.id.chat_deliverTime)).setText(sf.format(chat.lastTime));
            ((TextView) view.findViewById(R.id.chat_content)).setText(chat.lastMessage);

            //根据用户Id去向服务器请求头像和昵称
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = Global.server_addr + "/user?sessionId=" + Global.getSessionId() + "&detailed=false&targetId=" + chat.targetId;
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = connection.getInputStream();
                            String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                            System.out.println(msg);
                            JSONObject jsonObject = new JSONObject(msg);
                            JSONObject object = jsonObject.getJSONObject("info");
                            UserInfo user = UserInfo.fromBriefJson(object);
                            //现在可以更新头像和昵称了
                            chat.nickName = user.nickName;
                            chat.profile = user.profile;
                            //notifyDatasetChange
                            Message message = new Message();
                            Bundle data = new Bundle();
                            data.putString("notifyAll", "ok");
                            message.setData(data);
                            handler.sendMessage(message);
                        } else {
                            InputStream inputStream = connection.getInputStream();
                            String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                            System.out.println("error!" + msg);
                        }
                    } catch (Exception e) {
                        Utils.showToastInCenter(view.getContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                    }

                }
            }).start();

        }
        //如果不为空的话：说明是昵称和头像信息已经返回来了，全设置一遍。但头像可能不在本地，需要检查一下
        else {

            //点击跳转到聊天界面：必须是nickName已经加载出来之后才可以！
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("targetId", chat.targetId);
                    intent.putExtra("nickName", chat.nickName);
                    intent.putExtra("profileHim", chat.profile);
                    view.getContext().startActivity(intent);
                }
            });

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ((TextView) view.findViewById(R.id.chat_deliverTime)).setText(sf.format(chat.lastTime));
            ((TextView) view.findViewById(R.id.chat_content)).setText(chat.lastMessage);
            ((TextView) view.findViewById(R.id.tv_chat_username)).setText(chat.nickName);
            //检查头像在本地有没有
            String profileName = chat.profile;
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
        }


        //这里处理从BaseListViewAdapter的updateSingleItem传过来的刷新时间，用于更新当前convertView的图片
        //但不返回convertView，只需要在原来的对象上操作
        if (convertView != null) {
            //由于头像和博文图片只要加载任意一张就会来刷新，所以可能有图片还没写好。
            //有哪个文件就加载哪个文件，没有就跳过
            if(!chat.profile.equals("")) {
                File cacheFile = new File(view.getContext().getApplicationContext().getCacheDir(), chat.profile);
                if (cacheFile.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
                    ((CircleImageView) convertView.findViewById(R.id.header_image)).setImageBitmap(bm);
                }
            }
        }

        return view;
    }

    public void setData(ArrayList<Chat> chatArrayList) {
        chats = chatArrayList;
        //不能使用notifyDataSetChanged()，因为此时是在非UI线程里
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString("notifyAll", "ok");
        message.setData(data);
        handler.sendMessage(message);
    }

    public void addData(ArrayList<Chat> chatArrayList) {
        //需要去重。新加进来的这些必须排在前面
        ArrayList<Chat> newList = new ArrayList<>();
        //先为chatArrayList去重
        for (int i = 0; i < chatArrayList.size(); i++) {
            Chat chatToAdd = chatArrayList.get(i);
            boolean flag = true;
            //如果chatToAdd跟newList中所有的targetId都不重复，则添加
            for (int j = 0; j < newList.size(); j++) {
                Chat chatAlreadyHas = newList.get(j);
                if (chatToAdd.targetId == chatAlreadyHas.targetId) {
                    //如果碰到相同，就替换，因为后面的比前面的发送时间更晚，是要显示的
                    newList.set(j, chatToAdd);
                    flag = false;
                    break;
                }
            }
            if (flag)
                newList.add(chatToAdd);
        }
        //再为chats去重
        for (Chat chatToAdd : chats) {
            boolean flag = true;
            //如果chatToAdd跟newList中所有的targetId都不重复，则添加
            for (Chat chatAlreadyHas : newList) {
                if (chatToAdd.targetId == chatAlreadyHas.targetId) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                newList.add(chatToAdd);
        }
        chats = newList;
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString("notifyAll", "ok");
        message.setData(data);
        handler.sendMessage(message);
    }
}
