package com.fanchen.chat.menu.collection;

import android.content.Context;
import android.util.Log;
import android.view.View;


import com.fanchen.ui.R;
import com.fanchen.chat.menu.Menu;
import com.fanchen.chat.menu.view.MenuItem;
import com.fanchen.chat.utils.ViewUtil;


public class MenuItemCollection extends MenuCollection {


    public MenuItemCollection(Context context) {
        super(context);
        initDefaultMenu();
    }

    private void initDefaultMenu() {
        this.put(Menu.TAG_VOICE, inflaterMenu(R.layout.menu_item_voice));
        this.put(Menu.TAG_GALLERY, inflaterMenu(R.layout.menu_item_photo));
        this.put(Menu.TAG_CAMERA, inflaterMenu(R.layout.menu_item_camera));
        this.put(Menu.TAG_EMOJI, inflaterMenu(R.layout.menu_item_emoji));
        this.put(Menu.TAG_SEND, inflaterMenu(R.layout.menu_item_send));
        this.put(Menu.TAG_VIDEO, inflaterMenu(R.layout.menu_item_video));
    }

    private View inflaterMenu(int resource) {
        View view = mInflater.inflate(resource, null);
        view = ViewUtil.formatViewWeight(view, 1);
        return view;

    }

    public void addCustomMenuItem(String tag, int resource) {
        View view = mInflater.inflate(resource, null);
        addCustomMenuItem(tag, view);
    }


    public void addCustomMenuItem(String tag, View menu) {
        if (menu instanceof MenuItem) {
            menu.setClickable(true);
            menu = ViewUtil.formatViewWeight(menu, 1);
            addMenu(tag, menu);
        } else {
            Log.e(TAG, "Collection menu item failed !");
        }
    }


}
