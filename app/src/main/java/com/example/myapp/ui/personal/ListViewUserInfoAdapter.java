package com.example.myapp.ui.personal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.myapp.R;
import com.example.myapp.ui.BaseListViewUserInfoAdapter;

import java.util.ArrayList;

//用于展示userInfo的适配器，只负责维护一个item
public class ListViewUserInfoAdapter extends BaseListViewUserInfoAdapter {

    public ListViewUserInfoAdapter(Context context) {
        super(context);
    }
}
