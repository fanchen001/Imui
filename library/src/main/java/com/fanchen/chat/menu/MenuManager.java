package com.fanchen.chat.menu;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanchen.chat.ChatInputStyle;
import com.fanchen.chat.ChatInputView;
import com.fanchen.chat.emoji.EmoticonsKeyboardUtils;
import com.fanchen.chat.listener.CustomMenuEventListener;
import com.fanchen.chat.menu.collection.MenuCollection;
import com.fanchen.chat.menu.collection.MenuFeatureCollection;
import com.fanchen.chat.menu.collection.MenuItemCollection;
import com.fanchen.chat.utils.SimpleCommonUtils;
import com.fanchen.ui.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MenuManager implements MenuCollection.MenuCollectionChangedListener, View.OnClickListener {
    public static final String TAG = SimpleCommonUtils.formatTag(MenuManager.class.getSimpleName());

    private ChatInputView mChatInputView;
    private LinearLayout mChatInputContainer;
    private LinearLayout mMenuItemContainer;
    //    private FrameLayout mMenuContainer;
    private Context mContext;
    private View lastMenuFeature;
    private MenuItemCollection mMenuItemCollection;
    private MenuFeatureCollection mMenuFeatureCollection;
    private CustomMenuEventListener mCustomMenuEventListener;
    private ImageButton mVoiceBtn;
    private ImageButton mPhotoBtn;
    private ImageButton mCameraBtn;
    private ImageButton mSendBtn;
    private TextView mSendCountTv;
    private View mVoiceBtnContainer;
    private View mPhotoBtnContainer;
    private View mCameraBtnContainer;
    private View mEmojiBtnContainer;
    private View mVideoBtnContainer;
    private View.OnClickListener mClickListener;
    private ChatInputStyle mStyle;

    private List<View> mMenus = new ArrayList<>();

    public MenuManager(ChatInputView chatInputView) {
        mChatInputView = chatInputView;
        mContext = chatInputView.getContext();
        mChatInputContainer = chatInputView.getChatInputContainer();
        mMenuItemContainer = chatInputView.getMenuItemContainer();
        initCollection();
        initDefaultMenu();
    }

    private void initCollection() {
        mMenuItemCollection = new MenuItemCollection(mContext);
        mMenuItemCollection.setMenuCollectionChangedListener(this);
        mMenuFeatureCollection = new MenuFeatureCollection(mContext);
        mMenuFeatureCollection.setMenuCollectionChangedListener(new MenuCollection.MenuCollectionChangedListener() {
            @Override
            public void addMenu(String menuTag, View menu) {
                FrameLayout menuContainer = mChatInputView.getMenuContainer();
                if (menuContainer == null) {
                    mMenus.add(menu);
                } else {
                    menuContainer.addView(menu);
                }
            }
        });
    }

    private void showMenuFeatureByTag(String tag) {
        View menuFeature = mMenuFeatureCollection.get(tag);
        if (menuFeature == null) {
            return;
        }
        FrameLayout container = mChatInputView.getMenuContainer();
        if (menuFeature.getVisibility() == VISIBLE && container != null && container.getVisibility() == VISIBLE) {
            mChatInputView.dismissMenuLayout();
            return;
        }
        if (mChatInputView.isKeyboardVisible()) {
            mChatInputView.setPendingShowMenu(true);
            EmoticonsKeyboardUtils.closeSoftKeyboard(mChatInputView.getInputView());
        } else {
            mChatInputView.showMenuLayout();
        }
        mChatInputView.hideDefaultMenuLayout();
        hideCustomMenu();
        menuFeature.setVisibility(VISIBLE);
        if (mCustomMenuEventListener != null)
            mCustomMenuEventListener.onMenuFeatureVisibilityChanged(VISIBLE, tag, menuFeature);
        lastMenuFeature = menuFeature;
    }

    public void hideCustomMenu() {
        if (lastMenuFeature != null && lastMenuFeature.getVisibility() != GONE) {
            lastMenuFeature.setVisibility(View.GONE);
            if (mCustomMenuEventListener != null)
                mCustomMenuEventListener.onMenuFeatureVisibilityChanged(GONE, (String) lastMenuFeature.getTag(), lastMenuFeature);
        }
    }

    private void initDefaultMenu() {
        addBottomByTag(Menu.TAG_VOICE, Menu.TAG_VIDEO, Menu.TAG_GALLERY, Menu.TAG_CAMERA, Menu.TAG_EMOJI, Menu.TAG_SEND);
    }

    public void setMenu(Menu menu) {
        if (!menu.isCustomize()) return;
        mMenuItemContainer.removeAllViews();
        addViews(mChatInputContainer, 1, menu.getLeft());
        addViews(mChatInputContainer, mChatInputContainer.getChildCount() - 1, menu.getRight());
        addBottomByTag(menu.getBottom());
    }

    private void addBottomByTag(String... tags) {
        if (tags == null || tags.length == 0) {
            mChatInputView.setShowBottomMenu(false);
            return;
        }
        mChatInputView.setShowBottomMenu(true);
        addViews(mMenuItemContainer, -1, tags);
        findView(mMenuItemContainer);
    }

    private void addViews(LinearLayout parent, int index, String... tags) {
        if (parent == null || tags == null)
            return;
        for (String tag : tags) {
            View child = mMenuItemCollection.get(tag);
            if (child == null) {
                continue;
            }
            parent.addView(child, index);
        }
    }

    public MenuItemCollection getMenuItemCollection() {
        return mMenuItemCollection;
    }

    public MenuFeatureCollection getMenuFeatureCollection() {
        return mMenuFeatureCollection;
    }

    public void addCustomMenu(String tag, View menuItem, View menuFeature) {
        mMenuItemCollection.addCustomMenuItem(tag, menuItem);
        mMenuFeatureCollection.addMenuFeature(tag, menuFeature);
    }

    public void addCustomMenu(String tag, int menuItemResource, int menuFeatureResource) {
        mMenuItemCollection.addCustomMenuItem(tag, menuItemResource);
        mMenuFeatureCollection.addMenuFeature(tag, menuFeatureResource);
    }

    public void setCustomMenuClickListener(CustomMenuEventListener listener) {
        this.mCustomMenuEventListener = listener;
    }

    public void setMenuItemListener(View.OnClickListener clickListener) {
        if (mMenuItemContainer == null) {
            this.mClickListener = clickListener;
            return;
        }
        if (mVideoBtnContainer != null)
            mVideoBtnContainer.setOnClickListener(clickListener);
        if (mVoiceBtnContainer != null)
            mVoiceBtnContainer.setOnClickListener(clickListener);
        if (mPhotoBtnContainer != null)
            mPhotoBtnContainer.setOnClickListener(clickListener);
        if (mCameraBtnContainer != null)
            mCameraBtnContainer.setOnClickListener(clickListener);
        if (mEmojiBtnContainer != null)
            mEmojiBtnContainer.setOnClickListener(clickListener);
        if (mSendBtn != null)
            mSendBtn.setOnClickListener(clickListener);
    }

    public void setButtonStyle(ChatInputStyle mStyle) {
        if (mMenuItemContainer == null) {
            this.mStyle = mStyle;
            return;
        }
        mVoiceBtn.setImageResource(mStyle.getVoiceBtnIcon());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mVoiceBtn.setBackground(mStyle.getVoiceBtnBg());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mPhotoBtn.setBackground(mStyle.getPhotoBtnBg());
        }
        mPhotoBtn.setImageResource(mStyle.getPhotoBtnIcon());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mCameraBtn.setBackground(mStyle.getCameraBtnBg());
        }
        mCameraBtn.setImageResource(mStyle.getCameraBtnIcon());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSendBtn.setBackground(mStyle.getSendBtnBg());
        }
        if(mChatInputView.mInput != null && mChatInputView.mInput.length() > 0){
            mSendBtn.setImageResource(mStyle.getSendBtnPressedIcon());
        }else{
            mSendBtn.setImageResource(mStyle.getSendBtnIcon());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSendCountTv.setBackground(mStyle.getSendCountBg());
        }
    }

    private void findView(View mMenuItemContainer) {
        if (mMenuItemContainer == null) return;
        if (mVoiceBtn == null)
            mVoiceBtn = mMenuItemContainer.findViewById(R.id.aurora_menuitem_ib_voice);
        if (mPhotoBtn == null)
            mPhotoBtn = mMenuItemContainer.findViewById(R.id.aurora_menuitem_ib_photo);
        if (mCameraBtn == null)
            mCameraBtn = mMenuItemContainer.findViewById(R.id.aurora_menuitem_ib_camera);
        if (mSendBtn == null)
            mSendBtn = mMenuItemContainer.findViewById(R.id.aurora_menuitem_ib_send);
        if (mSendCountTv == null)
            mSendCountTv = mMenuItemContainer.findViewById(R.id.aurora_menuitem_tv_send_count);
        if (mVoiceBtnContainer == null)
            mVoiceBtnContainer = mMenuItemContainer.findViewById(R.id.aurora_ll_menuitem_voice_container);
        if (mPhotoBtnContainer == null)
            mPhotoBtnContainer = mMenuItemContainer.findViewById(R.id.aurora_ll_menuitem_photo_container);
        if (mCameraBtnContainer == null)
            mCameraBtnContainer = mMenuItemContainer.findViewById(R.id.aurora_ll_menuitem_camera_container);
        if (mEmojiBtnContainer == null)
            mEmojiBtnContainer = mMenuItemContainer.findViewById(R.id.aurora_ll_menuitem_emoji_container);
        if (mVideoBtnContainer == null)
            mVideoBtnContainer = mMenuItemContainer.findViewById(R.id.aurora_ll_menuitem_video_container);

        if (mStyle != null) {
            setButtonStyle(mStyle);
        }
        if (mClickListener != null) {
            setMenuItemListener(mClickListener);
        }
    }

    public void updateMenuContainer(FrameLayout menuContainer) {
        if (!mMenus.isEmpty() && menuContainer != null) {
            for (View v : mMenus) {
                menuContainer.addView(v);
            }
            mMenus.clear();
        }
    }

    public boolean isSeparate() {
        return mPhotoBtnContainer != null && mPhotoBtnContainer.getVisibility() == View.VISIBLE && mVideoBtnContainer != null && mVideoBtnContainer.getVisibility() == View.VISIBLE;
    }

    public boolean isAll() {
        return (mPhotoBtnContainer != null && mPhotoBtnContainer.getVisibility() == View.VISIBLE) || (mVideoBtnContainer != null && mVideoBtnContainer.getVisibility() != View.VISIBLE);
    }

    @Override
    public void addMenu(String menuTag, View menu) {
        menu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mChatInputView.getMenuContainer() == null) {
            mChatInputView.inflateViewStub();
        }
        mChatInputView.getInputView().clearFocus();
        String tag = (String) v.getTag();
        if (mCustomMenuEventListener != null && mCustomMenuEventListener.onMenuItemClick(tag, v)) {
            showMenuFeatureByTag(tag);
        }
    }

    public ImageButton getSendButton() {
        findView(mCameraBtnContainer);
        return mSendBtn;
    }

    public TextView getSendCountTextView() {
        findView(mCameraBtnContainer);
        return mSendCountTv;
    }

}
