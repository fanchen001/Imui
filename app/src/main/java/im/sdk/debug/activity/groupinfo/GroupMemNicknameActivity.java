package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.ErrorCode;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class GroupMemNicknameActivity extends Activity implements View.OnClickListener{
    private EditText mEtGroupId;
    private EditText mEtUsername;
    private EditText mEtAppKey;
    private EditText mEtNickname;
    private TextView mTvShowResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_group_mem_nickname);
        mEtGroupId = (EditText) findViewById(R.id.et_gid);
        mEtUsername = (EditText) findViewById(R.id.et_username);
        mEtAppKey = (EditText) findViewById(R.id.et_appKey);
        mEtNickname = (EditText) findViewById(R.id.et_nickname);
        mTvShowResult = (TextView) findViewById(R.id.tv_show_group_member_nickname);
    }

    private void initData() {
        findViewById(R.id.bt_get_nickname).setOnClickListener(this);
        findViewById(R.id.bt_set_nickname).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (TextUtils.isEmpty(mEtGroupId.getText())) {
            Toast.makeText(getApplicationContext(), "请输入gid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mEtUsername.getText())) {
            Toast.makeText(getApplicationContext(), "请输入username", Toast.LENGTH_SHORT).show();
            return;
        }
        long gid = Long.parseLong(mEtGroupId.getText().toString());
        String username = mEtUsername.getText().toString();
        String appkey = mEtAppKey.getText().toString();
        switch (view.getId()) {
            case R.id.bt_get_nickname:
                getNickname(gid, username, appkey);
                break;
            case R.id.bt_set_nickname:
                String nickname = mEtNickname.getText().toString();
                setNickname(gid, username, appkey, nickname);
        }
    }

    private void getNickname(long gid, String username, String appkey) {
        operateNickname(gid, username, appkey, true, null);
    }

    private void setNickname(long gid, String username, String appkey, String nickname) {
        operateNickname(gid, username, appkey, false, nickname);
    }

    private void operateNickname(long gid, final String username, final String appkey, final boolean isGet, final String nickname) {
        JMessageClient.getGroupInfo(gid, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                if (responseCode == ErrorCode.NO_ERROR) {
                    if (isGet) {
                        GroupMemberInfo memberInfo = groupInfo.getGroupMember(username, appkey);
                        if (memberInfo != null) {
                            mTvShowResult.setText("nickname:" + memberInfo.getNickName());
                        } else {
                            mTvShowResult.setText("群成员未找到");
                        }
                    } else {
                        groupInfo.setMemNickname(username, appkey, nickname, new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (responseCode == ErrorCode.NO_ERROR) {
                                    Toast.makeText(getApplicationContext(), "设置群昵称成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    mTvShowResult.setText("设置群昵称失败\ncode:" + responseCode + "\nmsg:" + responseMessage);
                                }
                            }
                        });
                    }
                } else {
                    mTvShowResult.setText("获取群信息失败");
                }
            }
        });
    }
}
