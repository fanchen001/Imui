package com.fanchen.chat.camera;


import com.fanchen.chat.listener.CameraEventListener;
import com.fanchen.chat.listener.OnCameraCallbackListener;

public interface CameraSupport {
    CameraSupport open(int cameraId, int width, int height, boolean isFacingBack, float cameraQuality);
    void release();
    void takePicture();
    void setCameraCallbackListener(OnCameraCallbackListener listener);
    void setCameraEventListener(CameraEventListener listener);
    void startRecordingVideo();
    void cancelRecordingVideo();
    String finishRecordingVideo();
}
