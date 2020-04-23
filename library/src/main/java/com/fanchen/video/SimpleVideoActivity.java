package com.fanchen.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.fanchen.ui.R;

public class SimpleVideoActivity extends Activity implements View.OnLongClickListener {

    public static final String TITLE = "title";
    public static final String PATH = "path";

    private static OnVideoLongClickListener mClickListener = null;

    public static void start(Activity activity,String name,String path){
        start(activity,name,path,null);
    }

    public static void start(Activity activity,String name,String path,OnVideoLongClickListener clickListener){
        Intent intent = new Intent(activity, SimpleVideoActivity.class);
        intent.putExtra(TITLE,name);
        intent.putExtra(PATH,path);
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_video);
        JzvdStd std = (JzvdStd) findViewById(R.id.main_video_view);
        std.setOnLongClickListener(this);
        std.setUp(getIntent().getStringExtra(PATH), getIntent().getStringExtra(TITLE), Jzvd.SCREEN_FULLSCREEN, JZMediaSystem.class);
        if (getIntent().hasExtra("cover"))
            Glide.with(this).load("cover").asBitmap().into(std.thumbImageView);
        std.startVideo();
    }

    @Override
    public boolean onLongClick(View v) {
        if(mClickListener != null)
            return mClickListener.onVideoLongClick( getIntent().getStringExtra(TITLE),getIntent().getStringExtra(PATH));
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClickListener = null;
    }

}
