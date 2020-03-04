package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class ChangeGroupAdminActivity extends Activity {

    private EditText mEtGroupID;
    private EditText mEtUsername;
    private EditText mEtAppKey;
    private Button mBtChangeGroupAdmin;
    private TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_change_group_admin);
        mEtGroupID = (EditText) findViewById(R.id.et_group_id);
        mEtUsername = (EditText) findViewById(R.id.et_username);
        mEtAppKey = (EditText) findViewById(R.id.et_appKey);
        mBtChangeGroupAdmin = (Button) findViewById(R.id.bt_change_group_admin);
        mTvResult = (TextView) findViewById(R.id.tv_result);
    }

    private void initData() {
        mBtChangeGroupAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEtGroupID.getText()) && !TextUtils.isEmpty(mEtUsername.getText())) {
                    long gid = Long.parseLong(mEtGroupID.getText().toString());
                    final String username = mEtUsername.getText().toString();
                    final String appkey = mEtAppKey.getText().toString();
                    JMessageClient.getGroupInfo(gid, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (responseCode == 0) {
                                groupInfo.changeGroupAdmin(username, appkey, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            Toast.makeText(ChangeGroupAdminActivity.this, "移交群主成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mTvResult.append("responseCode: " + responseCode + ".responseMessage: " + responseMessage);
                                            Toast.makeText(ChangeGroupAdminActivity.this, "移交群主失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                mTvResult.append("responseCode: " +  responseCode + ".responseMessage: " + responseMessage);
                                Toast.makeText(ChangeGroupAdminActivity.this, "移交群主失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChangeGroupAdminActivity.this, "请输入相关参数", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
