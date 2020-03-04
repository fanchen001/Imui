package im.sdk.debug.activity.conversation;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/11.
 *
 * @desc :会话中message排序
 */
public class OrderMessageActivity extends Activity {
    private EditText mEt_userName;
    private Conversation mConversation;
    private EditText mEt_startGet;
    private EditText mEt_endGet;
    private EditText mEt_inset;
    private Button mBt_getMessage;
    private TextView mTv_show_message_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {

        mBt_getMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEt_userName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "请输入userName", Toast.LENGTH_SHORT).show();
                    return;
                }
                mConversation = JMessageClient.getSingleConversation(name);
                if (mConversation != null) {
                    if (mEt_startGet.getText().toString().length() != 0 && mEt_endGet.getText().toString().length() != 0) {
                        final int start = Integer.parseInt(mEt_startGet.getText().toString());
                        final int end = Integer.parseInt(mEt_endGet.getText().toString());
                        final StringBuilder sb = new StringBuilder();
                        /**=================     会话的message列表排序    =================*/
                        if (mEt_inset.getText().toString().equals("true")) {
                            List<Message> messagesFromOldest = mConversation.getMessagesFromOldest(start, end);
                            if (messagesFromOldest != null) {
                                orderMessage(sb, messagesFromOldest);
                                mTv_show_message_info.setText("");
                                mTv_show_message_info.append("getMessagesFromOldest : " + "\n" + sb.toString() + "\n");
                            } else {
                                Toast.makeText(OrderMessageActivity.this, "排序失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            List<Message> messagesFromNewest = mConversation.getMessagesFromNewest(start, end);
                            if (messagesFromNewest != null) {
                                orderMessage(sb, messagesFromNewest);
                                mTv_show_message_info.setText("");
                                mTv_show_message_info.append("getMessagesFromNewest : " + "\n" + sb.toString() + "\n");
                            } else {
                                Toast.makeText(OrderMessageActivity.this, "排序失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        mTv_show_message_info.setText("");
                        final StringBuilder sb = new StringBuilder();
                        List<Message> allMessage = mConversation.getAllMessage();
                        if (allMessage != null) {
                            orderMessage(sb, allMessage);
                            mTv_show_message_info.setText("");
                            mTv_show_message_info.append("message = \n" + sb.toString());
                        }else {
                            Toast.makeText(OrderMessageActivity.this, "未能获取到Message", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(OrderMessageActivity.this, "会话不存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //demo排序需要文本消息类型.意思是发送方创建文本消息,接收方进行排序
    private void orderMessage(StringBuilder sb, List<Message> allMessage) {
        for (Message msgList : allMessage) {
            long time = msgList.getCreateTime();
            Date date = new Date(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            String createTime = simpleDateFormat.format(date);
            sb.append("消息ID = " + msgList.getId());
            sb.append("~~~消息类型 = " + msgList.getContentType());
            sb.append("~~~创建时间 = " + createTime);
            sb.append("\n");
        }
    }

    private void initView() {
        setContentView(R.layout.activity_conversation_info);
        mEt_userName = (EditText) findViewById(R.id.et_user_name);
        mEt_startGet = (EditText) findViewById(R.id.et_start_get);
        mEt_endGet = (EditText) findViewById(R.id.et_end_get);
        mEt_inset = (EditText) findViewById(R.id.et_inset);
        mBt_getMessage = (Button) findViewById(R.id.bt_get_message);
        mTv_show_message_info = (TextView) findViewById(R.id.tv_show_message_info);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
