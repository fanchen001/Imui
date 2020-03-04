package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/6.
 *
 * @desc :
 */
public class ShowMessageActivity extends Activity {
    public static final String EXTRA_MSG_TYPE = "msg_type";

    public static final String EXTRA_IS_GROUP = "isGroup";

    public static final String EXTRA_FROM_USERNAME = "from_username";

    public static final String EXTRA_FROM_APPKEY = "from_appkey";

    public static final String EXTRA_GROUPID = "from_gid";

    public static final String EXTRA_MSGID = "msgid";

    private final String TAG = ShowMessageActivity.class.getSimpleName();
    private ImageView mIv_showImage;
    private TextView mTv_showText;
    private Button mPlay;
    private Button mDownload;

    private ContentType contentType;
    private Message message;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String msgTypeString = intent.getStringExtra(EXTRA_MSG_TYPE);
        contentType = ContentType.valueOf(msgTypeString);
        boolean isGroup = intent.getBooleanExtra(EXTRA_IS_GROUP, false);
        long gid = intent.getLongExtra(EXTRA_GROUPID, 0);
        String user = intent.getStringExtra(EXTRA_FROM_USERNAME);
        String appkey = intent.getStringExtra(EXTRA_FROM_APPKEY);
        int msgid = intent.getIntExtra(EXTRA_MSGID, 0);
        Conversation conversation;
        if (isGroup) {
            conversation = JMessageClient.getGroupConversation(gid);
            mTv_showText.append("收到来自群聊：" + gid + ",用户" + user + "的消息\n");
        } else {
            conversation = JMessageClient.getSingleConversation(user, appkey);
            mTv_showText.append("收到来自用户：" + user + "的消息\n");
        }
        if (conversation == null) {
            Toast.makeText(getApplicationContext(), "会话对象为null", Toast.LENGTH_SHORT).show();
            return;
        }
        message = conversation.getMessage(msgid);

