package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoListCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * 屏蔽群消息,获取屏蔽群列表,查询指定群是否被屏蔽
 */
public class BlockedGroupMsgActivity extends Activity implements View.OnClickListener {

    private EditText mEt_groupId;
    private EditText mEt_setOrDel;
    private TextView mTv_blockedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_group_message);
        initView();
    }

    private void initView() {
        mEt_groupId = (EditText) findViewById(R.id.et_group_id);
        mEt_setOrDel = (EditText) findViewById(R.id.et_set_or_del);
        mTv_blockedList = (TextView) findViewById(R.id.blocked_list);

        Button getList = (Button) findViewById(R.id.bt_get_list);
        Button bt_set = (Button) findViewById(R.id.bt_set);
        Button bt_get = (Button) findViewById(R.id.bt_get);
        bt_set.setOnClickListener(this);
        bt_get.setOnClickListener(this);
        getList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String uid = mEt_groupId.getText().toString();
        String setOrDel = mEt_setOrDel.getText().toString();
        switch (v.getId()) {
            case R.id.bt_set:
                if (TextUtils.isEmpty(setOrDel) || TextUtils.isEmpty(uid)) {
                    Toast.makeText(BlockedGroupMsgActivity.this, "请输入相关参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                final int shielding = Integer.parseInt(setOrDel);
                JMessageClient.getGroupInfo(Long.parseLong(uid), new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                        if (responseCode == 0) {
                            groupInfo.setBlockGroupMessage(shielding, new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    if (responseCode == 0) {
                                        Toast.makeText(BlockedGroupMsgActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(BlockedGroupMsgActivity.this, "设置失败"+ responseMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(BlockedGroupMsgActivity.this, "输入有误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.bt_get:
                if (TextUtils.isEmpty(uid)) {
                    Toast.makeText(BlockedGroupMsgActivity.this, "请输入相关参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                JMessageClient.getGroupInfo(Long.parseLong(uid), new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                        if (responseCode == 0) {
                            int shieldingGroupMessage = groupInfo.isGroupBlocked();
                            mTv_blockedList.setText("是否屏蔽:" + shieldingGroupMessage);
                        } else {
                            Toast.makeText(BlockedGroupMsgActivity.this, "输入有误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case R.id.bt_get_list:
                JMessageClient.getBlockedGroupsList(new GetGroupInfoListCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<GroupInfo> groupInfos) {
                        if (responseCode == 0) {
                            mTv_blockedList.setText("");
                            if (groupInfos != null) {
                                mTv_blockedList.append(groupInfos.toString());
                            }else {
                                mTv_blockedList.setText("屏蔽列表为null");
                            }
                        } else {
                            mTv_blockedList.setText("");
                            mTv_blockedList.append("responseCode = " + responseCode + "\nresponseMessage = " + responseMessage);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
