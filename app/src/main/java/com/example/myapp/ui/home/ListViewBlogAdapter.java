package com.example.myapp.ui.home;

import android.content.Context;

import com.example.myapp.ui.BaseListViewBlogAdapter;

//package-private，只在本package中可见
class ListViewBlogAdapter extends BaseListViewBlogAdapter {

    ListViewBlogAdapter(Context context) {
        super(context);
        //TODO: 向服务器请求blog列表

    }
}
