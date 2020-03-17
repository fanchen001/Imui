package com.fanchen.chat.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fanchen.chat.ChatInputStyle;

public class ShrinkAnimator implements Animator.AnimatorListener {
    private boolean hasContent;
    private boolean isSelectPhoto;
    private TextView mSendCountTv;
    private ImageButton mSendBtn;
    private ChatInputStyle mStyle;
    private AnimatorSet mSet;

    public ShrinkAnimator(boolean hasContent, boolean isSelectPhoto, TextView mSendCountTv, ImageButton mSendBtn, ChatInputStyle mStyle, AnimatorSet mSet) {
        this.hasContent = hasContent;
        this.isSelectPhoto = isSelectPhoto;
        this.mSendCountTv = mSendCountTv;
        this.mSendBtn = mSendBtn;
        this.mStyle = mStyle;
        this.mSet = mSet;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        if(mSendCountTv == null)return;
        if (!hasContent && isSelectPhoto) {
            mSendCountTv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if(mSendBtn == null)return;
        Context context = mSendBtn.getContext();
        if (hasContent) {
            mSendBtn.setImageDrawable(ContextCompat.getDrawable(context, mStyle.getSendBtnPressedIcon()));
        } else {
            mSendBtn.setImageDrawable(ContextCompat.getDrawable(context, mStyle.getSendBtnIcon()));
        }
        mSet.start();
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
