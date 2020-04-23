package com.fanchen.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.fanchen.picture.view.PopupWindowList;
import com.fanchen.ui.R;

import java.io.File;
import java.util.Arrays;

public class SimpleVideoActivity extends Activity implements View.OnLongClickListener, AdapterView.OnItemClickListener {

    public static final String TITLE = "title";
    public static final String PATH = "path";

    private static OnVideoLongClickListener mClickListener = null;

    private PopupWindowList mPopupWindowList = null;

    public static void start(Activity activity, String name, String path) {
        start(activity, name, path, null);
    }

    public static void start(Activity activity, String path, OnVideoLongClickListener clickListener) {
        start(activity, new File(path).getName(), path, clickListener);
    }

    public static void start(Activity activity, String name, String path, OnVideoLongClickListener clickListener) {
        Intent intent = new Intent(activity, SimpleVideoActivity.class);
        intent.putExtra(TITLE, name);
        intent.putExtra(PATH, path);
        SimpleVideoActivity.mClickListener = clickListener;
        activity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPopupWindowList = new PopupWindowList(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_video);
        JzvdStd std = (JzvdStd) findViewById(R.id.main_video_view);
        findViewById(R.id.surface_container).setOnLongClickListener(this);
        std.setUp(getIntent().getStringExtra(PATH), getIntent().getStringExtra(TITLE), Jzvd.SCREEN_FULLSCREEN, JZMediaSystem.class);
        if (getIntent().hasExtra("cover"))
            Glide.with(this).load("cover").asBitmap().into(std.thumbImageView);
        std.startVideo();
    }


    @Override
    public boolean onLongClick(View v) {
        if (mClickListener != null) {
            if (!isFinishing() && !isDestroyed() && !mPopupWindowList.isShowing()) {
                mPopupWindowList.setAnchorView(v);
                mPopupWindowList.setItemData(Arrays.asList("转发给朋友", "下载视频"));
                mPopupWindowList.setModal(true);
                mPopupWindowList.setOnItemClickListener(this);
                mPopupWindowList.show();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClickListener = null;
        if(mPopupWindowList != null){
            mPopupWindowList.hide();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mClickListener.onVideoLongClick(position, getIntent().getStringExtra(TITLE), getIntent().getStringExtra(PATH));
    }
}
