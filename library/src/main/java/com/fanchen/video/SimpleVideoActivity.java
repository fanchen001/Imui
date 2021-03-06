package com.fanchen.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.fanchen.message.commons.models.IMessage;
import com.fanchen.popup.Popup;
import com.fanchen.popup.interfaces.OnSelectListener;
import com.fanchen.ui.R;

import java.io.File;

public class SimpleVideoActivity extends Activity implements View.OnLongClickListener {

    public static final String TITLE = "title";
    public static final String PATH = "path";

    private static IMessage mIMessage = null;
    private static OnVideoLongClickListener mClickListener = null;

    private Popup.Builder mPopupWindowList ;

    public static void start(Activity activity, String path) {
        start(activity, new File(path).getName(), path);
    }

    public static void start(Activity activity, String name, String path) {
        Intent intent = new Intent(activity, SimpleVideoActivity.class);
        intent.putExtra(TITLE, name);
        intent.putExtra(PATH, path);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, IMessage message, OnVideoLongClickListener clickListener) {
        Intent intent = new Intent(activity, SimpleVideoActivity.class);
        SimpleVideoActivity.mClickListener = clickListener;
        SimpleVideoActivity.mIMessage = message;
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
        mPopupWindowList = new Popup.Builder(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_video);
        JzvdStd std = (JzvdStd) findViewById(R.id.main_video_view);
        findViewById(R.id.surface_container).setOnLongClickListener(this);
        mPopupWindowList.watchView(findViewById(R.id.surface_container));
        if (mIMessage != null) {
            std.setUp(mIMessage.getMediaFilePath(), new File(mIMessage.getText()).getName(), Jzvd.SCREEN_FULLSCREEN, JZMediaSystem.class);
        } else {
            std.setUp(getIntent().getStringExtra(PATH), getIntent().getStringExtra(TITLE), Jzvd.SCREEN_FULLSCREEN, JZMediaSystem.class);
            if (getIntent().hasExtra("cover"))
                Glide.with(this).load("cover").asBitmap().into(std.thumbImageView);
        }
        std.startVideo();
    }


    @Override
    public boolean onLongClick(View v) {
        if (mClickListener != null) {
            if (!isFinishing() && !isDestroyed()) {
                mPopupWindowList.asAttachList(new String[]{"转发给朋友", "下载视频"}, null, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        if (mIMessage !=  null && mClickListener != null) {
                            mClickListener.onVideoLongClick(position, mIMessage);
                        }
                    }
                }).show();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
//        if (mPopupWindowList != null) {
//            mPopupWindowList.hide();
//        }
        mClickListener = null;
        mIMessage = null;
        super.onDestroy();
    }

}
