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
 * Created by ${chenyn} on 16/4/10.
 *
 * @desc :设置并获取附加字段boolean
 */
public class SetGetBooleanExtraActivity extends Activity {

    private Button         mBt_setGetBooleanExtra;
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
        mBt_setGetBooleanExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = mEt_setKey.getText().toString();
                String value = mEt_setValue.getText().toString();
                String name = mEt_setUserName.getText().toString();
                String updateValue = mEt_updateValue.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "请输入userName", Toast.LENGTH_SHORT).show();
                    return;
                }
                mConversation = JMessageClient.getSingleConversation(name);
                if (null == mConversation) {
                    mConversation = Conversation.createSingleConversation(name);
                }
                mMessageContent = new CustomContent();
                if (value.equals("true")) {
                    mMessageContent.setBooleanExtra(key, true);
                } else if (value.equals("false")) {
                    mMessageContent.setBooleanExtra(key, false);
                }else {
                    Toast.makeText(getApplicationContext(), "value要是true或者false", Toast.LENGTH_SHORT).show();
                    return;
                }
                Message message = mConversation.createSendMessage(mMessageContent);
                if (!TextUtils.isEmpty(updateValue) && updateValue.equals("true")) {
                    mConversation.updateMessageExtra(message, key, true);
                    JMessageClient.sendMessage(message);
                } else if (!TextUtils.isEmpty(updateValue) && updateValue.equals("false")) {
                    mConversation.updateMessageExtra(message, key, false);
                    JMessageClient.sendMessage(message);
                } else {
                    JMessageClient.sendMessage(message);
                }
                JMessageClient.sendMessage(message);
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Log.i("SetGetBooleanExtraActivity", "MessageContent.setBooleanExtra" + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("SetGetBooleanExtraActivity", "MessageContent.setBooleanExtra" + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_set_get_boolean_extra);
        mEt_setKey = (EditText) findViewById(R.id.et_set_key);
        mEt_setValue = (EditText) findViewById(R.id.et_set_value);
        mEt_setUserName = (EditText) findViewById(R.id.et_set_user_name);
        mEt_updateValue = (EditText) findViewById(R.id.et_update_value);

        mBt_setGetBooleanExtra = (Button) findViewById(R.id.bt_set_get_boolean_extra);

    }

}
