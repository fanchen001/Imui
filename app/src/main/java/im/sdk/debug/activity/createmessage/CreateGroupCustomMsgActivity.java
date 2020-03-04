package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/7.
 *
 * @desc :创建群聊自定义消息
 */
public class CreateGroupCustomMsgActivity extends Activity {
    private String TAG = CreateGroupCustomMsgActivity.class.getSimpleName();
    private EditText mEt_groupId;
    private EditText mEt_key;
    private EditText mEt_value;
    private Button mBt_send;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mEt_groupId.getText().toString();
                String key = mEt_key.getText().toString();
                String value = mEt_value.getText().toString();
                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(getApplicationContext(), "请填写群组id", Toast.LENGTH_SHORT).show();
                    return;
                }
                long gid = Long.parseLong(id);

                Map<String, String> valuesMap = new HashMap<String,String>();
                valuesMap.put(key, value);

                Message customMessage = JMessageClient.createGroupCustomMessage(gid, valuesMap);
                customMessage.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        mProgressDialog.dismiss();
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "JMessageClient.createGroupCustomMessage " + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
                mProgressDialog = MsgProgressDialog.show(CreateGroupCustomMsgActivity.this, customMessage);
                JMessageClient.sendMessage(customMessage);
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_create_group_custom_message);
        mEt_groupId = (EditText) findViewById(R.id.et_group_id);
        mEt_key = (EditText) findViewById(R.id.et_key);
        mEt_value = (EditText) findViewById(R.id.et_value);
        mBt_send = (Button) findViewById(R.id.bt_send);

    }
}
