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
import cn.jpush.im.android.api.callback.GetBlacklistCallback;
import cn.jpush.im.android.api.model.UserInfo;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/8.
 *
 * @desc :获取当前用户的黑名单列表
 */
public class GetBlackListActivity extends Activity {
    private ProgressDialog mProgressDialog;
    private TextView       mTv_showBlackList;
    private Button         mBt_get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }
/**=================     调用SDK获取当前用户的黑名单列表    =================*/
    private void initData() {
        mBt_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(GetBlackListActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);

                JMessageClient.getBlacklist(new GetBlacklistCallback() {
                    @Override
                    public void gotResult(int i, String s, List<UserInfo> list) {
                        if (i == 0) {
                            Toast.makeText(GetBlackListActivity.this, "获取成功", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            mTv_showBlackList.append(list.toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            Log.i("GetBlackListActivity", "JMessageClient.getBlacklist " + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_get_black_list);
        mTv_showBlackList = (TextView) findViewById(R.id.tv_show_black_list);
        mBt_get = (Button) findViewById(R.id.bt_get);
    }
}
