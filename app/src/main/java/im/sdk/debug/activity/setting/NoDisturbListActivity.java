package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetNoDisurbListCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import com.fanchen.R;

public class NoDisturbListActivity extends Activity {

    private TextView       mTv_showNoDisturbUser;
    private TextView       mTv_showNoDisturbGroup;
    private Button         mBt_getNoDisturbList;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    /**
     * #################     调用SDK 获取当前用户设置的免打扰名单    #################
     */
    private void initData() {
        mBt_getNoDisturbList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(NoDisturbListActivity.this, "提示：", "正在获取中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                JMessageClient.getNoDisturblist(new GetNoDisurbListCallback() {
                    @Override
                    public void gotResult(int i, String s, List<UserInfo> list, List<GroupInfo> list1) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            mTv_showNoDisturbUser.setText("");
                            mTv_showNoDisturbUser.setText("被设置免打扰的用户名或群组名 : \n");
                            StringBuilder sb1 = new StringBuilder();
                            StringBuilder sb2 = new StringBuilder();
                            if (list != null) {
                                for (UserInfo user : list) {
                                    sb1.append("userName : " + user.getUserName() + " == uid : " + user.getUserID());
                                    sb1.append("\n" + "\n");
                                }
                                mTv_showNoDisturbUser.append(sb1.toString());
                            }else {
                                mTv_showNoDisturbUser.setText("没有user被设置免打扰");
                            }
                            if (list1 != null) {
                                for (GroupInfo group : list1) {
                                    sb2.append("groupName : " + group.getGroupName() + " == gid : " + group.getGroupID());
                                    sb2.append("\n" + "\n");
                                }
                                mTv_showNoDisturbGroup.append(sb2.toString());
                            }else {
                                mTv_showNoDisturbGroup.setText("没有group被设置免打扰");
                            }
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            Log.i("NoDisturbListActivity", "JMessageClient.getNoDisturbList " + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_no_disturb_list);

        mTv_showNoDisturbUser = (TextView) findViewById(R.id.tv_show_no_disturb_user);
        mBt_getNoDisturbList = (Button) findViewById(R.id.bt_get_no_disturb_list);
        mTv_showNoDisturbGroup = (TextView) findViewById(R.id.tv_show_no_disturb_group);
    }
}
