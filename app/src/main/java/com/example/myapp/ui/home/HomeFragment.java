package com.example.myapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;
import com.example.myapp.objects.Blog;
import com.example.myapp.ui.BaseListViewBlogAdapter;
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

public class HomeFragment extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //设置标题栏
        Toolbar toolbar = root.findViewById(R.id.home_toolbar);
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        parent.setSupportActionBar(toolbar);
        //去掉默认的标题
        parent.getSupportActionBar().setDisplayShowTitleEnabled(false);

        //加载ListView
        listView = root.findViewById(R.id.lv_home);
        BaseListViewBlogAdapter adapter = new BaseListViewBlogAdapter(getContext());
        listView.setAdapter(adapter);

        //点击发布按钮：跳转到deliver activity
        root.findViewById(R.id.home_btn_deliver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeliverActivity.class);
                startActivity(intent);
            }
        });

        //向服务器请求blog列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Global.server_addr + "history/blog?sessionId=" + Global.getSessionId() + "&batchId=0&type=1";
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
