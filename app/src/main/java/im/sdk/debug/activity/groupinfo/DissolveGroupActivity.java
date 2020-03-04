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
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class DissolveGroupActivity extends Activity {

    private EditText mEtGroupId;
    private Button mBtDissolveGroup;
    private TextView mTvShowDissolveResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_dissolve_group);
        mEtGroupId = (EditText) findViewById(R.id.et_dissolve_gid);
        mBtDissolveGroup = (Button) findViewById(R.id.bt_dissolve_group);
        mTvShowDissolveResult = (TextView) findViewById(R.id.tv_show_dissolve_result);
    }

    private void initData() {
        mBtDissolveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEtGroupId.getText())) {
                    long gid = Long.parseLong(mEtGroupId.getText().toString());
                    JMessageClient.adminDissolveGroup(gid, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (0 == responseCode) {
                                Toast.makeText(getApplicationContext(), "解散群组成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "解散群组失败", Toast.LENGTH_SHORT).show();
                                mTvShowDissolveResult.append("responseCode:" + responseCode);
                                mTvShowDissolveResult.append("\nresponseMessage:" + responseMessage);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "请输入群组id", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
