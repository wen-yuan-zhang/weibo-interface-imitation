package com.example.myapp.ui.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.myapp.R;
import com.example.myapp.objects.Chat;

import java.util.ArrayList;

public class ListViewChatlistAdapter extends BaseAdapter {

    private ArrayList<Chat> chats;
    private LayoutInflater mInflater;

    public ListViewChatlistAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        init();
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = mInflater.inflate(R.layout.listitem_chatbrief, parent, false);

        return root;
    }

    public void init() {
        chats = new ArrayList<>();
        for(int i = 0; i < 10; i++)
            chats.add(new Chat());
    }
}
