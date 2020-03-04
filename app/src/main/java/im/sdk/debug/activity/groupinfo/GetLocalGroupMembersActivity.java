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

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.model.UserInfo;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/3/31.
 *
 * @desc :本地获取群成员list
 */
public class GetLocalGroupMembersActivity extends Activity {

    private EditText       mEt_getLocalGroupMember;
    private Button         mBt_getLocalGroupMember;
    private TextView       mTv_getLocalGroupMember;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mBt_getLocalGroupMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(GetLocalGroupMembersActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);

                String id = mEt_getLocalGroupMember.getText().toString();
                if (TextUtils.isEmpty(id)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "请输入id", Toast.LENGTH_SHORT).show();
                    return;
                }
                final long groupId = Long.parseLong(id);
                JMessageClient.getGroupMembers(groupId, new GetGroupMembersCallback() {
                    @Override
                    public void gotResult(int i, String s, List<UserInfo> list) {
                        if (i == 0) {
                            mTv_getLocalGroupMember.setText("");
                            mProgressDialog.dismiss();
                            StringBuilder sb = new StringBuilder();
                            for (UserInfo info : list) {
                                sb.append(info.getUserName());
                                sb.append("\n");
                            }
                            mTv_getLocalGroupMember.setText(sb.toString());
                        } else {
                            mProgressDialog.dismiss();
                            mTv_getLocalGroupMember.setText("");
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            Log.i("GetLocalGroupMembersActivity", "JMessageClient.getGroupMembers " + ", responseCode = " + i + " ; Desc = " + s);
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_get_local_group_members);

        mEt_getLocalGroupMember = (EditText) findViewById(R.id.et_get_local_group_member);
        mBt_getLocalGroupMember = (Button) findViewById(R.id.bt_get_local_group_member);
        mTv_getLocalGroupMember = (TextView) findViewById(R.id.tv_get_local_group_member);
    }
}
