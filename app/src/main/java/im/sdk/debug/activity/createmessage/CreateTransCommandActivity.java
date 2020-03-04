package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.PlatformType;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class CreateTransCommandActivity extends Activity {

    private EditText mEtTargetUsername;
    private EditText mEtTargetAppKey;
    private EditText mEtTargetGid;
    private EditText mEtCmd;
    private Button mBtSendTransCommand;
    private RadioGroup mRgPlatform;
    private PlatformType platformType;
    private Button mBtSendCrossDevice;

    private BasicCallback basicCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        basicCallback = new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                if (0 == responseCode) {
                    Toast.makeText(CreateTransCommandActivity.this, "消息透传成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateTransCommandActivity.this, "消息透传失败, code = " + responseCode +
                            "\nmsg = " + responseMessage, Toast.LENGTH_SHORT).show();
                }
            }
        };
        mBtSendTransCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target_username = mEtTargetUsername.getText().toString();
                String target_appKey = mEtTargetAppKey.getText().toString();
                String target_gid = mEtTargetGid.getText().toString();
                String cmd = mEtCmd.getText().toString();

                if (TextUtils.isEmpty(cmd)) {
                    Toast.makeText(CreateTransCommandActivity.this, "请输入透传内容", Toast.LENGTH_SHORT).show();
                } else {
                    if (!TextUtils.isEmpty(target_username) && !TextUtils.isEmpty(target_gid)) {
                        Toast.makeText(CreateTransCommandActivity.this, "请确定消息透传类型", Toast.LENGTH_SHORT).show();
                    } else if (!TextUtils.isEmpty(target_username)) {
                        JMessageClient.sendSingleTransCommand(target_username, target_appKey, cmd, basicCallback);
                    } else if (!TextUtils.isEmpty(target_gid)) {
                        long gid = Long.parseLong(target_gid);
                        JMessageClient.sendGroupTransCommand(gid, cmd, basicCallback);
                    } else {
                        Toast.makeText(CreateTransCommandActivity.this, "请填写会话相关属性username/gid", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBtSendCrossDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mEtCmd.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(CreateTransCommandActivity.this, "请输入透传内容", Toast.LENGTH_SHORT).show();
                } else if (platformType == null) {
                    Toast.makeText(CreateTransCommandActivity.this, "请选择平台类型", Toast.LENGTH_SHORT).show();
                } else {
                    JMessageClient.sendCrossDeviceTransCommand(platformType, msg, basicCallback);
                }
            }
        });

        mRgPlatform.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_android:
                        platformType = PlatformType.android;
                        break;
                    case R.id.rb_ios:
                        platformType = PlatformType.ios;
                        break;
                    case R.id.rb_windows:
                        platformType = PlatformType.windows;
                        break;
                    case R.id.rb_web:
                        platformType = PlatformType.web;
                        break;
                    case R.id.rb_all:
                        platformType = PlatformType.all;
                        break;
                    default:
                        platformType = null;
                }
            }
        });

    }

    private void initView() {
        setContentView(R.layout.activity_create_trans_command);
        mEtTargetUsername = (EditText) findViewById(R.id.et_target_username);
        mEtTargetAppKey = (EditText) findViewById(R.id.et_target_appKey);
        mEtTargetGid = (EditText) findViewById(R.id.et_target_gid);
        mEtCmd = (EditText) findViewById(R.id.et_cmd);
        mBtSendTransCommand = (Button) findViewById(R.id.bt_send_trans_command);
        mRgPlatform = (RadioGroup) findViewById(R.id.rg_platform);
        mBtSendCrossDevice = (Button) findViewById(R.id.bt_send_cross_device_trans_command);
    }
}
