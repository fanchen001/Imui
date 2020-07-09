package com.fanchen.popup.impl;

import android.content.Context;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanchen.ui.R;
import com.fanchen.popup.Popup;
import com.fanchen.popup.core.CenterPopupView;
import com.fanchen.popup.interfaces.OnCancelListener;
import com.fanchen.popup.interfaces.OnConfirmListener;

/**
 * Description: 确定和取消的对话框
 *  2018/12/16
 */
public class ConfirmPopupView extends CenterPopupView implements View.OnClickListener {
    OnCancelListener cancelListener;
    OnConfirmListener confirmListener;
    TextView tv_title, tv_content, tv_cancel, tv_confirm;
    CharSequence title, content, hint, cancelText, confirmText;
    boolean isHideCancel = false;

    public ConfirmPopupView(@NonNull Context context) {
        super(context);
    }

    /**
     * 绑定已有布局
     * @param layoutId 要求布局中必须包含的TextView以及id有：tv_title，tv_content，tv_cancel，tv_confirm
     * @return
     */
    public ConfirmPopupView bindLayout(int layoutId){
        bindLayoutId = layoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId != 0 ? bindLayoutId : R.layout._xpopup_center_impl_confirm;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);

        if(bindLayoutId==0) applyPrimaryColor();

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(GONE);
        }

        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
        }else {
            tv_content.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            tv_cancel.setText(cancelText);
        }
        if (!TextUtils.isEmpty(confirmText)) {
            tv_confirm.setText(confirmText);
        }
        if (isHideCancel) tv_cancel.setVisibility(GONE);
        if(bindItemLayoutId==0 && popupInfo.isDarkTheme){
            applyDarkTheme();
        }
    }

    protected void applyPrimaryColor() {
//        tv_cancel.setTextColor(Popup.getPrimaryColor());
        if(bindItemLayoutId==0){
            tv_confirm.setTextColor(Popup.getPrimaryColor());
        }
    }

    @Override
    protected void applyDarkTheme() {
        super.applyDarkTheme();
        tv_title.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        tv_content.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        tv_cancel.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        tv_confirm.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        findViewById(R.id.xpopup_divider).setBackgroundColor(getResources().getColor(R.color._xpopup_dark_color));
        findViewById(R.id.xpopup_divider_h).setBackgroundColor(getResources().getColor(R.color._xpopup_dark_color));
        ((ViewGroup)tv_title.getParent()).setBackgroundResource(R.drawable._xpopup_round3_dark_bg);
    }

    public ConfirmPopupView setListener(OnConfirmListener confirmListener, OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        this.confirmListener = confirmListener;
        return this;
    }

    public ConfirmPopupView setTitleContent(CharSequence title, CharSequence content, CharSequence hint) {
        this.title = title;
        this.content = content;
        this.hint = hint;
        return this;
    }

    public ConfirmPopupView setCancelText(CharSequence cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public ConfirmPopupView setConfirmText(CharSequence confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    public ConfirmPopupView hideCancelBtn() {
        isHideCancel = true;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            if (cancelListener != null) cancelListener.onCancel();
            dismiss();
        } else if (v == tv_confirm) {
            if (confirmListener != null) confirmListener.onConfirm();
            if (popupInfo.autoDismiss) dismiss();
        }
    }
}
