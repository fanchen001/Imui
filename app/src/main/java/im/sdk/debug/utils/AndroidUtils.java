package im.sdk.debug.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by jiguang on 2018/6/21.
 */

public class AndroidUtils {

    public static final String TAG = AndroidUtils.class.getSimpleName();

    public static boolean checkPermission(Context context, String[] permissions) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        for (String permission : permissions) {
            if (PackageManager.PERMISSION_DENIED == packageManager.checkPermission(permission, packageName)) {
                Log.w(TAG, "required permission not granted . permission = " + permission);
                return false;
            }
        }
        return true;
    }

    public static void requestPermission(Activity activity, String[] permissions) throws Exception {
        if (Build.VERSION.SDK_INT >= 23) {
            Class<?> activityClass = Class.forName("android.app.Activity");
            Method requestPermissionsMethod = activityClass.getMethod("requestPermissions", String[].class, int.class);
            requestPermissionsMethod.invoke(activity, permissions, 0);
        } else {
            Toast.makeText(activity, "缺少必要权限，请检查Manifest文件，并在设置中打开对应权限", Toast.LENGTH_LONG).show();
        }
    }
}
