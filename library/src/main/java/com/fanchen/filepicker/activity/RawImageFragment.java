package com.fanchen.filepicker.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fanchen.R;
import com.fanchen.filepicker.model.EssFile;
import com.fanchen.filepicker.util.PathUtils;
import com.fanchen.picture.view.photoview.PhotoView;
import com.fanchen.picture.view.photoview.PhotoViewAttacher;

import java.io.File;

public class RawImageFragment extends Fragment implements  Runnable{
    private static final String BUNDLE_IMAGE = "ImageFragment.image";
    private static final int MAX_SCALE = 15;
    private static final long MAX_IMAGE1 = 1024 * 1024L;
    private static final long MAX_IMAGE2 = 4 * MAX_IMAGE1;

    private PhotoView mImageView;
    private ProgressBar mProgress;
    private EssFile mMedia;
    private PhotoViewAttacher mAttacher;

    private RequestManager mGlide;

    private boolean isCreateView = false;

    public static RawImageFragment newInstance(@NonNull EssFile image) {
        RawImageFragment fragment = new RawImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGlide = Glide.with(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mMedia == null && getArguments() != null) {
            mMedia = getArguments().getParcelable(BUNDLE_IMAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_raw_image, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = (ProgressBar) view.findViewById(R.id.loading);
        mImageView = (PhotoView) view.findViewById(R.id.photo_view);
        mAttacher = new PhotoViewAttacher(mImageView);
        isCreateView = true;
        view.post(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isCreateView && isVisibleToUser && getView() != null){
            getView().post(this);
        }
    }

    /**
     * resize the image or not according to size.
     *
     * @param size the size of image
     */
    private Point getResizePointer(long size) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Point point = new Point(metrics.widthPixels, metrics.heightPixels);
        if (size >= MAX_IMAGE2) {
            point.x >>= 2;
            point.y >>= 2;
        } else if (size >= MAX_IMAGE1) {
            point.x >>= 1;
            point.y >>= 1;
        } else if (size > 0) {
            // avoid some images do not have a size.
            point.x = 0;
            point.y = 0;
        }
        return point;
    }

    private void dismissProgressDialog() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
        PictureViewActivity activity = getThisActivity();
        if (activity != null && activity.mProgressBar != null) {
            activity.mProgressBar.setVisibility(View.GONE);
        }
    }

    private PictureViewActivity getThisActivity() {
        Activity activity = getActivity();
        if (activity instanceof PictureViewActivity) {
            return (PictureViewActivity) activity;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAttacher != null) {
            mAttacher = null;
            mImageView = null;
        }
    }

    @Override
    public void run() {
        if (getUserVisibleHint()) {
            File file = null;
            if(!TextUtils.isEmpty(mMedia.getAbsolutePath())){
                file = mMedia.getFile();
            }else{
                file = new File(PathUtils.getPath(getContext(), mMedia.getUri()));
            }
            Point resizePointer = getResizePointer(file.length());
            BitmapTypeRequest<File> fileBitmapTypeRequest = mGlide.load(file).asBitmap();
            if(fileBitmapTypeRequest == null)return;
            if(resizePointer.x > 0 && resizePointer.y > 0){
                fileBitmapTypeRequest.override(resizePointer.x, resizePointer.y);
            }
            fileBitmapTypeRequest.into(simpleTarget);
        }
    }

    private SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            dismissProgressDialog();
            mImageView.setImageResource(R.drawable.ic_pic_broken_image);
            if (mAttacher != null) {
                mAttacher.update();
            }
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            dismissProgressDialog();
            mImageView.setImageBitmap(resource);
            Drawable drawable = mImageView.getDrawable();
            PhotoViewAttacher attacher = mAttacher;
            if (attacher != null && resource != null) {
                if (drawable.getIntrinsicHeight() > (drawable.getIntrinsicWidth() << 2)) {
                    int scale = drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
                    scale = Math.min(MAX_SCALE, scale);
                    attacher.setMaximumScale(scale);
                    attacher.setScale(scale, true);
                }
                attacher.update();
            }
            PictureViewActivity activity = getThisActivity();
            if (activity != null && activity.mGallery != null) {
                activity.mGallery.setVisibility(View.VISIBLE);
            }
        }

    };
}
