package com.fanchen.popup.impl;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanchen.popup.Popup;
import com.fanchen.ui.R;
import com.fanchen.popup.core.CenterPopupView;

/**
 * Description: 加载对话框
 *  2018/12/16
 */
public class LoadingPopupView extends CenterPopupView {
    private TextView tv_title;

    public LoadingPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId != 0 ? bindLayoutId : R.layout._xpopup_center_impl_loading;
    }

    /**
     * 绑定已有布局
     *
     * @param layoutId 如果要显示标题，则要求必须有id为tv_title的TextView，否则无任何要求
     * @return
     */
    public LoadingPopupView bindLayout(int layoutId) {
        bindLayoutId = layoutId;
        return this;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        tv_title = findViewById(R.id.tv_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getPopupImplView().setElevation(10f);
        }

        setup();
    }
    protected void setup() {
        if (title != null && title.length()!=0 && tv_title != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (tv_title.getText().length() != 0) {
                        TransitionManager.beginDelayedTransition((ViewGroup) tv_title.getParent(), new TransitionSet()
                                .setDuration(Popup.getAnimationDuration())
                                .addTransition(new ChangeBounds()));
                    }
                    tv_title.setVisibility(VISIBLE);
                    tv_title.setText(title);
                }
            });
        }
    }

    private CharSequence title;

    public LoadingPopupView setTitle(CharSequence title) {
        this.title = title;
        setup();
        return this;
    }
}
