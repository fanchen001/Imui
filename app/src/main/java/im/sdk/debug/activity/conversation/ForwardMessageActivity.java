package im.sdk.debug.activity.conversation;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class ForwardMessageActivity extends Activity implements View.OnClickListener {

    EditText et_local_conv_username;
    EditText et_local_conv_appkey;
    EditText et_local_conv_gid;
    EditText et_local_message_id;
    EditText et_target_conv_username;
    EditText et_target_conv_appkey;
    EditText et_target_conv_gid;
    TextView tv_show_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_message);
        initView();
    }

    private void initView() {
        et_local_conv_username = (EditText) findViewById(R.id.et_forward_local_username);
        et_local_conv_appkey = (EditText) findViewById(R.id.et_forward_local_appkey);
        et_local_conv_gid = (EditText) findViewById(R.id.et_forward_local_gid);
        et_local_message_id = (EditText) findViewById(R.id.et_forward_message_local_id);
        et_target_conv_username = (EditText) findViewById(R.id.et_forward_target_username);
        et_target_conv_appkey = (EditText) findViewById(R.id.et_forward_target_appkey);
        et_target_conv_gid = (EditText) findViewById(R.id.et_forward_target_gid);
        tv_show_message = (TextView) findViewById(R.id.tv_show_message_info);
        findViewById(R.id.bt_forward_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String localUsername = et_local_conv_username.getText().toString();
        String localAppkey = et_local_conv_appkey.getText().toString();
        String localGid = et_local_conv_gid.getText().toString();
        String localMsgId = et_local_message_id.getText().toString();
        String targetUsername = et_target_conv_username.getText().toString();
        String targetAppkey = et_target_conv_appkey.getText().toString();
        String targetGid = et_target_conv_gid.getText().toString();

        Conversation localConv = null;
        Conversation targetConv = null;
        Message localMsg = null;

        if (!TextUtils.isEmpty(localUsername) && !TextUtils.isEmpty(localGid)) {
            Toast.makeText(ForwardMessageActivity.this, "请确定是单聊会话还是群聊会话", Toast.LENGTH_SHORT).show();
            return;
        } else if (!TextUtils.isEmpty(localUsername)) {
            localConv = JMessageClient.getSingleConversation(localUsername, localAppkey);
        } else if (!TextUtils.isEmpty(localGid)) {
            long gidLong = Long.parseLong(localGid);
            localConv = JMessageClient.getGroupConversation(gidLong);
        } else {
            Toast.makeText(ForwardMessageActivity.this, "请填写会话相关属性username/gid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (null == localConv) {
            Toast.makeText(ForwardMessageActivity.this, "本地会话不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(localMsgId)) {
            try {
                int msgIdInt = Integer.parseInt(localMsgId);
                localMsg = localConv.getMessage(msgIdInt);
            } catch (NumberFormatException e) {
                Toast.makeText(ForwardMessageActivity.this, "请输入正确的msg id", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
        } else {
            Toast.makeText(ForwardMessageActivity.this, "请填写需要转发消息的msgid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (null == localMsg) {
            Toast.makeText(ForwardMessageActivity.this, "本地消息不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(targetUsername) && !TextUtils.isEmpty(targetGid)) {
            Toast.makeText(ForwardMessageActivity.this, "请确定是单聊会话还是群聊会话", Toast.LENGTH_SHORT).show();
            return;
        } else if (!TextUtils.isEmpty(targetUsername)) {
            targetConv = JMessageClient.getSingleConversation(targetUsername, targetAppkey);
            if (null == targetConv) {
                targetConv = Conversation.createSingleConversation(targetUsername, targetAppkey);
            }
        } else if (!TextUtils.isEmpty(targetGid)) {
            long gidLong = Long.parseLong(targetGid);
            targetConv = JMessageClient.getGroupConversation(gidLong);
        } else {
            Toast.makeText(ForwardMessageActivity.this, "请填写会话相关属性username/gid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (null == targetConv) {
            Toast.makeText(this, "目标会话不存在且创建失败", Toast.LENGTH_SHORT).show();
            return;
        }

        JMessageClient.forwardMessage(localMsg, targetConv, null, new RequestCallback<Message>() {
            @Override
            public void gotResult(int responseCode, String responseMessage, Message result) {
                if (responseCode == 0) {
                    Toast.makeText(ForwardMessageActivity.this, "转发消息成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForwardMessageActivity.this, "转发消息失败, code = " + responseCode + "\n msg = "
                            + responseMessage, Toast.LENGTH_SHORT).show();
                }
                if (result != null) {
                    tv_show_message.append("转发过程中创建的消息(仅展示id和status): id = " + result.getId() + ", status = " + result.getStatus() + ".\n");
                }
            }
        });
    }
}
