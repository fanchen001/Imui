package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/3/24.
 *
 * @desc :
 */
public class AssertEqualsActivity extends Activity {

    private EditText mEt_assertEquals;
    private Button mBt_assertEquals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mBt_assertEquals.setOnClickListener(new View.OnClickListener() {
/**=================     调用SDK方法判断输入密码是否匹配当前用户密码    =================*/
            @Override
            public void onClick(View v) {
                    String inputPwd = mEt_assertEquals.getText().toString();
                    boolean passwordValid = JMessageClient.isCurrentUserPasswordValid(inputPwd);
                    if (passwordValid) {
                        Toast.makeText(getApplicationContext(), "匹配正确", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "匹配失败", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_asserte_quals);

        mEt_assertEquals = (EditText) findViewById(R.id.et_assertEquals);
        mBt_assertEquals = (Button) findViewById(R.id.bt_assertEquals);
    }

}
