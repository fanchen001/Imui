package com.fanchen.filepicker.adapter;


import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.filepicker.model.BreadModel;
import com.fanchen.ui.R;

import java.util.List;

/**
 * BreadAdapter
 */

public class BreadAdapter extends BaseQuickAdapter<BreadModel, BaseViewHolder> {

    public BreadAdapter(@Nullable List<BreadModel> data) {
        super(R.layout.item_picker_bread,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BreadModel item) {
        helper.setText(R.id.btn_bread,item.getCurName());
        helper.addOnClickListener(R.id.btn_bread);
    }
}
