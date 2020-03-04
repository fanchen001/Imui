package com.fanchen.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanchen.chat.listener.OnSelectButtonListener;
import com.fanchen.ui.R;
import com.fanchen.chat.ChatInputView;
import com.fanchen.chat.listener.CustomMenuEventListener;
import com.fanchen.chat.listener.OnCameraCallbackListener;
import com.fanchen.chat.listener.OnClickEditTextListener;
import com.fanchen.chat.listener.OnMenuClickListener;
import com.fanchen.chat.listener.OnRecordVoiceListener;
import com.fanchen.chat.menu.Menu;
import com.fanchen.chat.menu.MenuManager;
import com.fanchen.chat.menu.view.MenuFeature;
import com.fanchen.chat.menu.view.MenuItem;
import com.fanchen.chat.record.RecordVoiceButton;
import com.fanchen.message.MessageListView;
import com.fanchen.message.messages.MsgListAdapter;
import com.fanchen.message.messages.ptr.PtrDefaultHeader;
import com.fanchen.message.messages.ptr.PullToRefreshLayout;
import com.fanchen.message.utils.DisplayUtil;
import com.fanchen.video.JZUtils;

public class ChatView extends RelativeLayout implements CustomMenuEventListener {

    private RelativeLayout mTitleContainer;
    private MessageListView mMsgList;
    private ChatInputView mChatInput;
    private RecordVoiceButton mRecordVoiceBtn;
    private PullToRefreshLayout mPtrLayout;

    public ChatView(Context context) {
        this(context, null);
    }

    public ChatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_chat_wrap, this);
        initModule();
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
        if (viewById != null) {
            viewById.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = JZUtils.scanForActivity(getContext());
                    if (activity != null) {
                        activity.finish();
                    }
                }
            });
        }
        mMsgList = findViewById(R.id.msg_list);
        mChatInput = findViewById(R.id.chat_input);
        mPtrLayout = findViewById(R.id.pull_to_refresh_layout);
        mChatInput.setUseKeyboardHeight(false);
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int defaultHeight = systemService.getDefaultDisplay().getHeight() / 3;
        mChatInput.setMenuContainerHeight(defaultHeight);
        mRecordVoiceBtn = mChatInput.getRecordVoiceButton();
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
        // set show display name or not
//        mMsgList.setShowReceiverDisplayName(true);
//        mMsgList.setShowSenderDisplayName(false);
        // add Custom Menu View
//        MenuManager menuManager = mChatInput.getMenuManager();
//        menuManager.addCustomMenu("MY_CUSTOM", R.layout.menu_text_item, R.layout.menu_text_feature);
////        // Custom menu order
//        menuManager.setMenu(Menu.newBuilder().
//                customize(true).
//                setRight(Menu.TAG_SEND).
//                setBottom(Menu.TAG_VOICE, Menu.TAG_EMOJI, Menu.TAG_GALLERY, Menu.TAG_CAMERA, "MY_CUSTOM").
//                build());

//        menuManager.setCustomMenuClickListener(new CustomMenuEventListener() {
//            @Override
//            public boolean onMenuItemClick(String tag, MenuItem menuItem) {
//                return true;
//            }
//
//            @Override
//            public void onMenuFeatureVisibilityChanged(int visibility, String tag, MenuFeature menuFeature) {
//                if (visibility == View.VISIBLE) {
//                } else {
//                }
//            }
//        });

    }

    public void customMenuBuild(String tag, BaseAdapter adapter, AdapterView.OnItemClickListener l) {
        MenuFeature inflate = (MenuFeature) View.inflate(getContext(), R.layout.menu_more_feature, null);
        GridView v = inflate.findViewById(R.id.menu_feature_grid);
        v.setOnItemClickListener(l);
        v.setAdapter(adapter);
        MenuManager menuManager = mChatInput.getMenuManager();
        menuManager.addCustomMenu(tag, (MenuItem) View.inflate(getContext(), R.layout.menu_item_more, null), inflate);
        menuManager.setMenu(Menu.newBuilder().customize(true).setRight(Menu.TAG_SEND).
                setBottom(Menu.TAG_VOICE, Menu.TAG_EMOJI, Menu.TAG_VIDEO, Menu.TAG_GALLERY, Menu.TAG_CAMERA, tag).build());
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
        getChatInputView().setOnSelectButtonListener( mOnSelectButtonListener);
    }

    public void setAdapter(MsgListAdapter adapter) {
        mMsgList.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mMsgList.setLayoutManager(layoutManager);
    }

    public void setRecordVoiceFile(String path, String fileName) {
        mRecordVoiceBtn.setVoiceFilePath(path, fileName);
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

    public void setOnTouchListener(OnTouchListener listener) {
        mMsgList.setOnTouchListener(listener);
    }

    public void setOnTouchEditTextListener(OnClickEditTextListener listener) {
        mChatInput.setOnClickEditTextListener(listener);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public ChatInputView getChatInputView() {
        return mChatInput;
    }

    public MessageListView getMessageListView() {
        return mMsgList;
    }

    @Override
    public boolean onMenuItemClick(String tag, MenuItem menuItem) {
        scrollToVisibleItem();
        return true;
    }

    @Override
    public void onMenuFeatureVisibilityChanged(int visibility, String tag, MenuFeature menuFeature) {

    }

    private class DelayedRunnable implements Runnable {
        private int firstVisibleItemPosition;

        public DelayedRunnable(int firstVisibleItemPosition) {
            this.firstVisibleItemPosition = firstVisibleItemPosition;
        }

        @Override
        public void run() {
            getMessageListView().smoothScrollToPosition(firstVisibleItemPosition);
        }

    }
}