        switch (contentType) {
            case text:
                TextContent textContent = (TextContent) message.getContent();
                mTv_showText.append("文本消息" +
                        "\n消息内容 = " + textContent.getText() + "\n附加字段 = " + textContent.getStringExtras() +
                        "\n群消息isAtMe = " + message.isAtMe() + "\n群消息isAtAll = " + message.isAtAll() + "\n");
                break;
            case image:
                mIv_showImage.setVisibility(View.VISIBLE);
                mDownload.setVisibility(View.VISIBLE);
                //缩略图在接收图片消息时由sdk自动下载。
                String thumbLocalPath = ((ImageContent) message.getContent()).getLocalThumbnailPath();
                if (!TextUtils.isEmpty(thumbLocalPath)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(thumbLocalPath);
                    mIv_showImage.setImageBitmap(bitmap);
                }
                mTv_showText.append("图片消息缩略图自动展示，需要原图请手动触发下载");
                break;

            case voice:
                mPlay.setVisibility(View.VISIBLE);
                String voiceFilePath = ((VoiceContent) message.getContent()).getLocalPath();
                //语音文件在接收语音消息时由sdk自动下载。
                mTv_showText.append("语音文件本地路径 = " + voiceFilePath + "\n");
                break;

            case location:
                LocationContent locationContent = (LocationContent) message.getContent();
                mTv_showText.append("地理位置信息：address = " + locationContent.getAddress() + "\nlatitude = " + locationContent.getLatitude() + "\nscale = " + locationContent.getScale() + "\nlongitude = " + locationContent.getLongitude());
                break;
            case file:
                mDownload.setVisibility(View.VISIBLE);
                message = conversation.getMessage(msgid);
                mTv_showText.append("文件消息，请手动触发文件下载\n");
                break;
            case custom:
                CustomContent content = (CustomContent) message.getContent();
                mTv_showText.append("自定义消息 \nall string values = " + content.getAllStringValues()
                        + "\nall number values = " + content.getAllNumberValues() + "\nall boolean values = " + content.getAllBooleanValues() + "\n");
                break;
            case eventNotification:
                EventNotificationContent eventNotificationContent = (EventNotificationContent) message.getContent();
                mTv_showText.append("群：" + gid + "的事件通知类型消息。 事件文本：" + eventNotificationContent.getEventText() + "\n");
                break;
            case prompt:
                PromptContent promptContent = (PromptContent) message.getContent();
                mTv_showText.append("提示类型消息。 提示文字：" + promptContent.getPromptText() + "\n");
                break;
            case video:
                mIv_showImage.setVisibility(View.VISIBLE);
                mDownload.setVisibility(View.VISIBLE);
                //视频缩略图在接收视频消息时由sdk自动下载
                String videoThumbPath = ((VideoContent) message.getContent()).getThumbLocalPath();
                if (!TextUtils.isEmpty(videoThumbPath)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(videoThumbPath);
                    mIv_showImage.setImageBitmap(bitmap);
                }
                mTv_showText.append("视频消息缩略图自动展示，需要视频原文件请手动触发下载");
                break;
        }


        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (contentType) {
                    case voice:
                        MediaPlayer player = new MediaPlayer();
                        VoiceContent voiceContent = (VoiceContent) message.getContent();
                        String voicePath = voiceContent.getLocalPath();
                        try {
                            if (TextUtils.isEmpty(voicePath)) {
                                Toast.makeText(getApplicationContext(), "先发送单聊语音消息", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            player.setDataSource(voicePath);
                            player.prepare();
                            player.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTv_showText.setText("");
                final ProgressDialog dialog = new ProgressDialog(ShowMessageActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("正在下载中...");
                dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelDownload();
                        dialog.dismiss();
                    }
                });
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                if (message != null) {
                    switch (contentType) {
                        case image:
                            ImageContent imageContent = (ImageContent) message.getContent();
                            imageContent.downloadOriginImage(message, new DownloadCompletionCallback() {
                                @Override
                                public void onComplete(int responseCode, String responseMessage, File file) {
                                    dialog.dismiss();
                                    if (responseCode == 0) {
                                        mTv_showText.append("原图文件下载成功，路径:" + file.getPath() + "\n");
                                        Toast.makeText(getApplicationContext(), "原图下载成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "原图下载失败", Toast.LENGTH_SHORT).show();
                                        Log.i("ShowMessageActivity", "downloadFile" + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                    }
                                }
                            });
                            break;
                        case file:
                            FileContent content = (FileContent) message.getContent();
                            message.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {
                                @Override
                                public void onProgressUpdate(double percent) {
                                    mTv_showText.append("文件下载中，进度:" + percent + "\n");
                                }
                            });
                            content.downloadFile(message, new DownloadCompletionCallback() {
                                @Override
                                public void onComplete(int i, String s, File file) {
                                    dialog.dismiss();
                                    if (i == 0) {
                                        mTv_showText.append("文件下载成功，路径:" + file.getPath() + "\n");
                                        Toast.makeText(getApplicationContext(), "文件下载成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mTv_showText.append("文件下载失败，responseCode:" + i + " ; Desc = " + s);
                                        Toast.makeText(getApplicationContext(), "文件下载失败", Toast.LENGTH_SHORT).show();
                                        Log.i("ShowMessageActivity", "downloadFile" + ", responseCode = " + i + " ; Desc = " + s);
                                    }
                                }
                            });
                            break;
                        case video:
                            VideoContent videoContent = (VideoContent) message.getContent();
                            videoContent.downloadVideoFile(message, new DownloadCompletionCallback() {
                                @Override
                                public void onComplete(int responseCode, String responseMessage, File file) {
                                    dialog.dismiss();
                                    if (responseCode == 0) {
                                        mTv_showText.append("视频文件下载成功，路径:" + file.getPath() + "\n");
                                        Toast.makeText(getApplicationContext(), "视频下载成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mTv_showText.append("视频文件下载失败，responseCode:" + responseCode + " ; Desc = " + responseCode);
                                        Toast.makeText(getApplicationContext(), "视频下载失败", Toast.LENGTH_SHORT).show();
                                        Log.i("ShowMessageActivity", "downloadFile" + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                    }
                                }
                            });
                            break;
                    }
                } else {
                    Toast.makeText(ShowMessageActivity.this, "未能获取到message对象", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cancelDownload() {
        if (message != null) {
            message.getContent().cancelDownload(message);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_show_message);
        mIv_showImage = (ImageView) findViewById(R.id.iv_show_image);
        mTv_showText = (TextView) findViewById(R.id.tv_show_text);
        mPlay = (Button) findViewById(R.id.play);
        mDownload = (Button) findViewById(R.id.download);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
