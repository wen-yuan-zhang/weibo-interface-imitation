package com.example.myapp.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        //返回上一页
        findViewById(R.id.settings_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //TODO: 提交更改到服务器，同时保存到本地设置
        findViewById(R.id.settings_btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitChange();
            }
        });

        //修改邮箱
        findViewById(R.id.settings_layout_changeMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChangeMailActivity.class);
                startActivity(intent);
            }
        });

        //修改密码
        findViewById(R.id.settings_layout_changePwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChangePwdActivity.class);
                startActivity(intent);
            }
        });

    }

    private void commitChange() {
    }
}