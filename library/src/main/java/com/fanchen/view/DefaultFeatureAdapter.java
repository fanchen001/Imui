package com.fanchen.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.ui.R;

public class DefaultFeatureAdapter extends BaseAdapter {

    int[] ICONS = {R.drawable.aurora_menuitem_order,
            R.drawable.aurora_menuitem_goods,
            R.drawable.aurora_menuitem_news,
            R.drawable.aurora_menuitem_car,
            R.drawable.aurora_menuitem_file,
            R.drawable.aurora_menuitem_local,
            R.drawable.aurora_menuitem_idcard};
    String[] TITLES = {"订单咨询","商品咨询","活动咨询","购物车","发送文件","位置信息","发送名片"};

    @Override
    public int getCount() {
        return ICONS.length;
    }

    @Override
    public Object getItem(int position) {
        return ICONS[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(parent.getContext(),R.layout.item_chat_base_feature,null);
        }
        TextView textView = convertView.findViewById(R.id.tv_feature);
        ImageView viewById = convertView.findViewById(R.id.iv_feature);
        viewById.setImageResource(ICONS[position]);
        textView.setText(TITLES[position]);
        return convertView;
    }

}
