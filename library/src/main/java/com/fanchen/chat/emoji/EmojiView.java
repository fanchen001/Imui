package com.fanchen.chat.emoji;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.AsyncLayoutInflater;
import android.util.AttributeSet;

import java.util.ArrayList;

import com.fanchen.ui.R;
import com.fanchen.chat.emoji.adapter.PageSetAdapter;
import com.fanchen.chat.emoji.data.PageSetEntity;
import com.fanchen.chat.emoji.widget.AutoHeightLayout;
import com.fanchen.chat.emoji.widget.EmoticonsFuncView;
import com.fanchen.chat.emoji.widget.EmoticonsIndicatorView;
//import com.fanchen.chat.emoji.widget.EmoticonsToolBarView;


public class EmojiView extends AutoHeightLayout implements EmoticonsFuncView.OnEmoticonsPageViewListener/*,
        EmoticonsToolBarView.OnToolBarItemClickListener*/ {

    private EmoticonsFuncView mEmoticonsFuncView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;
//    private EmoticonsToolBarView mEmoticonsToolBarView;

    public EmojiView(Context context) {
        super(context, null);
        init(context, null);
    }

    public EmojiView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int i) {

    }

    public EmojiView(@NonNull Context context, @Nullable AttributeSet attrs,@AttrRes int defStyleAttr) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_chatinput_emoji, this);

        mEmoticonsFuncView = ((EmoticonsFuncView) findViewById(R.id.view_epv));
        mEmoticonsIndicatorView = ((EmoticonsIndicatorView) findViewById(R.id.view_eiv));
//        mEmoticonsToolBarView = ((EmoticonsToolBarView) findViewById(R.id.view_etv));
        mEmoticonsFuncView.setOnIndicatorListener(this);
//        mEmoticonsToolBarView.setOnToolBarItemClickListener(this);
    }

    public void setAdapter(PageSetAdapter pageSetAdapter) {
        if (pageSetAdapter != null) {
            ArrayList<PageSetEntity> pageSetEntities = pageSetAdapter.getPageSetEntityList();
            if (pageSetEntities != null) {
                for (PageSetEntity pageSetEntity : pageSetEntities) {
//                    mEmoticonsToolBarView.addToolItemView(pageSetEntity);
                }
            }
        }
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {
//        mEmoticonsToolBarView.setToolBtnSelect(pageSetEntity.getUuid());
    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }

//    @Override
//    public void onToolBarItemClick(PageSetEntity pageSetEntity) {
//        mEmoticonsFuncView.setCurrentPageSet(pageSetEntity);
//    }

    public EmoticonsFuncView getEmoticonsFuncView() {
        return this.mEmoticonsFuncView;
    }
}
