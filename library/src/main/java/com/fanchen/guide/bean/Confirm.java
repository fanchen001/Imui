package com.fanchen.guide.bean;

import android.view.View;

import com.fanchen.guide.support.OnConfirmListener;

/**
 */
public class Confirm {

    public String text;

    public int textSize = -1;

    public OnConfirmListener listener;

    public Confirm(String text) {
        this.text = text;
    }

    public Confirm(String text, int textSize) {
        this.text = text;
        this.textSize = textSize;
    }

    public Confirm(String text, int textSize, OnConfirmListener listener) {
        this.text = text;
        this.textSize = textSize;
        this.listener = listener;
    }
}
