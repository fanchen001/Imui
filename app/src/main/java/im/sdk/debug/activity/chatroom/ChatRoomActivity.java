package im.sdk.debug.activity.chatroom;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.ErrorCode;
import cn.jpush.im.android.api.ChatRoomManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetChatRoomSilencesCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.event.ChatRoomMessageEvent;
import cn.jpush.im.android.api.model.ChatRoomInfo;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.SilenceInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by hxhg on 2017/10/18.
 */

public class ChatRoomActivity extends Activity implements View.OnClickListener {

    private EditText etRoomID;
    private EditText etGetStart;
    private EditText etGetCount;
    private EditText etInputText;
    private EditText etUserName;
    private EditText etSilenceTime;
    private EditText etSilenceStart;
    private EditText etSilenceCount;
    private TextView tvDisplay;
    private ScrollView svScorll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);

        setContentView(R.layout.activity_chatroom);
        etRoomID = (EditText) findViewById(R.id.et_roomID);
        etGetStart = (EditText) findViewById(R.id.et_chatroom_get_start);
        etGetCount = (EditText) findViewById(R.id.et_chatroom_get_count);
        etInputText = (EditText) findViewById(R.id.et_chatroom_inputtext);
        etUserName = (EditText) findViewById(R.id.et_username);
        tvDisplay = (TextView) findViewById(R.id.tv_chatroom_display);
        svScorll = (ScrollView) findViewById(R.id.sv_chatroom_scroll);
        etSilenceTime = (EditText) findViewById(R.id.et_chatroom_silence_times);
        etSilenceStart = (EditText) findViewById(R.id.et_chatroom_silence_get_start);
        etSilenceCount = (EditText) findViewById(R.id.et_chatroom_silence_get_count);
        findViewById(R.id.bt_get_room_by_id).setOnClickListener(this);
        findViewById(R.id.bt_get_room_by_appkey).setOnClickListener(this);
        findViewById(R.id.bt_get_room_by_user).setOnClickListener(this);
        findViewById(R.id.bt_enter_chatroom).setOnClickListener(this);
        findViewById(R.id.bt_leave_chatroom).setOnClickListener(this);
        findViewById(R.id.bt_chatroom_sendtext).setOnClickListener(this);
        findViewById(R.id.bt_add_admin).setOnClickListener(this);
        findViewById(R.id.bt_del_admin).setOnClickListener(this);
        findViewById(R.id.bt_add_blacklist).setOnClickListener(this);
        findViewById(R.id.bt_del_blacklist).setOnClickListener(this);
        findViewById(R.id.bt_get_admin).setOnClickListener(this);
        findViewById(R.id.bt_get_blacklist).setOnClickListener(this);
        findViewById(R.id.bt_add_silence).setOnClickListener(this);
        findViewById(R.id.bt_del_silence).setOnClickListener(this);
        findViewById(R.id.bt_get_room_silence).setOnClickListener(this);
        findViewById(R.id.bt_get_room_member_silence).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    @Override
    public void onClick(View v) {
        long roomID = 0;
        try {
            roomID = Long.parseLong(etRoomID.getText().toString());
        } catch (NumberFormatException e) {
            //ignore
        }
        String username = etUserName.getText().toString();
        switch (v.getId()) {
            case R.id.bt_get_room_by_appkey:
                int start = 0;
                int count = 0;
                try {
                    start = Integer.parseInt(etGetStart.getText().toString());
                    count = Integer.parseInt(etGetCount.getText().toString());
                } catch (NumberFormatException e) {
                    //ignore
                }
                ChatRoomManager.getChatRoomListByApp(start, count, new RequestCallback<List<ChatRoomInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<ChatRoomInfo> chatRoomInfos) {
                        String result = null != chatRoomInfos ? chatRoomInfos.toString() : null;
                        postTextToDisplay("getChatRoomListByApp", responseCode, responseMessage, result);
                    }
                });
                break;
            case R.id.bt_get_room_by_user:
                ChatRoomManager.getChatRoomListByUser(new RequestCallback<List<ChatRoomInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<ChatRoomInfo> chatRoomInfos) {
                        String result = null != chatRoomInfos ? chatRoomInfos.toString() : null;
                        postTextToDisplay("getChatRoomListByUser", responseCode, responseMessage, result);
                    }
                });
                break;
            case R.id.bt_get_room_by_id:
                ChatRoomManager.getChatRoomInfos(Collections.singleton(roomID), new RequestCallback<List<ChatRoomInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<ChatRoomInfo> chatRoomInfos) {
                        String result = null != chatRoomInfos ? chatRoomInfos.toString() : null;
                        postTextToDisplay("getChatRoomInfos", responseCode, responseMessage, result);
                    }
                });
                break;
            case R.id.bt_enter_chatroom:
                ChatRoomManager.enterChatRoom(roomID, new RequestCallback<Conversation>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, Conversation conversation) {
                        String result = null != conversation ? conversation.toString() : null;
                        postTextToDisplay("enterChatRoom", responseCode, responseMessage, result);
                    }
                });
                break;
            case R.id.bt_leave_chatroom:
                ChatRoomManager.leaveChatRoom(roomID, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        postTextToDisplay("leaveChatRoom", responseCode, responseMessage, null);
                    }
                });
                break;
            case R.id.bt_chatroom_sendtext:
                if (0 == roomID) {
                    Toast.makeText(getApplicationContext(), "请输入聊天室roomID", Toast.LENGTH_SHORT).show();
                    return;
                }
                Conversation conv = JMessageClient.getChatRoomConversation(roomID);
                if (null == conv) {
                    conv = Conversation.createChatRoomConversation(roomID);
                }
                String text = etInputText.getText().toString();
                final Message msg = conv.createSendTextMessage(text);//实际聊天室可以支持所有类型的消息发送，demo为了简便，仅仅实现了文本类型的消息发送
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        if (0 == responseCode) {
                            postMessageToDisplay("MessageSent", responseCode, responseMessage, msg);
                        } else {
                            postTextToDisplay("MessageSent", responseCode, responseMessage, "消息发送失败");
                        }
                    }
                });
                JMessageClient.sendMessage(msg);
                break;
            case R.id.bt_add_admin:
                operateChatRoomAdmin(roomID, username, true);
                break;
            case R.id.bt_del_admin:
                operateChatRoomAdmin(roomID, username, false);
                break;
            case R.id.bt_add_blacklist:
                operateChatRoomBlacklist(roomID, username, true);
                break;
            case R.id.bt_del_blacklist:
                operateChatRoomBlacklist(roomID, username, false);
                break;
            case R.id.bt_get_admin:
                if (0 == roomID) {
                    Toast.makeText(getApplicationContext(), "请输入聊天室roomID", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChatRoomManager.getChatRoomAdminList(roomID, new RequestCallback<List<UserInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<UserInfo> result) {
                        if (0 == responseCode) {
                            StringBuilder builder = new StringBuilder();
                            if (result.size() > 0) {
                                for (UserInfo userInfo : result) {
                                    builder.append(userInfo.getUserName()).append("\n");
                                }
                            } else {
                                builder.append("该聊天室还没有房管");
                            }
                            postTextToDisplay("getChatRoomAdminList", responseCode, responseMessage, builder.toString());
                        } else {
                            postTextToDisplay("getChatRoomAdminList", responseCode, responseMessage, "获取聊天室房管列表失败");
                        }
                    }
                });
                break;
            case R.id.bt_get_blacklist:
                if (0 == roomID) {
                    Toast.makeText(getApplicationContext(), "请输入聊天室roomID", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChatRoomManager.getChatRoomBlacklist(roomID, new RequestCallback<List<UserInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<UserInfo> result) {
                        if (0 == responseCode) {
                            StringBuilder builder = new StringBuilder();
                            if (result.size() > 0) {
                                for (UserInfo userInfo : result) {
                                    builder.append(userInfo.getUserName()).append("\n");
                                }
                            } else {
                                builder.append("该聊天室黑名单列表为空");
                            }
                            postTextToDisplay("getChatRoomAdminList", responseCode, responseMessage, builder.toString());
                        } else {
                            postTextToDisplay("getChatRoomAdminList", responseCode, responseMessage, "获取聊天室黑名单列表失败");
                        }
                    }
                });
                break;
            case R.id.bt_add_silence:
                if (TextUtils.isEmpty(etSilenceTime.getText())) {
                    Toast.makeText(getApplicationContext(), "请输入禁言时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                long times = 0;
                try {
                    times = 60000 * Long.parseLong(etSilenceTime.getText().toString());
                } catch (NumberFormatException e) {
                    //
                }
                operateChatRoomSilence(roomID, username, times, true);
                break;
            case R.id.bt_del_silence:
                operateChatRoomSilence(roomID, username, 0, false);
                break;
            case R.id.bt_get_room_silence:
                if (0 == roomID) {
                    Toast.makeText(getApplicationContext(), "请输入聊天室roomID", Toast.LENGTH_SHORT).show();
                    return;
                }
                int silenceStart = 0;
                int silenceCount = 0;
                try {
                    silenceStart = Integer.parseInt(etSilenceStart.getText().toString());
                } catch (NumberFormatException e) {

                }
                try {
                    silenceCount = Integer.parseInt(etSilenceCount.getText().toString());
                } catch (NumberFormatException e) {

                }
                ChatRoomManager.getChatRoomSilencesFromNewest(roomID, silenceStart, silenceCount, new GetChatRoomSilencesCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<SilenceInfo> silenceInfos, int total) {
                        if (0 == responseCode) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("聊天室禁言总量:").append(total).append(",本次获取到数量:")
                                    .append(silenceInfos.size()).append("\n");
                            if (silenceInfos.size() > 0) {
                                for (SilenceInfo silenceInfo : silenceInfos) {
                                    builder.append("username:").append(silenceInfo.getUserInfo().getUserName()).append("\n");
                                    builder.append("silenceStart:").append(silenceInfo.getSilenceStartTime()).append("\n");
                                    builder.append("silenceEnd:").append(silenceInfo.getSilenceEndTime()).append("\n\n");
                                }
                            }
                            postTextToDisplay("getChatRoomAdminList", responseCode, responseMessage, builder.toString());
                        } else {
                            postTextToDisplay("getChatRoomAdminList", responseCode, responseMessage, "获取禁言列表失败");
                        }
                    }
                });
                break;
            case R.id.bt_get_room_member_silence:
                if (0 == roomID) {
                    Toast.makeText(getApplicationContext(), "请输入聊天室roomID", Toast.LENGTH_SHORT).show();
                    return;
                }
                final long id = roomID;
                if (username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChatRoomManager.getChatRoomMemberSilence(roomID, username, null, new RequestCallback<SilenceInfo>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, SilenceInfo result) {
                        if (0 == responseCode) {
                            StringBuilder builder = new StringBuilder();
                            if (result != null) {
                                builder.append("usname:").append(result.getUserInfo().getUserName()).append("\n");
                                builder.append("silenceStart:").append(result.getSilenceStartTime()).append("\n");
                                builder.append("silenceEnd:").append(result.getSilenceEndTime()).append("\n");
                            } else {
                                builder.append("该用户没有被禁言");
                            }
                            postTextToDisplay("getChatRoomMemberSilence", responseCode, responseMessage, builder.toString());
                        } else {
                            postTextToDisplay("getChatRoomMemberSilence", responseCode, responseMessage, "获取用户禁言状态失败");
                        }
                    }
                });
            default:

        }
    }

    private void operateChatRoomBlacklist(final long roomID, String username, final boolean isAdd) {
        if (0 == roomID) {
            Toast.makeText(getApplicationContext(), "请输入聊天室roomID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        JMessageClient.getUserInfo(username, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0== responseCode) {
                    if (isAdd) {
                        ChatRoomManager.addChatRoomBlacklist(roomID, Collections.singletonList(info), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (0 == responseCode) {
                                    postTextToDisplay("addChatRoomBlacklist", responseCode, responseMessage, null);
                                } else {
                                    postTextToDisplay("addChatRoomBlacklist", responseCode, responseMessage, "添加聊天室黑名单失败");
                                }
                            }
                        });
                    } else {
                        ChatRoomManager.delChatRoomBlacklist(roomID, Collections.singletonList(info), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (0== responseCode) {
                                    postTextToDisplay("delChatRoomBlacklist", responseCode, responseMessage, null);
                                } else {
                                    postTextToDisplay("delChatRoomBlacklist", responseCode, responseMessage, "从黑名单中移除失败");
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "获取用户信息失败，请输入正确的用户名", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void operateChatRoomAdmin(final long roomID, String username, final boolean isAdd) {
        if (0 == roomID) {
            Toast.makeText(getApplicationContext(), "请输入聊天室roomID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        JMessageClient.getUserInfo(username, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0 == responseCode) {
                    if (isAdd) {
                        ChatRoomManager.addChatRoomAdmin(roomID, Collections.singletonList(info), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (0 == responseCode) {
                                    postTextToDisplay("addChatRoomAdmin", responseCode, responseMessage, null);
                                } else {
                                    postTextToDisplay("addChatRoomAdmin", responseCode, responseMessage, "设置房管失败");
                                }
                            }
                        });
                    } else {
                        ChatRoomManager.delChatRoomAdmin(roomID, Collections.singletonList(info), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (0 == responseCode) {
                                    postTextToDisplay("delChatRoomAdmin", responseCode, responseMessage, null);
                                } else {
                                    postTextToDisplay("delChatRoomAdmin", responseCode, responseMessage, "取消房管失败");
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "获取用户信息失败，请输入正确的用户名", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void operateChatRoomSilence(final long roomId, String username, final long times, final boolean keepSilence) {
        if (0 == roomId) {
            Toast.makeText(getApplicationContext(), "请输入聊天室roomID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        JMessageClient.getUserInfo(username, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0 == responseCode) {
                    if (keepSilence) {
                        ChatRoomManager.addChatRoomSilence(roomId, Collections.singletonList(info), times, new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (0 == responseCode) {
                                    postTextToDisplay("addChatRoomSilence", responseCode, responseMessage, null);
                                } else {
                                    postTextToDisplay("addChatRoomSilence", responseCode, responseMessage, "设置禁言失败");
                                }
                            }
                        });
                    } else {
                        ChatRoomManager.delChatRoomSilence(roomId, Collections.singletonList(info), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (0 == responseCode) {
                                    postTextToDisplay("delChatRoomSilence", responseCode, responseMessage, null);
                                } else {
                                    postTextToDisplay("delChatRoomSilence", responseCode, responseMessage, "设置禁言失败");
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "获取用户信息失败，请输入正确的用户名", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postTextToDisplay(String tag, int statusCode, String responseMsg, String text) {
        tvDisplay.append("-------------------\n");
        tvDisplay.append("[" + tag + "]");
        tvDisplay.append(" statusCode = " + statusCode);
        tvDisplay.append(" responseMsg = " + responseMsg + "\n");
        if (null != text) {
            tvDisplay.append(text);
        }
        tvDisplay.append("\n-------------------\n");
        tvDisplay.post(new Runnable() {
            @Override
            public void run() {
                svScorll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void postMessageToDisplay(final String tag, final int statusCode, final String responseMsg, final Message msg) {
        if (null == msg) {
            Toast.makeText(getApplicationContext(), "消息对象为null", Toast.LENGTH_SHORT).show();
            return;
        }
        final ChatRoomInfo chatRoomInfo = ((ChatRoomInfo) msg.getTargetInfo());
        final String msgText;
        if (ContentType.text == msg.getContentType()) {
            msgText = "说：\n" + ((TextContent) msg.getContent()).getText();
        } else {
            msgText = "发了一条" + msg.getContentType() + "类型的消息";
        }
        String text = "用户：" + msg.getFromUser().getUserName()
                + "在聊天室" + chatRoomInfo.getName() + "(roomID=" + chatRoomInfo.getRoomID() + ")中"
                + msgText;
        postTextToDisplay(tag, statusCode, responseMsg, text);
    }

    //接收聊天室消息
    public void onEventMainThread(ChatRoomMessageEvent event) {
        Log.d("tag", "ChatRoomMessageEvent received .");
        List<Message> msgs = event.getMessages();
        for (Message msg : msgs) {
            //这个页面仅仅展示聊天室会话的消息
            postMessageToDisplay("MessageReceived", event.getResponseCode(), event.getResponseDesc(), msg);
        }
    }
}
