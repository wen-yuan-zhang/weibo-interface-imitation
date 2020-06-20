package com.example.myapp.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myapp.R;
import com.example.myapp.objects.Blog;
import com.example.myapp.objects.UserInfo;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FollowListActivity extends AppCompatActivity {

    int userId = -1;
    boolean isMe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followlist);
        Intent intent = getIntent();
        userId = intent.getIntExtra("id", -1);
        isMe = intent.getBooleanExtra("isMe", false);

        //设置标题栏
        Toolbar toolbar = findViewById(R.id.follow_toolbar);

        //设置搜索栏事件
        FloatingSearchView searchView = findViewById(R.id.follow_search_bar);
        //获得焦点时
        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                //设置键盘回车键为“搜索”
                searchView.setShowSearchKey(true);
            }

            @Override
            public void onFocusCleared() {

            }
        });
        //搜索事件
        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                //TODO: 提交搜索
                System.out.println("提交搜索。");
            }
        });

        ViewPager viewPager = findViewById(R.id.viewpager_follow);
        FollowListPageFragmentAdapter adapter = new FollowListPageFragmentAdapter(getSupportFragmentManager(), isMe, userId);
        viewPager.setAdapter(adapter);
//        adapter.init(isMe, userId);

        //设置tablayout
        TabLayout tabLayout = findViewById(R.id.tablayout_follow);
        if(isMe) {
            tabLayout.addTab(tabLayout.newTab().setText("关注"));
            tabLayout.addTab(tabLayout.newTab().setText("粉丝"));
            tabLayout.addTab(tabLayout.newTab().setText("推荐"));
        }
        //如果不是本人的话：不显示推荐列表
        else {
            tabLayout.addTab(tabLayout.newTab().setText("关注"));
            tabLayout.addTab(tabLayout.newTab().setText("粉丝"));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setupWithViewPager(viewPager);

        //设置返回事件
        findViewById(R.id.follow_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}
