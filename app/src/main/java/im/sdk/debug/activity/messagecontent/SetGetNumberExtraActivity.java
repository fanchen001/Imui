package im.sdk.debug.activity.messagecontent;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/9.
 *
 * @desc :设置并获取附加字段number
 */
public class SetGetNumberExtraActivity extends Activity {
    private Button         mBt_setGetNumberExtra;
    private EditText       mEt_setKey;
    private EditText       mEt_setValue;
    private EditText       mEt_setUserName;
    private Conversation   mConversation;
    private MessageContent mMessageContent;
    private EditText       mEt_updateValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBt_setGetNumberExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = mEt_setKey.getText().toString();
                String value = mEt_setValue.getText().toString();
                String name = mEt_setUserName.getText().toString();
                String updateValue = mEt_updateValue.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
                    Toast.makeText(getApplicationContext(), "请输入相关参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                mConversation = JMessageClient.getSingleConversation(name);
                if (null == mConversation) {
                    mConversation = Conversation.createSingleConversation(name);
                }
                mMessageContent = new CustomContent();
                mMessageContent.setNumberExtra(key, Integer.valueOf(value));
                Message message = mConversation.createSendMessage(mMessageContent);

                if (updateValue.length() != 0) {
                    mConversation.updateMessageExtra(message, key, Integer.valueOf(updateValue));
                    JMessageClient.sendMessage(message);
                } else {
                    JMessageClient.sendMessage(message);
                }

                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Log.i("SetGetNumberExtraActivity", "MessageContent.setNumberExtra" + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.i("SetGetNumberExtraActivity", "MessageContent.setNumberExtra" + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_set_get_number_extra);
        mEt_setKey = (EditText) findViewById(R.id.et_set_key);
        mEt_setValue = (EditText) findViewById(R.id.et_set_value);
        mEt_setUserName = (EditText) findViewById(R.id.et_set_user_name);
        mEt_updateValue = (EditText) findViewById(R.id.et_update_value);

        mBt_setGetNumberExtra = (Button) findViewById(R.id.bt_set_get_number_extra);

    }

}
