package im.sdk.debug.activity.conversation;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetReceiptDetailsCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by hxhg on 2017/8/29.
 */

public class MessageReceiptActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MessageReceiptActivity.class.getSimpleName();

    EditText et_conv_username;
    EditText et_conv_appkey;
    EditText et_conv_gid;
    EditText et_message_local_id;
    TextView tv_receipt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        et_conv_username = (EditText) findViewById(R.id.et_conv_target_username);
        et_conv_appkey = (EditText) findViewById(R.id.et_conv_target_appkey);
        et_conv_gid = (EditText) findViewById(R.id.et_conv_target_gid);
        et_message_local_id = (EditText) findViewById(R.id.et_receipt_msgid);
        tv_receipt = (TextView) findViewById(R.id.tv_receipt);
        findViewById(R.id.bt_receipt_send_report).setOnClickListener(this);
        findViewById(R.id.bt_receipt_get_info).setOnClickListener(this);
        findViewById(R.id.bt_receipt_get_details).setOnClickListener(this);
        findViewById(R.id.bt_receipt_get_all_msg).setOnClickListener(this);
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

        Message msg = null;
        if (!TextUtils.isEmpty(msgId)) {
            int msgIdInt = Integer.parseInt(msgId);
            msg = conv.getMessage(msgIdInt);
        }

        switch (v.getId()) {
            case R.id.bt_receipt_send_report:
                if (null != msg) {
                    msg.setHaveRead(new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            Toast.makeText(MessageReceiptActivity.this, "set message have read. finished. responseCode = " + responseCode + " responseMessage =" + responseMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "请填写正确的消息local msgid", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_receipt_get_info:
                if (null != msg) {
                    tv_receipt.setText("");
                    setReceiptInfo(msg);
                } else {
                    Toast.makeText(this, "请填写正确的消息local msgid", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_receipt_get_details:
                if (null != msg) {
                    tv_receipt.setText("");
                    msg.getReceiptDetails(new GetReceiptDetailsCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, List<ReceiptDetails> receiptDetails) {
                            Toast.makeText(MessageReceiptActivity.this, "get receipt details finished . code = " + responseCode + " msg = " + responseMessage, Toast.LENGTH_SHORT).show();
                            if (null != receiptDetails) {
                                for (ReceiptDetails receiptDetail : receiptDetails) {
                                    List<UserInfo> receiptUsers = receiptDetail.getReceiptList();
                                    List<UserInfo> unReceiptUsers = receiptDetail.getUnreceiptList();

                                    tv_receipt.append("已发送已读回执用户：\n");
                                    for (UserInfo userInfo : receiptUsers) {
                                        tv_receipt.append("用户名：");
                                        tv_receipt.append(userInfo.getUserName() + "\n");
                                        tv_receipt.append("appkey：");
                                        tv_receipt.append(userInfo.getAppKey() + "\n");
                                    }

                                    tv_receipt.append("\n未发送已读回执用户：\n");
                                    for (UserInfo userInfo : unReceiptUsers) {
                                        tv_receipt.append("用户名：");
                                        tv_receipt.append(userInfo.getUserName() + "\n");
                                        tv_receipt.append("appkey：");
                                        tv_receipt.append(userInfo.getAppKey() + "\n");
                                    }
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "请填写正确的消息local msgid", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_receipt_get_all_msg:
                tv_receipt.setText("");
                List<Message> msgs = conv.getAllMessage();
                for (int i = msgs.size() - 1; i > 0; i--) {
                    setReceiptInfo(msgs.get(i));
                }
                break;
        }
    }

    private void setReceiptInfo(Message msg) {
        tv_receipt.append(" local msg id = " + msg.getId() + "\n");
        tv_receipt.append(" server msg id = " + msg.getServerMessageId() + "\n");
        tv_receipt.append(" 消息发送者 = " + msg.getFromID() + "\n");
        tv_receipt.append(" 消息接受者 = " + msg.getTargetID() + "\n");
        tv_receipt.append(" 消息方向 = " + msg.getDirect() + "\n");
        tv_receipt.append(" 消息是否已读 = " + msg.haveRead() + "\n");
        tv_receipt.append(" 消息尚未发送回执的人数 = " + msg.getUnreceiptCnt() + "\n");
        tv_receipt.append(" 最后一次更新未回执人数更新时间 = " + msg.getUnreceiptMtime() + "\n");
        tv_receipt.append("\n\n");
    }
}
