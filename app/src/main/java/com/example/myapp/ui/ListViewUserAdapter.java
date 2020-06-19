package com.example.myapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.myapp.R;
import com.example.myapp.objects.User;

import java.util.ArrayList;

//用户列表（头像+昵称+关注与否）的适配器
public class ListViewUserAdapter extends BaseAdapter {

    private ArrayList<User> users;

    private LayoutInflater mInflater;


    public ListViewUserAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        init();
    }

    private void init() {
        users = new ArrayList<>();
        users.add(new User());
    }

    @Override
    public int getCount() {
        if(users == null)
            return 0;
        else
            return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.listitem_personinfo, parent, false);

        User user = users.get(position);

        return view;
    }
}
