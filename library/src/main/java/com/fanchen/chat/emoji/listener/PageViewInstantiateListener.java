package com.fanchen.chat.emoji.listener;

import android.view.View;
import android.view.ViewGroup;

import com.fanchen.chat.emoji.data.PageEntity;


public interface PageViewInstantiateListener<T extends PageEntity> {

    View instantiateItem(ViewGroup container, int position, T pageEntity);
}
