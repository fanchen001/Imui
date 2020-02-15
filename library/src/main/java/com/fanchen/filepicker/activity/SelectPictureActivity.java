package com.fanchen.filepicker.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanchen.R;
import com.fanchen.filepicker.SelectOptions;
import com.fanchen.filepicker.adapter.BuketAdapter;
import com.fanchen.filepicker.adapter.EssMediaAdapter;
import com.fanchen.filepicker.loader.EssAlbumCollection;
import com.fanchen.filepicker.loader.EssMediaCollection;
import com.fanchen.filepicker.model.Album;
import com.fanchen.filepicker.model.EssFile;
import com.fanchen.filepicker.util.Const;
import com.fanchen.filepicker.util.UiUtils;
import com.fanchen.filepicker.widget.MediaItemDecoration;
import com.fanchen.filepicker.widget.ToolbarSpinner;
import com.fanchen.video.SimpleVideoActivity;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * 选择图片界面
 */
public class SelectPictureActivity extends AppCompatActivity implements
        EssAlbumCollection.EssAlbumCallbacks, AdapterView.OnItemSelectedListener,
        EssMediaCollection.EssMediaCallbacks, BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener {

    /*4. 最多可选择个数，默认10*/
    private int mMaxCount = 10;
    /*7. 是否需要显示照相机*/
    private boolean mNeedCamera = true;
    /*9. todo 是否可预览图片，默认可预览*/
    private boolean mCanPreview = true;

    private RecyclerView mRecyclerView;
    private BuketAdapter mBuketAdapter;
    private EssMediaAdapter mMediaAdapter = new EssMediaAdapter(new ArrayList<EssFile>());

    private final EssAlbumCollection mAlbumCollection = new EssAlbumCollection();
    private final EssMediaCollection mMediaCollection = new EssMediaCollection();
    private MenuItem mCountMenuItem;

    private List<EssFile> mCameraList = new ArrayList<>();

    private File mOutImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SelectOptions.getInstance().themeId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        mRecyclerView = findViewById(R.id.rcv_file_picture_list);
        initUI();
        SwipeBackHelper.onCreate(this);
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                builder.detectFileUriExposure();
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Drawable navigationIcon = toolbar.getNavigationIcon();
        TypedArray ta = getTheme().obtainStyledAttributes(new int[]{R.attr.album_element_color});
        int color = ta.getColor(0, 0);
        ta.recycle();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        mMaxCount = SelectOptions.getInstance().maxCount;

        mBuketAdapter = new BuketAdapter(this, null, false);
        ToolbarSpinner spinner = new ToolbarSpinner(this);
        spinner.setSelectedTextView((TextView) findViewById(R.id.selected_folder));
        spinner.setPopupAnchorView(findViewById(R.id.toolbar));
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(mBuketAdapter);

        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.load();
        mMediaCollection.onCreate(this, this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new MediaItemDecoration());
        mMediaAdapter.setImageResize(UiUtils.getImageResize(this, mRecyclerView));
        mRecyclerView.setAdapter(mMediaAdapter);
        mMediaAdapter.bindToRecyclerView(mRecyclerView);
        mMediaAdapter.setOnItemClickListener(this);
        mMediaAdapter.setOnItemChildClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_menu, menu);
        mCountMenuItem = menu.findItem(R.id.browser_select_count);
        mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mMediaAdapter.mSelectedFileList.size()), String.valueOf(mMaxCount)));
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
        mMediaCollection.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        mBuketAdapter.swapCursor(cursor);
        cursor.moveToFirst();
        Album album = Album.valueOf(cursor);
        mMediaCollection.load(album, mNeedCamera, mMediaAdapter.mSelectedFileList);
    }

    @Override
    public void onAlbumMediaReset() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mBuketAdapter.getCursor().moveToPosition(position);
        Album album = Album.valueOf(mBuketAdapter.getCursor());
        mMediaCollection.load(album, mNeedCamera, mMediaAdapter.mSelectedFileList);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMediaLoad(List<EssFile> essFileList) {
        if (essFileList == null) {
            essFileList = new ArrayList<>();
        }
        for (EssFile e : mCameraList) {
            if (mNeedCamera) {
                essFileList.add(1, e);
            } else {
                essFileList.add(0, e);
            }
        }
        mMediaAdapter.setNewData(essFileList);
        if (essFileList.isEmpty()) {
            mMediaAdapter.setEmptyView(R.layout.empty_picker_file_list);
        }
    }

    @Override
    public void onmMediaReset() {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        EssFile item = mMediaAdapter.getItem(position);
        if (!adapter.equals(mMediaAdapter) || item == null) {
            return;
        }
        if (view.getId() == R.id.check_view) {
            if (mMediaAdapter.mSelectedFileList.size() >= SelectOptions.getInstance().maxCount && !item.isChecked()) {
                mMediaAdapter.notifyItemChanged(position, "");
                Snackbar.make(mRecyclerView, "您最多只能选择" + SelectOptions.getInstance().maxCount + "个。", Snackbar.LENGTH_SHORT).show();
                return;
            }
            boolean addSuccess = mMediaAdapter.mSelectedFileList.add(mMediaAdapter.getItem(position));
            if (addSuccess) {
                mMediaAdapter.getData().get(position).setChecked(true);
            } else {
                //已经有了就删掉
                mMediaAdapter.mSelectedFileList.remove(item);
                mMediaAdapter.getData().get(position).setChecked(false);
            }
            mMediaAdapter.notifyItemChanged(position, "");
            mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mMediaAdapter.mSelectedFileList.size()), String.valueOf(mMaxCount)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.browser_select_count) {
            //选中
            if (mMediaAdapter.mSelectedFileList.isEmpty()) {
                return true;
            }
            Intent result = new Intent();
            result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, EssFile.getEssFileList(SelectPictureActivity.this, mMediaAdapter.mSelectedFileList));
            setResult(RESULT_OK, result);
            onBackPressed();
        }
        return true;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        EssFile essFile = mMediaAdapter.getData().get(position);
        if (!essFile.isVideo() && mNeedCamera && position == 0) {
            try {
                //获得项目缓存路径
                String filePath = getExternalCacheDir() + File.separator + "Photos";
                //如果目录不存在则必须创建目录
                File cameraFolder = new File(filePath);
                if (!cameraFolder.exists()) {
                    cameraFolder.mkdirs();
                }
                //根据时间随机生成图片名
                String photoName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".jpg";
                mOutImage = new File(filePath, photoName);
                //启动相机
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mOutImage));
                startActivityForResult(intent, 404);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else if (mCanPreview && SelectOptions.getInstance().cropConfig == null) {
            startPictureView(essFile, position);
        } else if (SelectOptions.getInstance().cropConfig != null) {
            mMediaAdapter.mSelectedFileList.add(mMediaAdapter.getData().get(position));
            ArrayList<EssFile> fileList = EssFile.getEssFileList(this, mMediaAdapter.mSelectedFileList);
            String absolutePath = fileList.get(0).getAbsolutePath();
            Uri from = new Uri.Builder().scheme("file").appendPath(absolutePath).build();
            String format = String.format(Locale.US, "%s.png", System.currentTimeMillis());
            String cachePath = null;
            if (getExternalCacheDir() != null) {
                cachePath = getExternalCacheDir().getAbsolutePath();
            } else if (getCacheDir() != null) {
                cachePath = getCacheDir().getAbsolutePath();
            }
            if (TextUtils.isEmpty(cachePath)) {
                super.onBackPressed();
                return;
            }
            Uri destUri = new Uri.Builder().scheme("file").appendPath(cachePath).appendPath(format).build();
            UCrop.Options crop = new UCrop.Options();
            crop.setCompressionFormat(Bitmap.CompressFormat.PNG);
            crop.withMaxResultSize(SelectOptions.getInstance().cropConfig.cropMaxWidth, SelectOptions.getInstance().cropConfig.cropMaxHeight);
            crop.withAspectRatio(SelectOptions.getInstance().cropConfig.cropAspectRatioX, SelectOptions.getInstance().cropConfig.cropAspectRatioY);
            UCrop.of(from, destUri).withOptions(crop).start(this, SelectOptions.getInstance().request_code);
        } else {
            Intent result = new Intent();
            result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, EssFile.getEssFileList(this, mMediaAdapter.mSelectedFileList));
            setResult(RESULT_OK, result);
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && data != null && resultCode == RESULT_OK) {
            List<EssFile> files = data.getParcelableArrayListExtra(PictureViewActivity.EXTRA_SELECTED_MEDIA);
            mMediaAdapter.mSelectedFileList = new HashSet<>(files);
            if (data.getBooleanExtra(PictureViewActivity.EXTRA_TYPE_BACK, false) && mMaxCount > files.size()) {
                mCountMenuItem.setTitle(String.format(getString(R.string.selected_file_count), String.valueOf(mMediaAdapter.mSelectedFileList.size()), String.valueOf(mMaxCount)));
            } else {
                Intent result = new Intent();
                result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, EssFile.getEssFileList(this, mMediaAdapter.mSelectedFileList));
                setResult(RESULT_OK, result);
                super.onBackPressed();
            }
        } else if (requestCode != 404 && data != null && resultCode == RESULT_OK) {
            setResult(resultCode, data);
            finish();
        } else if (mOutImage != null && mOutImage.exists()&& resultCode == RESULT_OK) {
            mCameraList.add(new EssFile(mOutImage));
        }else if(requestCode == SelectOptions.getInstance().request_code && resultCode == RESULT_OK){
            SelectOptions.getInstance().cropConfig = null;
            setResult(resultCode, data);
            finish();
        }
    }

    private void startPictureView(EssFile essFile, int position) {
        ArrayList<EssFile> essFiles = new ArrayList<>(mMediaAdapter.getData());
        if (mNeedCamera && position > 0) {
            essFiles.remove(0);
            position = position - 1;
        }
        if (essFile.isVideo()) {
            HashSet<EssFile> ef = new HashSet<>();
            ef.add(essFile);
            ArrayList<EssFile> essFileList = EssFile.getEssFileList(this, ef);
            Intent intent = new Intent(this, SimpleVideoActivity.class);
            intent.putExtra("path", essFileList.get(0).getAbsolutePath());
            intent.putExtra("name", essFileList.get(0).getFile().getName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, PictureViewActivity.class);
            intent.putParcelableArrayListExtra(PictureViewActivity.SELECTED_MEDIA, essFiles);
            intent.putExtra(PictureViewActivity.START_POS, position);
            startActivityForResult(intent, 300);
        }

    }
}
