package com.example.myapp.ui.personal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;
import com.example.myapp.objects.UserInfo;
import com.example.myapp.ui.BaseListViewUserAdapter;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SubfragmentInterestList extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        ListView listView = root.findViewById(R.id.listview);
        BaseListViewUserAdapter adapter = new BaseListViewUserAdapter(getContext());
        listView.setAdapter(adapter);

        //向服务器获取感兴趣的人列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Global.server_addr + "recommend/user?sessionId=" + Global.getSessionId();
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println(msg);
                        JSONObject jsonObject = new JSONObject(msg);
                        JSONArray jsonArray = jsonObject.getJSONArray("userList");
                        ArrayList<UserInfo> users = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++) {
                            users.add(UserInfo.fromBriefJson(jsonArray.getJSONObject(i)));
                        }
                        //在博客信息都获取到后，通知UI线程更新一次界面
                        //这次更新界面只会更新文字信息，图片的更新在BaseListViewBlogAdapter里实现
                        adapter.setData(users);
                    }
                    else {
                        InputStream inputStream = connection.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println("error!"+msg);
                    }
                } catch (Exception e) {
                    Utils.showToastInCenter(getContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                }

            }
        }).start();


        return root;
    }

}
