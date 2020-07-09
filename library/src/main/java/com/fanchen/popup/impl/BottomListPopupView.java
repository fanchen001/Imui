package com.fanchen.popup.impl;

import android.content.Context;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.ui.R;
import com.fanchen.popup.Popup;
import com.fanchen.popup.core.BottomPopupView;
import com.fanchen.popup.interfaces.OnSelectListener;
import com.fanchen.popup.widget.CheckView;
import com.fanchen.popup.widget.VerticalRecyclerView;

import java.util.Arrays;

/**
 * Description: 底部的列表对话框
 *  2018/12/16
 */
public class BottomListPopupView extends BottomPopupView {
    VerticalRecyclerView recyclerView;
    TextView tv_title;
    protected int bindLayoutId;
    protected int bindItemLayoutId;

    public BottomListPopupView(@NonNull Context context) {
        super(context);
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public BottomListPopupView bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public BottomListPopupView bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_bottom_impl_list : bindLayoutId;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setupDivider(popupInfo.isDarkTheme);
        tv_title = findViewById(R.id.tv_title);

        if(tv_title!=null){
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
                findViewById(R.id.xpopup_divider).setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        BaseQuickAdapter<String, BaseViewHolder> adapter = new BaseQuickAdapter<String, BaseViewHolder>(bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text_match : bindItemLayoutId,Arrays.asList(data)) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                int position = helper.getAdapterPosition();
                helper.setText(R.id.tv_text, item);
                if (iconIds != null && iconIds.length > position) {
                    helper.getView(R.id.iv_image).setVisibility(VISIBLE);
                    helper.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
                } else {
                    helper.getView(R.id.iv_image).setVisibility(GONE);
                }

                // 对勾View
                if (checkedPosition != -1) {
                    if(helper.getView(R.id.check_view)!=null){
                        helper.getView(R.id.check_view).setVisibility(position == checkedPosition ? VISIBLE : GONE);
                        helper.<CheckView>getView(R.id.check_view).setColor(Popup.getPrimaryColor());
                    }
                    helper.<TextView>getView(R.id.tv_text).setTextColor(position == checkedPosition ?
                            Popup.getPrimaryColor() : getResources().getColor(R.color._xpopup_title_color));
                }else {
                    if(helper.getView(R.id.check_view)!=null)helper.getView(R.id.check_view).setVisibility(GONE);
                    //如果没有选择，则文字居中
                    helper.<TextView>getView(R.id.tv_text).setGravity(Gravity.CENTER);
                }
                if(bindItemLayoutId==0 && popupInfo.isDarkTheme){
                    helper.<TextView>getView(R.id.tv_text).setTextColor(getResources().getColor(R.color._xpopup_white_color));
                }
            }

//            @Override
//            protected void bind(@NonNull BaseViewHolder holder, @NonNull String s, int position) {
//                holder.setText(R.id.tv_text, s);
//                if (iconIds != null && iconIds.length > position) {
//                    holder.getView(R.id.iv_image).setVisibility(VISIBLE);
//                    holder.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
//                } else {
//                    holder.getView(R.id.iv_image).setVisibility(GONE);
//                }
//
//                // 对勾View
//                if (checkedPosition != -1) {
//                    if(holder.getView(R.id.check_view)!=null){
//                        holder.getView(R.id.check_view).setVisibility(position == checkedPosition ? VISIBLE : GONE);
//                        holder.<CheckView>getView(R.id.check_view).setColor(Popup.getPrimaryColor());
//                    }
//                    holder.<TextView>getView(R.id.tv_text).setTextColor(position == checkedPosition ?
//                            Popup.getPrimaryColor() : getResources().getColor(R.color._xpopup_title_color));
//                }else {
//                    if(holder.getView(R.id.check_view)!=null)holder.getView(R.id.check_view).setVisibility(GONE);
//                    //如果没有选择，则文字居中
//                    holder.<TextView>getView(R.id.tv_text).setGravity(Gravity.CENTER);
//                }
//                if(bindItemLayoutId==0 && popupInfo.isDarkTheme){
//                    holder.<TextView>getView(R.id.tv_text).setTextColor(getResources().getColor(R.color._xpopup_white_color));
//                }
//            }
        };
//        adapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener() {
//            @Override
//            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//                if (selectListener != null) {
//                    selectListener.onSelect(position, adapter.getData().get(position));
//                }
//                if (checkedPosition != -1) {
//                    checkedPosition = position;
//                    adapter.notifyDataSetChanged();
//                }
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (popupInfo.autoDismiss) dismiss();
//                    }
//                }, 100);
//            }
//        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectListener != null) {
                    selectListener.onSelect(position, adapter.getData().get(position).toString());
                }
                if (checkedPosition != -1) {
                    checkedPosition = position;
                    adapter.notifyDataSetChanged();
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (popupInfo.autoDismiss) dismiss();
                    }
                }, 100);
            }
        });
        recyclerView.setAdapter(adapter);
        if (bindLayoutId==0 && popupInfo.isDarkTheme){
            applyDarkTheme();
        }
    }
    @Override
    protected void applyDarkTheme() {
        super.applyDarkTheme();
        tv_title.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        ((ViewGroup)tv_title.getParent()).setBackgroundResource(R.drawable._xpopup_round3_top_dark_bg);
        findViewById(R.id.xpopup_divider).setBackgroundColor(
                getResources().getColor(R.color._xpopup_list_dark_divider)
        );
    }

    CharSequence title;
    String[] data;
    int[] iconIds;

    public BottomListPopupView setStringData(CharSequence title, String[] data, int[] iconIds) {
        this.title = title;
        this.data = data;
        this.iconIds = iconIds;
        return this;
    }

    private OnSelectListener selectListener;

    public BottomListPopupView setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    int checkedPosition = -1;

    /**
     * 设置默认选中的位置
     *
     * @param position
     * @return
     */
    public BottomListPopupView setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }


}
