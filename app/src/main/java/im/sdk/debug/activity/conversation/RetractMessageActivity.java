package im.sdk.debug.activity.conversation;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by hxhg on 2017/5/18.
 */

public class RetractMessageActivity extends Activity implements View.OnClickListener {

    EditText et_conv_username;
    EditText et_conv_appkey;
    EditText et_conv_gid;
    EditText et_message_local_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retract);
        initView();
    }

    private void initView() {
        et_conv_username = (EditText) findViewById(R.id.et_conv_target_username);
        et_conv_appkey = (EditText) findViewById(R.id.et_conv_target_appkey);
        et_conv_gid = (EditText) findViewById(R.id.et_conv_target_gid);
        et_message_local_id = (EditText) findViewById(R.id.et_retract_message_local_id);
        findViewById(R.id.bt_retract_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = et_conv_username.getText().toString();
        String appkey = et_conv_appkey.getText().toString();
        String gid = et_conv_gid.getText().toString();
        String msgId = et_message_local_id.getText().toString();

        Conversation conv;
        if (!TextUtils.isEmpty(username)) {
            conv = JMessageClient.getSingleConversation(username, appkey);
        } else if (!TextUtils.isEmpty(gid)) {
            long gidLong = Long.parseLong(gid);
            conv = JMessageClient.getGroupConversation(gidLong);
        } else {
            Toast.makeText(this, "请填写会话相关属性username/gid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (null == conv) {
            Toast.makeText(this, "此会话不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(msgId)) {
            try {
                int msgIdInt = Integer.parseInt(msgId);
                Message msg = conv.getMessage(msgIdInt);
                conv.retractMessage(msg, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        if (0 == responseCode) {
                            Toast.makeText(RetractMessageActivity.this, "消息撤回成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RetractMessageActivity.this, "消息撤回失败，code = " + responseCode + " \n msg = " + responseMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入正确的msg id", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "请填写被撤回消息的msgid", Toast.LENGTH_SHORT).show();
        }
    }
}
