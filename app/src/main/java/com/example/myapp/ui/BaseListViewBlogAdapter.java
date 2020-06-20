package com.example.myapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.objects.Blog;
import com.example.myapp.ui.personal.WatchHomepageActivity;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.os.Handler;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;

//ListViewAdapter的基类，可以被home、discover、personal界面的博客展示区域共同使用
public class BaseListViewBlogAdapter extends BaseListViewAdapter {

    //帖子列表
    protected ArrayList<Blog> blogs;


    public BaseListViewBlogAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        blogs = new ArrayList<>();
    }

    @Override
    public int getCount() {
        if (blogs == null)
            return 0;
        else
            return blogs.size();
    }

    @Override
    public Object getItem(int position) {
        return blogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertView为null，说明首次创建，新建一个view
        Blog blog = (Blog) getItem(position);
        String profileName = blog.profile;  //头像文件名
        View view = mInflater.inflate(R.layout.listitem_blog, parent, false);

        //文字
        ((TextView) view.findViewById(R.id.tv_blog_username)).setText(blog.nickName);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ((TextView) view.findViewById(R.id.tv_blog_deliverTime)).setText(sf.format(blog.createTime));
        ((ExpandableTextView) view.findViewById(R.id.expand_text_view)).setText(blog.content);

        //头像
        //先查看本地有没有，如果有的话就直接从本地读
        File cacheDir = mInflater.getContext().getApplicationContext().getCacheDir();
        File cacheFile = new File(cacheDir, profileName);
        if (cacheFile.exists()) {
            System.out.println("从缓存读取的");
            Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            ((CircleImageView) view.findViewById(R.id.blog_header_image)).setImageBitmap(bm);
        } else {
            //如果没有的话，从服务器下载
            startDownloadThread(position, parent, profileName, cacheFile, "profile");
        }
        //头像点击事件：启动watchHomepageActivity
        ((CircleImageView) view.findViewById(R.id.blog_header_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInflater.getContext(), WatchHomepageActivity.class);
                intent.putExtra("id", blog.userId);
                mInflater.getContext().startActivity(intent);

            }
        });

        //图片（如果有的话）
        //把多余的预置图片隐藏掉
        //
        // 如果某一排全空，则置为GONE；如果只有1或2个空，则置为INVISIBLE，用于占位
        if (blog.pictures != null) {
            int blogImgNum = blog.pictures.size();
            if (blogImgNum <= 3) {
                for (int i = blogImgNum + 1; i <= 3; i++) {
                    view.findViewWithTag("img" + i).setVisibility(View.INVISIBLE);
                }
                for (int i = 4; i <= 6; i++) {
                    view.findViewWithTag("img" + i).setVisibility(View.GONE);
                }
            } else {
                for (int i = blogImgNum + 1; i <= 6; i++) {
                    view.findViewWithTag("img" + i).setVisibility(View.GONE);
                }
            }
            //对于每一张图片：重复上面读内存->下载图片的过程
            for (int i = 0; i < blogImgNum; i++) {
                String pictureName = blog.pictures.get(i);
                File cacheFile2 = new File(cacheDir, pictureName);
                if (cacheFile2.exists()) {
                    System.out.println("从缓存读取的");
                    Bitmap bm = BitmapFactory.decodeFile(cacheFile2.getAbsolutePath());
                    ((ImageView) view.findViewWithTag("img" + (i + 1))).setImageBitmap(bm);

                } else
                    //如果没有的话，从服务器下载
                    startDownloadThread(position, parent, pictureName, cacheFile2, "pictures");
            }
        }
        //如果没有picture：直接把所有图片都隐藏掉
        else {
            for (int i = 1; i <= 6; i++) {
                view.findViewWithTag("img" + i).setVisibility(View.GONE);
            }
        }


        //如果convertView不为空：说明要更新，此时头像和博文图片都已加载好，从本地读出并刷新
        if (convertView != null) {
            //由于头像和博文图片只要加载任意一张就会来刷新，所以可能有图片还没写好。
            //有哪个文件就加载哪个文件，没有就跳过

            //头像图片
            if (cacheFile.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
                ((CircleImageView) convertView.findViewById(R.id.blog_header_image)).setImageBitmap(bm);

            }
            //博文图片
            if (blog.pictures != null) {
                for (int i = 0; i < blog.pictures.size(); i++) {
                    File cacheFile2 = new File(cacheDir, blog.pictures.get(i));
                    if (cacheFile2.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(cacheFile2.getAbsolutePath());
                        ((ImageView) convertView.findViewWithTag("img" + (i + 1))).setImageBitmap(bm);

                    }
                }
            }
        }
        return view;
    }

//    private void updateSingleItem(int position, ViewGroup parent) {
//        View oldView = ((ListView)parent).getChildAt(position);
//        getView(position, oldView, parent);
//    }

//    private void startDownloadThread(int position, ViewGroup parent, String profileName, File cacheFile, String prefix) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //把这两个变量保存起来，用于图片下载完更新UI
//                    //TODO: 这里的子线程为什么可以访问外面的变量？
//                    int pos = position;
//                    ViewGroup listView = parent;
//                    HttpURLConnection conn;
//                    //访问头像和博文图片是两个接口
//                    if(prefix.equals("profile"))
//                        conn = (HttpURLConnection) new URL(Global.server_addr_static_profile + profileName).openConnection();
//                    else
//                        conn = (HttpURLConnection) new URL(Global.server_addr_static_picture + profileName).openConnection();
//
//                    conn.setRequestMethod("GET");
//                    conn.connect();
//                    int responseCode = conn.getResponseCode();
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        //获取服务器响应头中的流
//                        InputStream is = conn.getInputStream();
//
//                        //读取服务器返回流里的数据，把数据写入到本地，缓冲起来
//                        cacheFile.createNewFile();
//                        FileOutputStream fos = new FileOutputStream(cacheFile);
//                        //加一个共享锁
//                        while (true) {
//                            try {
//                                fos.getChannel().tryLock(0, Long.MAX_VALUE, true);
//                                break;
//                            } catch (Exception e) {
//                                Utils.showToastInCenter(mInflater.getContext(), "检测到读写冲突", Utils.TOAST_THREAD_QUEUE);
//                                //隔半秒再试
//                                Thread.sleep(500);
//                            }
//                        }
//                        byte[] b = new byte[1024];
//                        int len = 0;
//                        while ((len = is.read(b)) != -1) {
//                            fos.write(b, 0, len);
//                        }
//                        fos.close();
//                        is.close();
//
//                        //从本地加载图片
//                        Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
//
//                        //通知UI线程更新
//                        updateSingleItem(pos, listView);
//
//                    } else {
//                        //TODO
//                        throw new Exception("HTTP request return not 200");
//                    }
//                } catch (Exception e) {
//                    Utils.showToastInCenter(mInflater.getContext(), e.toString(), Utils.TOAST_THREAD_QUEUE);
//                }
//            }
//        }).start();
//
//    }

//    class myHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Bundle data = msg.getData();
//            String notifyAll = data.getString("notifyAll");
//            if(notifyAll.equals("ok")) {
////                loadingView.dismiss();
//                notifyDataSetChanged();
//            }
//        }
//    }

    public void setData(ArrayList<Blog> blogArrayList) {
        blogs = blogArrayList;
        //不能使用notifyDataSetChanged()，因为此时是在非UI线程里
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString("notifyAll", "ok");
        message.setData(data);
        handler.sendMessage(message);
    }

}
