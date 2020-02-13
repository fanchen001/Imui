package com.fanchen.chat.emoji.listener;

import android.view.ViewGroup;

import com.fanchen.chat.emoji.adapter.EmoticonsAdapter;


public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}