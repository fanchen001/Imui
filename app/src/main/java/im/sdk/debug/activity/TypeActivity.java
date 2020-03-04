package im.sdk.debug.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.event.ChatRoomNotificationEvent;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.GroupAnnouncementChangedEvent;
import cn.jpush.im.android.api.event.GroupApprovalEvent;
import cn.jpush.im.android.api.event.GroupApprovalRefuseEvent;
import cn.jpush.im.android.api.event.GroupApprovedNotificationEvent;
import cn.jpush.im.android.api.event.GroupBlackListChangedEvent;
import cn.jpush.im.android.api.event.GroupMemNicknameChangedEvent;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MyInfoUpdatedEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import com.fanchen.R;
import im.sdk.debug.activity.chatroom.ChatRoomActivity;
import im.sdk.debug.activity.conversation.ConversationActivity;
import im.sdk.debug.activity.createmessage.CreateMessageActivity;
import im.sdk.debug.activity.createmessage.ShowTransCommandActivity;
import im.sdk.debug.activity.friend.FriendContactManager;
import im.sdk.debug.activity.friend.ShowFriendReasonActivity;
import im.sdk.debug.activity.groupinfo.GroupInfoActivity;
import im.sdk.debug.activity.groupinfo.ShowGroupApprovalActivity;
import im.sdk.debug.activity.groupinfo.ShowMemNicknameChangedActivity;
import im.sdk.debug.activity.jmrtc.JMRTCActivity;
import im.sdk.debug.activity.setting.SettingMainActivity;
import im.sdk.debug.activity.setting.ShowLogoutReasonActivity;
import im.sdk.debug.activity.showinfo.ShowAnnouncementChangedActivity;
import im.sdk.debug.activity.showinfo.ShowChatRoomNotificationActivity;
import im.sdk.debug.activity.showinfo.ShowGroupBlcakListChangedActivity;
import im.sdk.debug.activity.showinfo.ShowMyInfoUpdateActivity;

/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 各个接口的的引导界面
 */
