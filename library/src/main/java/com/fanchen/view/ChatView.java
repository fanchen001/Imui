package com.fanchen.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanchen.chat.listener.OnSelectButtonListener;
import com.fanchen.message.messages.ViewHolderController;
import com.fanchen.ui.R;
import com.fanchen.chat.ChatInputView;
import com.fanchen.chat.listener.CustomMenuEventListener;
import com.fanchen.chat.listener.OnCameraCallbackListener;
import com.fanchen.chat.listener.OnMenuClickListener;
import com.fanchen.chat.listener.OnRecordVoiceListener;
import com.fanchen.chat.menu.Menu;
import com.fanchen.chat.menu.MenuManager;
import com.fanchen.chat.record.RecordVoiceButton;
import com.fanchen.message.MessageListView;
import com.fanchen.message.messages.MsgListAdapter;
import com.fanchen.message.messages.ptr.PtrDefaultHeader;
import com.fanchen.message.messages.ptr.PullToRefreshLayout;
import com.fanchen.message.utils.DisplayUtil;
import com.fanchen.video.JZUtils;
import com.fanchen.video.Jzvd;

public class ChatView extends RelativeLayout implements CustomMenuEventListener,
        View.OnTouchListener, SensorEventListener, View.OnClickListener, ChatInputView.OnWindowChangeListener {

    private RelativeLayout mTitleContainer;
    private MessageListView mMsgList;
    private ChatInputView mChatInput;
    private PullToRefreshLayout mPtrLayout;

    private BroadcastReceiver mReceiver;

    private InputMethodManager mImm;
    private Window mWindow;
    private Activity mActivity;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private AudioManager mAudioManager;

    public ChatView(Context context) {
        this(context, null);
    }

    public ChatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_chat_wrap, this);
        Activity activity = JZUtils.scanForActivity(context);
        if (activity != null) {
            mActivity = activity;
            mWindow = mActivity.getWindow();
            mImm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        initModule();
    }

    @SuppressLint("InvalidWakeLockTag")
    public void registerDefaultWakeLock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPowerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "ChatView");
            mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        }
    }

    public void unregisterDefaultWakeLock() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    public RelativeLayout getTitleContainer() {
        return mTitleContainer;
    }

    public TextView getTitleTextView() {
        return mTitleContainer.findViewById(R.id.tv_chat_title);
    }

    private void initModule() {
        mTitleContainer = findViewById(R.id.title_container);
        View viewById = findViewById(R.id.iv_chat_back);
        if (viewById != null) viewById.setOnClickListener(this);
        mMsgList = findViewById(R.id.msg_list);
        mChatInput = findViewById(R.id.chat_input);
        mPtrLayout = findViewById(R.id.pull_to_refresh_layout);
        mChatInput.setUseKeyboardHeight(false);
        mChatInput.setOnWindowChangeListener(this);
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int defaultHeight = systemService.getDefaultDisplay().getHeight() / 3;
        mChatInput.setMenuContainerHeight(defaultHeight);
        PtrDefaultHeader header = new PtrDefaultHeader(getContext());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new LayoutParams(-1, -2));
        header.setPadding(0, DisplayUtil.dp2px(getContext(), 15), 0, DisplayUtil.dp2px(getContext(), 10));
        header.setPtrFrameLayout(mPtrLayout);
//        mMsgList.setDateBgColor(Color.parseColor("#FF4081"));
//        mMsgList.setDatePadding(5, 10, 10, 5);
//        mMsgList.setEventTextPadding(5);
//        mMsgList.setEventBgColor(Color.parseColor("#34A350"));
//        mMsgList.setDateBgCornerRadius(15);
        mMsgList.setHasFixedSize(true);
        mPtrLayout.setLoadingMinTime(1000);
        mPtrLayout.setDurationToCloseHeader(1500);
        mPtrLayout.setHeaderView(header);
        mPtrLayout.addPtrUIHandler(header);
        // 下拉刷新时，内容固定，只有 Header 变化
        mPtrLayout.setPinContent(true);
