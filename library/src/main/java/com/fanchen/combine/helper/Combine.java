package com.fanchen.combine.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Region;
import android.support.annotation.ColorInt;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.fanchen.combine.helper.CombineHelper;
import com.fanchen.combine.helper.Utils;
import com.fanchen.combine.layout.DingLayoutManager;
import com.fanchen.combine.layout.ILayoutManager;
import com.fanchen.combine.layout.WechatLayoutManager;
import com.fanchen.combine.listener.OnFileListener;
import com.fanchen.combine.listener.OnProgressListener;
import com.fanchen.combine.listener.OnSubItemClickListener;
import com.fanchen.combine.region.DingRegionManager;
import com.fanchen.combine.region.IRegionManager;
import com.fanchen.combine.region.WechatRegionManager;

import java.util.List;

public class Combine {

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
         Context context;
         ImageView imageView;
         int size; // 最终生成bitmap的尺寸
         int gap; // 每个小bitmap之间的距离
         int gapColor; // 间距的颜色
         int placeholder  = 0; // 获取图片失败时的默认图片
         int count; // 要加载的资源数量
         int subSize; // 单个bitmap的尺寸

         ILayoutManager layoutManager; // bitmap的组合样式

         Region[] regions;
         OnSubItemClickListener subItemClickListener; // 单个bitmap点击事件回调

         OnProgressListener progressListener; // 最终的组合bitmap回调接口

         OnFileListener fileListener; // 最终的组合bitmap回调接口

         Bitmap[] bitmaps;
         int[] resourceIds;
         String[] urls;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setImageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder setSize(int size) {
            this.size = Utils.dp2px(context, size);
            return this;
        }

        public Builder setGap(int gap) {
            this.gap = Utils.dp2px(context, gap);
            return this;
        }

        public Builder setFileListener(OnFileListener fileListener) {
            this.fileListener = fileListener;
            return this;
        }

        public Builder setGapColor(@ColorInt int gapColor) {
            this.gapColor = gapColor;
            return this;
        }

        public Builder setPlaceholder(int placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder setLayoutManager(ILayoutManager layoutManager) {
            this.layoutManager = layoutManager;
            return this;
        }

        public Builder setOnProgressListener(OnProgressListener progressListener) {
            this.progressListener = progressListener;
            return this;
        }

        public Builder setOnSubItemClickListener(OnSubItemClickListener subItemClickListener) {
            this.subItemClickListener = subItemClickListener;
            return this;
        }

        public Builder setBitmaps(Bitmap... bitmaps) {
            this.bitmaps = bitmaps;
            this.count = bitmaps.length;
            return this;
        }

        public Builder setUrls(String... urls) {
            this.urls = urls;
            this.count = urls.length;
            return this;
        }

        public Builder setUrls(List<String> urls) {
            this.urls = new String[urls.size() > 4 ? 4 : urls.size()];
            for (int i = 0 ; i < this.urls.length; i ++){
                this.urls[i] = urls.get(i);
            }
            this.count = this.urls.length;
            return this;
        }

        public Builder setResourceIds(int... resourceIds) {
            this.resourceIds = resourceIds;
            this.count = resourceIds.length;
            return this;
        }

        public void build() {
            subSize = getSubSize(size, gap, layoutManager, count);
            initRegions();
            CombineHelper.init().load(this);
        }

        /**
         * 根据最终生成bitmap的尺寸，计算单个bitmap尺寸
         *
         * @param size
         * @param gap
         * @param layoutManager
         * @param count
         * @return
         */
        private int getSubSize(int size, int gap, ILayoutManager layoutManager, int count) {
            int subSize = 0;
            if (layoutManager instanceof DingLayoutManager) {
                subSize = size;
            } else if (layoutManager instanceof WechatLayoutManager) {
                if (count < 2) {
                    subSize = size;
                } else if (count < 5) {
                    subSize = (size - 3 * gap) / 2;
                } else if (count < 10) {
                    subSize = (size - 4 * gap) / 3;
                }
            } else {
                throw new IllegalArgumentException("Must use DingLayoutManager or WechatRegionManager!");
            }
            return subSize;
        }

        /**
         * 初始化RegionManager
         */
        private void initRegions() {
            if (subItemClickListener == null || imageView == null) {
                return;
            }

            IRegionManager regionManager;

            if (layoutManager instanceof DingLayoutManager) {
                regionManager = new DingRegionManager();
            } else if (layoutManager instanceof WechatLayoutManager) {
                regionManager = new WechatRegionManager();
            } else {
                throw new IllegalArgumentException("Must use DingLayoutManager or WechatRegionManager!");
            }

            regions = regionManager.calculateRegion(size, subSize, gap, count);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                int initIndex = -1;
                int currentIndex = -1;
                Point point = new Point();

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    point.set((int) event.getX(), (int) event.getY());
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initIndex = getRegionIndex(point.x, point.y);
                            currentIndex = initIndex;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            currentIndex = getRegionIndex(point.x, point.y);
                            break;
                        case MotionEvent.ACTION_UP:
                            currentIndex = getRegionIndex(point.x, point.y);
                            if (currentIndex != -1 && currentIndex == initIndex) {
                                subItemClickListener.onSubItemClick(currentIndex);
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            initIndex = currentIndex = -1;
                            break;
                    }
                    return true;
                }
            });
        }

        /**
         * 根据触摸点计算对应的Region索引
         *
         * @param x
         * @param y
         * @return
         */
        private int getRegionIndex(int x, int y) {
            for (int i = 0; i < regions.length; i++) {
                if (regions[i].contains(x, y)) {
                    return i;
                }
            }
            return -1;
        }

    }
}
