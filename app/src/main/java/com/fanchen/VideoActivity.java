//package com.fanchen;
//
//import android.app.Activity;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.Window;
//
//import com.fanchen.video.JZMediaSystem;
//import com.fanchen.video.Jzvd;
//import com.fanchen.video.JzvdStd;
//
//import static com.fanchen.video.Jzvd.SCREEN_NORMAL;
//
//
//public class VideoActivity extends Activity {
//
//    @Override
//    public void onBackPressed() {
//        if (Jzvd.backPress()) {
//            return;
//        }
//        super.onBackPressed();
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Jzvd.releaseAllVideos();
//    }
//
//    private JzvdStd std;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_main);
//        std = (JzvdStd) findViewById(R.id.main_video_view);
//        std.setUp(getIntent().getStringExtra("path"),getIntent().getStringExtra("name"),Jzvd.SCREEN_FULLSCREEN, JZMediaSystem.class);
////        std.thumbImageView.setImageResource();
//        std.startVideo();
////
////        mVideoView.setVideoURI(Uri.parse("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"));
////        LYunVideoController controller = new LYunVideoController(this,false);
////        mVideoView.setMediaController(controller);
////        mVideoView.start();
////        controller.show();
//
//
//    }
//
//}
