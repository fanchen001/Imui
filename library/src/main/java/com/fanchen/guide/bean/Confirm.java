package com.fanchen.guide.bean;

import android.view.View;

/**
 */
public class Confirm {

    public String text;

    public int textSize = -1;

    public View.OnClickListener listener;

    public Confirm(String text) {
        this.text = text;
    }

    public Confirm(String text, int textSize) {
        this.text = text;
        this.textSize = textSize;
    }

    public Confirm(String text, int textSize, View.OnClickListener listener) {
        this.text = text;
        this.textSize = textSize;
        this.listener = listener;
    }
}
