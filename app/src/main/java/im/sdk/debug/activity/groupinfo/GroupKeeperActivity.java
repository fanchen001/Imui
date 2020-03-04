package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class GroupKeeperActivity extends Activity implements View.OnClickListener{
    private EditText mEtGroupId;
    private EditText mEtUsername;
    private EditText mEtAppKey;
    private Button mBtAddGroupKeeper;
    private Button mBtRemoveGroupKeeper;
    private Button mBtGetGroupKeeper;
    private TextView mTvGroupKeeper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_group_keeper);
        mEtGroupId = (EditText) findViewById(R.id.et_gid);
        mEtUsername = (EditText) findViewById(R.id.et_keeper_username);
        mEtAppKey = (EditText) findViewById(R.id.et_keeper_appKey);
        mBtAddGroupKeeper = (Button) findViewById(R.id.bt_add_group_keeper);
        mBtRemoveGroupKeeper = (Button) findViewById(R.id.bt_remove_group_keeper);
        mBtGetGroupKeeper = (Button) findViewById(R.id.bt_get_group_keeper);
        mTvGroupKeeper = (TextView) findViewById(R.id.tv_show_group_keeper);
    }

    private void initData() {
        mBtAddGroupKeeper.setOnClickListener(this);
        mBtRemoveGroupKeeper.setOnClickListener(this);
        mBtGetGroupKeeper.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        if (TextUtils.isEmpty(mEtGroupId.getText())) {
            Toast.makeText(getApplicationContext(), "请输入gid", Toast.LENGTH_SHORT).show();
            return;
        }
        long gid = Long.parseLong(mEtGroupId.getText().toString());
        JMessageClient.getGroupInfo(gid, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                if (0 == responseCode) {
                    switch (v.getId()) {
                        case R.id.bt_add_group_keeper:
                            setGroupKeeper(groupInfo, true);
                            break;
                        case R.id.bt_remove_group_keeper:
                            setGroupKeeper(groupInfo, false);
                            break;
                        case R.id.bt_get_group_keeper:
                            List<UserInfo> userInfos = groupInfo.getGroupKeepers();
                            String result = "这里只展示username:";
                            for (UserInfo userInfo : userInfos) {
                                if (userInfo != null) {
                                    result += "\n" + userInfo.getUserName();
                                }
                            }
                            mTvGroupKeeper.setText(result);
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "找不到群信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setGroupKeeper(GroupInfo groupInfo, boolean isAdd) {
        if (!TextUtils.isEmpty(mEtUsername.getText())) {
            String username = mEtUsername.getText().toString();
            String appKey = mEtAppKey.getText().toString();
            UserInfo userInfo = groupInfo.getGroupMemberInfo(username, appKey);
            if (userInfo != null) {
                List<UserInfo> userInfos = new ArrayList<UserInfo>();
                userInfos.add(userInfo);
                if (isAdd) {
                    groupInfo.addGroupKeeper(userInfos, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                Toast.makeText(getApplicationContext(), "添加管理员成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "添加管理员失败", Toast.LENGTH_SHORT).show();
                                mTvGroupKeeper.setText("responseCode:" + responseCode + "\nresponseMessage:" + responseMessage);
                            }
                        }
                    });
                } else {
                    groupInfo.removeGroupKeeper(userInfos, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                Toast.makeText(getApplicationContext(), "取消管理员成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "取消管理员失败", Toast.LENGTH_SHORT).show();
                                mTvGroupKeeper.setText("responseCode:" + responseCode + "\nresponseMessage:" + responseMessage);
                            }
                        }
                    });
                }
            } else {
                mTvGroupKeeper.setText("can not find group member info with given username and appKey");
            }
        } else {
            Toast.makeText(getApplicationContext(), "请输入username", Toast.LENGTH_SHORT).show();
        }
    }

}
