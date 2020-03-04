package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/3/29.
 *
 * @desc :创建消息引导界面
 */
public class CreateMessageActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_create_message);

        findViewById(R.id.bt_create_single_text_message).setOnClickListener(this);
        findViewById(R.id.bt_create_group_text_message).setOnClickListener(this);
        findViewById(R.id.bt_create_single_image_message).setOnClickListener(this);
        findViewById(R.id.bt_create_single_custom_message).setOnClickListener(this);
        findViewById(R.id.bt_create_group_image_message).setOnClickListener(this);
        findViewById(R.id.bt_create_single_voice_message).setOnClickListener(this);
        findViewById(R.id.bt_create_group_custom_message).setOnClickListener(this);
        findViewById(R.id.bt_create_group_voice_message).setOnClickListener(this);
        findViewById(R.id.bt_create_send_file_message).setOnClickListener(this);
        findViewById(R.id.bt_create_send_video_message).setOnClickListener(this);
        findViewById(R.id.bt_send_location).setOnClickListener(this);
        findViewById(R.id.bt_send_trans_command).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt_create_single_text_message://创建单聊文本信息
                intent.setClass(getApplicationContext(), CreateSigTextMessageActivity.class);
                break;
            case R.id.bt_create_group_text_message://创建群聊文本信息
                intent.setClass(getApplicationContext(), CreateGroupTextMsgActivity.class);
                break;
            case R.id.bt_create_single_image_message://创建单聊图片信息
                intent.setClass(getApplicationContext(), CreateSigImageMessageActivity.class);
                break;
            case R.id.bt_create_single_custom_message://创建单聊自定义信息
                intent.setClass(getApplicationContext(), CreateSigCustomMsgActivity.class);
                break;
            case R.id.bt_create_group_image_message://创建群聊图片信息
                intent.setClass(getApplicationContext(), CreateGroupImageMsgActivity.class);
                break;
            case R.id.bt_create_single_voice_message://创建单聊语音信息
                intent.setClass(getApplicationContext(), CreateSigVoiceMsgActivity.class);
                break;
            case R.id.bt_create_group_voice_message://创建群聊语音信息
                intent.setClass(getApplicationContext(), CreateGroupVoiceMsgActivity.class);
                break;
            case R.id.bt_create_group_custom_message://创建群聊自定义信息
                intent.setClass(getApplicationContext(), CreateGroupCustomMsgActivity.class);
                break;
            case R.id.bt_create_send_file_message://创建发送文件消息
                intent.setClass(getApplicationContext(), CreateSendFileActivity.class);
                break;
            case R.id.bt_create_send_video_message://创建视频消息
                intent.setClass(getApplicationContext(), CreateVideoMsgActivity.class);
                break;
            case R.id.bt_send_location://创建发送位置消息
                intent.setClass(getApplicationContext(), CreateLocationMessageActivity.class);
                break;
            case R.id.bt_send_trans_command://消息透传
                intent.setClass(getApplicationContext(), CreateTransCommandActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
