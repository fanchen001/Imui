package com.fanchen.chat.listener;

public interface CameraControllerListener {
    void onFullScreenClick();
    void onRecoverScreenClick();
    void onCloseCameraClick();
    void onSwitchCameraModeClick(boolean isRecordVideoMode);
}
