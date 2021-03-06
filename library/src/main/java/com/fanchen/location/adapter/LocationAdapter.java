package com.fanchen.location.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.ui.R;
import com.fanchen.location.bean.LocationBean;

import java.util.List;


public class LocationAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {
    private int selectItemIndex;

    public LocationAdapter(@Nullable List<LocationBean> data) {
        super(R.layout.item_map_location_poi, data);
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

    public void setSelectSearchItemIndex(int selectItemIndex) {
        this.selectItemIndex = selectItemIndex;
    }
}
