package im.sdk.debug.activity.createmessage;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;
import im.sdk.debug.utils.FileUtils;

/**
 * Created by ${chenyn} on 16/3/29.
 *
 * @desc :创建单聊语音信息,本示例是内置了一段音频文件
 */
public class CreateSigVoiceMsgActivity extends Activity {
    private static final String TAG = CreateSigVoiceMsgActivity.class.getSimpleName();
    private Button mBt_send;
    private EditText mEt_username;
    private Conversation mConversation;
    private TextView mTv_showVoiceInfo;
    private TextView mTv_progress;
    private ProgressDialog mProgressDialog;
    private EditText mEt_appkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTv_progress.setText("");
                mTv_showVoiceInfo.setText("");
                String name = mEt_username.getText().toString();
                String appkey = mEt_appkey.getText().toString();
                File fileMp3 = new File(FileUtils.writeFileInAssetsToInternalDir(getApplicationContext(), "test.mp3"));
                if (!fileMp3.exists()) {
                    Toast.makeText(getApplicationContext(), "内置音频文件不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "请输入userName", Toast.LENGTH_SHORT).show();
                    return;
                }
                mConversation = JMessageClient.getSingleConversation(name);
                if (null == mConversation) {
                    mConversation = Conversation.createSingleConversation(name);
                }

                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(String.valueOf(fileMp3));
                    player.prepare();
                    int duration = player.getDuration();
                    final Message voiceMessage = JMessageClient.createSingleVoiceMessage(name, appkey, fileMp3, duration);
                    mProgressDialog = MsgProgressDialog.show(CreateSigVoiceMsgActivity.this, voiceMessage);
                    voiceMessage.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                mProgressDialog.dismiss();
                                mTv_showVoiceInfo.append("\nresponseCode:" + i);
                                mTv_showVoiceInfo.append("\nresponseMessage:" + s);
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "JMessageClient.createSingleVoiceMessage " + ", responseCode = " + i + " ; LoginDesc = " + s);
                            }
                        }
                    });
/**=================     voice上传进度    =================*/
                    voiceMessage.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                        @Override
                        public void onProgressUpdate(double v) {
                            String progressStr = (int) (v * 100) + "%";
                            mTv_progress.append("上传进度：" + progressStr + "\n");
                        }
                    });
                    JMessageClient.sendMessage(voiceMessage);
                    boolean callbackExists = voiceMessage.isContentUploadProgressCallbackExists();
                    boolean exists = voiceMessage.isSendCompleteCallbackExists();
                    mTv_showVoiceInfo.append("isSendCompleteCallbackExists = " + exists + "\n" +
                            "getServerMessageId = " + voiceMessage.getServerMessageId() + "\n" + "callbackExists = " + callbackExists);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_create_single_voice_message);
        mBt_send = (Button) findViewById(R.id.bt_send);
        mEt_username = (EditText) findViewById(R.id.et_username);
        mTv_showVoiceInfo = (TextView) findViewById(R.id.tv_show_voice_info);
        mTv_progress = (TextView) findViewById(R.id.tv_progress);
        mEt_appkey = (EditText) findViewById(R.id.et_appkey);
    }


}

