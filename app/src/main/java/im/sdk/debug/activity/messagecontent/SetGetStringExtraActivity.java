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
 * @desc :设置，更新，获取附加字段string
 */
public class SetGetStringExtraActivity extends Activity {

    private EditText       mEt_setKey;
    private EditText       mEt_setValue;
    private Button         mBt_setStringExtra;
    private EditText       mEt_setUserName;
    private Conversation   mConversation;
    private MessageContent mMessageContent;
    private EditText mEt_updateValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBt_setStringExtra.setOnClickListener(new View.OnClickListener() {
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
/**=================     设置消息体中的附加字段Str    =================*/
                mMessageContent.setStringExtra(key, value);
                Message message = mConversation.createSendMessage(mMessageContent);
/**=================     更新消息体中的附加字段Str    =================*/
                if (!TextUtils.isEmpty(updateValue)) {
                    mConversation.updateMessageExtra(message, key, updateValue);
                    JMessageClient.sendMessage(message);
                } else {
                    JMessageClient.sendMessage(message);
                }
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Log.i("SetGetStringExtraActivity", "MessageContent.setStringExtra" + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.i("SetGetStringExtraActivity", "MessageContent.setStringExtra" + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void initView() {
        setContentView(R.layout.activity_set_get_string_extra);

        mEt_setKey = (EditText) findViewById(R.id.et_set_key);
        mEt_setValue = (EditText) findViewById(R.id.et_set_value);
        mEt_setUserName = (EditText) findViewById(R.id.et_set_user_name);
        mEt_updateValue = (EditText) findViewById(R.id.et_update_value);

        mBt_setStringExtra = (Button) findViewById(R.id.bt_set_string_extra);
    }

}
