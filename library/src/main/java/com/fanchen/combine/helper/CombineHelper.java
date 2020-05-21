package com.fanchen.combine.helper;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.fanchen.combine.listener.OnHandlerListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CombineHelper {

    private Handler mH = new Handler(Looper.getMainLooper());

    public static CombineHelper init() {
        return SingletonHolder.instance;
    }

    private CombineHelper() {
    }

    public static class SingletonHolder {
        public static final CombineHelper instance = new CombineHelper();
    }

    /**
     * 通过url加载
     *
     * @param builder
     */
    private void loadByUrls(final Combine.Builder builder) {
        int subSize = builder.subSize;
        Bitmap defaultBitmap = null;
        if (builder.placeholder != 0) {
            defaultBitmap = CompressHelper.getInstance()
                    .compressResource(builder.context.getResources(), builder.placeholder, subSize, subSize);
        }
        ProgressHandler handler = new ProgressHandler(defaultBitmap, builder.count, new OnHandlerListener() {
            @Override
            public void onComplete(Bitmap[] bitmaps) {
                setBitmap(builder, bitmaps);
            }
        });
        for (int i = 0; i < builder.count; i++) {
            BitmapLoader.getInstance(builder.context).asyncLoad(i, builder.urls[i], subSize, subSize, handler);
        }
    }

    /**
     * 通过图片的资源id、bitmap加载
     *
     * @param builder
     */
    private void loadByResBitmaps(Combine.Builder builder) {
        int subSize = builder.subSize;
        Bitmap[] compressedBitmaps = new Bitmap[builder.count];
        for (int i = 0; i < builder.count; i++) {
            if (builder.resourceIds != null) {
                compressedBitmaps[i] = CompressHelper.getInstance()
                        .compressResource(builder.context.getResources(), builder.resourceIds[i], subSize, subSize);
            } else if (builder.bitmaps != null) {
                compressedBitmaps[i] = CompressHelper.getInstance()
                        .compressResource(builder.bitmaps[i], subSize, subSize);
            }
        }
        setBitmap(builder, compressedBitmaps);
    }

    public void load(Combine.Builder builder) {
        if (builder.progressListener != null) {
            builder.progressListener.onStart();
        }

        if (builder.urls != null) {
            loadByUrls(builder);
        } else {
            loadByResBitmaps(builder);
        }
    }

    public static File saveBitmapToExternal(Bitmap bitmap, String targetFileDirPath) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        File targetFileDir = new File(targetFileDirPath);
        if (!targetFileDir.exists() && !targetFileDir.mkdirs()) {
            return null;
        }

        File targetFile = new File(targetFileDir, UUID.randomUUID().toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            baos.writeTo(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }

    private void setBitmap(final Combine.Builder b, final Bitmap[] bitmaps) {
        new Thread() {

            @Override
            public void run() {
                final Bitmap result = b.layoutManager.combineBitmap(b.size, b.subSize, b.gap, b.gapColor, bitmaps);
                File file = null;
                if (b.fileListener != null && result != null) {
                    File externalCacheDir = b.context.getExternalCacheDir();
                    if (externalCacheDir != null) {
                        try {
                            file = saveBitmapToExternal(result, externalCacheDir.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                final File newFile = file;
                mH.post(new Runnable() {

                    @Override
                    public void run() {
                        // 返回最终的组合Bitmap
                        if (b.progressListener != null) {
                            b.progressListener.onComplete(result);
                        }
                        if (b.fileListener != null && newFile != null) {
                            b.fileListener.onComplete(newFile);
                        }
                        // 给ImageView设置最终的组合Bitmap
                        if (b.imageView != null) {
                            b.imageView.setImageBitmap(result);
                        }
                    }

                });
            }

        }.start();
    }
}
