package com.fanchen.video;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.fanchen.ui.R;

public class SimpleVideoActivity extends Activity {

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
        std.setUp(getIntent().getStringExtra("path"), getIntent().getStringExtra("name"), Jzvd.SCREEN_FULLSCREEN, JZMediaSystem.class);
        if (getIntent().hasExtra("cover"))
            Glide.with(this).load("cover").asBitmap().into(std.thumbImageView);
        std.startVideo();
    }

}
