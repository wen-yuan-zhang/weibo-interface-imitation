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
import com.example.myapp.ui.home.ListViewBlogAdapter;

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
        listView.setAdapter(new ListViewBlogAdapter(getContext()));

        //点击发布按钮：跳转到deliver activity
        root.findViewById(R.id.home_btn_deliver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeliverActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
