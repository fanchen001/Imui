package com.fanchen.chat.listener;

import android.view.View;

/**
 * Custom Menu' callbacks
 */
public interface CustomMenuEventListener {

    boolean onMenuItemClick(String tag, View menuItem);

    void onMenuFeatureVisibilityChanged(int visibility, String tag, View menuFeature);

}