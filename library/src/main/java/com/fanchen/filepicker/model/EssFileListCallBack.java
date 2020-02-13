package com.fanchen.filepicker.model;

import java.util.List;

/**
 * EssFileListCallBack
 */

public interface EssFileListCallBack {
    void onFindFileList(String queryPath, List<EssFile> essFileList);
}
