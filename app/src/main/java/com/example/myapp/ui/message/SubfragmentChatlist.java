package com.example.myapp.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;
import com.example.myapp.objects.Chat;
import com.example.myapp.objects.UserInfo;
import com.example.myapp.ui.BaseListViewChatlistAdapter;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SubfragmentChatlist extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        //加载用于展示聊天列表的listview
        ListView listView = root.findViewById(R.id.listview);
        BaseListViewChatlistAdapter adapter = new BaseListViewChatlistAdapter(getContext());
        listView.setAdapter(adapter);

        //从本地数据库拉取所有联系人
        List<Map<String, Object>> people = Global.db.fetchUser();
        ArrayList<Chat> chatList = new ArrayList<>();
        for (int i = 0; i < people.size(); i++) {
            int targetId = (Integer) people.get(i).get("targetId");
            long maxTime = (Long) people.get(i).get("maxTime");
            Date date = new Date();
            date.setTime(maxTime);
            Chat chat = new Chat(targetId, date);
            //拉取一次聊天记录，把最后一条拿出来作为预览界面的消息
            List<Map<String, Object>> messageList = Global.db.fetchChat(targetId,null,0,1000);
            String message = (String)messageList.get(0).get("message");
            chat.lastMessage = message;
            chatList.add(chat);
        }
        //第一次是调用setData
        if(chatList.size() != 0)
            adapter.setData(chatList);

        //向服务器获取新消息的列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Global.server_addr + "/history/chat?sessionId=" + Global.getSessionId();
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println(msg);
                        JSONObject jsonObject = new JSONObject(msg);
                        JSONArray array = jsonObject.getJSONArray("chats");
                        ArrayList<Chat> chats = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            Chat chat = Chat.fromJson(array.getJSONObject(i));
                            Global.db.addChat(1, chat.targetId, chat.lastTime.getTime(), chat.lastMessage);
                            chats.add(chat);
                        }
                        //通知UI线程更新一次界面
                        //第二次是调用addData，用于更新(distinct)聊天列表的顺序
                        adapter.addData(chats);
                    } else {
                        InputStream inputStream = connection.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println("error!" + msg);
                    }
                } catch (Exception e) {
                    Utils.showToastInCenter(getContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                }

            }
        }).start();


        return root;
    }
}

