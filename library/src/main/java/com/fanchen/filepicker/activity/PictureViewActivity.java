package com.fanchen.filepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fanchen.ui.R;
import com.fanchen.filepicker.SelectOptions;
import com.fanchen.filepicker.model.EssFile;
import com.fanchen.picture.view.HackyViewPager;

import java.util.ArrayList;

public class PictureViewActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE_BACK = "type_back";
    public static final String EXTRA_SELECTED_MEDIA = "EXTRA_SELECTED_MEDIA";

    public static final String SELECTED_MEDIA = "selected_media";
    public static final String START_POS = "start_pos";

    HackyViewPager mGallery;
    ProgressBar mProgressBar;

    private boolean mNeedLoading = false;
    private boolean mNeedAllCount = true;
    private int mCurrentPage;
    private int mTotalCount;
    private int mStartPos;
    private int mPos;
    private int mMaxCount;

    private Toolbar mToolbar;
    private ImagesAdapter mAdapter;
    private EssFile mCurrentImageItem;
    private Button mOkBtn;
    private ArrayList<EssFile> mImages;
    private ArrayList<EssFile> mSelectedImages;
    private MenuItem mSelectedMenuItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(SelectOptions.getInstance().themeId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);
        createToolbar();
        initData(savedInstanceState);
        initView();

        startLoading();
    }

    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.nav_top_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mImages = savedInstanceState.getParcelableArrayList(SELECTED_MEDIA);
            this.mStartPos = savedInstanceState.getInt(START_POS, 0);
        } else if (getIntent() != null) {
            this.mStartPos = getIntent().getIntExtra(START_POS, 0);
            this.mImages = getIntent().getParcelableArrayListExtra(SELECTED_MEDIA);
        }
        mSelectedImages = new ArrayList<>();
        mMaxCount = SelectOptions.getInstance().maxCount;
        if (mImages != null) {
            for (EssFile f : mImages){
                if(f.isChecked()){
                    mSelectedImages.add(f);
                }
            }
        }
    }

    private void initView() {
        mAdapter = new ImagesAdapter(getSupportFragmentManager());
        mOkBtn = (Button) findViewById(R.id.image_items_ok);
        mGallery = (HackyViewPager) findViewById(R.id.pager);
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mGallery.setAdapter(mAdapter);
        mGallery.addOnPageChangeListener(new OnPagerChangeListener());
        setOkTextNumber();
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishByBackPressed(false);
            }
        });
    }

    private void setOkTextNumber() {
        int selectedSize = mSelectedImages.size();
        int size = Math.max(mSelectedImages.size(), mMaxCount);
        mOkBtn.setText(getString(R.string.pic_image_preview_ok_fmt, String.valueOf(selectedSize)
                , String.valueOf(size)));
        mOkBtn.setEnabled(selectedSize > 0);
    }

    private void finishByBackPressed(boolean value) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_SELECTED_MEDIA, mSelectedImages);
        intent.putExtra(EXTRA_TYPE_BACK, value);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_picture_image_viewer, menu);
        mSelectedMenuItem = menu.findItem(R.id.menu_image_item_selected);
        if (mCurrentImageItem != null) {
            setMenuIcon(mCurrentImageItem.isChecked());
        } else {
            setMenuIcon(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_image_item_selected) {
            if (mCurrentImageItem == null) {
                return false;
            }
            if (mSelectedImages.size() >= mMaxCount && !mCurrentImageItem.isChecked()) {
                String warning = getString(R.string.pic_max_image_over_fmt, mMaxCount);
                Toast.makeText(this, warning, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (mCurrentImageItem.isChecked()) {
                cancelImage();
            } else {
                if (!mSelectedImages.contains(mCurrentImageItem)) {
                    mCurrentImageItem.setChecked(true);
                    mSelectedImages.add(mCurrentImageItem);
                }
            }
            setOkTextNumber();
            setMenuIcon(mCurrentImageItem.isChecked());
            if(mMaxCount == 1 && mMaxCount == mSelectedImages.size()){
                finishByBackPressed(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelImage() {
        if (mSelectedImages.contains(mCurrentImageItem)) {
            mSelectedImages.remove(mCurrentImageItem);
        }
        mCurrentImageItem.setChecked(false);
    }


    private void setMenuIcon(boolean isSelected) {
        mSelectedMenuItem.setIcon(isSelected ? R.drawable.ic_pic_checked : R.drawable.shape_pic_unchecked);
    }

    public void startLoading() {
        mCurrentImageItem = mImages.get(mStartPos);
        mToolbar.setTitle(getString(R.string.pic_image_preview_title_fmt, String.valueOf(mStartPos + 1)
                , String.valueOf(mSelectedImages.size())));
        mProgressBar.setVisibility(View.GONE);
        mGallery.setVisibility(View.VISIBLE);
        mAdapter.setMedias(mImages);
        if (mStartPos > 0 && mStartPos < mImages.size()) {
            mGallery.setCurrentItem(mStartPos, false);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSelectedImages != null) {
            outState.putParcelableArrayList(EXTRA_SELECTED_MEDIA, mSelectedImages);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
        finishByBackPressed(true);
    }

    private class ImagesAdapter extends FragmentStatePagerAdapter {
        private ArrayList<EssFile> mMedias;

        ImagesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return RawImageFragment.newInstance(mMedias.get(i));
        }

        @Override
        public int getCount() {
            return mMedias == null ? 0 : mMedias.size();
        }

        public void setMedias(ArrayList<EssFile> medias) {
            this.mMedias = medias;
            notifyDataSetChanged();
        }
    }

    private class OnPagerChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            if (mToolbar != null && position < mImages.size()) {
                mToolbar.setTitle(getString(R.string.pic_image_preview_title_fmt, String.valueOf(position + 1)
                        , mNeedLoading ? String.valueOf(mTotalCount) : String.valueOf(mImages.size())));
                mCurrentImageItem = (EssFile) mImages.get(position);
                invalidateOptionsMenu();
            }
        }

    }

}
