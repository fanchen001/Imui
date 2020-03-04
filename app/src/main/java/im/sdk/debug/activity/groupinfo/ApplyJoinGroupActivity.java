package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class ApplyJoinGroupActivity extends Activity {
    private static final String TAG = "ApplyJoinGroupActivity";

    private EditText mEt_groupID;
    private EditText mEt_apply_reason;
    private Button mBt_applyJoinGroup;
    private ProgressDialog mProgressDialog;
    private String reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBt_applyJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEt_groupID.getText())) {
                    Toast.makeText(getApplicationContext(), "请输入gid", Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressDialog = ProgressDialog.show(ApplyJoinGroupActivity.this, "提示：", "正在加载中。。。");
                if (!TextUtils.isEmpty(mEt_apply_reason.getText())) {
                    reason = mEt_apply_reason.getText().toString();
                }
                JMessageClient.applyJoinGroup(Long.parseLong(mEt_groupID.getText().toString()), reason, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        mProgressDialog.dismiss();
                        if (responseCode == 0) {
                            Toast.makeText(ApplyJoinGroupActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "apply failed. code :" + responseCode + " msg : " + responseMessage);
                            Toast.makeText(ApplyJoinGroupActivity.this, "申请失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_apply_join_group);

        mEt_groupID = (EditText) findViewById(R.id.et_group_id);
        mEt_apply_reason = (EditText) findViewById(R.id.et_apply_reason);
        mBt_applyJoinGroup = (Button) findViewById(R.id.bt_apply_join_group);
    }
}
