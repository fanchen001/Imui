package com.fanchen.chat.listener;


import com.fanchen.chat.photo.SelectView;

public interface OnFileSelectedListener {

    /**
     * Fires when selecting photo or video files in select photo mode.
     */
    void onFileSelected(SelectView view);

    /**
     * Fires when file was deselected in select photo mode.
     */
    void onFileDeselected(SelectView view);
}
