package im.sdk.debug.activity.conversation;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/11.
 * <p/>
 * 本类用于展示{@link JMessageClient#enterSingleConversation(String, String)}和
 * {@link JMessageClient#enterSingleConversation(String, String)}接口的用法：</br>
 * 当UI层调用此接口后，收到对应的user或group发过来的消息时,通知栏将不会展示消息通知，但是消息事件还是会照常下发。
 */
public class IsShowNotifySigActivity extends Activity implements View.OnClickListener {

    private EditText mEt_userAppkey;
    private EditText mEt_username;
    private EditText mEt_groupID;
    private Button mBt_enterSingleConv;
    private Button mBt_enterGroupConv;
    private Button mBt_exitConv;
    private ListView mLv_showMessage;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);
        initView();
        initData();
    }

    private void initData() {
        String text = mEt_userAppkey.getText().toString();
        mData = new ArrayList<String>();
        if (text.length() != 0) {
            mData.add(text);
        }
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mData);
        mLv_showMessage.setAdapter(mAdapter);
        mBt_enterSingleConv.setOnClickListener(this);
        mBt_enterGroupConv.setOnClickListener(this);
        mBt_exitConv.setOnClickListener(this);
    }

    private void initView() {
        setContentView(R.layout.activity_is_show_sig_notify);
        mEt_username = (EditText) findViewById(R.id.et_username);
        mEt_userAppkey = (EditText) findViewById(R.id.et_user_appkey);
        mBt_enterSingleConv = (Button) findViewById(R.id.bt_enter_conversation);
        mEt_groupID = (EditText) findViewById(R.id.et_group_id);
        mBt_enterGroupConv = (Button) findViewById(R.id.bt_enter_group_conversation);
        mBt_exitConv = (Button) findViewById(R.id.bt_exit_conv);
        mLv_showMessage = (ListView) findViewById(R.id.lv_show_message);
    }

    /**
     * #################    处理消息事件    #################
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        final MessageContent content = msg.getContent();
        switch (msg.getContentType()) {
            case text:
                TextContent textContent = (TextContent) content;
                final String str = textContent.getText();
                mData.add(0, str);
                mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLv_showMessage.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_enter_conversation:
                final String username = mEt_username.getText().toString().trim();
                final String appkey = mEt_userAppkey.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "请输入userName", Toast.LENGTH_SHORT).show();
                    return;
                }
                JMessageClient.getUserInfo(username, appkey, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                        if (responseCode == 0) {
                            //调用enterSingleConversation之后，收到对应用户发来的消息通知栏将不会有通知提示
                            /**在调用这个接口时sdk会对未读消息数进行重置处理,但是如果过程中再次收到信息还是会累加未读消息数,用户可以通过conversation.resetUnreadCount();
                             在需要的时候进行重置处理*/
                            JMessageClient.enterSingleConversation(username, appkey);
                            Toast.makeText(getApplicationContext(), "进入会话成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(IsShowNotifySigActivity.this, "没有此会话", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.bt_enter_group_conversation:
                try {
                    final long gid = Long.parseLong(mEt_groupID.getText().toString().trim());
                    JMessageClient.getGroupInfo(gid, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (responseCode == 0) {
                                //调用enterGroupConversation之后，收到对应群组发来的消息通知栏将不会有通知提示
                                JMessageClient.enterGroupConversation(gid);
                                Toast.makeText(getApplicationContext(), "进入会话成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(IsShowNotifySigActivity.this, "没有此会话", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "输入有误", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_exit_conv:
                //调用exitConversation之后，将恢复对应的通知栏提示
                JMessageClient.exitConversation();
                Toast.makeText(getApplicationContext(), "退出所有会话成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
