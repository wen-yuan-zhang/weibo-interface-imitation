package com.example.myapp.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapp.R;
import com.example.myapp.objects.Blog;
import com.example.myapp.objects.UserInfo;
import com.example.myapp.ui.BaseListViewBlogAdapter;
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

public class SubfragmentDisplay extends Fragment {

    int displayType;    //1获取关注人发帖，2搜索帖子，3搜索用户
    String params;

    public SubfragmentDisplay() {}

    public SubfragmentDisplay(int displayType, String params) {
        this.displayType = displayType;
        //如果displayType=2或3，需要指定params
        this.params = params;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        //加载用于展示blog的listview
        ListView listView = root.findViewById(R.id.listview);

        if(displayType == 1 || displayType == 2) {
            //设置adapter为blog的adapter
            BaseListViewBlogAdapter adapter = new BaseListViewBlogAdapter(getContext());
            listView.setAdapter(adapter);

            //向服务器请求blog列表
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = "";
                        if(displayType == 1)
                            url = Global.server_addr + "history/blog?sessionId=" + Global.getSessionId() + "&batchId=0&type=2";
                        else
                            url = Global.server_addr + "search/blog?sessionId=" + Global.getSessionId() + "&batchId=0&"+params;
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = connection.getInputStream();
                            String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                            System.out.println(msg);
                            JSONObject jsonObject = new JSONObject(msg);
                            JSONArray jsonArray = jsonObject.getJSONArray("blogList");
                            ArrayList<Blog> blogs = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                blogs.add(Blog.fromJson(jsonArray.getJSONObject(i)));
                            }
                            //在博客信息都获取到后，通知UI线程更新一次界面
                            //这次更新界面只会更新文字信息，图片的更新在BaseListViewBlogAdapter里实现
                            adapter.setData(blogs);
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
        }
        else if(displayType == 3) {
            //设置adapter为user的adapter
            BaseListViewUserAdapter adapter = new BaseListViewUserAdapter(getContext());
            listView.setAdapter(adapter);

            //向服务器请求blog列表
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = Global.server_addr + "search/user?sessionId=" + Global.getSessionId() +"&batchId=0&"+ params;
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = connection.getInputStream();
                            String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                            System.out.println(msg);
                            JSONObject jsonObject = new JSONObject(msg);
                            JSONArray jsonArray = jsonObject.getJSONArray("userList");
                            ArrayList<UserInfo> users = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                users.add(UserInfo.fromBriefJson(jsonArray.getJSONObject(i)));
                            }
                            //在个人信息都获取到后，通知UI线程更新一次界面
                            adapter.setData(users);
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

        }


        return root;
    }

}
