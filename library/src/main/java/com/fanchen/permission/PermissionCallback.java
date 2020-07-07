package com.fanchen.permission;

import java.io.Serializable;

public interface PermissionCallback extends Serializable {
    void onClose();

    void onFinish();

}
