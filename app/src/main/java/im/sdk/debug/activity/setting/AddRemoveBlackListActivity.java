package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;
import im.sdk.debug.activity.RegisterAndLoginActivity;

/**
 * Created by ${chenyn} on 16/4/8.
 *
 * @desc :将用户加入黑名单,根据appKey可以实现跨应用
 */
public class AddRemoveBlackListActivity extends Activity {
    private String TAG = AddRemoveBlackListActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private EditText mEt_addUsersToBlackList;
    private Button mBt_addUsersToBlackList;
    private Button mBt_removeUsersToBlackList;
    private List<String> mList;
    private EditText mEt_addOrRemoveBlackListAppkey;
    private String mAppKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    /**
     * =================     调用SDK添加user进黑名单    =================
     */
    private void initData() {
        mList = new ArrayList<String>();
        mBt_addUsersToBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(AddRemoveBlackListActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String user = mEt_addUsersToBlackList.getText().toString();
                if (TextUtils.isEmpty(user)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "请填写username", Toast.LENGTH_SHORT).show();
                    return;
                }
                mList.clear();
                mList.add(user);
                mAppKey = mEt_addOrRemoveBlackListAppkey.getText().toString();
                if (TextUtils.isEmpty(mAppKey)) {
                    mAppKey = RegisterAndLoginActivity.getAppKey(getApplicationContext());
                }
                JMessageClient.addUsersToBlacklist(mList, mAppKey, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Log.i(TAG, "JMessageClient.addUsersToBlacklist " + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
/**=================     调用SDK移除黑名单user    =================*/
        mBt_removeUsersToBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(AddRemoveBlackListActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String user = mEt_addUsersToBlackList.getText().toString();
                if (TextUtils.isEmpty(user)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "请输入username", Toast.LENGTH_SHORT).show();
                    return;
                }
                mList.clear();
                mList.add(user);
                mAppKey = mEt_addOrRemoveBlackListAppkey.getText().toString();
                if (TextUtils.isEmpty(mAppKey)) {
                    mAppKey = RegisterAndLoginActivity.getAppKey(getApplicationContext());
                }
                JMessageClient.delUsersFromBlacklist(mList, mAppKey, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "移出成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i(TAG, "JMessageClient.delUsersFromBlacklist " + ", responseCode = " + i + " ; LoginDesc = " + s);
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "移出失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_add_users_to_black_list);
        mEt_addUsersToBlackList = (EditText) findViewById(R.id.et_add_users_to_black_list);
        mBt_addUsersToBlackList = (Button) findViewById(R.id.bt_add_users_to_black_list);
        mBt_removeUsersToBlackList = (Button) findViewById(R.id.bt_remove_users_to_black_list);
        mEt_addOrRemoveBlackListAppkey = (EditText) findViewById(R.id.et_add_or_remove_black_list_appkey);
    }
}
