package im.sdk.debug.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/4/17.
 *
 * @desc :同意或拒绝好友申请
 */
public class ShowFriendReasonActivity extends Activity {
    private static final String TAG = ShowFriendReasonActivity.class.getSimpleName();
    public static final String EXTRA_TYPE = "event_type";

    private TextView mTv_showAddFriendInfo;
    private Button mAccept_invitation;
    private Button mDeclined_invitation;
    private EditText mEt_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        final Intent intent = getIntent();
        ContactNotifyEvent.Type type = ContactNotifyEvent.Type.valueOf(intent.getStringExtra(EXTRA_TYPE));
        switch (type) {
            case invite_received:
                mTv_showAddFriendInfo.append(intent.getStringExtra("invite_received"));
                break;
            case invite_accepted:
                mEt_reason.setVisibility(View.GONE);
                mAccept_invitation.setVisibility(View.GONE);
                mDeclined_invitation.setVisibility(View.GONE);
                mTv_showAddFriendInfo.append(intent.getStringExtra("invite_accepted"));
                break;
            case invite_declined:
                mEt_reason.setVisibility(View.GONE);
                mAccept_invitation.setVisibility(View.GONE);
                mDeclined_invitation.setVisibility(View.GONE);
                mTv_showAddFriendInfo.append(intent.getStringExtra("invite_declined"));
                break;
            case contact_deleted:
                mEt_reason.setVisibility(View.GONE);
                mAccept_invitation.setVisibility(View.GONE);
                mDeclined_invitation.setVisibility(View.GONE);
                mTv_showAddFriendInfo.append(intent.getStringExtra("contact_deleted"));
                break;
            case contact_updated_by_dev_api:
                mEt_reason.setVisibility(View.GONE);
                mAccept_invitation.setVisibility(View.GONE);
                mDeclined_invitation.setVisibility(View.GONE);
                mTv_showAddFriendInfo.append(intent.getStringExtra("contact_updated_by_dev_api"));
                break;
            default:
                break;

        }
        //同意好友添加邀请
        mAccept_invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactManager.acceptInvitation(intent.getStringExtra("username"), intent.getStringExtra("appkey"), new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "ContactManager.acceptInvitation" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });

        //拒绝好友添加邀请
        mDeclined_invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = mEt_reason.getText().toString();
                ContactManager.declineInvitation(intent.getStringExtra("username"), intent.getStringExtra("appkey"), reason, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "拒绝成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "拒绝失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "ContactManager.declineInvitation" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_show_friend_reason);
        mTv_showAddFriendInfo = (TextView) findViewById(R.id.tv_show_add_friend_info);
        mAccept_invitation = (Button) findViewById(R.id.accept_invitation);
        mDeclined_invitation = (Button) findViewById(R.id.declined_invitation);
        mEt_reason = (EditText) findViewById(R.id.et_reason);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
