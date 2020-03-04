package com.fanchen.chat.listener;


import java.io.File;

/**
 * Callback will invoked when record voice is finished
 */
public interface OnRecordVoiceListener {

    /**
     * Fires when started recording.
     */
    void onStartRecord();

    /**
     * Fires when finished recording.
     *
     * @param voiceFile The audio file.
     * @param duration  The duration of audio file, specified in seconds.
     */
    void onFinishRecord(File voiceFile, int duration);
}