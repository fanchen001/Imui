package com.fanchen.picture.bean;


/**
 * 图片信息
 */
public class ImageInfo implements  IImageInfo{

  private String thumbnailUrl;// 缩略图，质量很差
  private String originUrl;// 原图或者高清图

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public String getOriginUrl() {
    return originUrl;
  }

  public void setOriginUrl(String originUrl) {
    this.originUrl = originUrl;
  }

  @Override public String toString() {
    return "ImageInfo{"
        + "thumbnailUrl='"
        + thumbnailUrl
        + '\''
        + ", originUrl='"
        + originUrl
        + '\''
        + '}';
  }
}