package com.example.myapp.ui.personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 从头像点进个人主页时启动的activity
 */
public class WatchHomepageActivity extends AppCompatActivity {

    protected Handler handler = new myHandler();
    int userId = -1;    //当前是哪个人的主界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchhomepage);

        userId = getIntent().getIntExtra("id", -1);

        //加载ViewPager
        ViewPager viewPager = findViewById(R.id.personal_viewPager);
        PersonalPageFragmentAdapter adapter = new PersonalPageFragmentAdapter(getSupportFragmentManager(), userId);
        viewPager.setAdapter(adapter);

        //加载tab
        TabLayout tabLayout = findViewById(R.id.tablayout_personal);
        tabLayout.addTab(tabLayout.newTab().setText("主页"));
        tabLayout.addTab(tabLayout.newTab().setText("历史"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setupWithViewPager(viewPager);

        //设置关注和粉丝的点击事件
        //点击文字和数字都可以触发
        findViewById(R.id.tv_follow).setOnClickListener(new FollowListListener());
        findViewById(R.id.tv_followNum).setOnClickListener(new FollowListListener());
        findViewById(R.id.tv_follower).setOnClickListener(new FollowListListener());
        findViewById(R.id.tv_followerNum).setOnClickListener(new FollowListListener());

        //返回按钮
        findViewById(R.id.btn_return).setOnClickListener(v -> finish());

        //开启一个线程获得头部信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Global.server_addr + "/cover?sessionId=" + Global.getSessionId()+"&targetId="+userId;
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println(msg);
                        JSONObject jsonObject = new JSONObject(msg);
                        if (jsonObject.getInt("code") != 200) {
                            throw new Exception(jsonObject.getString("msg"));
                        }
                        JSONObject jsonInfo = jsonObject.getJSONObject("info");
                        String nickName = jsonInfo.getString("nickName");
                        String profile = jsonInfo.getString("profile");
                        String signature = jsonInfo.getString("signature");
                        int followNum = jsonInfo.getInt("followingCount");
                        int followerNum = jsonInfo.getInt("followerCount");
                        //在个人信息都获取到后，通知UI线程更新一次界面
                        //这次更新界面只会更新文字信息，头像签名由父界面负责更新
                        Message message = new Message();
                        Bundle data = new Bundle();
                        //标识这次send是从/cover线程传过去的
                        data.putString("from", "cover");
                        data.putString("nickName", jsonInfo.getString("nickName"));
                        data.putString("profile", jsonInfo.getString("profile"));
                        data.putString("signature", jsonInfo.getString("signature"));
                        data.putInt("followingCount", jsonInfo.getInt("followingCount"));
                        data.putInt("followerCount", jsonInfo.getInt("followerCount"));
                        message.setData(data);
                        handler.sendMessage(message);
                    } else {
                        InputStream inputStream = connection.getInputStream();
                        String msg = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                        System.out.println("error!" + msg);
                    }
                } catch (Exception e) {
                    Utils.showToastInCenter(WatchHomepageActivity.this, e.toString(), Utils.TOAST_THREAD_QUEUE);
                }

            }
        }).start();
    }

    class FollowListListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO
            Intent intent = new Intent(WatchHomepageActivity.this, FollowListActivity.class);
            //目标id以及是不是本人
            intent.putExtra("id", userId);
            intent.putExtra("isMe", false);
            startActivity(intent);
        }
    }

    class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String flag = data.getString("from");
            if (flag.equals("cover")) {
                String nickName = data.getString("nickName");
                String profile = data.getString("profile");
                String signature = data.getString("signature");
                int followNum = data.getInt("followingCount");
                int followerNum = data.getInt("followerCount");
                //更新UI界面
                View view = getWindow().getDecorView();
                ((TextView) view.findViewById(R.id.tv_nickname)).setText(nickName);
                ((TextView) view.findViewById(R.id.tv_signature)).setText(signature);
                ((TextView) view.findViewById(R.id.tv_followNum)).setText(String.valueOf(followNum));
                ((TextView) view.findViewById(R.id.tv_followerNum)).setText(String.valueOf(followerNum));

                //获取头像
                File cacheDir = view.getContext().getApplicationContext().getCacheDir();
                File cacheFile = new File(cacheDir, profile);
                if (cacheFile.exists()) {
                    System.out.println("从缓存读取的");
                    Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
                    ((CircleImageView) view.findViewById(R.id.header_image)).setImageBitmap(bm);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //如果没有的话，从服务器下载
                                HttpURLConnection conn = (HttpURLConnection) new URL(Global.server_addr_static_profile + profile).openConnection();

                                conn.setRequestMethod("GET");
                                conn.connect();
                                int responseCode = conn.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    //获取服务器响应头中的流
                                    InputStream is = conn.getInputStream();

                                    //读取服务器返回流里的数据，把数据写入到本地，缓冲起来
                                    cacheFile.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(cacheFile);

                                    byte[] b = new byte[1024];
                                    int len = 0;
                                    while ((len = is.read(b)) != -1) {
                                        fos.write(b, 0, len);
                                    }
                                    fos.close();
                                    is.close();

                                    //通知UI线程更新
                                    //发送的消息还是前面的handlerMessage接收
                                    Message message = new Message();
                                    Bundle data = new Bundle();
                                    //标识这次send是从/cover线程传过去的
                                    data.putString("from", "profile");
                                    data.putString("filename", cacheFile.getAbsolutePath());
                                    message.setData(data);
                                    handler.sendMessage(message);
                                } else {
                                    //TODO
                                    throw new Exception("HTTP request return not 200");
                                }
                            } catch (Exception e) {
                                Utils.showToastInCenter(WatchHomepageActivity.this, e.toString(), Utils.TOAST_THREAD_QUEUE);
                            }
                        }
                    }).start();
                }
            }
            //说明是上面的Thread线程传过来的，标志本地头像图片已经写入完成，直接读
            else if (flag.equals("profile")) {
                String dir = data.getString("filename");
                //从本地加载图片
                Bitmap bm = BitmapFactory.decodeFile(dir);
                View view = getWindow().getDecorView();
                ((CircleImageView) view.findViewById(R.id.header_image)).setImageBitmap(bm);

            }

        }
    }
}