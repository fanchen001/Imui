package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/3/24.
 *
 * @desc :获取当前用户的信息
 */public class InfoActivity extends Activity {

    private TextView mTv_userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }
/**=================     调用SDK 获取当前的用户信息    =================*/
    private void initData() {
        UserInfo userInfo = JMessageClient.getMyInfo();
        mTv_userInfo.setText("");
        if (userInfo != null) {
            mTv_userInfo.append("getUserName = " + userInfo.getUserName() + "\n" + "getMyUID = " + userInfo.getUserID() + "\n" +
                    "getAppKey = " + userInfo.getAppKey() + "\n" + "getAddress = " + userInfo.getAddress() + "\n" +
                    "getNoDisturb = " + userInfo.getNoDisturb() + "\n" + "getNickname = " + userInfo.getNickname() + "\n" +
                    "getAvatar = " + userInfo.getAvatar() + "\n" + "getNoteName = " + userInfo.getNotename() + "\n" +
                    "getNoteText = " + userInfo.getNoteText() + "\n" + "getRegion = " + userInfo.getRegion() + "\n" +
                    "getSignature = " + userInfo.getSignature() + "\n" + "getBirthday = " + userInfo.getBirthday() + "\n" +
                    "star = " + userInfo.getStar() + "\n" + "getGender = " + userInfo.getGender() + "\n" +
                    "getUserExtras = " + userInfo.getExtras());
        } else {
            Toast.makeText(InfoActivity.this, "获取userInfo失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_show_info);
        mTv_userInfo = (TextView) findViewById(R.id.tv_userInfo);
    }
}
