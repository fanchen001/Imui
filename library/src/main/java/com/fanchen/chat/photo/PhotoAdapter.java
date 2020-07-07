package com.fanchen.chat.photo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.fanchen.chat.ChatInputView;
import com.fanchen.chat.listener.OnFileSelectedListener;
import com.fanchen.chat.model.FileItem;
import com.fanchen.ui.R;
import com.fanchen.chat.model.VideoItem;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private SelectView mView;

    private List<FileItem> mMedias = new ArrayList<>();
    private List<FileItem> mSendFiles = new ArrayList<>();
    private List<Integer> mSelectedItems = new ArrayList<>();

    private OnFileSelectedListener mListener;
    private LayoutInflater mLayoutInflater;

    public PhotoAdapter(SelectView mView,List<FileItem> list) {
        this.mView = mView;
        if (list != null) mMedias = list;
        mLayoutInflater = LayoutInflater.from(mView.getContext());
    }

    public List<FileItem> getSelectedFiles() {
        return mSendFiles;
    }

    public void setOnPhotoSelectedListener(OnFileSelectedListener listener) {
        mListener = listener;
    }

    public void resetCheckedState() {
        mSendFiles.clear();
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == FileItem.Type.Image.getCode()){
            View layout = mLayoutInflater.inflate(R.layout.item_msg_photo_select, parent, false);
            return new PhotoViewHolder(layout);
        }else{
            View layout = mLayoutInflater.inflate(R.layout.item_msg_video_select, parent, false);
            return new PhotoViewHolder(layout);
        }
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        if (holder.container.getHeight() != ChatInputView.sMenuHeight) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ChatInputView.sMenuHeight, ChatInputView.sMenuHeight);
            holder.container.setLayoutParams(layoutParams);
        }

        FileItem item = mMedias.get(position);

        Glide.with(mView.getContext())
                .load(Uri.fromFile(new File(item.getFilePath()))).asBitmap()
                .override(ChatInputView.sMenuHeight / 2, ChatInputView.sMenuHeight)
               .into(new OverrideTarget(holder.ivPhoto,true,position));

        if (mSelectedItems.contains(position)) {    // Current photo is selected
            holder.ivTick.setVisibility(VISIBLE);
            addSelectedAnimation(holder.container);
        } else if (holder.ivTick.getVisibility() == View.VISIBLE) { // Selected before, now have not been selected
            holder.ivTick.setVisibility(View.GONE);
            addDeselectedAnimation(holder.container);
        }
        final FileItem.Type fileItem = item.getType();

        if (fileItem == FileItem.Type.Video && holder.tvDuration != null) {
            holder.tvDuration.setVisibility(View.VISIBLE);
            long duration = ((VideoItem) item).getDuration();
            holder.tvDuration.setText(formatSeconds(duration));
        }else if( holder.tvDuration != null){
            holder.tvDuration.setVisibility(View.GONE);
        }

        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = holder.getAdapterPosition();

                if (holder.ivTick.getVisibility() == GONE && !mSelectedItems.contains(p)) {
                    holder.setIsRecyclable(false);

                    mSelectedItems.add(p);
                    mSendFiles.add(mMedias.get(p));

                    holder.ivTick.setVisibility(VISIBLE);
                    addSelectedAnimation(holder.container);

                    if (mListener != null) {
                        mListener.onFileSelected(mView);
                    }
                } else {
                    holder.setIsRecyclable(true);

                    mSelectedItems.remove(Integer.valueOf(p));
                    mSendFiles.remove(mMedias.get(p));

                    holder.ivTick.setVisibility(GONE);
                    addDeselectedAnimation(holder.container);

                    if (mListener != null) {
                        mListener.onFileDeselected(mView);
                    }
                }
            }
        });
    }

    public String formatSeconds(long seconds){
        String standardTime;
        if (seconds <= 0){
            standardTime = "00:00";
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
        } else if (seconds < 3600) {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        } else {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
        }
        return standardTime;
    }

    @Override
    public int getItemCount() {
        return mMedias.size();
    }

    @Override
    public int getItemViewType(int position) {
        FileItem item = mMedias.get(position);
        if(item != null){
            FileItem.Type type = item.getType();
            if(type != null){
                return type.getCode();
            }
        }
        return FileItem.Type.Image.getCode();
    }

    private void addDeselectedAnimation(View... views) {
        List<Animator> valueAnimators = new ArrayList<>();
        for (View v : views) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f);

            valueAnimators.add(scaleX);
            valueAnimators.add(scaleY);
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(valueAnimators);
        set.setDuration(150);
        set.start();
    }

    private void addSelectedAnimation(View... views) {
        List<Animator> valueAnimators = new ArrayList<>();
        for (View v : views) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 0.96f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 0.96f);

            valueAnimators.add(scaleX);
            valueAnimators.add(scaleY);
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(valueAnimators);
        set.setDuration(150);
        set.start();
    }

    static final class PhotoViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView tvDuration;
        ImageView ivPhoto;
        ImageView ivTick;

        PhotoViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            tvDuration =  itemView.findViewById(R.id.text_photoselect_duration);
            ivPhoto = itemView.findViewById(R.id.image_photoselect_photo);
            ivTick =  itemView.findViewById(R.id.image_photoselect_tick);
        }
    }
}
