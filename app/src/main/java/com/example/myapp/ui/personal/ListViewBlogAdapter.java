package com.example.myapp.ui.personal;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.example.myapp.objects.Blog;
import com.example.myapp.ui.BaseListViewBlogAdapter;
import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class ListViewBlogAdapter extends BaseListViewBlogAdapter {

    ListViewBlogAdapter(Context context) {
        super(context);

        //TODO：这部分应该放到fragment里

    }
}
