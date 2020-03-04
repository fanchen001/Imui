package com.fanchen.chat.listener;

public interface OnRecordVoiceUIListener extends OnRecordVoiceListener{

    /**
     * Fires when canceled recording, will delete the audio file.
     */
    void onCancelRecord();

    /**
     * In preview record voice layout, click cancel button will fire this method.
     * Add since 0.7.3
     */
    void onPreviewCancel();

    /**
     * In preview record voice layout, click send voice button will fire this method.
     * Add since 0.7.3
     */
    void onPreviewSend();
}
