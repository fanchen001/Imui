package com.fanchen.message.utils;

import android.content.Context;

import com.fanchen.message.commons.models.IMessage;
import com.fanchen.picture.ImagePreview;
import com.fanchen.picture.bean.IImageInfo;
import com.fanchen.picture.view.listener.OnPopItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PreviewUtil {

    public static void startPreview(Context context, List<? extends IMessage> list, int position, OnPopItemClickListener clickListener) {
        ArrayList<String> strings = new ArrayList<>();
        for (IMessage message : list) strings.add(message.getMediaFilePath());
        ImagePreview instance = ImagePreview.getInstance();
        instance.setContext(context);
        instance.setOnPopItemClickListener(clickListener);
        instance.setShowCloseButton(true);
        instance.setEnableDragClose(true);
        instance.setShowDownButton(true);
        instance.setIndex(position).setImageList(strings).start();
    }

    public static void startInfoPreview(Context context, List<? extends IImageInfo> list, int position, OnPopItemClickListener clickListener) {
        ImagePreview instance = ImagePreview.getInstance();
        instance.setContext(context);
        instance.setOnPopItemClickListener(clickListener);
        instance.setShowCloseButton(true);
        instance.setEnableDragClose(true);
        instance.setShowDownButton(true);
        instance.setIndex(position).setImageInfoList(list).start();
    }

}
