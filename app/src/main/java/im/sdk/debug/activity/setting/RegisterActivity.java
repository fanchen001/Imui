package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.RegisterOptionalUserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;
import im.sdk.debug.activity.RegisterAndLoginActivity;

/**
 * Created by ${chenyn} on 16/3/22.
 *
 * @desc :注册界面
 */
public class RegisterActivity extends Activity {

    private static int RESULT_LOAD_IMAGE = 1;

    private static final String TAG = "RegisterActivity";

    private EditText mEt_userName;
    private EditText mEt_password;
    private EditText mEt_nickname;
    private EditText mEt_birthday;
    private EditText mEt_signature;
    private RadioButton mRb_male;
    private RadioButton mRb_female;
    private RadioButton mRb_unknown;
    private RadioGroup mRg_gender;
    private EditText mEt_region;
    private EditText mEt_address;
    private EditText mEt_extras_key;
    private EditText mEt_extras_value;
    private EditText mEt_avatar;
    private Button mBt_register;
    private ProgressDialog mProgressDialog = null;
    private UserInfo.Gender gender;
    private String avatarPath;
    private RegisterOptionalUserInfo registerOptionalUserInfo;
    private Map<String, String> extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    //注册功能实现
    private void initData() {
        mBt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(RegisterActivity.this, "提示：", "正在加载中。。。");

                final String userName = mEt_userName.getText().toString();
                final String password = mEt_password.getText().toString();
                if (!setRegisterOptionalParameters()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                    return;
                }

/**=================     调用SDK注册接口    =================*/
                JMessageClient.register(userName, password, registerOptionalUserInfo, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String registerDesc) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);
                            Intent result = new Intent();
                            result.putExtra(RegisterAndLoginActivity.KEY_USERNAME, userName);
                            result.putExtra(RegisterAndLoginActivity.KEY_PWD, password);
                            setResult(RESULT_OK, result);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);
                        }
                    }
                });
            }
        });

        mRg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mRb_female.getId()) {
                    gender = UserInfo.Gender.female;
                } else if (checkedId == mRb_male.getId()) {
                    gender = UserInfo.Gender.male;
                } else if (checkedId == mRb_unknown.getId()) {
                    gender = UserInfo.Gender.unknown;
                }
            }
        });

    }

    private void initView() {
        setContentView(R.layout.activity_register);
        mEt_userName = (EditText) findViewById(R.id.et_register_username);
        mEt_password = (EditText) findViewById(R.id.et_register_password);
        mEt_nickname = (EditText) findViewById(R.id.et_register_nickname);
        mEt_birthday = (EditText) findViewById(R.id.et_register_birthday);
        mEt_signature = (EditText) findViewById(R.id.et_register_signature);
        mRb_male = (RadioButton) findViewById(R.id.rb_register_male);
        mRb_female = (RadioButton) findViewById(R.id.rb_register_female);
        mRb_unknown = (RadioButton) findViewById(R.id.rb_register_unknown);
        mRg_gender = (RadioGroup) findViewById(R.id.rg_register_gender);
        mEt_region = (EditText) findViewById(R.id.et_register_region);
        mEt_address = (EditText) findViewById(R.id.et_register_address);
        mEt_avatar = (EditText) findViewById(R.id.et_register_avatar);
        mEt_extras_key = (EditText) findViewById(R.id.et_register_extras_key);
        mEt_extras_value = (EditText) findViewById(R.id.et_register_extras_value);
        mBt_register = (Button) findViewById(R.id.bt_register);
    }

    private boolean setRegisterOptionalParameters() {
        registerOptionalUserInfo = new RegisterOptionalUserInfo();
        if (!TextUtils.isEmpty(mEt_nickname.getText())) {
            registerOptionalUserInfo.setNickname(mEt_nickname.getText().toString());
        }
        if (!TextUtils.isEmpty(mEt_birthday.getText())) {
            String birthdayString = mEt_birthday.getText().toString();
            if (!TextUtils.isEmpty(birthdayString)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                try {
                    long time = sdf.parse(birthdayString).getTime();
                    registerOptionalUserInfo.setBirthday(time);
                } catch (Exception e) {
                    Log.d(TAG, "birthday format error!");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        if (!TextUtils.isEmpty(mEt_signature.getText())) {
            registerOptionalUserInfo.setSignature(mEt_signature.getText().toString());
        }
        if (gender != null) {
            registerOptionalUserInfo.setGender(gender);
        }
        if (!TextUtils.isEmpty(mEt_region.getText())) {
            registerOptionalUserInfo.setRegion(mEt_region.getText().toString());
        }
        if (!TextUtils.isEmpty(mEt_address.getText())) {
            registerOptionalUserInfo.setAddress(mEt_address.getText().toString());
        }
        if (!TextUtils.isEmpty(mEt_avatar.getText())) {
            registerOptionalUserInfo.setAvatar(mEt_avatar.getText().toString());
        }
        if (!TextUtils.isEmpty(mEt_extras_key.getText()) && !TextUtils.isEmpty(mEt_extras_value.getText())) {
            extras = new HashMap<String, String>();
            extras.put(mEt_extras_key.getText().toString(), mEt_extras_value.getText().toString());
            registerOptionalUserInfo.setExtras(extras);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (null != cursor) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                avatarPath = cursor.getString(columnIndex);
                cursor.close();
                mEt_avatar.setText(avatarPath);
            }
        }

    }
}
