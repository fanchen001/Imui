package im.sdk.debug.activity.friend;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/7/20.
 *
 * @desc :
 */
public class AddFriendActivity extends Activity {

    private EditText mEt_userName;
    private EditText mEt_appkey;
    private EditText mEt_reason;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    //发送添加好友请求
    private void initData() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEt_userName.getText().toString();
                String appkey = mEt_appkey.getText().toString();
                String reason = mEt_reason.getText().toString();
                ContactManager.sendInvitationRequest(name, appkey, reason, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "申请成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.i("AddFriendActivity", "ContactManager.sendInvitationRequest" + ", responseCode = " + i + " ; Desc = " + s);
                            Toast.makeText(getApplicationContext(), "申请失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_add_friend);
        mEt_userName = (EditText) findViewById(R.id.et_user_name);
        mEt_appkey = (EditText) findViewById(R.id.et_appkey);
        mEt_reason = (EditText) findViewById(R.id.et_reason);

        mButton = (Button) findViewById(R.id.bt_add_friend);
    }
}
