package com.example.myapp.ui.personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;
import com.example.myapp.objects.Blog;
import com.example.myapp.objects.UserInfo;
import com.example.myapp.ui.BaseListViewUserInfoAdapter;
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

public class SubfragmentPersonalHomepage extends Fragment {

    int userId = -1;

    public SubfragmentPersonalHomepage() {
    }

    public SubfragmentPersonalHomepage(int userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        //加载用于展示通知的listview
        ListView listView = root.findViewById(R.id.listview);
        BaseListViewUserInfoAdapter adapter = new BaseListViewUserInfoAdapter(getContext());
        listView.setAdapter(adapter);

        //向服务器请求用户个人信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "";
                    if (userId == -1)
                        url = Global.server_addr + "/user?sessionId=" + Global.getSessionId() + "&detailed=true";
                    else
                        url = Global.server_addr + "/user?sessionId=" + Global.getSessionId() + "&detailed=true&targetId=" + userId;
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println(msg);
                        JSONObject jsonObject = new JSONObject(msg);
                        JSONObject jsonInfo = jsonObject.getJSONObject("info");
                        UserInfo userInfo = UserInfo.fromDetailedJson(jsonInfo);
                        //在个人信息都获取到后，通知UI线程更新一次界面
                        //这次更新界面只会更新文字信息，头像签名由父界面负责更新
                        adapter.setData(userInfo);
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