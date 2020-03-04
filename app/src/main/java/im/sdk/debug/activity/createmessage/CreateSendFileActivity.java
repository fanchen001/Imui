package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.app.ProgressDialog;
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
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/6/30.
 *
 * @desc :创建发送文件消息,有两中发送文件消息接口,不同之处在于先创建FileContent就可以通过content实现更多功能,
 * 如获取自己填写的文件名;
 *
 * sdk有提供手动下载文件接口,用法演示是A发送文件消息,B会收到通知,然后B点击通知栏会进入到手动
 * 下载界面,点击手动下载,就会将文件下载到B的本地;
 *
 * 收到MessageEvent事件之后的处理请看{@link im.sdk.debug.activity.TypeActivity}
 */
public class CreateSendFileActivity extends Activity {
    private File           mFileMp3;
    private Button         mBt_getFile;
    private Button         mBt_send;
    private EditText       mEt_username;
    private TextView       mTv_showVoiceInfo;
    private TextView       mTv_progress;
    private ProgressDialog mProgressDialog;
    private EditText       mEt_gid;
    private EditText       mEt_file_name;
    private EditText       mEt_appkey;
    private Message        mFileMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBt_getFile.setOnClickListener(new View.OnClickListener() {
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
                Toast.makeText(getApplicationContext(), "文件加载成功", Toast.LENGTH_SHORT).show();
                mTv_showVoiceInfo.append("文件已加载到 ：" + mFileMp3 + "\n");
            }
        });

        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTv_progress.setText("");
                mTv_showVoiceInfo.setText("");
                String name = mEt_username.getText().toString();
                String id = mEt_gid.getText().toString();
                String appkey = mEt_appkey.getText().toString();

                File fileMp3 = new File(Environment.getExternalStorageDirectory().getPath() + "/voice/test.mp3");
                String fileName = mEt_file_name.getText().toString();
                mFileMessage = null;
                if (!TextUtils.isEmpty(name) && TextUtils.isEmpty(id)) {
                    try {
                        mFileMessage = JMessageClient.createSingleFileMessage(name, appkey, fileMp3, fileName);
                        mFileMessage.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    mTv_showVoiceInfo.append("responseCode:" + i + "\n");
                                    mTv_showVoiceInfo.append("responseMessage:" + s + "\n");
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                                    Log.i("CreateSendFileActivity", "JMessageClient.createSingleFileMessage " + ", responseCode = " + i + " ; LoginDesc = " + s);
                                }
                            }
                        });

                        /**=================     file上传进度    =================*/
                        mFileMessage.setOnContentUploadProgressCallback(progressUpdateCallback);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(id)) {
                    try {
                        mFileMessage = JMessageClient.createGroupFileMessage(Long.parseLong(id), fileMp3, fileName);
                        mFileMessage.setOnContentUploadProgressCallback(progressUpdateCallback);
                        mFileMessage.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                                    Log.i("CreateSendFileActivity", "JMessageClient.createGroupFileMessage " + ", responseCode = " + i + " ; LoginDesc = " + s);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (mFileMessage != null) {
                    mProgressDialog = MsgProgressDialog.show(CreateSendFileActivity.this, mFileMessage);
                    JMessageClient.sendMessage(mFileMessage);
                } else {
                    Toast.makeText(CreateSendFileActivity.this, "输入参数有误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ProgressUpdateCallback progressUpdateCallback = new ProgressUpdateCallback() {
        @Override
        public void onProgressUpdate(double percent) {
            String progressStr = (int) (percent * 100) + "%";
            mTv_progress.append("上传进度：" + progressStr + "\n");
        }
    };

    private void initView() {
        setContentView(R.layout.activity_create_send_file_message);
        mBt_getFile = (Button) findViewById(R.id.bt_get_file);
        mBt_send = (Button) findViewById(R.id.bt_send_con);
        mEt_username = (EditText) findViewById(R.id.et_username);
        mTv_showVoiceInfo = (TextView) findViewById(R.id.tv_show_voice_info);
        mTv_progress = (TextView) findViewById(R.id.tv_progress);
        mEt_gid = (EditText) findViewById(R.id.et_gid);
        mEt_file_name = (EditText) findViewById(R.id.et_file_name);
        mEt_appkey = (EditText) findViewById(R.id.et_appkey);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
