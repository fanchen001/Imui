package com.fanchen.filepicker.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fanchen.filepicker.BaseFileFragment;
import com.fanchen.permission.PermissionCallback;
import com.fanchen.permission.PermissionHelper;
import com.fanchen.permission.PermissionItem;
import com.fanchen.ui.R;
import com.fanchen.base.BaseIActivity;
import com.fanchen.filepicker.SelectOptions;
import com.fanchen.filepicker.adapter.FragmentPagerAdapter;
import com.fanchen.filepicker.loader.EssMimeTypeCollection;
import com.fanchen.filepicker.model.EssFile;
import com.fanchen.filepicker.model.FileScanActEvent;
import com.fanchen.filepicker.model.FileScanFragEvent;
import com.fanchen.filepicker.model.FileScanSortChangedEvent;
import com.fanchen.filepicker.util.Const;
import com.fanchen.filepicker.util.FileUtils;
import com.fanchen.filepicker.util.UiUtils;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 通过扫描来选择文件
 */
public class SelectFileByScanActivity extends BaseIActivity implements ViewPager.OnPageChangeListener, PermissionCallback {

    /*todo 是否可预览文件，默认可预览*/
    private boolean mCanPreview = true;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolBar;
    private MenuItem mCountMenuItem;

    private ArrayList<EssFile> mSelectedFileList = new ArrayList<>();
    /*当前选中排序方式的位置*/
    private int mSelectSortTypeIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SelectOptions.getInstance().themeId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file_by_scan);
//        EventBus.getDefault().register(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BaseFileFragment.S_FileScanFragEvent);
        registerReceiver(mReceiver, intentFilter);

        ArrayList<PermissionItem> permissionItems = new ArrayList<>();
        permissionItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE,getString(R.string.permission_storage),R.drawable.permission_ic_storage));
        PermissionHelper.create(this).permissions(permissionItems).checkMutiPermission(this);


    }

    private void initUi() {
        mViewPager = findViewById(R.id.vp_select_file_scan);
        mTabLayout = findViewById(R.id.tabl_select_file_scan);
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("文件选择");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < SelectOptions.getInstance().getFileTypes().length; i++) {
            fragmentList.add(FileTypeListFragment.newInstance(SelectOptions.getInstance().getFileTypes()[i], SelectOptions.getInstance().isSingle, SelectOptions.getInstance().maxCount, SelectOptions.getInstance().getSortType(), EssMimeTypeCollection.LOADER_ID + i));
        }
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, Arrays.asList(SelectOptions.getInstance().getFileTypes()));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mViewPager.addOnPageChangeListener(this);

    }


//    /**
////     * Fragment中选择文件后
////     *
////     * @param event event
////     */
////    @Subscribe
////    public void onFragSelectFile(FileScanFragEvent event) {
////        if (event.isAdd()) {
////            if (SelectOptions.getInstance().isSingle) {
////                mSelectedFileList.add(event.getSelectedFile());
////                Intent result = new Intent();
////                result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, mSelectedFileList);
////                setResult(RESULT_OK, result);
////                super.onBackPressed();
////                return;
////            }
////            mSelectedFileList.add(event.getSelectedFile());
////        } else {
////            mSelectedFileList.remove(event.getSelectedFile());
////        }
////        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(SelectOptions.getInstance().maxCount)));
////        EventBus.getDefault().post(new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
////    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_menu, menu);
        mCountMenuItem = menu.findItem(R.id.browser_select_count);
        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(SelectOptions.getInstance().maxCount)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.browser_select_count) {
            //选中
            if (mSelectedFileList.isEmpty()) {
                return true;
            }
            //不为空
            Intent result = new Intent();
            result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, mSelectedFileList);
            setResult(RESULT_OK, result);
            super.onBackPressed();
        } else if (i == R.id.browser_sort) {
            //排序
            new AlertDialog
                    .Builder(this)
                    .setSingleChoiceItems(R.array.sort_list_scan, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSelectSortTypeIndex = which;
                        }
                    })
                    .setNegativeButton("降序", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (mSelectSortTypeIndex) {
                                case 0:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_NAME_DESC);
                                    break;
                                case 1:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_TIME_ASC);
                                    break;
                                case 2:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_SIZE_DESC);
                                    break;
                            }
                            Intent intent = new Intent(BaseFileFragment.S_FileScanSortChangedEvent);
                            intent.putExtra("data", new FileScanSortChangedEvent(SelectOptions.getInstance().getSortType(), mViewPager.getCurrentItem()));
                            sendBroadcast(intent);
//                            EventBus.getDefault().post(new FileScanSortChangedEvent(SelectOptions.getInstance().getSortType(),mViewPager.getCurrentItem()));
                        }
                    })
                    .setPositiveButton("升序", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (mSelectSortTypeIndex) {
                                case 0:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_NAME_ASC);
                                    break;
                                case 1:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_TIME_DESC);
                                    break;
                                case 2:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_SIZE_ASC);
                                    break;
                            }
                            Intent intent = new Intent(BaseFileFragment.S_FileScanSortChangedEvent);
                            intent.putExtra("data", new FileScanSortChangedEvent(SelectOptions.getInstance().getSortType(), mViewPager.getCurrentItem()));
                            sendBroadcast(intent);
//                            EventBus.getDefault().post(new FileScanSortChangedEvent(SelectOptions.getInstance().getSortType(),mViewPager.getCurrentItem()));
                        }
                    })
                    .setTitle("请选择")
                    .show();

        }
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Intent intent = new Intent(BaseFileFragment.S_FileScanActEvent);
        intent.putExtra("data", new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
        sendBroadcast(intent);
//        EventBus.getDefault().post(new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!BaseFileFragment.S_FileScanFragEvent.equals(intent.getAction())) return;
            if (!intent.hasExtra("data")) return;
            FileScanFragEvent event = intent.getParcelableExtra("data");
            if (event.isAdd()) {
                if (SelectOptions.getInstance().isSingle) {
                    mSelectedFileList.add(event.getSelectedFile());
                    Intent result = new Intent();
                    result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, mSelectedFileList);
                    SelectFileByScanActivity.this.setResult(RESULT_OK, result);
                    SelectFileByScanActivity.this.onBackPressed();
                    return;
                }
                mSelectedFileList.add(event.getSelectedFile());
            } else {
                mSelectedFileList.remove(event.getSelectedFile());
            }
            mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(SelectOptions.getInstance().maxCount)));
            Intent sendIntent = new Intent(BaseFileFragment.S_FileScanActEvent);
            sendIntent.putExtra("data", new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
            sendBroadcast(sendIntent);
//                EventBus.getDefault().post(new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
        }

    };

    @Override
    public void onClose() {
        Toast.makeText(this,R.string.permission_error, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFinish() {
        initUi();
        UiUtils.setViewPadding(findViewById(R.id.abl_title));
    }
}
