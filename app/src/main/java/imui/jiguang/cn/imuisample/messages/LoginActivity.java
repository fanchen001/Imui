package imui.jiguang.cn.imuisample.messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fanchen.R;
import com.fanchen.base.BaseIActivity;
import com.fanchen.crash.ExceptionHandler;
import com.fanchen.crash.HookCrash;
import com.fanchen.location.utils.CommonUtils;
import com.fanchen.zxing.CaptureActivity;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class LoginActivity extends BaseIActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        HookCrash.install(getApplication(), new ExceptionHandler() {

            @Override
            protected void onUncaughtExceptionHappened(Thread thread, Throwable throwable) {

            }

            @Override
            protected void onBandageExceptionHappened(Throwable throwable) {

            }

            @Override
            protected void onEnterSafeMode() {

            }
        });
    }

    public void onClick(View v) {
        EditText e1 = findViewById(R.id.ed_login_username);
        EditText e2 = findViewById(R.id.ed_login_password);
       final EditText e3 = findViewById(R.id.ed_username);
        if (TextUtils.isEmpty(e1.getText().toString()) || TextUtils.isEmpty(e2.getText().toString())) {
            CommonUtils.showToastShort(this, "用户名密码不能为空");

        }

        if (R.id.bt_login == v.getId()) {
            startActivity(new Intent(this, CaptureActivity.class));
//            throw new RuntimeException("用户名密码不能为空");
//            CommonUtils.showDialogNumal(this, "正在登录...");
//            JMessageClient.login(e1.getText().toString(), e2.getText().toString(), new BasicCallback() {
//
//                @Override
//                public void gotResult(int i, String s) {
//                    CommonUtils.cencelDialog();
//                    if (i == 0) {
//                        CommonUtils.showToastShort(LoginActivity.this, "登录成功");
//                        Intent intent = new Intent(LoginActivity.this, MessageListActivity.class);
//                        intent.putExtra("to",e3.getText().toString());
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        CommonUtils.showToastShort(LoginActivity.this, "登录失败，请检查用户名和密码");
//                    }
//                }
//
//            });

        } else if (R.id.bt_goto_regester == v.getId()) {
            CommonUtils.showDialogNumal(this, "正在注册...");
            JMessageClient.register(e1.getText().toString(), e2.getText().toString(), new BasicCallback() {

                @Override
                public void gotResult(int i, String s) {
                    CommonUtils.cencelDialog();
                    if (i == 0) {
                        CommonUtils.showToastShort(LoginActivity.this, "注册成功");
                    } else {
                        CommonUtils.showToastShort(LoginActivity.this, "注册失败，用户已存在，或账号密码格式不支持");
                    }
                }

            });
        }
    }
}
