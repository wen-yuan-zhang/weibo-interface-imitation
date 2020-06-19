package com.example.myapp.ui.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.objects.Notification;

import java.util.ArrayList;

public class ListViewNotificationAdapter extends BaseAdapter {

    private ArrayList<Notification> notifies;
    private LayoutInflater mInflater;

    public ListViewNotificationAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        init();
    }

    @Override
    public int getCount() {
        if(notifies == null)
            return 0;
        else
            return notifies.size();
    }

    @Override
    public Object getItem(int position) {
        return notifies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = mInflater.inflate(R.layout.listitem_notification, parent, false);

        TextView textView = root.findViewById(R.id.tv_notification_content);
        textView.setText("在文字录入比赛（打字比赛）中，最公平的比赛用文本就是随机文本，这个随机汉字生成器便是为此所作。普通人的汉字录入速度一般是每分钟几十个到一百多个，我们可以生成一两千字的随机汉字文本，让比赛者录入完这些汉字，依据他们的比赛用时和正确率判断名次。生成随机汉字的原始文字一般选择常用汉字，经过随机排列之后只能一个字一个字的输入，对参赛者来说是相对公平的方案。");

        return root;
    }

    private void init() {
        notifies = new ArrayList<>();
        //TODO: just for test
        for(int i = 0; i < 10; i++) {
            notifies.add(new Notification());
        }
    }
}
