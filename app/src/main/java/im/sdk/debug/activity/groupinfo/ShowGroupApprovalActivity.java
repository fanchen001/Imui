package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.event.GroupApprovalEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

public class ShowGroupApprovalActivity extends Activity {
    private static final String TAG = ShowGroupApprovalActivity.class.getSimpleName();
    public static final String EXTRA_EVENT_TYPE = "event_type";
    public static final int TYPE_APPROVAL = 1;
    public static final int TYPE_APPROVAL_REFUSE = 2;

    private Button mBt_acceptApplyJoin;
    private Button mBt_refuseApplyJoin;
    private EditText mEt_reason;
    private TextView mTv_showUserInfo;
    private ListView listView;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        final Intent intent = getIntent();
        int eventType = intent.getIntExtra(EXTRA_EVENT_TYPE, 0);
        if (eventType == TYPE_APPROVAL) {
            final String eventJson = intent.getStringExtra("GroupApprovalEvent");
            Gson gson = new Gson();
            final GroupApprovalEvent event = gson.fromJson(eventJson, GroupApprovalEvent.class);
            event.getFromUserInfo(new GetUserInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                    if (0 == responseCode) {
                        final String fromUsername = info.getUserName();
                        final String fromUserAppKey = info.getAppKey();
                        if (event.getType() == GroupApprovalEvent.Type.apply_join_group) {
                            listView.setVisibility(View.GONE);
                            mTv_showUserInfo.append("主动申请入群\n" + "username: " + event.getFromUsername()
                                    + "\nappKey: " + event.getfromUserAppKey() + "\ngid:" + event.getGid()
                                    + "\nreason: " + event.getReason());
                            mBt_acceptApplyJoin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    event.acceptGroupApproval(fromUsername, fromUserAppKey, new BasicCallback() {
                                        @Override
                                        public void gotResult(int responseCode, String responseMessage) {
                                            if (0 == responseCode) {
                                                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.i(TAG, "acceptApplyJoinGroup failed," + " code = " + responseCode + ";msg = " + responseMessage);
                                                Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            mBt_refuseApplyJoin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String reason = null;
                                    if (!TextUtils.isEmpty(mEt_reason.getText())) {
                                        reason = mEt_reason.getText().toString();
                                    }
                                    event.refuseGroupApproval(fromUsername, fromUserAppKey, reason, new BasicCallback() {
                                        @Override
                                        public void gotResult(int responseCode, String responseMessage) {
                                            if (0 == responseCode) {
                                                Toast.makeText(getApplicationContext(), "拒绝成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.i(TAG, "refuseApplyJoinGroup failed," + " code = " + responseCode + ";msg = " + responseMessage);
                                                Toast.makeText(getApplicationContext(), "拒绝失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        } else if (event.getType() == GroupApprovalEvent.Type.invited_into_group) {
                            mBt_acceptApplyJoin.setVisibility(View.GONE);
                            mBt_refuseApplyJoin.setVisibility(View.GONE);
                            mEt_reason.setVisibility(View.GONE);
                            mTv_showUserInfo.setText("邀请入群\n" + "邀请人userName: " + fromUsername + "\n邀请人appKey: " + fromUserAppKey
                                    + "\nGid:" + event.getGid() + "\nreason: " + event.getReason() + "\n\n");
                            event.getApprovalUserInfoList(new GetUserInfoListCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                                    if (0 == responseCode) {
                                        myAdapter = new MyAdapter(ShowGroupApprovalActivity.this, userInfoList, event);
                                        listView.setAdapter(myAdapter);
                                    } else {
                                        mTv_showUserInfo.setText("\n被邀请人userInfo未找到");
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else if (eventType == TYPE_APPROVAL_REFUSE) {
            mBt_acceptApplyJoin.setVisibility(View.GONE);
            mBt_refuseApplyJoin.setVisibility(View.GONE);
            mEt_reason.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            mTv_showUserInfo.setText(intent.getStringExtra("notification"));
        }
    }

    private void initView() {
        setContentView(R.layout.activity_show_group_member_approval);
        mBt_acceptApplyJoin = (Button) findViewById(R.id.accept_join_group);
        mBt_refuseApplyJoin = (Button) findViewById(R.id.refuse_join_group);
        mEt_reason = (EditText) findViewById(R.id.et_reason);
        mTv_showUserInfo = (TextView) findViewById(R.id.tv_show_join_group_user_info);
        listView = (ListView) findViewById(R.id.list_view);

    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<UserInfo> mUsers;
        private GroupApprovalEvent event;
        private int mTouchItemPosition = -1;
        private Map<Long, String> reasons;

        public MyAdapter(Context context, List<UserInfo> users, GroupApprovalEvent event) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mUsers = users;
            reasons = new HashMap<Long, String>();
            this.event = event;
        }

        @Override
        public int getCount() {
            return (mUsers != null ? mUsers.size() : 0);
        }

        @Override
        public Object getItem(int position) {
            return (mUsers != null ? mUsers.get(position) : null);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_show_group_member_approval_list_item, null);
                holder = new ViewHolder();

                holder.mBt_accept = (Button) convertView.findViewById(R.id.accept_invite_to_group);
                holder.mBt_refuse = (Button) convertView.findViewById(R.id.refuse_invite_to_group);
                holder.mEt_reason = (EditText) convertView.findViewById(R.id.et_refuse_invite_to_group_reason);
                holder.mTv_userInfo = (TextView) convertView.findViewById(R.id.tv_show_invited_user_info);
                holder.mEt_reason = (EditText) convertView.findViewById(R.id.et_refuse_invite_to_group_reason);
                MyTextWatcher myTextWatcher = new MyTextWatcher();
                myTextWatcher.updatePosition(position);
                holder.mEt_reason.addTextChangedListener(myTextWatcher);

                holder.mEt_reason.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        mTouchItemPosition = (Integer) view.getTag();
                        return false;
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mEt_reason.setText(reasons.get(mUsers.get(position).getUserID()));

            holder.mEt_reason.setTag(position);

            if (mTouchItemPosition == position) {
                holder.mEt_reason.requestFocus();
                holder.mEt_reason.setSelection(holder.mEt_reason.getText().length());
            } else {
                holder.mEt_reason.clearFocus();
            }

            UserInfo userInfo = null != mUsers ? mUsers.get(position) : null;
            if (userInfo != null) {
                final EditText et_reason = holder.mEt_reason;
                holder.mTv_userInfo.setText("待审批用户信息：" + "\nusername:" + userInfo.getUserName() + "\nappKey:" + userInfo.getAppKey());
                holder.mBt_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        event.acceptGroupApproval(mUsers.get(position).getUserName(), mUsers.get(position).getAppKey(),
                                new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (0 == responseCode) {
                                            Toast.makeText(getApplicationContext(), "添加" + mUsers.get(position).getUserName() + "成功", Toast.LENGTH_SHORT).show();
                                            mUsers.remove(position);
                                            MyAdapter.this.notifyDataSetChanged();
                                        } else {
                                            Log.i(TAG, "acceptInviteToGroup failed," + " code = " + responseCode + ";msg = " + responseMessage);
                                            Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                holder.mBt_refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String reason = null;
                        if (!TextUtils.isEmpty(et_reason.getText())) {
                            reason = et_reason.getText().toString();
                        }
                        event.refuseGroupApproval(mUsers.get(position).getUserName(), mUsers.get(position).getAppKey(), reason,
                                new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (0 == responseCode) {
                                            Toast.makeText(getApplicationContext(), "拒绝" + mUsers.get(position).getUserName() + "成功", Toast.LENGTH_SHORT).show();
                                            mUsers.remove(position);
                                            MyAdapter.this.notifyDataSetChanged();
                                        } else {
                                            Log.i(TAG, "refuseInviteToGroup failed," + " code = " + responseCode + ";msg = " + responseMessage);
                                            Toast.makeText(getApplicationContext(), "拒绝失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }
            return convertView;
        }

        private class ViewHolder {
            Button mBt_accept;
            Button mBt_refuse;
            TextView mTv_userInfo;
            EditText mEt_reason;
        }

        private class MyTextWatcher implements TextWatcher {
            private int mPosition;

            public void updatePosition(int position) {
                mPosition = position;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                reasons.put(mUsers.get(mPosition).getUserID(), s.toString());
            }
        }
    }
}
