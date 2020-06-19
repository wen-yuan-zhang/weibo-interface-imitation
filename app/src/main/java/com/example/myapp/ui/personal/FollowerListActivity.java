package com.example.myapp.ui.personal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myapp.R;
import com.google.android.material.tabs.TabLayout;

public class FollowerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followerlist);

        //设置标题栏
        Toolbar toolbar = findViewById(R.id.follower_toolbar);

        //设置搜索栏事件
        FloatingSearchView searchView = findViewById(R.id.follower_search_bar);
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

        //设置listView
        ListView listView = findViewById(R.id.follower_listview);
        listView.setAdapter(new FollowerListAdapter(this));

        //设置返回事件
        findViewById(R.id.follower_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class FollowerListAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        public FollowerListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.listitem_user, parent, false);
            return view;
        }
    }
}
