package com.example.myapp.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ecloud.pulltozoomview.PullToZoomListViewEx;
import com.example.myapp.R;
import com.example.myapp.objects.UserInfo;
import com.example.myapp.ui.BaseListViewUserInfoAdapter;
import com.example.myapp.ui.message.MessagePageFragmentAdapter;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人页面
 */
public class PersonalFragment extends Fragment {


    protected Handler handler = new PersonalFragment.myHandler();

    int userId = -1;    //本人的id

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal, container, false);

        //加载菜单栏
        Toolbar toolbar = root.findViewById(R.id.personal_toolbar);
        toolbar.inflateMenu(R.menu.personal_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_editInfo:
                        Intent intent1 = new Intent(getActivity(), EditInfoActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.option_settings:
                        Intent intent2 = new Intent(getActivity(), SettingsActivity.class);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //设置渐变
        AppBarLayout app_bar = root.findViewById(R.id.app_bar_personal);
        final int alphaMaxOffset = 400;
        toolbar.getBackground().setAlpha(0);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // 设置 toolbar 背景
                if (verticalOffset > -alphaMaxOffset) {
                    toolbar.getBackground().setAlpha(255 * -verticalOffset / alphaMaxOffset);
                } else {
                    toolbar.getBackground().setAlpha(255);
                }
            }
        });

        //加载ViewPager
        ViewPager viewPager = root.findViewById(R.id.personal_viewPager);
        viewPager.setAdapter(new PersonalPageFragmentAdapter(getChildFragmentManager()));

        //加载tab
        TabLayout tabLayout = root.findViewById(R.id.tablayout_personal);
        tabLayout.addTab(tabLayout.newTab().setText("主页"));
        tabLayout.addTab(tabLayout.newTab().setText("历史"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setupWithViewPager(viewPager);

        //设置关注和粉丝的点击事件
        //点击文字和数字都可以触发
        root.findViewById(R.id.tv_follow).setOnClickListener(new FollowListListener());
        root.findViewById(R.id.tv_followNum).setOnClickListener(new FollowListListener());
        root.findViewById(R.id.tv_follower).setOnClickListener(new FollowListListener());
        root.findViewById(R.id.tv_followerNum).setOnClickListener(new FollowListListener());

        //开启一个线程获得头部信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Global.server_addr + "/cover?sessionId=" + Global.getSessionId();
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
                        userId = jsonInfo.getInt("id");
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
                    Utils.showToastInCenter(getContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                }

            }
        }).start();

        return root;
    }

    class FollowListListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO
            //如果userId=-1，说明还没加载完，还没从服务器返回id结果
            if(userId == -1)
                return;
            Intent intent = new Intent(getActivity(), FollowListActivity.class);
            intent.putExtra("id", userId);
            intent.putExtra("isMe", true);
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
                View view = getView();
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
                                Utils.showToastInCenter(getContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
                            }
                        }
                    }).start();
                }
            }
            //说明是上面的Thread线程传过来的，标志本地头像图片已经写入完成，直接读
            else if(flag.equals("profile")){
                String dir = data.getString("filename");
                //从本地加载图片
                Bitmap bm = BitmapFactory.decodeFile(dir);
                View view = getView();
                ((CircleImageView) view.findViewById(R.id.header_image)).setImageBitmap(bm);

            }
        }
    }
}
