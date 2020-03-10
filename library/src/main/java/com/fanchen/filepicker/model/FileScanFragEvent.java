package com.fanchen.filepicker.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * FileScanFragEvent
 */

public class FileScanFragEvent implements Parcelable {

    private EssFile selectedFile;
    private boolean isAdd;

    public FileScanFragEvent(EssFile selectedFile, boolean isAdd) {
        this.selectedFile = selectedFile;
        this.isAdd = isAdd;
    }

    protected FileScanFragEvent(Parcel in) {
        selectedFile = in.readParcelable(EssFile.class.getClassLoader());
        isAdd = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(selectedFile, flags);
        dest.writeByte((byte) (isAdd ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileScanFragEvent> CREATOR = new Creator<FileScanFragEvent>() {
        @Override
        public FileScanFragEvent createFromParcel(Parcel in) {
            return new FileScanFragEvent(in);
        }

        @Override
        public FileScanFragEvent[] newArray(int size) {
            return new FileScanFragEvent[size];
        }
    };

    public EssFile getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(EssFile selectedFile) {
        this.selectedFile = selectedFile;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public EssFile getSelectedFileList() {
        return selectedFile;
    }

    public void setSelectedFileList(EssFile selectedFileList) {
        this.selectedFile = selectedFileList;
    }
}
