package com.fanchen.filepicker.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class OpenFileUtil {

    public static void openFile(Context mContext,String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return ;
        Intent intent = null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
        /* 依扩展名的类型决定MimeType */
        try{
            if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
                intent = getAudioFileIntent(mContext,filePath);
            } else if (end.equals("3gp") || end.equals("mp4")) {
                intent =  getVideoFileIntent(mContext,filePath);
            } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
                intent =  getImageFileIntent(mContext,filePath);
            } else if (end.equals("apk")) {
                intent =  getApkFileIntent(mContext,filePath);
            } else if (end.equals("ppt")) {
                intent =  getPptFileIntent(mContext,filePath);
            } else if (end.equals("xls")) {
                intent =  getExcelFileIntent(mContext,filePath);
            } else if (end.equals("doc")) {
                intent =  getWordFileIntent(mContext,filePath);
            } else if (end.equals("pdf")) {
                intent =  getPdfFileIntent(mContext,filePath);
            } else if (end.equals("chm")) {
                intent =  getChmFileIntent(mContext,filePath);
            } else if (end.equals("txt")) {
                intent =  getTextFileIntent(mContext,filePath, false);
            } else {
                intent =  getAllIntent(mContext,filePath);
            }
            mContext.startActivity(intent);
        }catch (Throwable e){
            Toast.makeText(mContext,"未找到可以打开该文件的程序",Toast.LENGTH_SHORT).show();
        }
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(Context mContext,String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(Context mContext,String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(Context mContext,String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(Context mContext,String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(Context mContext,String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(Context mContext,String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Context mContext,String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(Context mContext,String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(Context mContext,String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = getUri(mContext,intent,new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(Context mContext,String param, boolean paramBoolean) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = getUri(mContext,intent,new File(param));
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = getUri(mContext,intent,new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Context context,String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = getUri(context,intent,new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    private static Uri getUri(Context mContext,Intent intent, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判断版本是否在7.0以上
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".chat.provider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
