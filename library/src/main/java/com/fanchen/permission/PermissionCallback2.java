package com.fanchen.permission;

public interface PermissionCallback2 extends PermissionCallback {

    void onDeny(String permission, int position);

    void onGuarantee(String permission, int position);

}
