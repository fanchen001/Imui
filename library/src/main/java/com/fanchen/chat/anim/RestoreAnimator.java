package com.fanchen.chat.anim;

import android.animation.Animator;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.fanchen.chat.ChatInputView;
import com.fanchen.chat.photo.SelectView;

public class RestoreAnimator implements Animator.AnimatorListener {

    private ChatInputView mChatInputView;
    private TextView mSendCountTv;
    private SelectView mView;

    public RestoreAnimator(ChatInputView mChatInputView, TextView mSendCountTv, SelectView mView) {
        this.mChatInputView = mChatInputView;
        this.mSendCountTv = mSendCountTv;
        this.mView = mView;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(mSendCountTv == null) return;
        mSendCountTv.bringToFront();
        if(mChatInputView == null) return;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mChatInputView.requestLayout();
            mChatInputView.invalidate();
        }
        if(mView == null) return;
        if (mView.getSelectFiles() != null && mView.getSelectFiles().size() > 0) {
            mSendCountTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
