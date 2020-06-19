package com.example.myapp.ui.personal;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;
import com.example.myapp.objects.Blog;
import com.example.myapp.ui.ListViewUserAdapter;
import com.example.myapp.ui.personal.ListViewBlogAdapter;
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

public class SubfragmentPersonalhistory extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        //加载用于展示通知的listview
        ListView listView = root.findViewById(R.id.listview);
        ListViewBlogAdapter adapter = new ListViewBlogAdapter(getContext());
        listView.setAdapter(adapter);

        //向服务器请求blog列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Global.server_addr + "history/blog?sessionId=" + Global.getSessionId() + "&batchId=0&type=0";
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println(msg);
                        JSONObject jsonObject = new JSONObject(msg);
                        JSONArray jsonArray = jsonObject.getJSONArray("blogList");
                        ArrayList<Blog> blogs = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++) {
                            blogs.add(Blog.fromJson(jsonArray.getJSONObject(i)));
                        }
                        //在博客信息都获取到后，通知UI线程更新一次界面
                        //这次更新界面只会更新文字信息，图片的更新在BaseListViewBlogAdapter里实现
                        adapter.setData(blogs);
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