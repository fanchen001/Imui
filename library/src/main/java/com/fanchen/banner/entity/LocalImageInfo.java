package com.fanchen.banner.entity;

import android.support.annotation.DrawableRes;

/**
 * describe: 本地资源图片
 */
public class LocalImageInfo extends SimpleBannerInfo {

    @DrawableRes
    private int bannerRes;

    public LocalImageInfo(int bannerRes) {
        this.bannerRes = bannerRes;
    }

    @Override
    public Integer getXBannerUrl() {
        return bannerRes;
    }
}
