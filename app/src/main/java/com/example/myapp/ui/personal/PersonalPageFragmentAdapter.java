package com.example.myapp.ui.personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PersonalPageFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> fragmentTitles;

    public PersonalPageFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new SubfragmentPersonalHomepage());
        fragments.add(new SubfragmentPersonalhistory());

        //需要跟tabLayout同步更改
        fragmentTitles = new ArrayList<>();
        fragmentTitles.add("主页");
        fragmentTitles.add("历史");
    }

    public PersonalPageFragmentAdapter(@NonNull FragmentManager fm, int userId) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new SubfragmentPersonalHomepage(userId));
        fragments.add(new SubfragmentPersonalhistory(userId));

        //需要跟tabLayout同步更改
        fragmentTitles = new ArrayList<>();
        fragmentTitles.add("主页");
        fragmentTitles.add("历史");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //得到对应position的Fragment的title
        return fragmentTitles.get(position);
    }

}