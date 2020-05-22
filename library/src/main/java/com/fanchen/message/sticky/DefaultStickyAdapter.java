package com.fanchen.message.sticky;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fanchen.message.commons.models.ISticky;
import com.fanchen.message.pinyin.PinyinComparator;
import com.fanchen.ui.R;
import com.fanchen.view.ContactsView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultStickyAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer, View.OnClickListener,
        SideBarView.OnTouchingLetterChangedListener, View.OnLongClickListener {

    private List<ISticky> mData = new ArrayList<>();
    private boolean mCheck;
    private int[] mSectionIndices;
    private String[] mSectionLetters;
    private LayoutInflater mInflater;
    private ContactsView mView;

    private OnLoadAvatarListener mOnLoadAvatarListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemCheckListener mOnItemCheckListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public DefaultStickyAdapter(ContactsView view, List<ISticky> mData, boolean check) {
        if (mData != null) {
            this.mData.addAll(mData);
        }
        this.mCheck = check;
        this.mView = view;
        mView.setSideBarTouchListener(this);
        Collections.sort(mData, new PinyinComparator());
        mInflater = LayoutInflater.from(view.getContext());
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    public DefaultStickyAdapter(ContactsView view, List<ISticky> mData) {
        this(view, mData, false);
    }

    public List<ISticky> getData() {
        return mData;
    }

    public void remove(ISticky friend) {
        mData.remove(friend);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mData.remove(index);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        notifyDataSetChanged();
    }

    public void add(ISticky friend) {
        mData.add(friend);
        Collections.sort(mData, new PinyinComparator());
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        notifyDataSetChanged();
    }

    public void add(List<ISticky> friend) {
        mData.addAll(friend);
        Collections.sort(mData, new PinyinComparator());
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        notifyDataSetChanged();
    }

    public void setDate(List<ISticky> friend) {
        mData.clear();
        if (friend != null) {
            mData.addAll(friend);
        }
        Collections.sort(mData, new PinyinComparator());
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        notifyDataSetChanged();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        ISticky model = mData.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_im_contacts_header, parent, false);
            if (Build.VERSION.SDK_INT >= 11) convertView.setAlpha(0.85f);
        }
        TextView text = convertView.findViewById(R.id.section_tv);
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        text.setText(model.getLetter());
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            text.setText(model.getLetter());
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_im_contacts, parent, false);
        }
        CheckBox checkBox = convertView.findViewById(R.id.cb_selected);
        ImageView avatar = convertView.findViewById(R.id.iv_photo);
        TextView displayName = convertView.findViewById(R.id.tv_name);
        View viewById = convertView.findViewById(R.id.bt_delete);
        View mNote = convertView.findViewById(R.id.bt_note);
        View view = convertView.findViewById(R.id.ll_contacts_content);
        //所有好友列表
        ISticky friend = mData.get(position);
        checkBox.setVisibility(mCheck ? View.VISIBLE : View.GONE);
        if (friend.getAvatar() != null) {
            if (new File(friend.getAvatar()).exists()) {
                avatar.setImageBitmap(BitmapFactory.decodeFile(friend.getAvatar()));
            } else if (friend.getAvatar().startsWith("http")) {
                Glide.with(parent.getContext()).load(friend.getAvatar()).asBitmap().into(avatar);
            }else if(mOnLoadAvatarListener != null){
                mOnLoadAvatarListener.onLoadAvatar(avatar, friend);
            }
        }
        displayName.setText(friend.getDisplayName());
        checkBox.setChecked(friend.isSelect());

        mNote.setTag(new Object[]{friend, position, checkBox});
        mNote.setOnClickListener(this);
        viewById.setTag(new Object[]{friend, position, checkBox});
        viewById.setOnClickListener(this);

        view.setTag(new Object[]{friend, position, checkBox});
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mData.get(position).getLetter().charAt(0);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        if (mData.size() > 0) {
            char lastFirstChar = mData.get(0).getLetter().charAt(0);
            sectionIndices.add(0);
            for (int i = 1; i < mData.size(); i++) {
                if (mData.get(i).getLetter().charAt(0) != lastFirstChar) {
                    lastFirstChar = mData.get(i).getLetter().charAt(0);
                    sectionIndices.add(i);
                }
            }
            int[] sections = new int[sectionIndices.size()];
            for (int i = 0; i < sectionIndices.size(); i++) {
                sections[i] = sectionIndices.get(i);
            }
            return sections;
        }
        return null;
    }

    private String[] getSectionLetters() {
        if (null != mSectionIndices) {
            String[] letters = new String[mSectionIndices.length];
            for (int i = 0; i < mSectionIndices.length; i++) {
                letters[i] = mData.get(mSectionIndices[i]).getLetter();
            }
            return letters;
        }
        return null;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (null == mSectionIndices || mSectionIndices.length == 0) {
            return 0;
        }
        if (sectionIndex >= mSectionIndices.length) {
            sectionIndex = mSectionIndices.length - 1;
        } else if (sectionIndex < 0) {
            sectionIndex = 0;
        }
        return mSectionIndices[sectionIndex];
    }

    @Override
    public int getSectionForPosition(int position) {
        if (null != mSectionIndices) {
            for (int i = 0; i < mSectionIndices.length; i++) {
                if (position < mSectionIndices[i]) {
                    return i - 1;
                }
            }
            return mSectionIndices.length - 1;
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        Object[] tag = (Object[]) v.getTag();
        ISticky friend = (ISticky) tag[0];
        int position = (int) tag[1];
        if (mCheck) {
            if (mOnItemCheckListener != null) {
                mOnItemCheckListener.onItemCheck((CheckBox) tag[2], friend);
            } else {
                friend.setSelect(!friend.isSelect());
                ((CheckBox) tag[2]).setChecked(friend.isSelect());
            }
        } else {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position, v, friend);
            }
        }
    }

    public void setOnLoadAvatarListener(OnLoadAvatarListener mOnLoadAvatarListener) {
        this.mOnLoadAvatarListener = mOnLoadAvatarListener;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemCheckListener(OnItemCheckListener mOnItemCheckListener) {
        this.mOnItemCheckListener = mOnItemCheckListener;
    }

    public List<ISticky> getSelectList() {
        ArrayList<ISticky> iStickies = new ArrayList<>();
        for (ISticky sticky : mData) {
            if (sticky.isSelect()) {
                iStickies.add(sticky);
            }
        }
        return iStickies;
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = getSectionForLetter(s);
        if (position != -1 && position < getCount()) {
            mView.setSelection(position);
        }
    }

    private int getSectionForLetter(String letter) {
        if (null != mSectionIndices) {
            for (int i = 0; i < mSectionIndices.length; i++) {
                if (mSectionLetters[i].equals(letter)) {
                    return mSectionIndices[i] + 1;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean onLongClick(View v) {
        Object[] tag = (Object[]) v.getTag();
        ISticky friend = (ISticky) tag[0];
        int position = (int) tag[1];
        if (mOnItemLongClickListener != null) {
            return mOnItemLongClickListener.onItemLongClick(position, v, friend);
        }
        return false;
    }

    public interface OnLoadAvatarListener {

        void onLoadAvatar(ImageView avatarView, ISticky friend);

    }

    public interface OnItemClickListener {

        void onItemClick(int position, View convertView, ISticky friend);

    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(int position, View convertView, ISticky friend);

    }

    public interface OnItemCheckListener {

        void onItemCheck(CheckBox checkBox, ISticky friend);

    }
}
