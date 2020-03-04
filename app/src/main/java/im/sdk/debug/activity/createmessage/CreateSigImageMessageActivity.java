package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/1.
 *
 * @desc :创建单聊图片信息
 */
public class CreateSigImageMessageActivity extends Activity {
    private static final String TAG = CreateSigImageMessageActivity.class.getSimpleName();

    private static int RESULT_LOAD_IMAGE = 1;
    private Button mBt_localImage;
    private Button mBt_send;
    private EditText mEt_singleImageMessage;
    private Bitmap mPicture;
    private ProgressDialog mProgressDialog;
    private EditText mEt_singleImageAppkey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mBt_localImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEt_singleImageMessage.getText().toString();
                String appkey = mEt_singleImageAppkey.getText().toString();
                if (TextUtils.isEmpty(name) || mPicture == null) {
                    Toast.makeText(getApplicationContext(), "请输入相关参数并选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                Conversation conversation = JMessageClient.getSingleConversation(name, appkey);
                if (conversation == null) {
                    conversation = Conversation.createSingleConversation(name, appkey);
                }

                Message imageMessage = conversation.createSendMessage(new ImageContent(mPicture));
                mProgressDialog = MsgProgressDialog.show(CreateSigImageMessageActivity.this, imageMessage);
                imageMessage.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "JMessageClient.createSingleImageMessage " + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
                JMessageClient.sendMessage(imageMessage);
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_create_single_image_message);
        mBt_localImage = (Button) findViewById(R.id.bt_local_image);
        mBt_send = (Button) findViewById(R.id.bt_send);
        mEt_singleImageMessage = (EditText) findViewById(R.id.et_single_image_message);
        mEt_singleImageAppkey = (EditText) findViewById(R.id.et_single_image_appkey);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            try {
                ImageView imageView = (ImageView) findViewById(R.id.iv_show_image);

                mPicture = CreateGroupImageMsgActivity.getimage(getApplicationContext(), selectedImage);
                imageView.setImageBitmap(mPicture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
