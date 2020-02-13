package com.fanchen.location.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.R;
import com.fanchen.location.bean.LocationBean;

import java.util.List;

public class SearchPositionAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {
    private int selectItemIndex;

    public SearchPositionAdapter(@Nullable List<LocationBean> data) {
        super(R.layout.location_item_poi, data);
        selectItemIndex = 0;
    }

    public void setSelectSearchItemIndex(int selectItemIndex) {
        this.selectItemIndex = selectItemIndex;
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationBean item) {
        helper.setText(R.id.tv_poi_name, item.getName());
        helper.setText(R.id.tv_poi_address, item.getAddress());
        ImageView imageView = helper.getView(R.id.img_cur_point);
        if (selectItemIndex == helper.getAdapterPosition()) {
            imageView.setImageResource(R.mipmap.position_is_select);
        } else {
            imageView.setImageDrawable(null);
        }
    }
}
