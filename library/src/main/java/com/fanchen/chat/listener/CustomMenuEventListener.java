package com.fanchen.chat.listener;

import com.fanchen.chat.menu.view.MenuFeature;
import com.fanchen.chat.menu.view.MenuItem;

/**
 * Custom Menu' callbacks
 */
public interface CustomMenuEventListener {

    boolean onMenuItemClick(String tag, MenuItem menuItem);

    void onMenuFeatureVisibilityChanged(int visibility, String tag, MenuFeature menuFeature);

}