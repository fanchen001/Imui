package com.fanchen.popup.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.ui.R;
import com.fanchen.popup.core.AttachPopupView;
import com.fanchen.popup.interfaces.OnSelectListener;
import com.fanchen.popup.widget.VerticalRecyclerView;
import java.util.Arrays;

/**
 * Description: Attach类型的列表弹窗
 *  2018/12/12
 */
public class AttachListPopupView extends AttachPopupView {
    RecyclerView recyclerView;
    protected int bindLayoutId;
    protected int bindItemLayoutId;

    public AttachListPopupView(@NonNull Context context) {
        super(context);
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView
     * @return
     */
    public AttachListPopupView bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public AttachListPopupView bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_attach_impl_list : bindLayoutId;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        recyclerView = findViewById(R.id.recyclerView);
        if(recyclerView instanceof VerticalRecyclerView){
            ((VerticalRecyclerView)recyclerView).setupDivider(popupInfo.isDarkTheme);
        }else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        BaseQuickAdapter<String, BaseViewHolder> adapter = new BaseQuickAdapter<String, BaseViewHolder>(bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text : bindItemLayoutId,Arrays.asList(data)) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_text, item);
                int position = helper.getAdapterPosition();
                if (iconIds != null && iconIds.length > position) {
                    helper.getView(R.id.iv_image).setVisibility(VISIBLE);
                    helper.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
                } else {
                    helper.getView(R.id.iv_image).setVisibility(GONE);
                }
                View check = helper.getView(R.id.check_view);
                if (check!=null) check.setVisibility(GONE);

                if(bindItemLayoutId==0 && popupInfo.isDarkTheme){
                    helper.<TextView>getView(R.id.tv_text).setTextColor(getResources().getColor(R.color._xpopup_white_color));
                }
            }

        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectListener != null) {
                    selectListener.onSelect(position, adapter.getData().get(position).toString());
                }
                if (popupInfo.autoDismiss) dismiss();
            }
        });
//        final EasyAdapter<String> adapter = new EasyAdapter<String>(Arrays.asList(data), bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text : bindItemLayoutId) {
//            @Override
//            protected void bind(@NonNull RecyclerView.ViewHolder holder, @NonNull String s, int position) {
//                holder.setText(R.id.tv_text, s);
//                if (iconIds != null && iconIds.length > position) {
//                    holder.getView(R.id.iv_image).setVisibility(VISIBLE);
//                    holder.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
//                } else {
//                    holder.getView(R.id.iv_image).setVisibility(GONE);
//                }
//                View check = holder.getView(R.id.check_view);
//                if (check!=null) check.setVisibility(GONE);
//
//                if(bindItemLayoutId==0 && popupInfo.isDarkTheme){
//                    holder.<TextView>getView(R.id.tv_text).setTextColor(getResources().getColor(R.color._xpopup_white_color));
//                }
//            }
//        };
//        adapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener() {
//            @Override
//            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//                if (selectListener != null) {
//                    selectListener.onSelect(position, adapter.getData().get(position));
//                }
//                if (popupInfo.autoDismiss) dismiss();
//            }
//        });
        recyclerView.setAdapter(adapter);
        if (bindLayoutId==0 && popupInfo.isDarkTheme){
            applyDarkTheme();
        }
    }

    @Override
    protected void applyDarkTheme() {
        super.applyDarkTheme();
        recyclerView.setBackgroundColor(getResources().getColor(R.color._xpopup_dark_color));
    }

    String[] data;
    int[] iconIds;

    public AttachListPopupView setStringData(String[] data, int[] iconIds) {
        this.data = data;
        this.iconIds = iconIds;
        return this;
    }

//    public AttachListPopupView setOffsetXAndY(int offsetX, int offsetY) {
//        this.defaultOffsetX += offsetX;
//        this.defaultOffsetY += offsetY;
//        return this;
//    }

    private OnSelectListener selectListener;

    public AttachListPopupView setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }
}
