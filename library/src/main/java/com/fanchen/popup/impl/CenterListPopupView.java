package com.fanchen.popup.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanchen.popup.Popup;
import com.fanchen.ui.R;
import com.fanchen.popup.core.CenterPopupView;
import com.fanchen.popup.interfaces.OnSelectListener;
import com.fanchen.popup.widget.CheckView;
import com.fanchen.popup.widget.VerticalRecyclerView;

import java.util.Arrays;

/**
 * Description: 在中间的列表对话框
 *  2018/12/16
 */
public class CenterListPopupView extends CenterPopupView {
    RecyclerView recyclerView;
    TextView tv_title;

    public CenterListPopupView(@NonNull Context context) {
        super(context);
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public CenterListPopupView bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public CenterListPopupView bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_center_impl_list : bindLayoutId;
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
        tv_title = findViewById(R.id.tv_title);

        if (tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
                findViewById(R.id.xpopup_divider).setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

       BaseQuickAdapter<String , BaseViewHolder> adapter = new BaseQuickAdapter<String , BaseViewHolder>(bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text_match : bindItemLayoutId,Arrays.asList(data)) {
           @Override
           protected void convert(BaseViewHolder holder, String item) {
               holder.setText(R.id.tv_text, item);
               int position = holder.getAdapterPosition();
               if (iconIds != null && iconIds.length > position) {
                   holder.getView(R.id.iv_image).setVisibility(VISIBLE);
                   holder.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
               } else {
                   holder.getView(R.id.iv_image).setVisibility(GONE);
               }

               // 对勾View
               if (checkedPosition != -1) {
                   if (holder.getView(R.id.check_view) != null) {
                       holder.getView(R.id.check_view).setVisibility(position == checkedPosition ? VISIBLE : GONE);
                       holder.<CheckView>getView(R.id.check_view).setColor(Popup.getPrimaryColor());
                   }
                   holder.<TextView>getView(R.id.tv_text).setTextColor(position == checkedPosition ?
                           Popup.getPrimaryColor() : getResources().getColor(R.color._xpopup_title_color));
               }else {
                   if(holder.getView(R.id.check_view)!=null) holder.getView(R.id.check_view).setVisibility(GONE);
                   //如果没有选择，则文字居中
                   holder.<TextView>getView(R.id.tv_text).setGravity(Gravity.CENTER);
               }
               if(bindItemLayoutId==0 && popupInfo.isDarkTheme){
                   holder.<TextView>getView(R.id.tv_text).setTextColor(getResources().getColor(R.color._xpopup_white_color));
               }
           }

        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectListener != null) {
                    if (position >= 0 && position < adapter.getData().size())
                        selectListener.onSelect(position, adapter.getData().get(position).toString());
                }
                if (checkedPosition != -1) {
                    checkedPosition = position;
                    adapter.notifyDataSetChanged();
                }
                if (popupInfo.autoDismiss) dismiss();
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
        ((ViewGroup)tv_title.getParent()).setBackgroundResource(R.drawable._xpopup_round3_dark_bg);
        findViewById(R.id.xpopup_divider).setBackgroundColor(getResources().getColor(R.color._xpopup_list_dark_divider));
    }
    CharSequence title;
    String[] data;
    int[] iconIds;

    public CenterListPopupView setStringData(CharSequence title, String[] data, int[] iconIds) {
        this.title = title;
        this.data = data;
        this.iconIds = iconIds;
        return this;
    }

    private OnSelectListener selectListener;

    public CenterListPopupView setOnSelectListener(OnSelectListener selectListener) {
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
    public CenterListPopupView setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }

    @Override
    protected int getMaxWidth() {
        return popupInfo.maxWidth == 0 ? (int) (super.getMaxWidth() * .8f)
                : popupInfo.maxWidth;
    }
}