//        mMsgList.setShowReceiverDisplayName(true);
//        mMsgList.setShowSenderDisplayName(false);
    }

    public void customLiftRightBuild(String tag,String lift,String right, BaseAdapter adapter, AdapterView.OnItemClickListener l) {
        GridView v = (GridView)View.inflate(getContext(), R.layout.menu_more_feature, null);
        v.setOnItemClickListener(l);
        v.setAdapter(adapter);
        MenuManager menuManager = mChatInput.getMenuManager();
        menuManager.addCustomMenu(tag,  View.inflate(getContext(), R.layout.menu_item_more, null), v);
        menuManager.setMenu(Menu.newBuilder().customize(true).setRight(right).setLeft(lift).
                setBottom(Menu.TAG_EMOJI, Menu.TAG_VIDEO, Menu.TAG_GALLERY, Menu.TAG_CAMERA, tag).build());
        menuManager.setCustomMenuClickListener(this);
    }

    public void customRightBuild(String tag,String right, BaseAdapter adapter, AdapterView.OnItemClickListener l) {
        GridView v = (GridView)View.inflate(getContext(), R.layout.menu_more_feature, null);
        v.setOnItemClickListener(l);
        v.setAdapter(adapter);
        MenuManager menuManager = mChatInput.getMenuManager();
        menuManager.addCustomMenu(tag, View.inflate(getContext(), R.layout.menu_item_more, null), v);
        menuManager.setMenu(Menu.newBuilder().customize(true).setRight(right).
                setBottom(Menu.TAG_VOICE,Menu.TAG_EMOJI, Menu.TAG_VIDEO, Menu.TAG_GALLERY, Menu.TAG_CAMERA, tag).build());
        menuManager.setCustomMenuClickListener(this);
    }

    public void customBuild(String tag, BaseAdapter adapter, AdapterView.OnItemClickListener l) {
        GridView v = (GridView)View.inflate(getContext(), R.layout.menu_more_feature, null);
        v.setOnItemClickListener(l);
        v.setAdapter(adapter);
        MenuManager menuManager = mChatInput.getMenuManager();
        menuManager.addCustomMenu(tag, View.inflate(getContext(), R.layout.menu_item_more, null), v);
        menuManager.setMenu(Menu.newBuilder().customize(true).setRight(Menu.TAG_SEND).
                setBottom(Menu.TAG_VOICE,Menu.TAG_EMOJI, Menu.TAG_VIDEO, Menu.TAG_GALLERY, Menu.TAG_CAMERA, tag).build());
        menuManager.setCustomMenuClickListener(this);
    }

    public void scrollToVisibleItem() {
        RecyclerView.LayoutManager layoutManager = getMessageListView().getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            postDelayed(new DelayedRunnable(position), 200);
        }
    }

    public void defaultMenuBuild() {
        MenuManager menuManager = mChatInput.getMenuManager();
        menuManager.setMenu(Menu.newBuilder().customize(true).setRight(Menu.TAG_SEND).
                setBottom(Menu.TAG_VOICE, Menu.TAG_EMOJI, Menu.TAG_GALLERY, Menu.TAG_CAMERA).
                build());
    }

    public PullToRefreshLayout getPtrLayout() {
        return mPtrLayout;
    }

    public void setMenuClickListener(OnMenuClickListener listener) {
        mChatInput.setMenuClickListener(listener);
    }

    public void setOnSelectButtonListener(OnSelectButtonListener mOnSelectButtonListener) {
        getChatInputView().setOnSelectButtonListener(mOnSelectButtonListener);
    }

    public void setAdapter(MsgListAdapter adapter) {
        setAdapter(adapter, false);
    }

    public void setAdapter(MsgListAdapter adapter, boolean supportAnimations) {
        RecyclerView.ItemAnimator itemAnimator = mMsgList.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(supportAnimations);
        }
        mMsgList.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mMsgList.setLayoutManager(layoutManager);
    }

    public void setRecordVoiceFile(String path, String fileName) {
        RecordVoiceButton recordVoiceButton = mChatInput.getRecordVoiceButton();
        if(recordVoiceButton != null){
            recordVoiceButton.setVoiceFilePath(path, fileName);
        }
    }

    public void setCameraCaptureFile(String path, String fileName) {
        mChatInput.setCameraCaptureFile(path, fileName);
    }

    public void setRecordVoiceListener(OnRecordVoiceListener listener) {
        mChatInput.setRecordVoiceListener(listener);
    }

    public void setOnCameraCallbackListener(OnCameraCallbackListener listener) {
        mChatInput.setOnCameraCallbackListener(listener);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setOnTouchListener(OnTouchListener listener) {
        mMsgList.setOnTouchListener(listener);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void defaultOnTouchListener() {
        mMsgList.setOnTouchListener(this);
        mChatInput.getInputView().setOnTouchListener(this);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void registerDefaultListener(){
        defaultOnTouchListener();
        registerDefaultWakeLock();
        unregistertDetectReceiver();
    }

    public void unregisterDefaultListener(){
        unregistertDetectReceiver();
        unregisterDefaultWakeLock();
    }

    public ChatInputView getChatInputView() {
        return mChatInput;
    }

    public MessageListView getMessageListView() {
        return mMsgList;
    }

    @Override
    public boolean onMenuItemClick(String tag, View menuItem) {
        scrollToVisibleItem();
        return true;
    }

    @Override
    public void onMenuFeatureVisibilityChanged(int visibility, String tag, View menuFeature) {
    }

    public void scrollToBottom(int delayed) {
        postDelayed(new DelayedRunnable(0), delayed);
    }

    public void scrollToBottom() {
        scrollToBottom(300);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == getMessageListView()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mChatInput.getMenuState() == View.VISIBLE) {
                        mChatInput.dismissMenuLayout();
                    }
                    hideSoftInput(v);
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    break;
            }
        } else {
            scrollToBottom();
        }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mAudioManager == null || mAudioManager.isBluetoothA2dpOn() || mAudioManager.isWiredHeadsetOn()) {
            return;
        }
        MsgListAdapter<?> adapter = getMsgListAdapter();
        if (mSensor != null && adapter != null && adapter.getMediaPlayer().isPlaying()) {
            float distance = event.values[0];
            if (distance >= mSensor.getMaximumRange()) {
                adapter.setAudioPlayByEarPhone(0);
                setScreen(true);
            } else {
                adapter.setAudioPlayByEarPhone(2);
                ViewHolderController.getInstance().replayVoice();
                setScreen(false);
            }
        } else {
            if (mWakeLock != null && mWakeLock.isHeld()) {
                try {
                    mWakeLock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mWakeLock = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean onBackPressed() {
        return Jzvd.backPress();
    }

    public void onPause() {
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_chat_back) {
            if (mActivity != null) {
                mActivity.finish();
            }
        }
    }

    @Override
    public void onWindowChange(boolean full) {
        View viewById = findViewById(R.id.abl_title_container);
        if(viewById == null || mPtrLayout == null){
            return;
        }
        mPtrLayout.setVisibility(full ? View.GONE : View.VISIBLE);
        viewById.setVisibility(full ? View.GONE : View.VISIBLE);
    }

    public boolean onBack(){
        return mChatInput != null && mChatInput.onBack();
    }

    private class DelayedRunnable implements Runnable {
        private int firstVisibleItemPosition;

        public DelayedRunnable(int firstVisibleItemPosition) {
            this.firstVisibleItemPosition = firstVisibleItemPosition;
        }

        @Override
        public void run() {
            final MessageListView messageListView = getMessageListView();
            if (messageListView == null) return;
            RecyclerView.Adapter adapter = messageListView.getAdapter();
            if (adapter == null) return;
            if (adapter.getItemCount() > firstVisibleItemPosition && firstVisibleItemPosition >= 0) {
                messageListView.smoothScrollToPosition(firstVisibleItemPosition);
            }
        }

    }

    private void hideSoftInput(View v) {
        if (mActivity != null) {
            View view = mActivity.getCurrentFocus();
            if (mImm != null && view != null) {
                mImm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                v.clearFocus();
            }
        }
    }

    public void unregistertDetectReceiver() {
        try {
            getContext().unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registertDetectReceiver() {
        try {
            mReceiver = new HeadsetDetectReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
            getContext().registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MsgListAdapter<?> getMsgListAdapter() {
        MessageListView messageListView = getMessageListView();
        if (messageListView == null) return null;
        RecyclerView.Adapter adapter = messageListView.getAdapter();
        if (adapter == null) return null;
        if (!(adapter instanceof MsgListAdapter<?>)) return null;
        return (MsgListAdapter<?>) adapter;
    }

    @SuppressLint({"WakelockTimeout", "InvalidWakeLockTag"})
    private void setScreen(boolean bool) {
        if (bool) {
            if (mWakeLock != null) {
                try {
                    mWakeLock.setReferenceCounted(false);
                    mWakeLock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mWakeLock = null;
        } else {
            if (mWakeLock == null && mPowerManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "ChatView");
                }
            }
            if (mWakeLock != null) {
                try {
                    mWakeLock.acquire();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class HeadsetDetectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MsgListAdapter<?> adapter = getMsgListAdapter();
            if (adapter != null && intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_HEADSET_PLUG) && intent.hasExtra("state")) {
                int state = intent.getIntExtra("state", 0);
                adapter.setAudioPlayByEarPhone(state);
            }
        }

    }

}
