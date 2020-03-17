package com.fanchen.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanchen.message.sticky.SideBarView;
import com.fanchen.message.sticky.StickyListHeadersAdapter;
import com.fanchen.message.sticky.StickyListHeadersListView;
import com.fanchen.ui.R;

public class ContactsView extends FrameLayout {
    private StickyListHeadersListView mListView;
    private SideBarView mSideBar;

    private View mVerify;
    private View mGroup;
    private View mView;
    private LayoutInflater mInflater;
    private LinearLayout mLoadingTv;

    public ContactsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);

        mInflater.inflate(R.layout.view_im_contacts, this);

        mListView = findViewById(R.id.lv_contacts);
        mSideBar = findViewById(R.id.sb_contacts);
        TextView mLetterHintTv = findViewById(R.id.tv_contacts);
        mSideBar.setTextView(mLetterHintTv);
        mSideBar.bringToFront();

        View header = mInflater.inflate(R.layout.layout_im_contacts_header, null);
        mVerify = header.findViewById(R.id.ll_verification);
        mGroup = header.findViewById(R.id.ll_group);
        mView = header.findViewById(R.id.view_verification_group);

        mLoadingTv = (LinearLayout) mInflater.inflate(R.layout.layout_im_contacts_loading, null);

        mListView.addHeaderView(header, null, false);
        mListView.addHeaderView(mLoadingTv);

        mListView.setDrawingListUnderStickyHeader(true);
        mListView.setAreHeadersSticky(true);
        mListView.setStickyHeaderTopOffset(0);
    }

    public void setListener(OnClickListener contactsController) {
        mVerify.setOnClickListener(contactsController);
        mGroup.setOnClickListener(contactsController);
    }

    public void setSelection(int position) {
        mListView.setSelection(position);
    }

    public void setSideBarTouchListener(SideBarView.OnTouchingLetterChangedListener listener) {
        mSideBar.setOnTouchingLetterChangedListener(listener);
    }

    public void setAdapter(StickyListHeadersAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void showLoading() {
        if(mLoadingTv.getParent() == null){
            mListView.addHeaderView(mLoadingTv);
        }
    }

    public void dismissLoading() {
        if(mLoadingTv.getParent() != null){
            mListView.removeHeaderView(mLoadingTv);
        }
    }

    public void showContact() {
        mSideBar.setVisibility(VISIBLE);
        mListView.setVisibility(VISIBLE);
    }

    private void setGroupVerifyVisibility(int visibility){
        mView.setVisibility(visibility);
        mGroup.setVisibility(visibility);
        mVerify.setVisibility(visibility);
    }

}
