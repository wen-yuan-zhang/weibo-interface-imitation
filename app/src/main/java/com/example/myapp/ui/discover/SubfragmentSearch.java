package com.example.myapp.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;

public class SubfragmentSearch extends Fragment {



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_discover_search, container, false);
        ConstraintLayout layout_blog = root.findViewById(R.id.discover_search_layout_blog);
        ConstraintLayout layout_user = root.findViewById(R.id.discover_search_layout_user);

        Spinner spinner = root.findViewById(R.id.discover_search_rangeSpinner);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = parent.getItemAtPosition(position).toString();
                if(selectedText.equals("帖子")) {
                    layout_blog.setVisibility(View.VISIBLE);
                    layout_user.setVisibility(View.INVISIBLE);
                }
                else if(selectedText.equals("用户")) {
                    layout_user.setVisibility(View.VISIBLE);
                    layout_blog.setVisibility(View.INVISIBLE);
                }
                else if(selectedText.equals("不限")) {
                    layout_user.setVisibility(View.INVISIBLE);
                    layout_blog.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("Nothing selected.");
            }
        });

        return root;
    }
}
