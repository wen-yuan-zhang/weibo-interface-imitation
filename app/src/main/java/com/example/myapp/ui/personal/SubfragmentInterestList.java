package com.example.myapp.ui.personal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;

public class SubfragmentInterestList extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        ListView listView = root.findViewById(R.id.listview);
        listView.setAdapter(new InterestListAdapter(getContext()));

        return root;
    }

    class InterestListAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        public InterestListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.listitem_user, parent, false);
            return view;
        }
    }

}
