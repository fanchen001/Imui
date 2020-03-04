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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.SilenceInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class SetGroupMemSilenceActivity extends Activity {
    private String TAG = SetGroupMemSilenceActivity.class.getSimpleName();
    private EditText mEtGroupId;
    private EditText mEtMemberName;
    private EditText mEtMemberAppKey;
    private EditText mEtSilenceTime;
    private Button mBtKeepSilence;
    private Button mBtKeepSilenceCancel;
    private Button mBtGetGroupMemberSilence;
    private Button mBtGetGroupSilenceList;
    private ProgressDialog mProgressDialog = null;
    private TextView mTv_showSilenceInfo;
    private long mGroupID;
    private long silenceTime;
    private String mNames;
    private String mAppKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBtKeepSilence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGroupMemSilence(true);
            }
        });
        mBtKeepSilenceCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGroupMemSilence(false);
            }
        });
        mBtGetGroupMemberSilence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preparedData()) {
                    JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, GroupInfo groupInfo) {
                            if (i == 0) {
                                groupInfo.getGroupMemberSilence(mNames, mAppKey, new RequestCallback<SilenceInfo>() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage, SilenceInfo result) {
                                        mProgressDialog.dismiss();
                                        if (responseCode == 0) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append("用户:" + mNames);
                                            if (result == null) {
                                                stringBuilder.append(", 没有被禁言");
                                            } else {
                                                stringBuilder.append(", 已被禁言\n")
                                                        .append("禁言开始时间:").append(result.getSilenceStartTime()).append("\n")
                                                        .append("禁言结束时间：").append(result.getSilenceEndTime()).append("\n");
                                            }
                                            mTv_showSilenceInfo.setText(stringBuilder);
                                        } else {
                                            Log.i(TAG, "getGroupMemberSilence" + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                mProgressDialog.dismiss();
                                Log.i(TAG, "getGroupMemberSilence" + ", responseCode = " + i + " ; Desc = " + s);
                                Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "请输入相关参数", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBtGetGroupSilenceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtGroupId.getText())) {
                    Toast.makeText(getApplicationContext(), "请输入群组id", Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressDialog = ProgressDialog.show(SetGroupMemSilenceActivity.this, "提示：", "正在加载中。。。");
                mGroupID = Long.parseLong(mEtGroupId.getText().toString());
                mTv_showSilenceInfo.setText("");
                JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                        mProgressDialog.dismiss();
                        if (responseCode == 0) {
                            groupInfo.getGroupSilenceList(new RequestCallback<List<SilenceInfo>>() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage, List<SilenceInfo> result) {
                                    if (0 == responseCode) {
                                        StringBuilder sb = new StringBuilder();
                                        for (SilenceInfo silenceInfo : result) {
                                            sb.append(silenceInfo.getUserInfo().getUserName())
                                                    .append(", begin:" + silenceInfo.getSilenceStartTime())
                                                    .append(", end:" + silenceInfo.getSilenceEndTime())
                                                    .append("\n");
                                        }
                                        mTv_showSilenceInfo.append("群成员禁言信息列表(这里获取name,需要其他信息请自行获取)：\n" + sb.toString());
                                        Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.i(TAG, "getGroupSilenceList" + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                        Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Log.i(TAG, "getGroupSilenceList" + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_set_group_mem_silence);
        mEtGroupId = (EditText) findViewById(R.id.et_group_id);
        mEtMemberName = (EditText) findViewById(R.id.et_member_name);
        mEtMemberAppKey = (EditText) findViewById(R.id.et_member_appkey);
        mEtSilenceTime = (EditText) findViewById(R.id.et_silence_time);
        mBtKeepSilence = (Button) findViewById(R.id.bt_keep_silence);
        mBtKeepSilenceCancel = (Button) findViewById(R.id.bt_keep_silence_cancel);
        mBtGetGroupMemberSilence = (Button) findViewById(R.id.bt_get_group_member_silence);
        mBtGetGroupSilenceList = (Button) findViewById(R.id.bt_get_group_silence_list);
        mTv_showSilenceInfo = (TextView) findViewById(R.id.tv_show_silence_info);
    }

    private boolean preparedData() {
        mProgressDialog = ProgressDialog.show(SetGroupMemSilenceActivity.this, "提示：", "正在加载中。。。");
        if (TextUtils.isEmpty(mEtGroupId.getText()) || TextUtils.isEmpty(mEtMemberName.getText())) {
            mProgressDialog.dismiss();
            return false;
        }
        mGroupID = Long.parseLong(mEtGroupId.getText().toString());
        mNames = mEtMemberName.getText().toString();
        mAppKey = mEtMemberAppKey.getText().toString();
        return true;
    }

    private class SetSilenceCallback extends BasicCallback {
        private boolean keepSilence;
        SetSilenceCallback(boolean keepSilence) {
            this.keepSilence = keepSilence;
        }
        @Override
        public void gotResult(int responseCode, String responseMessage) {
            mProgressDialog.dismiss();
            if (0 == responseCode) {
                Toast.makeText(getApplicationContext(), keepSilence ? "设置禁言成功" : "取消禁言成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), keepSilence ? "设置禁言失败" : "取消禁言失败", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "GroupInfo.addGroupSilenceWithTime " + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
            }
        }
    }

    private void setGroupMemSilence(final boolean keepSilence) {
        if (preparedData() && (!keepSilence || !TextUtils.isEmpty(mEtSilenceTime.getText()))) {
            silenceTime = keepSilence ? 60000 * Long.parseLong(mEtSilenceTime.getText().toString()) : 0;
            JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                    if (0 == responseCode) {
                        GroupMemberInfo memberInfo = groupInfo.getGroupMember(mNames, mAppKey);
                        UserInfo userInfo = memberInfo != null ? memberInfo.getUserInfo() : null;
                        if (userInfo != null) {
                            List<UserInfo> userInfos = Collections.singletonList(userInfo);
                            SetSilenceCallback callback = new SetSilenceCallback(keepSilence);
                            if (keepSilence) {
                                groupInfo.addGroupSilenceWithTime(userInfos, silenceTime, callback);
                            } else {
                                groupInfo.delGroupSilence(userInfos, callback);
                            }
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "用户信息获取失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), keepSilence ? "设置禁言失败" : "取消禁言失败", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "getGroupInfo failed " + ", responseCode = " + responseCode + " :Desc = " + responseMessage);
                    }
                }
            });
        } else {
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "请输入相关参数", Toast.LENGTH_SHORT).show();
        }
    }
}
