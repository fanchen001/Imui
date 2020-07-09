package com.fanchen.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.fanchen.filepicker.model.UCropConfig;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.lang.ref.WeakReference;


/**
 * FilePicker 构造器
 */
public final class FilePicker {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    public static UCropConfig U1X2 = new UCropConfig(400,800,1,2);
    public static UCropConfig U1X1 = new UCropConfig(400,400,1,1);
    public static UCropConfig U3X2 = new UCropConfig(900,600,3,2);
    public static UCropConfig U2X3 = new UCropConfig(600,900,2,3);

    private FilePicker(Activity activity) {
        this(activity, null);
    }

    private FilePicker(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private FilePicker(Activity mContext, Fragment mFragment) {
        this.mContext = new WeakReference<>(mContext);
        this.mFragment = new WeakReference<>(mFragment);
    }

    public static FilePicker from(Activity activity) {
        return new FilePicker(activity);
    }

    public static FilePicker from(Fragment fragment) {
        return new FilePicker(fragment);
    }

    public SelectCreator chooseForBrowser() {
        return new SelectCreator(this, SelectOptions.CHOOSE_TYPE_BROWSER);
    }

    public SelectCreator chooseForMimeType() {
        return new SelectCreator(this, SelectOptions.CHOOSE_TYPE_SCAN);
    }

    public SelectCreator chooseMedia() {
        return new SelectCreator(this, SelectOptions.CHOOSE_TYPE_MEDIA);
    }

    public SelectCreator chooseUCrop(UCropConfig cropConfig,int code) {
        return new SelectCreator(this, SelectOptions.CHOOSE_TYPE_MEDIA).uCropMedia(cropConfig).requestCode(code);
    }

    public Activity getActivity() {
        return mContext.get();
    }

    public Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

    public static File getUCropFile(Intent data) {
        Throwable throwable = UCrop.getError(data);
        Uri output = UCrop.getOutput(data);
        if (throwable != null || output == null) {
            return null;
        } else
            return new File(output.getPath());
    }

}
