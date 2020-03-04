package im.sdk.debug.activity.groupinfo;

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
import im.sdk.debug.activity.setting.AddRemoveBlackListActivity;

/**
 * Created by ${chenyn} on 16/3/30.
 *
 * @desc :想群组中添加或者是删除成员
 */
public class AddRemoveGroupMemberActivity extends Activity {
    private String TAG = AddRemoveBlackListActivity.class.getSimpleName();
    private EditText mEt_addGroupMembers;
    private EditText mEt_addName;
    private Button mBt_add;
    private ProgressDialog mProgressDialog = null;
    private Button mBt_remove;
    private List<String> mNames;
    private long mAddID;
    private EditText mEt_addOrDeleteAppkey;
    private String appKey;
    private EditText mEtAddReason;
    private String addReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mBt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = addOrRemove();
                if (b) {
                    Toast.makeText(getApplicationContext(), "请输入相关参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                BasicCallback callback = new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "JMessageClient.addGroupMembers " + ", responseCode = " + i + " ; Desc = " + s);
                        }
                    }
                };
                if (TextUtils.isEmpty(addReason)) {
                    JMessageClient.addGroupMembers(mAddID, appKey, mNames, callback);
                } else {
                    JMessageClient.addGroupMembers(mAddID, appKey, mNames, addReason, callback);
                }
            }
        });

        mBt_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = addOrRemove();
                if (b) {
                    Toast.makeText(getApplicationContext(), "请输入相关参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                JMessageClient.removeGroupMembers(mAddID, appKey, mNames, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "JMessageClient.removeGroupMembers " + ", responseCode = " + i + " ; Desc = " + s);
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_add_remove_group_member);

        mEt_addGroupMembers = (EditText) findViewById(R.id.et_add_group_members);
        mEt_addName = (EditText) findViewById(R.id.et_add_name);
        mBt_add = (Button) findViewById(R.id.bt_add);
        mEt_addOrDeleteAppkey = (EditText) findViewById(R.id.et_add_or_delete_appkey);

        mBt_remove = (Button) findViewById(R.id.bt_remove);
        mEtAddReason = (EditText) findViewById(R.id.et_add_reason);
    }

    private boolean addOrRemove() {
        mProgressDialog = ProgressDialog.show(AddRemoveGroupMemberActivity.this, "提示：", "正在加载中。。。");
        String id = mEt_addGroupMembers.getText().toString();
        String name = mEt_addName.getText().toString();
        String appkey = mEt_addOrDeleteAppkey.getText().toString();
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name)) {
            mProgressDialog.dismiss();
            return true;
        }
        if (TextUtils.isEmpty(appkey)) {
            appKey = RegisterAndLoginActivity.getAppKey(getApplicationContext());
        } else {
            appKey = appkey;
        }
        mAddID = Long.parseLong(id);
        mNames = new ArrayList<String>();
        mNames.add(name);
        addReason = mEtAddReason.getText().toString();
        return false;
    }

}
