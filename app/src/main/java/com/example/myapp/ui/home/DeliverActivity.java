package com.example.myapp.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.utils.Global;
import com.example.myapp.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;

import com.example.myapp.R;
import com.roger.catloadinglibrary.CatLoadingView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DeliverActivity extends AppCompatActivity {
    private int maxSelectNum = 6;   //最多允许上传6张
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private RecyclerView mRecyclerView;
    private PopupWindow pop;

    CatLoadingView loadingView = new CatLoadingView();

    Handler mHandler = new deliverHandler();

    private int REQUEST_CAMERA_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverblog);

        //设置标题栏
        Toolbar toolbar = findViewById(R.id.deliver_toolbar);
        setSupportActionBar(toolbar);
        //去掉默认的标题
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRecyclerView = findViewById(R.id.deliver_recycler_img);

        //点击取消：返回上一个activity
        findViewById(R.id.deliver_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击发布：向服务器发布帖子
        findViewById(R.id.deliver_btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 向服务器发送
                publishBlog();
            }
        });

        //猫咪loading不能点击取消，只能认为取消
        loadingView.setClickCancelAble(false);

        //初始化上传图片区域
        initImageWidget();

    }

    private void publishBlog() {
        String content = ((EditText) findViewById(R.id.deliver_editText_content)).getText().toString();
        List<LocalMedia> mediaList = adapter.getMediaList();
        if (content.equals("")) {
            Utils.showToastInCenter(getApplicationContext(), "帖子内容不能为空！", Utils.TOAST_UI_QUEUE);
            return ;
        }

        //发布要放到UI线程里来阻塞，此时界面上用一个Loading挡住
        loadingView.show(getSupportFragmentManager(), "Loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("content", content);
                    //添加图片资源
                    for (int i = 0; i < mediaList.size(); i++) {
                        String path = mediaList.get(i).getPath();
                        builder = builder.addFormDataPart("pictures", path, RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(path)));
                    }

                    RequestBody reqbody = builder.build();
                    String des_url = Global.server_addr + "/blog?sessionId=H6gIffhWb1iwXAa8";
                    Request request = new Request.Builder()
                            .url(des_url)
                            .method("POST", reqbody)
                            .build();
                    Response response = client.newCall(request).execute();
                    //这个是HTTP的code
                    if (response.code() == 200) {
                        System.out.println(response);
                        String resp = response.body().string();
                        JSONObject object = new JSONObject(resp);
                        int code = object.getInt("code");
                        if(code == 200) {
                            //通知UI线程把Loading图标取消掉
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("result", "ok");
                            msg.setData(data);
                            mHandler.sendMessage(msg);
                        }
                        else {
                            throw new Exception(object.getString("msg"));
                        }
                    } else {
                        String resp = response.body().string();
                        System.out.println(response);
                        throw new Exception(resp);
                    }

                } catch (Exception e) {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("result", "false");
                    data.putString("log", e.toString());
                    msg.setData(data);
                    mHandler.sendMessage(msg);
                }
            }
        //TODO
        });

    }

    private void initImageWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "../.../", selectList);
                            PictureSelector.create(DeliverActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(DeliverActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(DeliverActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            showPop();
        }
    };

    private void showPop() {
        View bottomView = View.inflate(DeliverActivity.this, R.layout.dialog_deliver_bottom, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCamera = bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_album:
                        //相册
                        PictureSelector.create(DeliverActivity.this)
                                .openGallery(PictureMimeType.ofImage())
                                .maxSelectNum(adapter.getRestSelectNum())
                                .minSelectNum(1)
                                .imageSpanCount(4)
                                .selectionMode(PictureConfig.MULTIPLE)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_camera:
                        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            // 权限未被授予
                            System.out.println("permission not granted detected.");
//                            requestCameraPermission();
                        }
                        //拍照
                        PictureSelector.create(DeliverActivity.this)
                                .openCamera(PictureMimeType.ofImage())
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_cancel:
                        //取消
                        //closePopupWindow();
                        break;
                }
                closePopupWindow();
            }
        };

        mAlbum.setOnClickListener(clickListener);
        mCamera.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    public void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调

                    images = PictureSelector.obtainMultipleResult(data);
                    selectList.addAll(images);

//                    selectList = PictureSelector.obtainMultipleResult(data);
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * 清空图片缓存，包括裁剪、压缩后的图片，避免OOM
     * 注意:必须要在上传完成后调用 必须要获取权限
     */
    private void clearCache() {
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    //清除缓存
                    PictureFileUtils.deleteCacheDirFile(DeliverActivity.this);
                } else {
                    Toast.makeText(DeliverActivity.this, getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void requestCameraPermission() {
        System.out.println("相机权限未被授予，需要申请！");
        // 相机权限未被授予，需要申请！
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // 如果访问了，但是没有被授予权限，则需要告诉用户，使用此权限的好处
            System.out.println("申请权限说明！");

//            Snackbar.make(mLayout, R.string.permission_camera_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // 这里重新申请权限
//                            ActivityCompat.requestPermissions(MainActivity.this,
//                                    new String[]{Manifest.permission.CAMERA},
//                                    REQUEST_CAMERA_CODE);
//                        }
//                    })
//                    .show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_CODE);
        } else {
            // 第一次申请，就直接申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_CODE);
        }
    }

    private class deliverHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            if(result.equals("ok")) {
//                loadingView.dismiss();
                Utils.showToastInCenter(getApplicationContext(), "发布成功！", Utils.TOAST_UI_QUEUE);
                finish();
            }
            else {
                String error = data.getString("error", "遇到未知错误！");
//                loadingView.dismiss();
                Utils.showToastInCenter(getApplicationContext(), error, Utils.TOAST_UI_QUEUE);
            }
        }
    }
}
