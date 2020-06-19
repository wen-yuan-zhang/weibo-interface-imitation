package com.example.myapp.ui.personal;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;

public class EditInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpersoninfo);

        findViewById(R.id.editInfo_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回上一页
                finish();
            }
        });

        findViewById(R.id.editInfo_btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 提交更改到服务器
                submitChange();
            }
        });

    }

    private void submitChange() {
    }
}
