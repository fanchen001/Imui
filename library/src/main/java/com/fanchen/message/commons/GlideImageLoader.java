package com.fanchen.message.commons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class GlideImageLoader implements ImageLoader {

    private Context mContext;
    private RequestManager with;
    private int avatar;
    private int image;
    private int video;
    private float MIN_WIDTH;
    private float MAX_WIDTH;
    private float MIN_HEIGHT;
    private float MAX_HEIGHT;

    public GlideImageLoader(Context context) {
        this(context, 0, 0, 0);
    }

    public GlideImageLoader(Context context, int avatar, int image, int video) {
        this.mContext = context;
        this.avatar = avatar;
        this.image = image;
        this.video = video;
        this.with = Glide.with(mContext.getApplicationContext());
        float density = mContext.getResources().getDisplayMetrics().density;
        MIN_WIDTH = 60 * density;
        MAX_WIDTH = 200 * density;
        MIN_HEIGHT = 60 * density;
        MAX_HEIGHT = 200 * density;
    }

    @Override
    public void loadAvatarImage(ImageView avatarImageView, String string) {
        if (string.contains("R.drawable")) {
            Integer resId = mContext.getResources().getIdentifier(string.replace("R.drawable.", ""), "drawable", mContext.getPackageName());
            avatarImageView.setImageResource(resId);
        } else {
            if (avatar != 0) {
                with.load(string).asBitmap().placeholder(avatar).into(avatarImageView);
            } else {
                with.load(string).asBitmap().into(avatarImageView);
            }
        }
    }

    /**
     * Load image message
     *
     * @param imageView Image message's ImageView.
     * @param string    A file path, or a uri or url.
     */
    @Override
    public void loadImage(final ImageView imageView, String string,final RecyclerView.LayoutManager layoutManager) {
        BitmapTypeRequest<String> stringBitmapTypeRequest = with.load(string).asBitmap();
        if (image != 0) { stringBitmapTypeRequest.placeholder(image);  }
        stringBitmapTypeRequest.into(new SimpleTarget2(layoutManager,imageView));
    }

    /**
     * Load video message
     *
     * @param imageCover Video message's image cover
     * @param uri        Local path or url.
     */
    @Override
    public void loadVideo(ImageView imageCover, String uri) {
        if (video != 0) {
            with.load(uri).asBitmap().placeholder(video).override(200, 400).into(imageCover);
        } else {
            with.load(uri).asBitmap().override(200, 400).into(imageCover);
        }
    }

    private class SimpleTarget2 extends SimpleTarget<Bitmap>{
        private RecyclerView.LayoutManager layoutManager;
        private ImageView imageView;

        public SimpleTarget2(RecyclerView.LayoutManager layoutManager, ImageView imageView) {
            this.layoutManager = layoutManager;
            this.imageView = imageView;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            int firstVisibleItemPosition = -1;
            LinearLayoutManager mLinearLayoutManager = null;
            if(layoutManager instanceof LinearLayoutManager){
                mLinearLayoutManager = (LinearLayoutManager) layoutManager;
                firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
            }
            int imageWidth = resource.getWidth();
            int imageHeight = resource.getHeight();
            // 裁剪 bitmap
            float width, height;
            if (imageWidth > imageHeight) {
                if (imageWidth > MAX_WIDTH) {
                    float temp = MAX_WIDTH / imageWidth * imageHeight;
                    height = temp > MIN_HEIGHT ? temp : MIN_HEIGHT;
                    width = MAX_WIDTH;
                } else if (imageWidth < MIN_WIDTH) {
                    float temp = MIN_WIDTH / imageWidth * imageHeight;
                    height = temp < MAX_HEIGHT ? temp : MAX_HEIGHT;
                    width = MIN_WIDTH;
                } else {
                    float ratio = imageWidth / imageHeight;
                    if (ratio > 3) {
                        ratio = 3;
                    }
                    height = imageHeight * ratio;
                    width = imageWidth;
                }
            } else {
                if (imageHeight > MAX_HEIGHT) {
                    float temp = MAX_HEIGHT / imageHeight * imageWidth;
                    width = temp > MIN_WIDTH ? temp : MIN_WIDTH;
                    height = MAX_HEIGHT;
                } else if (imageHeight < MIN_HEIGHT) {
                    float temp = MIN_HEIGHT / imageHeight * imageWidth;
                    width = temp < MAX_WIDTH ? temp : MAX_WIDTH;
                    height = MIN_HEIGHT;
                } else {
                    float ratio = imageHeight / imageWidth;
                    if (ratio > 3) {
                        ratio = 3;
                    }
                    width = imageWidth * ratio;
                    height = imageHeight;
                }
            }
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = (int) width;
            params.height = (int) height;
            imageView.setLayoutParams(params);
            Matrix matrix = new Matrix();
            float scaleWidth = width / imageWidth;
            float scaleHeight = height / imageHeight;
            matrix.postScale(scaleWidth, scaleHeight);
            imageView.setImageBitmap(Bitmap.createBitmap(resource, 0, 0, imageWidth, imageHeight, matrix, true));
            if(firstVisibleItemPosition == 0){
                mLinearLayoutManager.scrollToPositionWithOffset(0,0);
            }
        }
    }
}
