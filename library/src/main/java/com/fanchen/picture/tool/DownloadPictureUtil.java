package com.fanchen.picture.tool;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.fanchen.picture.ImagePreview;
import com.fanchen.picture.glide.engine.SimpleFileTarget;

import java.io.File;

/**
 * description:图片下载工具类
 */
public class DownloadPictureUtil {

  public static void downloadPicture(final Context context, final String url) {
    Glide.with(context.getApplicationContext()).load(url).downloadOnly(new SimpleFileTarget() {
      @Override public void onLoadStarted(Drawable placeholder) {
        super.onLoadStarted(placeholder);
        ToastUtil.getInstance()._short(context, "开始下载...");
      }

      @Override public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        ToastUtil.getInstance()._short(context, "保存失败");
      }

      @Override
      public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
        final String downloadFolderName = ImagePreview.getInstance().getFolderName();
        final String path = Environment.getExternalStorageDirectory() + "/" + downloadFolderName + "/";
        String name = "";
        try {
          name = url.substring(url.lastIndexOf("/") + 1, url.length());
          if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
          }
          name = MD5Util.md5Encode(name);
        } catch (Exception e) {
          e.printStackTrace();
          name = System.currentTimeMillis() + "";
        }
        String mimeType = ImageUtil.getImageTypeWithMime(resource.getAbsolutePath());
        name = name + "." + mimeType;
        FileUtil.createFileByDeleteOldFile(path + name);
        boolean result = FileUtil.copyFile(resource, path, name);
        if (result) {
          ToastUtil.getInstance()._short(context, "成功保存到 ".concat(path).concat(name));
          new SingleMediaScanner(context, path.concat(name), new SingleMediaScanner.ScanListener() {
            @Override public void onScanFinish() {
              // scanning...
            }
          });
        } else {
          ToastUtil.getInstance()._short(context, "保存失败");
        }
      }
    });
  }
}