package com.example.myapp.ui.personal;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myapp.R;
import com.google.android.material.tabs.TabLayout;

public class FollowListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followlist);

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
        viewPager.setAdapter(new FollowListPageFragmentAdapter(getSupportFragmentManager()));

        //设置tablayout
        TabLayout tabLayout = findViewById(R.id.tablayout_follow);
        tabLayout.addTab(tabLayout.newTab().setText("关注的人"));
        tabLayout.addTab(tabLayout.newTab().setText("推荐"));
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
