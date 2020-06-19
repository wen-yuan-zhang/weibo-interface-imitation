package com.example.myapp.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;

public class SubfragmentChatlist extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        //加载用于展示聊天列表的listview
        ListView listView = root.findViewById(R.id.listview);
        listView.setAdapter(new ListViewChatlistAdapter(getContext()));

        return root;

    }
}