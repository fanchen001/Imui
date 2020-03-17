package com.fanchen.chat.emoji.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import com.fanchen.ui.R;
import com.fanchen.chat.emoji.data.EmoticonPageEntity;
import com.fanchen.chat.emoji.listener.EmoticonClickListener;
import com.fanchen.chat.emoji.listener.EmoticonDisplayListener;

public class EmoticonsAdapter<T> extends BaseAdapter {

    protected final int DEF_HEIGHTMAXTATIO = 2;
    protected final int mDefalutItemHeight;

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ArrayList<T> mData = new ArrayList<>();
    protected EmoticonPageEntity mEmoticonPageEntity;
    protected double mItemHeightMaxRatio;
    protected int mItemHeightMax;
    protected int mItemHeightMin;
    protected int mItemHeight;
    protected int mDelbtnPosition;
    protected EmoticonDisplayListener<T> mOnDisPlayListener;
    protected EmoticonClickListener mOnEmoticonClickListener;

    public EmoticonsAdapter(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mEmoticonPageEntity = emoticonPageEntity;
        this.mOnEmoticonClickListener = onEmoticonClickListener;
        this.mItemHeightMaxRatio = DEF_HEIGHTMAXTATIO;
        this.mDelbtnPosition = -1;
        this.mDefalutItemHeight = this.mItemHeight = (int) context.getResources().getDimension(R.dimen.item_emoticon_size_default);
        this.mData.addAll(emoticonPageEntity.getEmoticonList());
        checkDelBtn(emoticonPageEntity);
    }

    private void checkDelBtn(EmoticonPageEntity entity) {
        EmoticonPageEntity.DelBtnStatus delBtnStatus = entity.getDelBtnStatus();
        if (EmoticonPageEntity.DelBtnStatus.GONE.equals(delBtnStatus)) {
            return;
        }
        if (EmoticonPageEntity.DelBtnStatus.FOLLOW.equals(delBtnStatus)) {
            mDelbtnPosition = getCount();
            mData.add(null);
        } else if (EmoticonPageEntity.DelBtnStatus.LAST.equals(delBtnStatus)) {
            int max = entity.getLine() * entity.getRow();
            while (getCount() < max) {
                mData.add(null);
            }
            mDelbtnPosition = getCount() - 1;
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setGravity(Gravity.CENTER);
            ImageView imageView = new ImageView(parent.getContext());
            int padding = dpToPx(parent.getContext(), 7);
            imageView.setPadding(padding,0,padding,0);
            linearLayout.addView(imageView);
            convertView = linearLayout;
            viewHolder.iv_emoticon = imageView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindView(position, parent, viewHolder);
        updateUI(viewHolder, parent);
        return convertView;
    }

    protected void bindView(int position, ViewGroup parent, ViewHolder viewHolder) {
        if (mOnDisPlayListener != null) {
            mOnDisPlayListener.onBindView(position, parent, viewHolder, mData.get(position), position == mDelbtnPosition);
        }
    }

    protected boolean isDelBtn(int position) {
        return position == mDelbtnPosition;
    }

    protected void updateUI(final ViewHolder viewHolder, final ViewGroup parent) {
        final View newParent = (View) parent.getParent();
        int measuredHeight = newParent.getMeasuredHeight();
        if (mDefalutItemHeight != mItemHeight) {
            viewHolder.iv_emoticon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mItemHeight));
        }
        mItemHeightMax = this.mItemHeightMax != 0 ? this.mItemHeightMax : (int) (mItemHeight * mItemHeightMaxRatio);
        mItemHeightMin = this.mItemHeightMin != 0 ? this.mItemHeightMin : mItemHeight;
        int realItemHeight = measuredHeight / mEmoticonPageEntity.getLine();
        realItemHeight = Math.min(realItemHeight, mItemHeightMax);
        realItemHeight = Math.max(realItemHeight, mItemHeightMin);
        viewHolder.iv_emoticon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, realItemHeight));
    }

    public void setOnDisPlayListener(EmoticonDisplayListener mOnDisPlayListener) {
        this.mOnDisPlayListener = mOnDisPlayListener;
    }

    public void setItemHeightMaxRatio(double mItemHeightMaxRatio) {
        this.mItemHeightMaxRatio = mItemHeightMaxRatio;
    }

    public void setItemHeightMax(int mItemHeightMax) {
        this.mItemHeightMax = mItemHeightMax;
    }

    public void setItemHeightMin(int mItemHeightMin) {
        this.mItemHeightMin = mItemHeightMin;
    }

    public void setItemHeight(int mItemHeight) {
        this.mItemHeight = mItemHeight;
    }

    public void setDelbtnPosition(int mDelbtnPosition) {
        this.mDelbtnPosition = mDelbtnPosition;
    }

    public static class ViewHolder {
        public ImageView iv_emoticon;
    }

    /***
     * DP 转 PX
     * @param c
     * @param dipValue
     * @return
     */
    public int dpToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}