public class TypeActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "TypeActivity";
    public static final String CREATE_GROUP_CUSTOM_KEY = "create_group_custom_key";
    public static final String SET_DOWNLOAD_PROGRESS = "set_download_progress";
    public static final String IS_DOWNLOAD_PROGRESS_EXISTS = "is_download_progress_exists";
    public static final String CUSTOM_MESSAGE_STRING = "custom_message_string";
    public static final String CUSTOM_FROM_NAME = "custom_from_name";
    public static final String DOWNLOAD_ORIGIN_IMAGE = "download_origin_image";
    public static final String DOWNLOAD_THUMBNAIL_IMAGE = "download_thumbnail_image";
    public static final String IS_UPLOAD = "is_upload";
    public static final String LOGOUT_REASON = "logout_reason";
    private TextView mTv_showOfflineMsg;
    private TextView tv_refreshEvent;
    private TextView tv_deviceInfo;
    private TextView tv_header;
    public static final String DOWNLOAD_INFO = "download_info";
    public static final String INFO_UPDATE = "info_update";
    public static final String TRANS_COMMAND_SENDER = "trans_command_sender";
    public static final String TRANS_COMMAND_TARGET = "trans_command_target";
    public static final String TRANS_COMMAND_TYPE = "trans_command_type";
    public static final String TRANS_COMMAND_CMD = "trans_command_cmd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_type);
        tv_header = (TextView) findViewById(R.id.tv_header);
        findViewById(R.id.bt_about_setting).setOnClickListener(this);
        findViewById(R.id.bt_create_message).setOnClickListener(this);
        findViewById(R.id.bt_group_info).setOnClickListener(this);
        findViewById(R.id.bt_conversation).setOnClickListener(this);
        findViewById(R.id.bt_friend).setOnClickListener(this);
        findViewById(R.id.bt_chatroom).setOnClickListener(this);
        findViewById(R.id.bt_jmrtc).setOnClickListener(this);

        mTv_showOfflineMsg = (TextView) findViewById(R.id.tv_showOfflineMsg);
        tv_refreshEvent = (TextView) findViewById(R.id.tv_refreshEvent);
        tv_deviceInfo = (TextView) findViewById(R.id.tv_deviceInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_header.setText("");
        tv_deviceInfo.setText("");
        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            tv_header.append("当前已登录用户：" + info.getUserName() + "\n");
            tv_header.append("用户所属appkey：" + info.getAppKey() + "\n");
        }
        tv_header.append("版本号：" + JMessageClient.getSdkVersionString());
        Intent intent = getIntent();
        Gson gson = new Gson();
        List<DeviceInfo> deviceInfos = gson.fromJson(intent.getStringExtra("deviceInfos"), new TypeToken<List<DeviceInfo>>() {}.getType());
        if (deviceInfos != null) {
            for (DeviceInfo deviceInfo : deviceInfos) {
                tv_deviceInfo.append("设备登陆记录:\n");
                tv_deviceInfo.append("设备ID: " + deviceInfo.getDeviceID() + " 平台：" + deviceInfo.getPlatformType()
                        + " 上次登陆时间:" + deviceInfo.getLastLoginTime() + "登陆状态:" + deviceInfo.isLogin() + "在线状态:" + deviceInfo.getOnlineStatus()
                        + " flag:" + deviceInfo.getFlag());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt_about_setting:
                intent.setClass(getApplicationContext(), SettingMainActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.bt_create_message:
                intent.setClass(getApplicationContext(), CreateMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_group_info:
                intent.setClass(getApplicationContext(), GroupInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_conversation:
                intent.setClass(getApplicationContext(), ConversationActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_friend:
                intent.setClass(getApplicationContext(), FriendContactManager.class);
                startActivity(intent);
                break;
            case R.id.bt_chatroom:
                intent.setClass(getApplicationContext(), ChatRoomActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_jmrtc:
                intent.setClass(getApplicationContext(), JMRTCActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 8) {
            mTv_showOfflineMsg.setText("");
            tv_refreshEvent.setText("");
        }
    }


    public void onEvent(ContactNotifyEvent event) {
        String reason = event.getReason();
        String fromUsername = event.getFromUsername();
        String appkey = event.getfromUserAppKey();

        Intent intent = new Intent(getApplicationContext(), ShowFriendReasonActivity.class);
        intent.putExtra(ShowFriendReasonActivity.EXTRA_TYPE, event.getType().toString());
        switch (event.getType()) {
            case invite_received://收到好友邀请
                intent.putExtra("invite_received", "fromUsername = " + fromUsername + "\nfromUserAppKey" + appkey + "\nreason = " + reason);
                intent.putExtra("username", fromUsername);
                intent.putExtra("appkey", appkey);
                startActivity(intent);
                break;
            case invite_accepted://对方接收了你的好友邀请
                intent.putExtra("invite_accepted", "对方接受了你的好友邀请");
                startActivity(intent);
                break;
            case invite_declined://对方拒绝了你的好友邀请
                intent.putExtra("invite_declined", "对方拒绝了你的好友邀请\n拒绝原因:" + event.getReason());
                startActivity(intent);
                break;
            case contact_deleted://对方将你从好友中删除
                intent.putExtra("contact_deleted", "对方将你从好友中删除");
                startActivity(intent);
                break;
            case contact_updated_by_dev_api://好友关系更新，由api管理员操作引起
                intent.putExtra("contact_updated_by_dev_api", "好友关系被管理员更新");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void onEvent(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(getApplicationContext(), ShowLogoutReasonActivity.class);
        intent.putExtra(LOGOUT_REASON, "reason = " + reason + "\n" + "logout user name = " + myInfo.getUserName());
        startActivity(intent);
    }

    public void onEventMainThread(OfflineMessageEvent event) {
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        List<Integer> offlineMsgIdList = new ArrayList<Integer>();
        if (conversation != null && newMessageList != null) {
            for (Message msg : newMessageList) {
                offlineMsgIdList.add(msg.getId());
            }
            mTv_showOfflineMsg.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
            mTv_showOfflineMsg.append("会话类型 = " + conversation.getType() + "\n消息ID = " + offlineMsgIdList + "\n\n");
        } else {
            mTv_showOfflineMsg.setText("conversation is null or new message list is null");
        }
    }

    public void onEventMainThread(ConversationRefreshEvent event) {
        Conversation conversation = event.getConversation();
        ConversationRefreshEvent.Reason reason = event.getReason();
        if (conversation != null) {
            tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到ConversationRefreshEvent事件,待刷新的会话是%s.\n", conversation.getTargetId()));
            tv_refreshEvent.append("事件发生的原因 : " + reason + "\n");
        } else {
            tv_refreshEvent.setText("conversation is null");
        }
    }

    public void onEvent(GroupMemNicknameChangedEvent event) {
        new ShowMemChangeTask(getApplicationContext()).execute(event);
    }

    public void onEvent(GroupAnnouncementChangedEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("群组ID:").append(event.getGroupID()).append("\n\n");
        for (GroupAnnouncementChangedEvent.ChangeEntity entity : event.getChangeEntities()) {
            builder.append("类型:").append(entity.getType().toString()).append("\n");
            builder.append("发起者(username):").append(entity.getFromUserInfo().getUserName()).append("\n");
            builder.append("内容:").append(entity.getAnnouncement().toJson()).append("\n");
            builder.append("时间:").append(entity.getCtime()).append("\n\n");
        }
        Intent intent = new Intent(getApplicationContext(), ShowAnnouncementChangedActivity.class);
        intent.putExtra(ShowAnnouncementChangedActivity.SHOW_ANNOUNCEMENT_CHANGED, builder.toString());
        startActivity(intent);
    }

    public void onEvent(GroupBlackListChangedEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("群组ID:").append(event.getGroupID()).append("\n\n");
        for (GroupBlackListChangedEvent.ChangeEntity entity : event.getChangeEntities()) {
            builder.append("类型:").append(entity.getType().toString()).append("\n");
            builder.append("操作者(username):").append(entity.getOperator().getUserName()).append("\n");
            builder.append("被操作的用户(username):\n");
            for (UserInfo userInfo : entity.getUserInfos()) {
                builder.append(userInfo.getUserName()).append(" ");
            }
            builder.append("\n");
            builder.append("时间:").append(entity.getCtime()).append("\n\n");
        }
        Intent intent = new Intent(getApplicationContext(), ShowGroupBlcakListChangedActivity.class);
        intent.putExtra(ShowGroupBlcakListChangedActivity.SHOW_GROUP_BLACK_LIST_CHANGED, builder.toString());
        startActivity(intent);
    }

    public void onEventBackgroundThread(ChatRoomNotificationEvent event) {
        new ShowChatRoomNotificationTask(getApplicationContext(), event).run();
    }

    private static class ShowChatRoomNotificationTask {
        private WeakReference<Context> contextWeakReference;
        ChatRoomNotificationEvent event;
        ShowChatRoomNotificationTask(Context context, ChatRoomNotificationEvent event) {
            this.contextWeakReference = new WeakReference<Context>(context);
            this.event = event;
        }
        void run() {
            final StringBuilder builder = new StringBuilder();
            builder.append("事件id:").append(event.getEventID()).append("\n");
            builder.append("聊天室id:").append(event.getRoomID()).append("\n");
            builder.append("事件类型:").append(event.getType()).append("\n");
            final CountDownLatch countDownLatch = new CountDownLatch(2);
            event.getOperator(new GetUserInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                    builder.append("事件发起者:");
                    if (0 == responseCode) {
                        if (info == null) {
                            builder.append("api操作");
                        } else {
                            builder.append(info.getUserName());
                        }
                    } else {
                        builder.append("获取失败");
                    }
                    countDownLatch.countDown();
                }
            });
            event.getTargetUserInfoList(new GetUserInfoListCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                    builder.append("目标用户:\n");
                    if (0 == responseCode) {
                        for (UserInfo userInfo : userInfoList) {
                            builder.append(userInfo.getUserName()).append("\n");
                        }
                    } else {
                        builder.append("获取失败");
                    }
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            builder.append("事件发生时间:" + event.getCtime());
            Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent(context.getApplicationContext(), ShowChatRoomNotificationActivity.class);
                intent.putExtra(ShowChatRoomNotificationActivity.SHOW_CHAT_ROOM_NOTIFICATION, builder.toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    public void onEvent(MyInfoUpdatedEvent event) {
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(TypeActivity.this, ShowMyInfoUpdateActivity.class);
        intent.putExtra(INFO_UPDATE, myInfo.getUserName());
        startActivity(intent);
    }

    public void onEvent(final CommandNotificationEvent event) {
        event.getSenderUserInfo(new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0 == responseCode) {
                    final String sender = info.getUserName();
                    event.getTargetInfo(new CommandNotificationEvent.GetTargetInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, Object targetInfo, CommandNotificationEvent.Type type) {
                            if (0 == responseCode) {
                                String target = "";
                                if (type == CommandNotificationEvent.Type.single) {
                                    target = ((UserInfo) targetInfo).getUserName();
                                } else if (type == CommandNotificationEvent.Type.group) {
                                    target += ((GroupInfo) targetInfo).getGroupID();
                                }
                                Intent intent = new Intent(getApplicationContext(), ShowTransCommandActivity.class);
                                intent.putExtra(TRANS_COMMAND_SENDER, sender);
                                intent.putExtra(TRANS_COMMAND_TARGET, target);
                                intent.putExtra(TRANS_COMMAND_TYPE, event.getType().toString());
                                intent.putExtra(TRANS_COMMAND_CMD, event.getMsg());
                                startActivity(intent);
                            } else {
                                Log.w(TAG, "CommandNotificationEvent getSenderUserInfo failed. " + "code = " +
                                        responseCode + " desc = " + responseMessage);
                            }
                        }
                    });
                } else {
                    Log.w(TAG, "CommandNotificationEvent getTargetInfo failed. " + "code = " +
                            responseCode + " desc = " + responseMessage);
                }
            }
        });
    }

    public void onEvent(GroupApprovalEvent event) {
        Intent intent = new Intent(getApplicationContext(), ShowGroupApprovalActivity.class);
        Gson gson = new Gson();
        intent.putExtra("GroupApprovalEvent", gson.toJson(event));
        intent.putExtra(ShowGroupApprovalActivity.EXTRA_EVENT_TYPE, ShowGroupApprovalActivity.TYPE_APPROVAL);

        startActivity(intent);
    }

    public void onEvent(final GroupApprovalRefuseEvent event) {
        final Intent intent = new Intent(getApplicationContext(), ShowGroupApprovalActivity.class);
        event.getFromUserInfo(new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0 == responseCode) {
                    final String fromUsername = info.getUserName();
                    final String fromAppKey = info.getAppKey();
                    event.getToUserInfoList(new GetUserInfoListCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                            if (0 == responseCode) {
                                String toUsername = userInfoList.get(0).getUserName();
                                String toAppKey = userInfoList.get(0).getAppKey();
                                intent.putExtra("notification", "入群审批拒绝通知" + "\n群组gid:" + event.getGid()
                                        + "\n群主username:" + fromUsername
                                        + "\n群主appKey:" + fromAppKey
                                        + "\n被拒绝者username:" + toUsername
                                        + "\n被拒绝者appKey:" + toAppKey
                                        + "\n拒绝理由:" + event.getReason());
                                intent.putExtra(ShowGroupApprovalActivity.EXTRA_EVENT_TYPE, ShowGroupApprovalActivity.TYPE_APPROVAL_REFUSE);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    public void onEventMainThread(GroupApprovedNotificationEvent event) {
        tv_refreshEvent.append("\n收到入群审批已审批事件通知.对应审批事件id: " + event.getApprovalEventID());
    }

    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        Conversation conv = event.getConversation();
        tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "\n收到MessageReceiptStatusChangeEvent事件,会话对象是%s\n", conv.getTargetId()));
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : event.getMessageReceiptMetas()) {
            tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE,
                    "回执数有更新的消息serverMsgID：%d\n这条消息当前还未发送已读回执的人数：%d\n", meta.getServerMsgId(), meta.getUnReceiptCnt()));
        }

    }

    private static class ShowMemChangeTask extends AsyncTask<GroupMemNicknameChangedEvent, Integer, String> {
        private WeakReference<Context> contextWeakReference;

        ShowMemChangeTask(Context context) {
            this.contextWeakReference = new WeakReference<Context>(context);
        }
        @Override
        protected String doInBackground(GroupMemNicknameChangedEvent... groupMemNicknameChangedEvents) {
            final StringBuilder builder = new StringBuilder();
            if (groupMemNicknameChangedEvents != null && groupMemNicknameChangedEvents.length > 0) {
                GroupMemNicknameChangedEvent event = groupMemNicknameChangedEvents[0];
                builder.append("群组id:" + event.getGroupID() + "\n");
                final CountDownLatch countDownLatch = new CountDownLatch(event.getChangeEntities().size());
                for (final GroupMemNicknameChangedEvent.ChangeEntity changeEntity : event.getChangeEntities()) {
                    changeEntity.getFromUserInfo(new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                            String fromUsername = info != null ? info.getUserName() : "";
                            builder.append("修改昵称者：" + fromUsername + "\n");
                            changeEntity.getToUserInfoList(new GetUserInfoListCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                                    List<String> useNames = new ArrayList<>();
                                    if (userInfoList != null) {
                                        for (UserInfo userInfo : userInfoList) {
                                            useNames.add(userInfo.getUserName());
                                        }
                                    }
                                    builder.append("被修改昵称者: " + useNames + "\n");
                                    builder.append("修改后的昵称：" + changeEntity.getNickname() + "\n");
                                    builder.append("修改时间：" + changeEntity.getCtime() + "\n\n");
                                    countDownLatch.countDown();
                                }
                            });
                        }
                    });
                }
                try {
                    countDownLatch.await(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent();
                intent.setClass(context, ShowMemNicknameChangedActivity.class);
                intent.putExtra(ShowMemNicknameChangedActivity.SHOW_MEM_NICKNAME_CHANGED, s);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }
}
