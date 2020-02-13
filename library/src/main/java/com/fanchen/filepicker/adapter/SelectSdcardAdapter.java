package com.fanchen.filepicker.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.R;

import java.util.List;

/**
 * SelectSdcardAdapter
 */
public class SelectSdcardAdapter extends BaseQuickAdapter<String,BaseViewHolder>{
    public SelectSdcardAdapter(@Nullable List<String> data) {
        super(R.layout.item_select_sdcard,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_item_select_sdcard,item);
    }
}
