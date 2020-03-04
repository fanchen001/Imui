package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;
import im.sdk.debug.utils.FileUtils;

/**
 * Created by hxhg on 2018/4/12.
 */

public class CreateVideoMsgActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = CreateVideoMsgActivity.class.getSimpleName();
    private static final String VIDEO_FILE_NAME = "jiguang_test.mp4";
    private static final String VIDEO_THUMB_FILE_NAME = "jiguang_test_img.png";

    private EditText etTargetUserName;
    private EditText etTargetAppkey;
    private EditText etTargetGid;
    private EditText etVideoFileName;
    private EditText etVideoDuration;
    private TextView tvVideoSendLog;
    private boolean sendVideoThumb = false;
    private Bitmap videoThumb;
    private File videoFile;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_msg_send);

        etTargetUserName = (EditText) findViewById(R.id.et_video_username);
        etTargetAppkey = (EditText) findViewById(R.id.et_video_appkey);
        etTargetGid = (EditText) findViewById(R.id.et_video_gid);
        etVideoFileName = (EditText) findViewById(R.id.et_video_filename);
        etVideoDuration = (EditText) findViewById(R.id.et_video_duration);
        tvVideoSendLog = (TextView) findViewById(R.id.tv_video_info_log);

        findViewById(R.id.bt_video_send).setOnClickListener(this);
        ((CheckBox) findViewById(R.id.cb_send_video_thumb)).setOnCheckedChangeListener(this);


        try {
            videoThumb = BitmapFactory.decodeStream(getApplicationContext().getAssets().open(VIDEO_THUMB_FILE_NAME));
        } catch (IOException e) {
            Log.w(TAG, "decode video thumb image failed .");
            e.printStackTrace();
        }

        //init video file
        videoFile = new File(FileUtils.getOutputPath(getApplicationContext()), VIDEO_FILE_NAME);
        if (!videoFile.exists() || videoFile.length() <= 0) {
            videoFile = new File(FileUtils.writeFileInAssetsToInternalDir(getApplicationContext(), VIDEO_FILE_NAME));
        }
    }

    @Override
    public void onClick(View v) {
        String targetUserName = etTargetUserName.getText().toString();
        String targetAppkey = etTargetAppkey.getText().toString();
        String targetGid = etTargetGid.getText().toString();
        String customVideoFileName = etVideoFileName.getText().toString();
        int duration = 0;
        try {
            duration = Integer.valueOf(etVideoDuration.getText().toString());
        } catch (Exception e) {
            //ignore
        }
        Conversation conversation;
        if (!TextUtils.isEmpty(targetUserName)) {
            conversation = JMessageClient.getSingleConversation(targetUserName, targetAppkey);
            if (null == conversation) {
                conversation = Conversation.createSingleConversation(targetUserName, targetAppkey);
            }
        } else if (!TextUtils.isEmpty(targetGid)) {
            long gid = Long.parseLong(targetGid);
            conversation = JMessageClient.getGroupConversation(gid);
            if (null == conversation) {
                conversation = Conversation.createGroupConversation(gid);
            }
        } else {
            Toast.makeText(getApplicationContext(), "未指定消息发送对象，视频消息发送失败", Toast.LENGTH_SHORT).show();
            return;
        }

        //创建video类型的content
        VideoContent content = null;
        try {
            if (sendVideoThumb) {
                content = new VideoContent(videoThumb, "png", videoFile, customVideoFileName, duration);
            } else {
                content = new VideoContent(null, null, videoFile, customVideoFileName, duration);
            }
        } catch (IOException e) {
            Log.w(TAG, "create video content failed .");
            e.printStackTrace();
        }

        if (null != content) {
            Message videoMessage = conversation.createSendMessage(content);
            videoMessage.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double percent) {
                    tvVideoSendLog.append("文件上传进度 -- " + percent + "\n");
                }
            });

            videoMessage.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage) {
                    mProgressDialog.dismiss();
                    tvVideoSendLog.append("消息发送完成 -- code = " + responseCode + " desc = " + responseMessage + "\n");
                    tvVideoSendLog.append("----------------- \n");
                }
            });
            mProgressDialog = MsgProgressDialog.show(CreateVideoMsgActivity.this, videoMessage);
            JMessageClient.sendMessage(videoMessage);
        } else {
            Toast.makeText(getApplicationContext(), "videoContent创建不成功，视频消息发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sendVideoThumb = isChecked;
    }
}
