package com.fanchen.filepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * FileScanSortChangedEvent
 */
public class FileScanSortChangedEvent implements Parcelable {
    private int sortType;
    private int currentItem;

    public FileScanSortChangedEvent(int sortType, int currentItem) {
        this.sortType = sortType;
        this.currentItem = currentItem;
    }

    protected FileScanSortChangedEvent(Parcel in) {
        sortType = in.readInt();
        currentItem = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sortType);
        dest.writeInt(currentItem);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileScanSortChangedEvent> CREATOR = new Creator<FileScanSortChangedEvent>() {
        @Override
        public FileScanSortChangedEvent createFromParcel(Parcel in) {
            return new FileScanSortChangedEvent(in);
        }

        @Override
        public FileScanSortChangedEvent[] newArray(int size) {
            return new FileScanSortChangedEvent[size];
        }
    };

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public FileScanSortChangedEvent(int sortType) {
        this.sortType = sortType;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }
}
