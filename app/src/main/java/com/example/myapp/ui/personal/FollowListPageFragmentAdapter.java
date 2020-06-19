package com.example.myapp.ui.personal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FollowListPageFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> fragmentTitles;

    public FollowListPageFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new SubfragmentFollowList());
        fragments.add(new SubfragmentInterestList());

        fragmentTitles = new ArrayList<>();
        fragmentTitles.add("关注的人");
        fragmentTitles.add("推荐");
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

}
