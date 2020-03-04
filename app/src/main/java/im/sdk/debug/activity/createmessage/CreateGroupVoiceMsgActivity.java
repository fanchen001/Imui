package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/6.
 *
 * @desc :创建群聊语音消息,本示例是内置了一段音频文件
 */
public class CreateGroupVoiceMsgActivity extends Activity {
    private static final String TAG = CreateGroupVoiceMsgActivity.class.getSimpleName();
    private EditText mEt_groupId;
    private Button mBt_getVoice;
    private Button mBt_send;
    private TextView mTv_showVoiceInfo;
    private File mFileMp3;
    private Conversation mConversation;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        InitData();
    }

    private void InitData() {
        mBt_getVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/voice/");
                if (!file.exists()) {
                    file.mkdir();
                }
                mFileMp3 = new File(Environment.getExternalStorageDirectory().getPath() + "/voice/test.mp3");
                InputStream in = null;
                try {
                    in = getApplicationContext().getAssets().open("test.mp3");

                    OutputStream out = new FileOutputStream(mFileMp3);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "voice文件加载成功", Toast.LENGTH_SHORT).show();
                mTv_showVoiceInfo.setText("voice文件已加载到 ：" + mFileMp3);
            }
        });

        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fileMp3 = new File(Environment.getExternalStorageDirectory().getPath() + "/voice/test.mp3");
                String id = mEt_groupId.getText().toString();
                if (!fileMp3.exists()) {
                    Toast.makeText(getApplicationContext(), "先获取内置音频文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(getApplicationContext(), "请输入群组id", Toast.LENGTH_SHORT).show();
                    return;
                }
                long gid = Long.parseLong(id);
                mConversation = JMessageClient.getGroupConversation(gid);
                if (null == mConversation) {
                    mConversation = Conversation.createGroupConversation(gid);
                }
                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(String.valueOf(fileMp3));
                    player.prepare();
                    int duration = player.getDuration();
                    Message voiceMessage = JMessageClient.createGroupVoiceMessage(gid, fileMp3, duration);
                    voiceMessage.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "JMessageClient.createGroupVoiceMessage " + ", responseCode = " + i + " ; LoginDesc = " + s);
                            }
                        }
                    });
                    mProgressDialog = MsgProgressDialog.show(CreateGroupVoiceMsgActivity.this, voiceMessage);
                    JMessageClient.sendMessage(voiceMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initView() {
        setContentView(R.layout.activity_create_group_voice_message);
        mEt_groupId = (EditText) findViewById(R.id.et_group_id);
        mBt_getVoice = (Button) findViewById(R.id.bt_get_voice);
        mBt_send = (Button) findViewById(R.id.bt_send);
        mTv_showVoiceInfo = (TextView) findViewById(R.id.tv_show_voice_info);

    }
}
