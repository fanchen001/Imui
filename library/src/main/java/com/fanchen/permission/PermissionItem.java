package com.fanchen.permission;

import android.Manifest;

import com.fanchen.ui.R;

import java.io.Serializable;

public class PermissionItem implements Serializable {

    public static PermissionItem READ_STORAGE = new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE,R.string.permission_storage, R.drawable.permission_ic_storage);
    public static PermissionItem WRITE_STORAGE = new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE,R.string.permission_storage, R.drawable.permission_ic_storage);
    public static PermissionItem AUDIO = new PermissionItem(Manifest.permission.RECORD_AUDIO,R.string.permission_audio, R.drawable.permission_ic_micro_phone);
    public static PermissionItem LOCATION = new PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION,R.string.permission_location, R.drawable.permission_ic_location);
    public static PermissionItem READ_SMS = new PermissionItem(Manifest.permission.READ_SMS,"", R.drawable.permission_ic_sms);
    public static PermissionItem CAMERA = new PermissionItem(Manifest.permission.CAMERA,R.string.permission_camera, R.drawable.permission_ic_camera);
    public static PermissionItem PHONE = new PermissionItem(Manifest.permission.READ_PHONE_STATE,R.string.permission_phone, R.drawable.permission_ic_phone);

    public String PermissionName;
    public String Permission;
    public int PermissionIconRes;
    public int PermissionNameRes;

    public PermissionItem(String permission, String permissionName, int permissionIconRes) {
        Permission = permission;
        PermissionName = permissionName;
        PermissionIconRes = permissionIconRes;
    }

    public PermissionItem(String permission, int permissionName, int permissionIconRes) {
        Permission = permission;
        PermissionNameRes = permissionName;
        PermissionIconRes = permissionIconRes;
    }

    public PermissionItem(String permission) {
        Permission = permission;
    }
}
