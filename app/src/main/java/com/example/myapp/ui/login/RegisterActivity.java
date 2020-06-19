package com.example.myapp.ui.login;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.util.Util;
import com.example.myapp.R;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        findViewById(R.id.btn_sendCaptcha).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCaptcha();
            }
        });

        //显示返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getCaptcha() {
        String mail = ((EditText)findViewById(R.id.register_email)).getText().toString();
        if(mail.equals("")) {
            Utils.showToastInCenter(getApplicationContext(), "请输入邮箱", Utils.TOAST_UI_QUEUE);
            return ;
        }
        else if(!mail.contains("edu.cn")) {
            Utils.showToastInCenter(getApplicationContext(), "请输入正确的清华邮箱", Utils.TOAST_UI_QUEUE);
            return ;
        }
        //TODO: 向服务器获取验证码
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Global.server_addr + "getCheckCode?mail=" + mail;
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = conn.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println(msg);
                        Utils.showToastInCenter(getApplicationContext(), "已经向邮箱发送验证码，有效期为5分钟，请检查", Utils.TOAST_THREAD_QUEUE);
                    }
                    else {
                        InputStream inputStream = conn.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println("error!"+msg);
                    }
                } catch (Exception e) {
                    Utils.showToastInCenter(getApplicationContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                }
            }
        }).start();

    }

    private void register() {
        //TODO: 邮箱合法性验证（是邮箱->是edu邮箱）
        //TODO: 按钮设为1分钟后再次可用
//        findViewById(R.id.btn_register).setEnabled(false);

        //获取数据
        String nickName = ((EditText)findViewById(R.id.register_tv_nickName)).getText().toString();
        String realName = ((EditText)findViewById(R.id.register_tv_realName)).getText().toString();
        RadioButton selectedRbSex = findViewById(((RadioGroup)findViewById(R.id.register_rg_sex)).getCheckedRadioButtonId());
        String inputAge = ((EditText)findViewById(R.id.register_tv_age)).getText().toString();
        RadioButton selectedRbIdentity = findViewById(((RadioGroup)findViewById(R.id.register_rg_identity)).getCheckedRadioButtonId());
        String school = ((EditText)findViewById(R.id.register_tv_school)).getText().toString();
        String department = ((EditText)findViewById(R.id.register_tv_department)).getText().toString();
        String mail = ((EditText)findViewById(R.id.register_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.register_pwd)).getText().toString();
        String captcha = ((EditText)findViewById(R.id.register_captcha)).getText().toString();

        //合法性检查
        if(selectedRbSex == null || selectedRbIdentity == null || inputAge.equals("")) {
            Utils.showToastInCenter(getApplicationContext(), "请完善注册信息！", Utils.TOAST_UI_QUEUE);
            return ;
        }
        boolean isMale = selectedRbSex.getText().toString().equals("男");
        boolean isTeacher = selectedRbIdentity.getText().toString().equals("老师");
        int age = Integer.parseInt(inputAge);

        //向服务器注册
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(Global.server_addr+"user").openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    String body = "nickName=" + nickName
                            + "&realName=" + realName
                            + "&isMale=" + isMale
                            + "&age=" + age
                            + "&isTeacher=" + isTeacher
                            + "&school=" + school
                            + "&department=" + department
                            + "&mail=" + mail
                            + "&password=" + password
                            + "&checkCode=" + captcha;
                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    writer.write(body);
                    writer.close();

                    //注意：这里的code并不是服务器手动返回的code，是http请求的code，如果不是200就肯定出错了
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = conn.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        JSONObject result = new JSONObject(msg);
                        int code = result.getInt("code");
                        if(code == 200) {
                            Utils.showToastInCenter(getApplicationContext(), "注册成功", Utils.TOAST_THREAD_QUEUE);
                            finish();
                        }
                        else {
                            //如果不是200；直接带着错误信息抛出异常
                            throw new Exception(result.getString("msg"));
                        }
                    }
                    else {
                        //TODO: 输出错误信息
                        InputStream inputStream = conn.getErrorStream();
//                        InputStream inputStream = conn.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        Utils.showToastInCenter(getApplicationContext(), msg, Utils.TOAST_THREAD_QUEUE);
                    }
                } catch (Exception e) {
                    Utils.showToastInCenter(getApplicationContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                }
            }
        }).start();
    }
}
