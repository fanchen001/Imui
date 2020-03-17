package imui.jiguang.cn.imuisample.messages;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.fanchen.R;
import com.fanchen.sticky.StickyListHeadersAdapter;

import java.util.ArrayList;

public class StickyListAdapter  extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {
    private ArrayList<String> mList = new ArrayList<>();
    private int[] mSectionIndices;
    private String[] mSectionLetters;

    public StickyListAdapter(){
        mList.add("1");
        mList.add("18");
        mList.add("17");
        mList.add("16");
        mList.add("15");
        mList.add("14");
        mList.add("13");
        mList.add("12");
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        if (mList.size() > 0) {
            char lastFirstChar = mList.get(0).charAt(0);
            sectionIndices.add(0);
            for (int i = 1; i < mList.size(); i++) {
                if (mList.get(i).charAt(0) != lastFirstChar) {
                    lastFirstChar = mList.get(i).charAt(0);
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
                letters[i] = mList.get(mSectionIndices[i]);
            }
            return letters;
        }
        return null;
    }

    @Override
    public int getCount() {
        Log.e("xxxxx","getView" + mList.size());
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("xxxxx","getView" + position);
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_im_contacts, parent, false);
        return inflate;
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
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(parent.getContext());
        textView.setText("" + position);
        return textView;
    }

    @Override
    public long getHeaderId(int position) {
        return mList.get(position).charAt(0);
    }
}
