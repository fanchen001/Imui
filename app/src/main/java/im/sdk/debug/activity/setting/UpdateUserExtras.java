package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * 更新用户扩展字段
 */

public class UpdateUserExtras extends Activity implements View.OnClickListener {
    private static final String TAG = "UpdateUserExtras";

    private EditText mEt_userExtraKey;
    private EditText mEt_userExtrasValue;
    private Button mBt_updateUserExtras;
    private EditText mEt_userMapKey;
    private EditText mEt_userMapValue;
    private Button mBt_updateUserExtrasFromMap;
    private TextView mTv_getUserExtras;
    private Button mBt_getUserExtras;
    private Button mBt_getUserExtra;
    private EditText mEt_extraKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mBt_updateUserExtras.setOnClickListener(this);
        mBt_updateUserExtrasFromMap.setOnClickListener(this);
        mBt_getUserExtras.setOnClickListener(this);
        mBt_getUserExtra.setOnClickListener(this);
    }

    private void initView() {
        setContentView(R.layout.activity_update_user_extras);

        mEt_userExtraKey = (EditText) findViewById(R.id.et_userExtraKey);
        mEt_userExtrasValue = (EditText) findViewById(R.id.et_userExtraValue);
        mBt_updateUserExtras = (Button) findViewById(R.id.bt_update_user_extras);
        mEt_userMapKey = (EditText) findViewById(R.id.et_userMapKey);
        mEt_userMapValue = (EditText) findViewById(R.id.et_userMapValue);
        mBt_updateUserExtrasFromMap = (Button) findViewById(R.id.bt_update_user_extras_form_map);
        mTv_getUserExtras = (TextView) findViewById(R.id.tv_getUserExtras);
        mBt_getUserExtras = (Button) findViewById(R.id.bt_get_user_extras);
        mBt_getUserExtra = (Button) findViewById(R.id.bt_get_user_extra);
        mEt_extraKey = (EditText) findViewById(R.id.et_extraKey);

    }

    @Override
    public void onClick(View v) {
        UserInfo userInfo = JMessageClient.getMyInfo();
        if (null == userInfo) {
            Log.w(TAG, "userinfo is null. update userinfo extra failed.");
            return;
        }
        switch (v.getId()) {
            case R.id.bt_update_user_extras:
                userInfo.setUserExtras(mEt_userExtraKey.getText().toString(), mEt_userExtrasValue.getText().toString());
                updateUserExtra(userInfo);
                break;
            case R.id.bt_update_user_extras_form_map:
                Map<String, String> stringMap = new HashMap<String, String>();
                stringMap.put(mEt_userMapKey.getText().toString(), mEt_userMapValue.getText().toString());
                userInfo.setUserExtras(stringMap);
                updateUserExtra(userInfo);
                break;
            case R.id.bt_get_user_extras:
                mTv_getUserExtras.setText("");
                mTv_getUserExtras.append("userExtras = " + userInfo.getExtras());
                break;
            case R.id.bt_get_user_extra:
                mTv_getUserExtras.setText("");
                mTv_getUserExtras.append("userExtra: " + "value = " + userInfo.getExtra(mEt_extraKey.getText().toString()));
                break;
            default:
                break;
        }
    }

    private void updateUserExtra(UserInfo userInfo) {
        JMessageClient.updateMyInfo(UserInfo.Field.extras, userInfo, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                Log.d(TAG, "responseCode: " + responseCode + "responseMessage: " + responseMessage);
                String result = 0 == responseCode ? "更新成功" : "更新失败";
                Toast.makeText(UpdateUserExtras.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
