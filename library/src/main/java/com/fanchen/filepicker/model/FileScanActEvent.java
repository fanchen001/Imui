package com.fanchen.filepicker.model;

/**
 * FileScanActEvent
 */

public class FileScanActEvent {
    private int canSelectMaxCount;

    public FileScanActEvent(int canSelectMaxCount) {
        this.canSelectMaxCount = canSelectMaxCount;
    }

    public int getCanSelectMaxCount() {
        return canSelectMaxCount;
    }

    public void setCanSelectMaxCount(int canSelectMaxCount) {
        this.canSelectMaxCount = canSelectMaxCount;
    }
}
