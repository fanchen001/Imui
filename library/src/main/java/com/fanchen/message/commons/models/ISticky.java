package com.fanchen.message.commons.models;

public interface ISticky {
    String getLetter();

    String getAvatar();

    String getDisplayName();

    String getUsername();

    boolean isSelect();

    boolean isShow();

    void setSelect(boolean select);
}
