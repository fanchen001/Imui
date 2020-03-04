package com.fanchen.filepicker.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.ui.R;
import com.fanchen.filepicker.SelectOptions;
import com.fanchen.filepicker.model.EssFile;
import com.fanchen.filepicker.util.UiUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * EssMediaAdapter
 */

public class EssMediaAdapter extends BaseQuickAdapter<EssFile, BaseViewHolder> {
    public Set<EssFile> mSelectedFileList = new HashSet<>();
    private int mImageResize;

    public EssMediaAdapter(@Nullable List<EssFile> data) {
        super(R.layout.item_picker_ess_media,data);
    }

    public void setImageResize(int imageSize) {
        this.mImageResize = imageSize;
    }

    @Override
    protected void convert(BaseViewHolder helper, EssFile item) {
        if (item.getItemType() == EssFile.CAPTURE) {
            helper.setGone(R.id.media,false);
            helper.setGone(R.id.capture,true);
            helper.itemView.setLayoutParams(new ViewGroup.LayoutParams(mImageResize- UiUtils.dpToPx(mContext,4), mImageResize));
//            helper.addOnClickListener(R.id.capture);
        } else {
            helper.setGone(R.id.video_duration,item.isVideo());
            helper.setText(R.id.video_duration,formatSeconds(item.getDuration()));
            helper.setGone(R.id.capture,false);
            helper.setGone(R.id.media,true);
            helper.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mImageResize));
            ImageView imageView = helper.getView(R.id.media_thumbnail);
            DrawableTypeRequest<?> load = null;
            if(!TextUtils.isEmpty(item.getAbsolutePath())){
                load =  Glide.with(mContext).load(item.getFile());
            }else{
                load =  Glide.with(mContext).load(item.getUri());
            }
            load  .centerCrop()
                    .override(mImageResize, mImageResize)
                    .placeholder(SelectOptions.getInstance().placeHolder == null ? mContext.getResources().getDrawable(R.mipmap.png_holder) : SelectOptions.getInstance().placeHolder)
                    .into(imageView);
            if(SelectOptions.getInstance().isSingle || SelectOptions.getInstance().maxCount == 1){
                helper.setVisible(R.id.check_view,false);
            }else {
                AppCompatCheckBox checkBox = helper.getView(R.id.check_view);
                helper.setVisible(R.id.check_view,true);
                helper.addOnClickListener(R.id.check_view);
//                helper.addOnClickListener(R.id.media_thumbnail);
                checkBox.setChecked(item.isChecked());
            }
        }

    }
    public String formatSeconds(long seconds){
        String standardTime;
        if (seconds <= 0){
            standardTime = "00:00";
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
        } else if (seconds < 3600) {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        } else {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
        }
        return standardTime;
    }

}
