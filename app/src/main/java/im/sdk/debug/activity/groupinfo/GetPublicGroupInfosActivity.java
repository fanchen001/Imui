package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupBasicInfo;
import com.fanchen.R;

public class GetPublicGroupInfosActivity extends Activity {

    private EditText etAppKey;

    private EditText etGetStart;

    private EditText etGetCount;

    private TextView tvDisplay;

    private ScrollView scrollView;

    private Button btGetPublicGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_get_public_group_infos);
        etAppKey = (EditText) findViewById(R.id.et_public_group_appkey);
        etGetStart = (EditText) findViewById(R.id.et_public_group_start);
        etGetCount = (EditText) findViewById(R.id.et_public_group_count);
        tvDisplay = (TextView) findViewById(R.id.tv_public_group_display);
        scrollView = (ScrollView) findViewById(R.id.sv_public_group_scroll);
        btGetPublicGroup = (Button) findViewById(R.id.bt_get_public_group_by_appkey);
    }

    private void initData() {
        btGetPublicGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appkey = etAppKey.getText().toString();
                int start = 0;
                int count = 0;
                try {
                    start = Integer.parseInt(etGetStart.getText().toString());
                    count = Integer.parseInt(etGetCount.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(GetPublicGroupInfosActivity.this, "请输入start和count", Toast.LENGTH_SHORT).show();
                    return;
                }
                JMessageClient.getPublicGroupListByApp(appkey, start, count, new RequestCallback<List<GroupBasicInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<GroupBasicInfo> groupBasicInfos) {
                        if (responseCode == 0) {
                            String result = "";
                            for (GroupBasicInfo groupBasicInfo : groupBasicInfos) {
                                result += "GroupID: " + groupBasicInfo.getGroupID() + ", GroupType: " + groupBasicInfo.getGroupType() +
                                        ", GroupName: " + groupBasicInfo.getGroupName() + ", GroupDescription: " + groupBasicInfo.getGroupDescription() +
                                        ", GroupAvatarMediaID: " + groupBasicInfo.getAvatar() + ", GroupMaxMemberCount: " + groupBasicInfo.getMaxMemberCount()+ "\n\n";
                            }
                            tvDisplay.setText(result);
                            tvDisplay.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                        } else {
                            tvDisplay.setText("获取失败!\nresponseCode:" + responseCode + "\nresponseMsg" + responseMessage);
                        }
                    }
                });
            }
        });
    }
}
