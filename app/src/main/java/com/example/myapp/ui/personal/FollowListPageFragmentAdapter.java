package com.example.myapp.ui.personal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FollowListPageFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> fragmentTitles;
    private boolean isMe = false;
    private int userId = -1;

    public FollowListPageFragmentAdapter(@NonNull FragmentManager fm, boolean isMe, int userId) {
        super(fm);
        fragments = new ArrayList<>();
        SubfragmentFollowList f1 = new SubfragmentFollowList(userId);
        fragments.add(f1);
        SubfragmentFollowerList f2 = new SubfragmentFollowerList(userId);
        fragments.add(f2);

        fragmentTitles = new ArrayList<>();
        fragmentTitles.add("关注");
        fragmentTitles.add("粉丝");

        //如果是自己的：添加推荐列表
        if (isMe) {
            fragments.add(new SubfragmentInterestList());
            fragmentTitles.add("推荐");
        }
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        if(fragments == null)
            return 0;
        else
            return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //得到对应position的Fragment的title
        return fragmentTitles.get(position);
    }

    public void init(boolean isMe, int id) {
        this.isMe = isMe;
        this.userId = id;
        ((SubfragmentFollowList)fragments.get(0)).init(id);
        ((SubfragmentFollowerList)fragments.get(1)).init(id);
    }

}
