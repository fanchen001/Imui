package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;
import im.sdk.debug.activity.RegisterAndLoginActivity;

/**
 * Created by ${chenyn} on 16/3/24.
 *
 * @desc :获取指定用户的信息.根据不同的appkey可跨应用获取;将此用户设置为免打扰
 */
public class GetUserInfoActivity extends Activity {


    private EditText mEd_inputUserName;
    private Button   mBt_getUserInfo;
    private TextView mTv_getUserInfo;
    private Button   mBt_setNoDisturb;
    private EditText mEt_number;
    private EditText mEt_appkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {

        mBt_getUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTv_getUserInfo.setText("");
                final String userName = mEd_inputUserName.getText().toString();
                String appKey = mEt_appkey.getText().toString();
                    if (TextUtils.isEmpty(appKey)) {
                        appKey = RegisterAndLoginActivity.getAppKey(getApplicationContext());

                    }
                    /**=================     调用SDK接口获取指定用户信息    =================*/
                    JMessageClient.getUserInfo(userName, appKey, new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String getUserInfoDesc, UserInfo userInfo) {
                            if (responseCode == 0) {
                                Log.i("GetUserInfoActivity", "JMessageClient.getUserInfo" + ", responseCode = " + responseCode + " ; desc = " + getUserInfoDesc);
                                Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                                mTv_getUserInfo.setText("");
                                mTv_getUserInfo.append("getUserName = " + userInfo.getUserName() + "\n" + "getMyUID = " + userInfo.getUserID() + "\n" +
                                        "getAppKey = " + userInfo.getAppKey() + "\n" + "getAddress = " + userInfo.getAddress() + "\n" +
                                        "getNoDisturb = " + userInfo.getNoDisturb() + "\n" + "getNickname = " + userInfo.getNickname() + "\n" +
                                        "getAvatar = " + userInfo.getAvatar() + "\n" + "getNoteName = " + userInfo.getNotename() + "\n" +
                                        "getNoteText = " + userInfo.getNoteText() + "\n" + "getRegion = " + userInfo.getRegion() + "\n" +
                                        "getSignature = " + userInfo.getSignature() + "\n" + "getBirthday = " + userInfo.getBirthday() + "\n" +
                                        "star = " + userInfo.getStar() + "\n" + "isInBlackList = " + userInfo.getBlacklist() + "\n" +
                                        "getGender = " + userInfo.getGender() + "\n" + "isFriend = " + userInfo.isFriend() + "\n" +
                                        "getUserExtras = " + userInfo.getExtras());
                            } else {
                                Log.i("GetUserInfoActivity", "JMessageClient.getUserInfo" + ", responseCode = " + responseCode + " ; desc = " + getUserInfoDesc);
                                mEd_inputUserName.setText("");
                                mTv_getUserInfo.append("您查询的用户:[ " + userName + " ]可能不存在，请查证后重试");
                            }
                        }
                    });
            }
        });
/**=================     调用SDK接口 将此用户设置为免打扰    =================*/
        mBt_setNoDisturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEd_inputUserName.getText().toString();
                final String num = mEt_number.getText().toString();
                if (!TextUtils.isEmpty(num)) {
                    String appKey = mEt_appkey.getText().toString();
                    if (TextUtils.isEmpty(appKey)) {
                        appKey = RegisterAndLoginActivity.getAppKey(getApplicationContext());
                    }
                    JMessageClient.getUserInfo(name, appKey, new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if (i == 0) {
                                Log.i("GetUserInfoActivity", "JMessageClient.getUserInfo" + ", responseCode = " + i + " ; desc = " + s);
                                userInfo.setNoDisturb(Integer.parseInt(num), new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i == 0) {
                                            Log.i("GetUserInfoActivity", "userInfo.setNoDisturb" + ", responseCode = " + i + " ; desc = " + s);
                                            Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i("GetUserInfoActivity", "userInfo.setNoDisturb" + ", responseCode = " + i + " ; desc = " + s);
                                            Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                                Log.i("GetUserInfoActivity", "JMessageClient.getUserInfo" + ", responseCode = " + i + " ; desc = " + s);

                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "请设置免打扰参数", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_get_user_info);

        mEd_inputUserName = (EditText) findViewById(R.id.et_inputUserName);
        mBt_getUserInfo = (Button) findViewById(R.id.bt_getUserInfo);
        mTv_getUserInfo = (TextView) findViewById(R.id.tv_getUserInfo);
        mBt_setNoDisturb = (Button) findViewById(R.id.bt_set_no_disturb);
        mEt_number = (EditText) findViewById(R.id.et_number);
        mEt_appkey = (EditText) findViewById(R.id.et_appkey);
    }
}
