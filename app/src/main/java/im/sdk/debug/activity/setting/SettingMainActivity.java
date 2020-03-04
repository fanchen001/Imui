package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.IntegerCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;
import im.sdk.debug.activity.RegisterAndLoginActivity;
import im.sdk.debug.activity.notify.NotifyTypeActivity;

/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 设置用户信息
 */
public class SettingMainActivity extends Activity implements View.OnClickListener {

    private EditText mEt_setNoDisturbGlobal;
    private TextView mTv_showNoDisturbGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_about_setting);

        mEt_setNoDisturbGlobal = (EditText) findViewById(R.id.et_set_no_disturb_global);
        mTv_showNoDisturbGlobal = (TextView) findViewById(R.id.tv_show_no_disturb_global);
        Button bt_getNoDisturbGlobal = (Button) findViewById(R.id.bt_get_no_disturb_global);
        Button bt_setNoDisturbGlobal = (Button) findViewById(R.id.bt_set_no_disturb_global);

        Button bt_getMyInfo = (Button) findViewById(R.id.bt_get_my_info);
        Button bt_logout = (Button) findViewById(R.id.bt_logout);
        Button bt_isCurrentUserPasswordValid = (Button) findViewById(R.id.bt_iscurrent_user_password_valid);
        Button bt_getUserInfo = (Button) findViewById(R.id.bt_get_user_info);
        Button bt_updateUserPassword = (Button) findViewById(R.id.bt_update_user_password);
        Button bt_updateMyInfo = (Button) findViewById(R.id.bt_update_my_info);
        Button bt_getSdkVersion = (Button) findViewById(R.id.bt_get_sdk_version);
        Button bt_getBlacklist = (Button) findViewById(R.id.bt_get_black_list);
        Button bt_addUsersToBlacklist = (Button) findViewById(R.id.bt_add_or_remove_users_to_blacklist);
        Button bt_updateUserAvatar = (Button) findViewById(R.id.bt_update_user_avatar);
        Button bt_updateUserExtras = (Button) findViewById(R.id.bt_update_user_extras);
        Button bt_getNoDisturbList = (Button) findViewById(R.id.bt_get_no_disturb_list);
        Button bt_setNotificationMode = (Button) findViewById(R.id.bt_set_notification_mode);

        bt_getMyInfo.setOnClickListener(this);
        bt_logout.setOnClickListener(this);
        bt_isCurrentUserPasswordValid.setOnClickListener(this);
        bt_getUserInfo.setOnClickListener(this);
        bt_updateUserPassword.setOnClickListener(this);
        bt_updateMyInfo.setOnClickListener(this);
        bt_getSdkVersion.setOnClickListener(this);
        bt_getBlacklist.setOnClickListener(this);
        bt_addUsersToBlacklist.setOnClickListener(this);
        bt_updateUserAvatar.setOnClickListener(this);
        bt_updateUserExtras.setOnClickListener(this);
        bt_getNoDisturbList.setOnClickListener(this);
        bt_setNotificationMode.setOnClickListener(this);
        bt_setNoDisturbGlobal.setOnClickListener(this);
        bt_getNoDisturbGlobal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        ProgressDialog mProgressDialog;
        switch (v.getId()) {
/**#################    获取当前用户信息    #################*/
            case R.id.bt_get_my_info:
                intent.setClass(SettingMainActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
/**#################    用户登出    #################*/
            case R.id.bt_logout:
                mProgressDialog = ProgressDialog.show(SettingMainActivity.this, "提示：", "正在加载中。。。");
                UserInfo myInfo = JMessageClient.getMyInfo();
                if (myInfo != null) {
                    JMessageClient.logout();
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "登出成功", Toast.LENGTH_SHORT).show();
                    intent.setClass(SettingMainActivity.this, RegisterAndLoginActivity.class);
                    setResult(8);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SettingMainActivity.this, "登出失败", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
                break;
/**#################    判断输入的字符串是否匹配当前密码    #################*/
            case R.id.bt_iscurrent_user_password_valid:
                intent.setClass(SettingMainActivity.this, AssertEqualsActivity.class);
                startActivity(intent);
                break;
/**#################    获取指定用户信息    #################*/
            case R.id.bt_get_user_info:
                intent.setClass(SettingMainActivity.this, GetUserInfoActivity.class);
                startActivity(intent);
                break;
/**#################    更新密码    #################*/
            case R.id.bt_update_user_password:
                intent.setClass(SettingMainActivity.this, UpdatePassword.class);
                startActivity(intent);
                break;
/**#################    更新用户信息    #################*/
            case R.id.bt_update_my_info:
                intent.setClass(SettingMainActivity.this, UpdateUserInfoActivity.class);
                startActivity(intent);
                break;
/**#################    获取sdk版本号    #################*/
            case R.id.bt_get_sdk_version:
                String versionString = JMessageClient.getSdkVersionString();
                Toast.makeText(getApplicationContext(), "sdk版本是 ＝ " + versionString, Toast.LENGTH_SHORT).show();
                break;
/**#################    更新头像    #################*/
            case R.id.bt_update_user_avatar:
                intent.setClass(SettingMainActivity.this, UpdateUserAvatar.class);
                startActivity(intent);
                break;
/**#################    更新用户扩展字段    #################*/
            case R.id.bt_update_user_extras:
                intent.setClass(SettingMainActivity.this, UpdateUserExtras.class);
                startActivity(intent);
                break;
/**#################    获取当前用户的黑名单列表    #################*/
            case R.id.bt_get_black_list:
                intent.setClass(SettingMainActivity.this, GetBlackListActivity.class);
                startActivity(intent);
                break;
/**#################    加入或移除黑名单    #################*/
            case R.id.bt_add_or_remove_users_to_blacklist:
                intent.setClass(SettingMainActivity.this, AddRemoveBlackListActivity.class);
                startActivity(intent);
                break;
/**#################    设置通知类型    #################*/
            case R.id.bt_set_notification_mode:
                intent.setClass(SettingMainActivity.this, NotifyTypeActivity.class);
                startActivity(intent);
                break;
/**#################    获取当前用户设置的免打扰名单    #################*/
            case R.id.bt_get_no_disturb_list:
                intent.setClass(SettingMainActivity.this, NoDisturbListActivity.class);
                startActivity(intent);
                break;
/**#################    全局免打扰设置    #################*/
            case R.id.bt_set_no_disturb_global:
                String num = mEt_setNoDisturbGlobal.getText().toString();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(getApplicationContext(), "请填写免打扰参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                JMessageClient.setNoDisturbGlobal(Integer.parseInt(num), new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                            Log.i("SettingMainActivity", "JMessageClient.setNoDisturbGlobal" + ", responseCode = " + i + " ; desc = " + s);
                        } else {
                            Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
                            Log.i("SettingMainActivity", "JMessageClient.setNoDisturbGlobal" + ", responseCode = " + i + " ; desc = " + s);

                        }
                    }
                });
                break;
            case R.id.bt_get_no_disturb_global:
                JMessageClient.getNoDisturbGlobal(new IntegerCallback() {
                    @Override
                    public void gotResult(int i, String s, Integer integer) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                            mTv_showNoDisturbGlobal.setText(integer + "");
                            Log.i("SettingMainActivity", "JMessageClient.getNoDisturbGlobal" + ", responseCode = " + i + " ; desc = " + s);
                        } else {
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            Log.i("SettingMainActivity", "JMessageClient.getNoDisturbGlobal" + ", responseCode = " + i + " ; desc = " + s);
                            mTv_showNoDisturbGlobal.setText(s);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
