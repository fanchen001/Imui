package com.fanchen.filepicker.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanchen.filepicker.BaseFileFragment;
import com.fanchen.ui.R;
import com.fanchen.filepicker.SelectOptions;
import com.fanchen.filepicker.adapter.FileListAdapter;
import com.fanchen.filepicker.loader.EssMimeTypeCollection;
import com.fanchen.filepicker.model.EssFile;
import com.fanchen.filepicker.model.FileScanActEvent;
import com.fanchen.filepicker.model.FileScanFragEvent;
import com.fanchen.filepicker.model.FileScanSortChangedEvent;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * FileTypeListFragment
 */
public class FileTypeListFragment extends BaseFileFragment implements BaseQuickAdapter.OnItemClickListener,
        EssMimeTypeCollection.EssMimeTypeCallbacks {

    private static final String ARG_FileType = "ARG_FileType";
    private static final String ARG_IsSingle = "mIsSingle";
    private static final String ARG_MaxCount = "mMaxCount";
    private static final String ARG_SortType = "mSortType";
    private static final String ARG_Loader_Id = "ARG_Loader_Id";

    private String mFileType;
    private boolean mIsSingle;
    private int mMaxCount;
    private int mSortType;
    private int mLoaderId;

    private boolean mSortTypeHasChanged = false;

    private List<EssFile> mSelectedFileList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private FileListAdapter mAdapter;
    private EssMimeTypeCollection mMimeTypeCollection = new EssMimeTypeCollection();

    public FileTypeListFragment() {
    }

    public static FileTypeListFragment newInstance(String param1, boolean IsSingle, int mMaxCount, int mSortType, int loaderId) {
        FileTypeListFragment fragment = new FileTypeListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FileType, param1);
        args.putBoolean(ARG_IsSingle, IsSingle);
        args.putInt(ARG_MaxCount, mMaxCount);
        args.putInt(ARG_SortType, mSortType);
        args.putInt(ARG_Loader_Id, loaderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileType = getArguments().getString(ARG_FileType);
            mIsSingle = getArguments().getBoolean(ARG_IsSingle);
            mMaxCount = getArguments().getInt(ARG_MaxCount);
            mSortType = getArguments().getInt(ARG_SortType);
            mLoaderId = getArguments().getInt(ARG_Loader_Id);
        }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_file_type_list;
    }

    @Override
    protected void lazyLoad() {
        mMimeTypeCollection.load(mFileType, mSortType, mLoaderId);
    }

    @Override
    protected void initUI(View view) {
        mMimeTypeCollection.onCreate(getActivity(), this);
//        EventBus.getDefault().register(this);
        registerReceiver(mReceiver,S_FileScanSortChangedEvent,S_FileScanActEvent);
        mRecyclerView = view.findViewById(R.id.rcv_file_list_scan);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FileListAdapter(new ArrayList<EssFile>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.layout_picker_search_loading);
        mAdapter.setOnItemClickListener(this);
        super.initUI(view);
    }

//    /**
//     * 接收到Activity刷新最大数量后
//     *
//     * @param event event
//     */
//    @Subscribe
//    public void onFreshCount(FileScanActEvent event) {
//        mMaxCount = event.getCanSelectMaxCount();
//    }
//
//    /**
//     * 接收到Activity改变排序方式后
//     */
//    @Subscribe
//    public void onFreshSortType(FileScanSortChangedEvent event) {
//            mSortType = event.getSortType();
//            if (mLoaderId == event.getCurrentItem() + EssMimeTypeCollection.LOADER_ID) {
//               mMimeTypeCollection.load(mFileType, mSortType, mLoaderId);
//           } else {
//               mSortTypeHasChanged = true;
//           }
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (!isFirstLoad && mSortTypeHasChanged) {
                mSortTypeHasChanged = false;
                mAdapter.setNewData(new ArrayList<EssFile>());
                mAdapter.setEmptyView(R.layout.layout_picker_search_loading);
                mMimeTypeCollection.load(mFileType, mSortType, mLoaderId);
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        EssFile item = mAdapter.getData().get(position);
        //选中某文件后，判断是否单选
        if (mIsSingle) {
            mSelectedFileList.add(item);
            sendBroadcast(S_FileScanFragEvent,new FileScanFragEvent(item, true));
//            EventBus.getDefault().post(new FileScanFragEvent(item, true));
            return;
        }
        if (mAdapter.getData().get(position).isChecked()) {
            int index = findFileIndex(item);
            if (index != -1) {
                mSelectedFileList.remove(index);
                sendBroadcast(S_FileScanFragEvent,new FileScanFragEvent(item, false));
//                EventBus.getDefault().post(new FileScanFragEvent(item, false));
                mAdapter.getData().get(position).setChecked(!mAdapter.getData().get(position).isChecked());
                mAdapter.notifyItemChanged(position, "");
            }
        } else {
            if (mMaxCount <= 0) {
                //超出最大可选择数量后
                Snackbar.make(mRecyclerView, "您最多只能选择" + SelectOptions.getInstance().maxCount + "个。", Snackbar.LENGTH_SHORT).show();
                return;
            }
            mSelectedFileList.add(item);
            sendBroadcast(S_FileScanFragEvent,new FileScanFragEvent(item, true));
//            EventBus.getDefault().post(new FileScanFragEvent(item, true));
            mAdapter.getData().get(position).setChecked(!mAdapter.getData().get(position).isChecked());
            mAdapter.notifyItemChanged(position, "");
        }

    }

    /**
     * 查找文件位置
     */
    private int findFileIndex(EssFile item) {
        for (int i = 0; i < mSelectedFileList.size(); i++) {
            if (mSelectedFileList.get(i).getAbsolutePath().equals(item.getAbsolutePath())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
//        EventBus.getDefault().unregister(this);
        mMimeTypeCollection.onDestroy();
    }

    @Override
    public void onFileLoad(List<EssFile> essFileList) {
        mAdapter.setNewData(essFileList);
        mRecyclerView.scrollToPosition(0);
        if (essFileList.isEmpty()) {
            mAdapter.setEmptyView(R.layout.empty_picker_file_list);
        }
    }

    @Override
    public void onFileReset() {

    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(S_FileScanActEvent.equals(intent.getAction())){
                FileScanActEvent event = intent.getParcelableExtra("data");
                mMaxCount = event.getCanSelectMaxCount();
            }else if(S_FileScanSortChangedEvent.equals(intent.getAction())){
                FileScanSortChangedEvent event = intent.getParcelableExtra("data");
                mSortType = event.getSortType();
                if (mLoaderId == event.getCurrentItem() + EssMimeTypeCollection.LOADER_ID) {
                    mMimeTypeCollection.load(mFileType, mSortType, mLoaderId);
                } else {
                    mSortTypeHasChanged = true;
                }
            }
        }

    };

}
