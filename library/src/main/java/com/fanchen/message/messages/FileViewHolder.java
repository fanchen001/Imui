package com.fanchen.message.messages;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fanchen.message.utils.DateUtil;
import com.fanchen.ui.BuildConfig;
import com.fanchen.ui.R;
import com.fanchen.message.commons.models.IMessage;
import com.fanchen.message.view.RoundImageView;
import com.fanchen.message.view.RoundTextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class FileViewHolder<Message extends IMessage> extends BaseMessageViewHolder<Message>
        implements MsgListAdapter.DefaultMessageViewHolder, View.OnClickListener, View.OnLongClickListener {

    private RoundTextView mDateTv;
    private TextView mDisplayNameTv;
    private RoundImageView mAvatarIv;
    private ImageButton mResendIb;
    private ProgressBar mSendingPb;

    private TextView mNameTv;
    private TextView mSizeTv;
    private ImageView mTypeIv;
    private TextView mReadTv;

    private View mL;

    private boolean isSender;

    public FileViewHolder(View itemView, boolean isSender) {
        super(itemView);
        this.isSender = isSender;
        mL = itemView.findViewById(R.id.aurora_ll_msgitem_file);
        mNameTv = itemView.findViewById(R.id.aurora_tv_msgitem_file);
        mSizeTv = itemView.findViewById(R.id.aurora_tv_msgitem_size);
        mTypeIv = itemView.findViewById(R.id.aurora_iv_msgitem_file);

        mDateTv = itemView.findViewById(R.id.aurora_tv_msgitem_date);
        mAvatarIv = itemView.findViewById(R.id.aurora_iv_msgitem_avatar);
        if (isSender) {
            mReadTv = itemView.findViewById(R.id.aurora_ib_msgitem_read_status);
            mDisplayNameTv = (TextView) itemView.findViewById(R.id.aurora_tv_msgitem_sender_display_name);
        } else {
            mDisplayNameTv = (TextView) itemView.findViewById(R.id.aurora_tv_msgitem_receiver_display_name);
        }
        mResendIb = itemView.findViewById(R.id.aurora_ib_msgitem_resend);
        mSendingPb = itemView.findViewById(R.id.aurora_pb_msgitem_sending);
    }

    @Override
    public void onBind(final Message message) {
        HashMap<String, String> extras = message.getExtras();
        if (extras != null && !extras.isEmpty()) {
            String fileName = extras.get("fileName");
            String path = extras.get("path");
            mNameTv.setText(fileName);
            mSizeTv.setText(extras.get("fileSize"));
            if (path != null) {
                Glide.with(mTypeIv.getContext()).load(path).asBitmap().placeholder(R.mipmap.attachment).into(mTypeIv);
            } else if (fileName != null && fileName.contains(".")) {
                String s = fileName.toUpperCase();
                Context context = mTypeIv.getContext();
                String packageName = context.getPackageName();
                Resources resources = context.getResources();
                String name = "more";
                if (s.contains("XLS") || s.contains("XLSX")) {
                    name = "xls";
                } else if (s.contains("PPT") || s.contains("PPTX")) {
                    name = "ppt";
                } else if (s.contains("MP4") || s.contains("RMVB") || s.contains("WMA")) {
                    name = "movie";
                } else if (s.contains("ZIP") || s.contains("7Z") || s.contains("RAR")) {
                    name = "zip";
                } else if (s.contains("DOC") || s.contains("DOCX")) {
                    name = "doc";
                } else {
                    String[] split = fileName.split("\\.");
                    name = split[split.length - 1];
                }
                try {
                    Integer resId = resources.getIdentifier(name, "mipmap", packageName);
                    mTypeIv.setImageResource(resId);
                } catch (Exception e) {
                    mTypeIv.setImageResource(R.mipmap.more);
                }
            }
        }
        if (message.getTime() > 0 && message.showTime()) {
            mDateTv.setVisibility(View.VISIBLE);
            mDateTv.setText(DateUtil.getTimeStringAutoShort2(new Date(message.getTime()),true));
        } else {
            mDateTv.setVisibility(View.GONE);
        }
        if(mReadTv != null && message.getMessageStatus() == IMessage.MessageStatus.SEND_SUCCEED){
            mReadTv.setText(message.haveRead() ? mContext.getString(R.string.im_read):mContext.getString(R.string.im_un_read));
        }else if(mReadTv != null){
            mReadTv.setText("");
        }
        boolean isAvatarExists = message.getFromUser().getAvatarFilePath() != null
                && !message.getFromUser().getAvatarFilePath().isEmpty();
        if (isAvatarExists && mImageLoader != null) {
            mImageLoader.loadAvatarImage(mAvatarIv, message.getFromUser().getAvatarFilePath());
        } else if (mImageLoader == null) {
            mAvatarIv.setVisibility(View.GONE);
        }
        if (mDisplayNameTv.getVisibility() == View.VISIBLE) {
            mDisplayNameTv.setText(message.getFromUser().getDisplayName());
        }
        if (isSender) {
            switch (message.getMessageStatus()) {
                case SEND_GOING:
                    mSendingPb.setVisibility(View.VISIBLE);
                    mResendIb.setVisibility(View.GONE);
                    Log.i("LocationViewHolder", "sending message");
                    break;
                case SEND_FAILED:
                    mSendingPb.setVisibility(View.GONE);
                    Log.i("LocationViewHolder", "send message failed");
                    mResendIb.setVisibility(View.VISIBLE);
                    mResendIb.setTag(message);
                    mResendIb.setOnClickListener(this);
                    break;
                case SEND_SUCCEED:
                    mSendingPb.setVisibility(View.GONE);
                    mResendIb.setVisibility(View.GONE);
                    Log.i("TxtViewHolder", "send message succeed");
                    break;
            }
        }

        mL.setTag(message);
        mL.setOnClickListener(this);

        mL.setOnLongClickListener(this);
        mAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAvatarClickListener != null) {
                    mAvatarClickListener.onAvatarClick(message);
                }
            }
        });
    }

    @Override
    public void applyStyle(MessageListStyle style) {
        if(mReadTv != null){
            mReadTv.setVisibility(style.isShowReadStatus() ? View.VISIBLE : View.GONE);
        }
        if (isSender) {
            if (style.getSendingProgressDrawable() != null) {
                mSendingPb.setProgressDrawable(style.getSendingProgressDrawable());
            }
            if (style.getSendingIndeterminateDrawable() != null) {
                mSendingPb.setIndeterminateDrawable(style.getSendingIndeterminateDrawable());
            }
            if (style.getShowSenderDisplayName()) {
                mDisplayNameTv.setVisibility(View.VISIBLE);
            } else {
                mDisplayNameTv.setVisibility(View.GONE);
            }
        } else {
            if (style.getShowReceiverDisplayName()) {
                mDisplayNameTv.setVisibility(View.VISIBLE);
            } else {
                mDisplayNameTv.setVisibility(View.GONE);
            }
        }
        mDisplayNameTv.setTextSize(style.getDisplayNameTextSize());
        mDisplayNameTv.setTextColor(style.getDisplayNameTextColor());
        mDisplayNameTv.setPadding(style.getDisplayNamePaddingLeft(), style.getDisplayNamePaddingTop(),
                style.getDisplayNamePaddingRight(), style.getDisplayNamePaddingBottom());
        mDisplayNameTv.setEms(style.getDisplayNameEmsNumber());
        mDateTv.setTextSize(style.getDateTextSize());
        mDateTv.setTextColor(style.getDateTextColor());
        mDateTv.setPadding(style.getDatePaddingLeft(), style.getDatePaddingTop(), style.getDatePaddingRight(),
                style.getDatePaddingBottom());
        mDateTv.setBgCornerRadius(style.getDateBgCornerRadius());
        mDateTv.setBgColor(style.getDateBgColor());

        android.view.ViewGroup.LayoutParams layoutParams = mAvatarIv.getLayoutParams();
        layoutParams.width = style.getAvatarWidth();
        layoutParams.height = style.getAvatarHeight();
        mAvatarIv.setLayoutParams(layoutParams);
        mAvatarIv.setBorderRadius(style.getAvatarRadius());
    }

    @Override
    public void onClick(View v) {
        Message message = (Message) v.getTag();
        if (mL == v) {
            if (mMsgClickListener != null) {
                mMsgClickListener.onMessageClick(message);
            }
        } else if (mResendIb == v) {
            if (mMsgStatusViewClickListener != null) {
                mMsgStatusViewClickListener.onStatusViewClick(message);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Message message = (Message) v.getTag();
        if (mMsgLongClickListener != null) {
            mMsgLongClickListener.onMessageLongClick(v, message);
        } else {
            if (BuildConfig.DEBUG) {
                Log.w("MsgListAdapter", "Didn't set long click listener! Drop event.");
            }
        }
        return true;
    }
}
