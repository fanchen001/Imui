package com.fanchen.message.commons.models;

public interface ISticky {
    String getLetter();

    String getAvatar();

    String getDisplayName();

    String getUsername();

    boolean isSelect();

    void setSelect(boolean select);
}
