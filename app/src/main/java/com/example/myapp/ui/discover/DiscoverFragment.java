package com.example.myapp.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myapp.R;
import com.example.myapp.ui.discover.ListViewBlogAdapter;

import com.arlib.floatingsearchview.*;

public class DiscoverFragment extends Fragment {

    //subfragment的标签
    private String NESTED_FRAGMENT_TAG = "subfragment";
    private Fragment subfragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_discover, container, false);

        //加载标题栏
        Toolbar toolbar = root.findViewById(R.id.discover_toolbar);
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        parent.setSupportActionBar(toolbar);
        //去掉默认的标题
        parent.getSupportActionBar().setDisplayShowTitleEnabled(false);

        //动态添加subfragment。注意：不能把subfragment直接写在xml里
        subfragment = getChildFragmentManager().findFragmentByTag(NESTED_FRAGMENT_TAG);
        if(subfragment == null) {
            subfragment = new Fragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, subfragment, NESTED_FRAGMENT_TAG).commit();
        }

        //初始时：直接用display子界面替换，用于加载所有blogs
        FragmentManager manager = getChildFragmentManager();    //在fragment中嵌套fragment，要用这个函数
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new SubfragmentDisplay();
        transaction.replace(R.id.fragment_container, fragment).commit();

        //搜索栏事件
        FloatingSearchView searchView = root.findViewById(R.id.discover_search_bar);

        //进入搜索栏时，切换下面的subfragment为条件搜索
        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                    ConstraintLayout layout = root.findViewById(R.id.search_constraintLayout);
                    //探测是否有这个layout，如果没有说明
                    if (layout == null) {
                        FragmentManager manager = getChildFragmentManager();    //在fragment中嵌套fragment，要用这个函数
                        FragmentTransaction transaction = manager.beginTransaction();
                        Fragment searchFrag = new SubfragmentSearch();
                        transaction.replace(R.id.fragment_container, searchFrag).commit();
                        //设置键盘回车键为“搜索”
                        searchView.setShowSearchKey(true);
                    }
            }

            @Override
            public void onFocusCleared() {

            }

        });

        //相应搜索事件
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

        //点击返回：切换回display subfragment
        View leftActionIcon = root.findViewById(com.arlib.floatingsearchview.R.id.left_action);
        leftActionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("点击返回按钮");
                //判断点击的是搜索图标还是返回图标，因为点这两个图标都会触发该事件。
                //只需要判断当前的fragment即可
                ListView listView = root.findViewById(R.id.listview);
                //第二个选项是为了判断此时的按钮是返回按钮
                if(listView == null && searchView.hasFocus()) {
                    //取消焦点，键盘下落
                    searchView.setSearchFocused(false);

                    FragmentManager manager = getChildFragmentManager();    //在fragment中嵌套fragment，要用这个函数
                    FragmentTransaction transaction = manager.beginTransaction();
                    Fragment displayFrag = new SubfragmentDisplay();
                    transaction.replace(R.id.fragment_container, displayFrag).commit();
                }
            }
        });

        return root;
    }

}
