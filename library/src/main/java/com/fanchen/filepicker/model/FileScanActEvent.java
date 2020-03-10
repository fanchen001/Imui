package com.fanchen.filepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * FileScanActEvent
 */

public class FileScanActEvent implements Parcelable {
    private int canSelectMaxCount;

    public FileScanActEvent(int canSelectMaxCount) {
        this.canSelectMaxCount = canSelectMaxCount;
    }

    protected FileScanActEvent(Parcel in) {
        canSelectMaxCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(canSelectMaxCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileScanActEvent> CREATOR = new Creator<FileScanActEvent>() {
        @Override
        public FileScanActEvent createFromParcel(Parcel in) {
            return new FileScanActEvent(in);
        }

        @Override
        public FileScanActEvent[] newArray(int size) {
            return new FileScanActEvent[size];
        }
    };

    public int getCanSelectMaxCount() {
        return canSelectMaxCount;
    }

    public void setCanSelectMaxCount(int canSelectMaxCount) {
        this.canSelectMaxCount = canSelectMaxCount;
    }
}
