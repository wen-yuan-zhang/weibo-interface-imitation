package com.example.myapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.objects.UserInfo;

import java.util.ArrayList;

public class BaseListViewUserInfoAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    ArrayList<UserInfo> userInfos;

    protected Handler handler = new BaseListViewUserInfoAdapter.myHandler();


    public BaseListViewUserInfoAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        userInfos = new ArrayList<>();
    }

    @Override
    public int getCount() {
        if(userInfos == null)
            return 0;
        else
            return userInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.listitem_personinfo, parent, false);
        //设置文字信息
        UserInfo info = userInfos.get(position);
        ((TextView) view.findViewById(R.id.personal_info_tv_nickName)).setText(info.nickName);
        ((TextView) view.findViewById(R.id.personal_info_tv_realName)).setText(info.realName);
        if(info.isMale)
            ((TextView) view.findViewById(R.id.personal_info_tv_sex)).setText("男");
        else
            ((TextView) view.findViewById(R.id.personal_info_tv_sex)).setText("女");
        ((TextView) view.findViewById(R.id.personal_info_tv_age)).setText(String.valueOf(info.age));
        if(info.isTeacher)
            ((TextView) view.findViewById(R.id.personal_info_tv_identity)).setText("教师");
        else
            ((TextView) view.findViewById(R.id.personal_info_tv_identity)).setText("学生");
        ((TextView) view.findViewById(R.id.personal_info_tv_school)).setText(info.school);
        ((TextView) view.findViewById(R.id.personal_info_tv_department)).setText(info.department);
        ((TextView) view.findViewById(R.id.personal_info_tv_interest)).setText(info.interest);
        ((TextView) view.findViewById(R.id.personal_info_tv_experience)).setText(info.experience);

        return view;
    }

    class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String notifyAll = data.getString("notifyAll");
            if(notifyAll.equals("ok")) {
//                loadingView.dismiss();
                notifyDataSetChanged();
            }
        }
    }

    public void setData(UserInfo userInfo) {
        userInfos.add(userInfo);
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString("notifyAll", "ok");
        message.setData(data);
        handler.sendMessage(message);
    }

}
