package com.fanchen.filepicker.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.R;
import com.fanchen.filepicker.SelectOptions;
import com.fanchen.filepicker.model.EssFile;
import com.fanchen.filepicker.util.UiUtils;

import java.util.List;

/**
 * EssMediaAdapter
 */

public class EssMediaAdapter extends BaseQuickAdapter<EssFile, BaseViewHolder> {

    private int mImageResize;

    public EssMediaAdapter(@Nullable List<EssFile> data) {
        super(R.layout.ess_media_item,data);
    }

    public void setImageResize(int imageSize) {
        this.mImageResize = imageSize;
    }

    @Override
    protected void convert(BaseViewHolder helper, EssFile item) {
        if (item.getItemType() == EssFile.CAPTURE) {
            helper.getView(R.id.media).setVisibility(View.GONE);
            helper.getView(R.id.capture).setVisibility(View.VISIBLE);
            helper.itemView.setLayoutParams(new ViewGroup.LayoutParams(mImageResize- UiUtils.dpToPx(mContext,4), mImageResize));
            helper.addOnClickListener(R.id.capture);
        } else {
            helper.getView(R.id.capture).setVisibility(View.GONE);
            helper.getView(R.id.media).setVisibility(View.VISIBLE);
            helper.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mImageResize));
            ImageView imageView = helper.getView(R.id.media_thumbnail);
            Glide
                    .with(mContext)
                    .load(item.getUri())
                    .centerCrop()
                    .override(mImageResize, mImageResize)
                    .placeholder(SelectOptions.getInstance().placeHolder == null ? mContext.getResources().getDrawable(R.mipmap.png_holder) : SelectOptions.getInstance().placeHolder)
                    .into(imageView);
            if(SelectOptions.getInstance().isSingle || SelectOptions.getInstance().maxCount == 1){
                helper.setVisible(R.id.check_view,false);
            }else {
                AppCompatCheckBox checkBox = helper.getView(R.id.check_view);
                helper.setVisible(R.id.check_view,true);
                helper.addOnClickListener(R.id.check_view);
                helper.addOnClickListener(R.id.media_thumbnail);
                checkBox.setChecked(item.isChecked());
            }
        }

    }


}
