package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.SilenceInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/3/29.
 *
 * @desc :获取指定群的信息
 */
public class GetGroupInfoActivity extends Activity {

    private TextView mTv_showGroupInfo;
    public EditText mEt_groupId;
    private long mGetId;
    private EditText mEt_number;
    private EditText mEt_singleMemberAppkey;
    private ProgressDialog mProgressDialog;
    private EditText mEt_singleInfo;
    private Button mBt_groupInfo, mBt_setNoDisturb,
            mBt_getGroupMembersList, mBt_getGroupMemberInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    //获取群组信息,可以根据需要自行获取其他属性
    private void initData() {
        mBt_groupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info();
                JMessageClient.getGroupInfo(mGetId, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            mTv_showGroupInfo.append("groupID = " + (int) groupInfo.getGroupID() + "\ngroupName = " + groupInfo.getGroupName() +
                                    "\ngetGroupOwner = " + groupInfo.getGroupOwner() + "\ngetNoDisturb = " + groupInfo.getNoDisturb() +
                                    "\ngetOwnerAppkey = " + groupInfo.getOwnerAppkey() + "\ngetMaxMemberCount = " + groupInfo.getMaxMemberCount() +
                                    "\ngetGroupDescription = " + groupInfo.getGroupDescription() + "\navatarMediaID = " + groupInfo.getAvatar() +
                                    "\navatarFile = " + groupInfo.getAvatarFile() + "\ngetGroupFlag = " + groupInfo.getGroupFlag() +
                                    "\ngroupType = " + groupInfo.getGroupType());
                            Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            Log.i("GetGroupInfoActivity", " JMessageClient.getGroupInfo" + ", responseCode = " + i + " ; Desc = " + s);
                        }
                    }
                });
            }
        });

//获取群组成员信息列表(userName)
        mBt_getGroupMembersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info();
                JMessageClient.getGroupInfo(mGetId, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            List<UserInfo> groupMembers = groupInfo.getGroupMembers();
                            StringBuilder sb = new StringBuilder();
                            for (UserInfo info : groupMembers) {
                                sb.append(info.getUserName());
                                sb.append("\n");
                            }
                            mTv_showGroupInfo.append("群成员信息列表(这里获取name,需要其他信息请自行获取)：\n" + sb.toString());
                            Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Log.i("GetGroupInfoActivity", "groupInfo.getGroupMembers" + ", responseCode = " + i + " ; Desc = " + s);
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

//获取单个群成员信息(userName)
        mBt_getGroupMemberInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info();
                final String appKey = mEt_singleMemberAppkey.getText().toString();
                final String name = mEt_singleInfo.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "请输入userName", Toast.LENGTH_SHORT).show();
                    return;
                }
                JMessageClient.getGroupInfo(mGetId, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            if (groupInfo.getGroupMemberInfo(name, appKey) != null) {
                                Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                                mTv_showGroupInfo.append("获取单个群成员信息(这里获取name,需要其他信息请自行获取)：" + groupInfo.getGroupMemberInfo(name, appKey).getUserName());
                            } else {
                                Toast.makeText(GetGroupInfoActivity.this, "用户不存在或不在指定群", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mProgressDialog.dismiss();
                            Log.i("GetGroupInfoActivity", "groupInfo.getGroupMemberInfo" + ", responseCode = " + i + " ; Desc = " + s);
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        /**#################    将此群设置为免打扰    #################*/
        mBt_setNoDisturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info();
                String number = mEt_number.getText().toString();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(getApplicationContext(), "请输入相关参数", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                final int num = Integer.parseInt(number);
                JMessageClient.getGroupInfo(mGetId, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            groupInfo.setNoDisturb(num, new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    if (i == 0) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
                                        Log.i("GetGroupInfoActivity", "groupInfo.setNoDisturb" + ", responseCode = " + i + " ; Desc = " + s);
                                    }
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            Log.i("GetGroupInfoActivity", "JMessageClient.getGroupInfo" + ", responseCode = " + i + " ; Desc = " + s);
                        }
                    }
                });
            }
        });

    }

    private void info() {
        mProgressDialog = ProgressDialog.show(GetGroupInfoActivity.this, "提示：", "正在加载中。。。");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mTv_showGroupInfo.setText("");
        String gid = mEt_groupId.getText().toString();
        if (TextUtils.isEmpty(gid)) {
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "请输入群id", Toast.LENGTH_SHORT).show();
            return;
        }
        mGetId = Long.parseLong(gid);
    }

    private void initView() {
        setContentView(R.layout.activity_show_groupinfo);
        mTv_showGroupInfo = (TextView) findViewById(R.id.tv_show_group_info);
        mEt_groupId = (EditText) findViewById(R.id.et_groupId);
        mEt_singleInfo = (EditText) findViewById(R.id.et_single_info);
        mBt_groupInfo = (Button) findViewById(R.id.bt_get_group_info);
        mBt_getGroupMemberInfo = (Button) findViewById(R.id.bt_get_group_member_info);
        mBt_getGroupMembersList = (Button) findViewById(R.id.bt_get_group_members_list);
        mBt_setNoDisturb = (Button) findViewById(R.id.bt_set_no_disturb);
        mEt_number = (EditText) findViewById(R.id.et_number);
        mEt_singleMemberAppkey = (EditText) findViewById(R.id.et_single_member_appkey);
    }
}



