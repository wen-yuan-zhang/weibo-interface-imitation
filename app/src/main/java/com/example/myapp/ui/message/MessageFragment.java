package com.example.myapp.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myapp.R;
import com.google.android.material.tabs.TabLayout;

public class MessageFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_message, container, false);

        //隐藏标题栏
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        //加载tab
        TabLayout tabLayout = root.findViewById(R.id.tablayout_discover);
        tabLayout.addTab(tabLayout.newTab().setText("通知"));
        tabLayout.addTab(tabLayout.newTab().setText("消息"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);



        //加载ViewPager
        final ViewPager viewPager = root.findViewById(R.id.viewpager_discover);
        PagerAdapter adapter = new MessagePageFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        //和MainActivity的navigation类似地，设置监听
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }

}
