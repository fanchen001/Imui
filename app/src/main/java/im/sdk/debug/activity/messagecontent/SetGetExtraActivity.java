package im.sdk.debug.activity.messagecontent;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

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
 * @desc :设置并获取附加字段map
 */
public class SetGetExtraActivity extends Activity {

    private EditText       mEt_setKey;
    private EditText       mEt_setValue;
    private Button         mBt_setGetExtra;
    private EditText       mEt_setUserName;
    private Conversation   mConversation;
    private MessageContent mMessageContent;
    public static final String MAP_EXTRA = "map_extra";
    private EditText mEt_updateValue;
    private EditText mEt_updateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBt_setGetExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = mEt_setKey.getText().toString();
                String value = mEt_setValue.getText().toString();
                String name = mEt_setUserName.getText().toString();
                String updateValue = mEt_updateValue.getText().toString();
                String updateKey = mEt_updateKey.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "请输入userName", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> map = new HashMap<String,String>();
                map.put(key, value);

                mConversation = JMessageClient.getSingleConversation(name);
                if (null == mConversation) {
                    mConversation = Conversation.createSingleConversation(name);
                }
                mMessageContent = new CustomContent();
                mMessageContent.setExtras(map);
                Message message = mConversation.createSendMessage(mMessageContent);
                if (updateKey.length() != 0 && updateValue.length() != 0) {
                    Map<String, String> updataeMap = new HashMap<String,String>();
                    updataeMap.put(updateKey, updateValue);
                    mConversation.updateMessageExtras(message, updataeMap);
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
        setContentView(R.layout.activity_set_get_extra);

        mEt_setKey = (EditText) findViewById(R.id.et_set_key);
        mEt_setValue = (EditText) findViewById(R.id.et_set_value);
        mEt_setUserName = (EditText) findViewById(R.id.et_set_user_name);
        mEt_updateValue = (EditText) findViewById(R.id.et_update_value);
        mEt_updateKey = (EditText) findViewById(R.id.et_update_key);

        mBt_setGetExtra = (Button) findViewById(R.id.bt_set_get_extra);
    }
}
