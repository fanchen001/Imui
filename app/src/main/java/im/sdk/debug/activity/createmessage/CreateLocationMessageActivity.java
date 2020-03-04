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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;


/**
 * Created by ${chenyn} on 16/7/21.
 *
 * @desc :创建发送位置消息
 */
public class CreateLocationMessageActivity extends Activity {
    private static final String TAG = CreateLocationMessageActivity.class.getSimpleName();

    private EditText mEt_latitude;
    private EditText mEt_longtitude;
    private EditText mEt_scale;
    private EditText mEt_address;
    private EditText mEt_user_name;
    private EditText mEt_appkey;
    private Button mBt_send;
    private ProgressDialog progressDialog;

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
                String latitude = mEt_latitude.getText().toString();
                String longtitude = mEt_longtitude.getText().toString();
                String scale = mEt_scale.getText().toString();
                String address = mEt_address.getText().toString();

                String userName = mEt_user_name.getText().toString();
                String appkey = mEt_appkey.getText().toString();

                if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longtitude) || TextUtils.isEmpty(scale)) {
                    Toast.makeText(getApplicationContext(), "请输入相关参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern pattern = Pattern.compile("^[0-9]+(.[0-9])?$");
                Matcher matcherLatitude = pattern.matcher(latitude);
                Matcher matcherLongtitude = pattern.matcher(longtitude);
                if (matcherLatitude.matches() && matcherLongtitude.matches()) {
                    LocationContent content = new LocationContent(Double.parseDouble(latitude), Double.parseDouble(longtitude), Integer.parseInt(scale), address);
                    if (TextUtils.isEmpty(userName)) {
                        Toast.makeText(CreateLocationMessageActivity.this, "输入userName", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Conversation conversation = Conversation.createSingleConversation(userName, appkey);
                    Message msg = conversation.createSendMessage(content);
                    msg.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            progressDialog.dismiss();
                            if (i == 0) {
                                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "LocationContent" + ", responseCode = " + i + " ; Desc = " + s);
                            }
                        }
                    });
                    progressDialog = MsgProgressDialog.show(CreateLocationMessageActivity.this, msg);
                    JMessageClient.sendMessage(msg);
                } else {
                    Toast.makeText(CreateLocationMessageActivity.this, "参数不合法", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_location_message);
        mEt_latitude = (EditText) findViewById(R.id.et_latitude);
        mEt_longtitude = (EditText) findViewById(R.id.et_longtitude);
        mEt_scale = (EditText) findViewById(R.id.et_scale);
        mEt_address = (EditText) findViewById(R.id.et_address);
        mEt_user_name = (EditText) findViewById(R.id.et_user_name);
        mEt_appkey = (EditText) findViewById(R.id.et_appkey);
        mBt_send = (Button) findViewById(R.id.bt_send);

    }
}
