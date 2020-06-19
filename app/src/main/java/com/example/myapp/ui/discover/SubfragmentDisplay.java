package com.example.myapp.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapp.R;
import com.example.myapp.ui.discover.ListViewBlogAdapter;

public class SubfragmentDisplay extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        //加载用于展示blog的listview
        ListView listView = root.findViewById(R.id.listview);
//        listView.setAdapter(new ListViewBlogAdapter(getContext()));

        return root;
    }

}
