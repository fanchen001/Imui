package im.sdk.debug.activity.jmrtc;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import cn.jiguang.jmrtc.api.JMRtcClient;
import cn.jiguang.jmrtc.api.JMRtcListener;
import cn.jiguang.jmrtc.api.JMRtcSession;
import cn.jiguang.jmrtc.api.JMSignalingMessage;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;
import im.sdk.debug.utils.AndroidUtils;

public class JMRTCActivity extends Activity implements View.OnClickListener {

    private static final String TAG = JMRTCActivity.class.getSimpleName();
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    LinearLayout contentLayout;
    EditText etInvitedUsername;
    LinearLayout surfaceViewContainer;
    TextView tvLog;
    ScrollView svLog;
    LinearLayout controlPanel;
    TableRow firstRow;
    LongSparseArray<SurfaceView> surfaceViewCache = new LongSparseArray<SurfaceView>();
    UserInfo myinfo = JMessageClient.getMyInfo();

    private JMRtcSession session;//通话数据元信息对象
    boolean requestPermissionSended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jmrtc);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        etInvitedUsername = (EditText) contentLayout.findViewById(R.id.et_invited_user);
        surfaceViewContainer = (LinearLayout) contentLayout.findViewById(R.id.surface_container);
        controlPanel = (LinearLayout) contentLayout.findViewById(R.id.control_panel);
        firstRow = (TableRow) findViewById(R.id.tr_first_row);

        svLog = (ScrollView) contentLayout.findViewById(R.id.sv_log);
        tvLog = (TextView) contentLayout.findViewById(R.id.tv_log);

        findViewById(R.id.btn_audio_call).setOnClickListener(this);
        findViewById(R.id.btn_video_call).setOnClickListener(this);
        findViewById(R.id.btn_invite).setOnClickListener(this);
        findViewById(R.id.btn_accept).setOnClickListener(this);
        findViewById(R.id.btn_refuse).setOnClickListener(this);
        findViewById(R.id.btn_hangup).setOnClickListener(this);
        findViewById(R.id.btn_resolution).setOnClickListener(this);
        contentLayout.findViewById(R.id.btn_enable_video).setOnClickListener(this);
        contentLayout.findViewById(R.id.btn_enable_audio).setOnClickListener(this);
        contentLayout.findViewById(R.id.btn_enable_speakerphone).setOnClickListener(this);
        contentLayout.findViewById(R.id.btn_switch_camara).setOnClickListener(this);
        contentLayout.findViewById(R.id.btn_session_print).setOnClickListener(this);
        JMRtcClient.getInstance().initEngine(jmRtcListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.jmrtc_menu, menu);
        initCheckableMenuItem(menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestPermissionSended) {
            if (AndroidUtils.checkPermission(this, REQUIRED_PERMISSIONS)) {
                JMRtcClient.getInstance().reinitEngine();
            } else {
                Toast.makeText(this, "缺少必要权限，音视频引擎初始化失败，请在设置中打开对应权限", Toast.LENGTH_LONG).show();
            }
        }
        requestPermissionSended = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMRtcClient.getInstance().releaseEngine();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        JMRtcClient.VideoProfile videoProfile = null;
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.video_240P:
                videoProfile = JMRtcClient.VideoProfile.Profile_240P;
                break;
            case R.id.video_360P:
                videoProfile = JMRtcClient.VideoProfile.Profile_360P;
                break;
            case R.id.video_480P:
                videoProfile = JMRtcClient.VideoProfile.Profile_480P;
                break;
            case R.id.video_720P:
                videoProfile = JMRtcClient.VideoProfile.Profile_720P;
                break;
        }
        JMRtcClient.getInstance().setVideoProfile(videoProfile);
        return true;
    }

    private void initCheckableMenuItem(Menu menu) {
        JMRtcClient.VideoProfile videoProfile = JMRtcClient.getInstance().getVideoProfile();
        switch (videoProfile) {
            case Profile_240P:
                menu.getItem(0).setChecked(true);
                break;
            case Profile_360P:
                menu.getItem(1).setChecked(true);
                break;
            case Profile_480P:
                menu.getItem(2).setChecked(true);
                break;
            case Profile_720P:
                menu.getItem(3).setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String username;
        switch (v.getId()) {
            case R.id.btn_video_call:
                username = etInvitedUsername.getText().toString();
                if (!TextUtils.isEmpty(username.trim())) {
                    startCall(username, JMSignalingMessage.MediaType.VIDEO);
                } else {
                    Toast.makeText(getApplicationContext(), "请输入对方用户名", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_audio_call:
                username = etInvitedUsername.getText().toString();
                if (!TextUtils.isEmpty(username.trim())) {
                    startCall(username, JMSignalingMessage.MediaType.AUDIO);
                } else {
                    Toast.makeText(getApplicationContext(), "请输入对方用户名", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_resolution:
                openOptionsMenu();
                break;
            case R.id.btn_invite:
                username = etInvitedUsername.getText().toString();
                if (!TextUtils.isEmpty(username.trim())) {
                    inviteUser(username);
                } else {
                    Toast.makeText(getApplicationContext(), "请输入对方用户名", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_accept:
                acceptCall();
                break;
            case R.id.btn_refuse:
                refuseCall();
                break;
            case R.id.btn_hangup:
                hangUp();
                break;
            case R.id.btn_enable_video:
                CheckBox enableVideo = (CheckBox) v;
                JMRtcClient.getInstance().enableVideo(enableVideo.isChecked());
                //是否需要停止本地视频预览。
                SurfaceView localSurfaceView = surfaceViewCache.get(myinfo.getUserID());
                if (null != localSurfaceView) {
                    localSurfaceView.setVisibility(enableVideo.isChecked() ? View.VISIBLE : View.GONE);
                }
                break;
            case R.id.btn_enable_audio:
                CheckBox enableAudio = (CheckBox) v;
                JMRtcClient.getInstance().enableAudio(enableAudio.isChecked());
                break;
            case R.id.btn_enable_speakerphone:
                CheckBox enableSpeakerphone = (CheckBox) v;
                JMRtcClient.getInstance().enableSpeakerphone(enableSpeakerphone.isChecked());
                break;
            case R.id.btn_switch_camara:
                JMRtcClient.getInstance().switchCamera();
                break;
            case R.id.btn_session_print:
                printSession();
                break;
        }
    }

    private void printSession() {
        String textToShow = (null == session) ? "当前不在通话中" : session.toString();
        postTextToDisplay("printSession", 0, "", textToShow, null);
    }

    private void initControlPanel(boolean isVideoEnabled) {
        controlPanel.setVisibility(View.VISIBLE);
        CheckBox enableVideo = (CheckBox) contentLayout.findViewById(R.id.btn_enable_video);
        Button switchCamera = (Button) contentLayout.findViewById(R.id.btn_switch_camara);

        enableVideo.setVisibility(isVideoEnabled ? View.VISIBLE : View.INVISIBLE);
        enableVideo.setChecked(isVideoEnabled);
        switchCamera.setVisibility(isVideoEnabled ? View.VISIBLE : View.INVISIBLE);
        ((CheckBox) contentLayout.findViewById(R.id.btn_enable_audio)).setChecked(true);
        ((CheckBox) contentLayout.findViewById(R.id.btn_enable_speakerphone)).setChecked(false);
    }

    private void startCall(String username, final JMSignalingMessage.MediaType mediaType) {
        JMessageClient.getUserInfo(username, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                Log.d(TAG, "get user info complete. code = " + responseCode + " msg = " + responseMessage);
                if (null != info) {
                    JMRtcClient.getInstance().call(Collections.singletonList(info), mediaType, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            Log.d(TAG, "call send complete . code = " + responseCode + " msg = " + responseMessage);
                            postTextToDisplay("startCall", responseCode, responseMessage, "发起通话成功,等待对方响应", "发起失败");
                        }
                    });
                } else {
                    postTextToDisplay("startCall", responseCode, responseMessage, "发起失败", "发起失败");
                }
            }
        });
    }

    private void inviteUser(String username) {
        JMessageClient.getUserInfo(username, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                Log.d(TAG, "get user info complete. code = " + responseCode + " msg = " + responseMessage);
                if (null != info) {
                    JMRtcClient.getInstance().invite(Collections.singletonList(info), new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            Log.d(TAG, "invite send complete . code = " + responseCode + " msg = " + responseMessage);
                            postTextToDisplay("inviteUser", responseCode, responseMessage, "邀请用户成功，等待对方响应", "邀请失败");
                        }
                    });
                } else {
                    postTextToDisplay("inviteUser", responseCode, responseMessage, "邀请用户失败", "邀请用户失败");
                }
            }
        });
    }

    private void acceptCall() {
        JMRtcClient.getInstance().accept(new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                Log.d(TAG, "accept call!. code = " + responseCode + " msg = " + responseMessage);
                postTextToDisplay("accceptCall", responseCode, responseMessage, "接听电话成功", "接听失败");
            }
        });
    }

    private void refuseCall() {
        JMRtcClient.getInstance().refuse(new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                Log.d(TAG, "refuse call!. code = " + responseCode + " msg = " + responseMessage);
                postTextToDisplay("refuseCall", responseCode, responseMessage, "拒听电话成功", "拒听失败");
            }
        });
    }

    private void hangUp() {
        JMRtcClient.getInstance().hangup(new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                Log.d(TAG, "hangup call!. code = " + responseCode + " msg = " + responseMessage);
                postTextToDisplay("hangUp", responseCode, responseMessage, "挂断成功", "挂断失败");
            }
        });
    }

    JMRtcListener jmRtcListener = new JMRtcListener() {
        @Override
        public void onEngineInitComplete(final int errCode, final String errDesc) {
            super.onEngineInitComplete(errCode, errDesc);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "音视频引擎初始化完成。 errCode = " + errCode + " err Desc = " + errDesc, Toast.LENGTH_LONG).show();
                }
            });

        }

        @Override
        public void onCallOutgoing(JMRtcSession callSession) {
            super.onCallOutgoing(callSession);
            Log.d(TAG, "onCallOutgoing invoked!. session = " + callSession);
            postTextToDisplay("onCallOutgoing", 0, "", "通话已播出 " +
                    "\nsession" + " " + callSession.toString(), "");
            session = callSession;
        }

        @Override
        public void onCallInviteReceived(JMRtcSession callSession) {
            super.onCallInviteReceived(callSession);
            Log.d(TAG, "onCallInviteReceived invoked!. session = " + callSession);
            postTextToDisplay("onCallInviteReceived", 0, "", "收到通话邀请 " +
                    "\nsession" + " " + callSession.toString(), "");
            session = callSession;
        }

        @Override
        public void onCallOtherUserInvited(UserInfo fromUserInfo, List<UserInfo> invitedUserInfos, JMRtcSession callSession) {
            super.onCallOtherUserInvited(fromUserInfo, invitedUserInfos, callSession);
            Log.d(TAG, "onCallOtherUserInvited invoked!. session = " + callSession + " from user = " + fromUserInfo
                    + " invited user = " + invitedUserInfos);
            postTextToDisplay("onCallOtherUserInvited", 0, "", "通话中有其他人被邀请"
                    + "\ninviter =  " + fromUserInfo.getUserID()
                    + "\ninvited user =  " + invitedUserInfos
                    + "\nsession" + " " + callSession.toString(), "");
            session = callSession;
        }

        //主线程回调
        @Override
        public void onCallConnected(JMRtcSession callSession, SurfaceView localSurfaceView) {
            super.onCallConnected(callSession, localSurfaceView);
            Log.d(TAG, "onCallConnected invoked!. session = " + callSession + " localSerfaceView = " + localSurfaceView);
            postTextToDisplay("onCallConnected", 0, "", "通话连接已建立" +
                    "\nsession" + " " + callSession.toString(), "");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            localSurfaceView.setLayoutParams(layoutParams);
            surfaceViewCache.append(myinfo.getUserID(), localSurfaceView);
            surfaceViewContainer.addView(localSurfaceView);
            initControlPanel(JMSignalingMessage.MediaType.VIDEO == callSession.getMediaType());
            firstRow.setVisibility(View.GONE);
            session = callSession;
        }

        //主线程回调
        @Override
        public void onCallMemberJoin(UserInfo joinedUserInfo, SurfaceView remoteSurfaceView) {
            super.onCallMemberJoin(joinedUserInfo, remoteSurfaceView);
            Log.d(TAG, "onCallMemberJoin invoked!. joined user  = " + joinedUserInfo + " remoteSerfaceView = " + remoteSurfaceView);
            postTextToDisplay("onCallMemberJoin", 0, "", "有其他人加入通话"
                    + "\njoinedUser = " + joinedUserInfo, "");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            remoteSurfaceView.setLayoutParams(layoutParams);
            surfaceViewCache.append(joinedUserInfo.getUserID(), remoteSurfaceView);
            surfaceViewContainer.addView(remoteSurfaceView);
        }

        @Override
        public void onPermissionNotGranted(final String[] requiredPermissions) {
            Log.d(TAG, "[onPermissionNotGranted] permission = " + requiredPermissions.length);
            try {
                AndroidUtils.requestPermission(JMRTCActivity.this, requiredPermissions);
                requestPermissionSended = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCallMemberOffline(final UserInfo leavedUserInfo, JMRtcClient.DisconnectReason reason) {
            super.onCallMemberOffline(leavedUserInfo, reason);
            Log.d(TAG, "onCallMemberOffline invoked!. leave user = " + leavedUserInfo + " reason = " + reason);
            postTextToDisplay("onCallMemberOffline", 0, "", "有人退出通话"
                    + "\nleavedUser = " + leavedUserInfo
                    + "\nreason = " + reason, "");
            surfaceViewContainer.post(new Runnable() {
                @Override
                public void run() {
                    SurfaceView cachedSurfaceView = surfaceViewCache.get(leavedUserInfo.getUserID());
                    if (null != cachedSurfaceView) {
                        surfaceViewCache.remove(leavedUserInfo.getUserID());
                        surfaceViewContainer.removeView(cachedSurfaceView);
                    }
                }
            });
        }

        @Override
        public void onCallDisconnected(JMRtcClient.DisconnectReason reason) {
            super.onCallDisconnected(reason);
            Log.d(TAG, "onCallDisconnected invoked!. reason = " + reason);
            postTextToDisplay("onCallDisconnected", 0, "", "本地通话连接断开"
                    + "\nreason = " + reason, "");
            surfaceViewContainer.post(new Runnable() {
                @Override
                public void run() {
                    surfaceViewCache.clear();
                    surfaceViewContainer.removeAllViews();
                    controlPanel.setVisibility(View.GONE);
                    firstRow.setVisibility(View.VISIBLE);
                }
            });
            session = null;
        }

        @Override
        public void onCallError(int errorCode, String desc) {
            super.onCallError(errorCode, desc);
            Log.d(TAG, "onCallError invoked!. errCode = " + errorCode + " desc = " + desc);
            postTextToDisplay("onCallError", 0, "", "通话发生错误"
                    + "\nerrorCode = " + errorCode
                    + "\ndesc = " + desc, "");
            session = null;
        }

        @Override
        public void onRemoteVideoMuted(UserInfo remoteUser, boolean isMuted) {
            super.onRemoteVideoMuted(remoteUser, isMuted);
            Log.d(TAG, "onRemoteVideoMuted invoked!. remote user = " + remoteUser + " isMuted = " + isMuted);
            SurfaceView remoteSurfaceView = surfaceViewCache.get(remoteUser.getUserID());
            if (null != remoteSurfaceView) {
                remoteSurfaceView.setVisibility(isMuted ? View.GONE : View.VISIBLE);
            }
        }
    };

    private void postTextToDisplay(final String tag, final int statusCode, final String responseMsg, final String successText, final String failText) {
        tvLog.post(new Runnable() {
            @Override
            public void run() {
                tvLog.append("-------------------\n");
                tvLog.append("[" + tag + "]");
                tvLog.append(" statusCode = " + statusCode);
                tvLog.append(" responseMsg = " + responseMsg + "\n");
                if (0 == statusCode) {
                    tvLog.append(successText);
                } else {
                    tvLog.append(failText);
                }
                tvLog.append("\n-------------------\n");

                tvLog.post(new Runnable() {
                    @Override
                    public void run() {
                        svLog.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
    }

}
