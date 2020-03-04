package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.GroupApprovalEvent;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by hxhg on 2018/5/10.
 */
public class HandleGroupApprovalInBatchActivity extends Activity implements View.OnClickListener {

    ScrollView scrollView;
    TextView textView;
    Collection<GroupApprovalEvent> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_approval_batch);
        scrollView = (ScrollView) findViewById(R.id.sv_approval_batch);
        textView = (TextView) findViewById(R.id.tv_approval_batch);
        findViewById(R.id.bt_approval_batch).setOnClickListener(this);
        JMessageClient.registerEventReceiver(this);
    }

    public void onEventMainThread(GroupApprovalEvent event) {
        events.add(event);
        textView.append("收到新的审批事件， event id = " + event.getEventId() + "\n" + " 当前已收到事件id列表:[");
        for (GroupApprovalEvent groupApprovalEvent : events) {
            textView.append(groupApprovalEvent.getEventId() + " ");
        }
        textView.append("]\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    @Override
    public void onClick(View v) {
        for (GroupApprovalEvent event : events) {
            Log.d("tag", "sended event id = " + event.getEventId());
        }
        GroupApprovalEvent.acceptGroupApprovalInBatch(events, false, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                textView.append("批量审批请求发送完成。 responseCode = " + responseCode + " responseMessage = " + responseMessage + "\n");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                events.clear();
            }
        });
    }
}
