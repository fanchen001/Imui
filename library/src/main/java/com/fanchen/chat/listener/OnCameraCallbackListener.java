package com.fanchen.chat.listener;


public interface OnCameraCallbackListener {

    /**
     * Fires when take picture finished.
     *
     * @param photoPath Return the absolute path of picture file.
     */
    void onTakePictureCompleted(String photoPath);

    /**
     * Fires when record video finished.
     *
     * @param videoPath Return the absolute path of video file.
     */
    void onFinishVideoRecord(String videoPath);

}
