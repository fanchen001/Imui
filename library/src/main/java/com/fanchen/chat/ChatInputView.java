package com.fanchen.chat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.Space;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fanchen.chat.anim.RestoreAnimator;
import com.fanchen.chat.anim.ShrinkAnimator;
import com.fanchen.chat.listener.OnRecordVoiceUIListener;
import com.fanchen.ui.R;
import com.fanchen.chat.camera.CameraNew;
import com.fanchen.chat.camera.CameraOld;
import com.fanchen.chat.camera.CameraSupport;
import com.fanchen.chat.emoji.Constants;
import com.fanchen.chat.emoji.EmojiBean;
import com.fanchen.chat.emoji.EmojiView;
import com.fanchen.chat.emoji.listener.EmoticonClickListener;
import com.fanchen.chat.emoji.data.EmoticonEntity;
import com.fanchen.chat.emoji.widget.EmoticonsEditText;
import com.fanchen.chat.emoji.EmoticonsKeyboardUtils;
import com.fanchen.chat.listener.CameraControllerListener;
import com.fanchen.chat.listener.CameraEventListener;
import com.fanchen.chat.listener.OnCameraCallbackListener;
import com.fanchen.chat.listener.CustomMenuEventListener;
import com.fanchen.chat.listener.OnFileSelectedListener;
import com.fanchen.chat.listener.OnMenuClickListener;
import com.fanchen.chat.listener.OnSelectButtonListener;
import com.fanchen.chat.listener.OnRecordVoiceListener;
import com.fanchen.chat.menu.MenuManager;
import com.fanchen.chat.model.FileItem;
import com.fanchen.chat.model.VideoItem;
import com.fanchen.chat.photo.SelectView;
import com.fanchen.chat.record.ProgressButton;
import com.fanchen.chat.record.RecordControllerView;
import com.fanchen.chat.record.RecordVoiceButton;
import com.fanchen.chat.utils.SimpleCommonUtils;
import com.fanchen.video.JZUtils;

public class ChatInputView extends LinearLayout implements View.OnClickListener, TextWatcher,
        RecordControllerView.OnRecordActionListener, OnFileSelectedListener, CameraEventListener,
        ViewTreeObserver.OnPreDrawListener, EmoticonsEditText.OnBackKeyClickListener, EmoticonClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, TextureView.SurfaceTextureListener,
        MediaPlayer.OnErrorListener, Runnable {

    private static final String TAG = "ChatInputView";
    public CharSequence mInput;

    private EmoticonsEditText mChatInput;
//    private TextView mSendCountTv;

    private Space mInputMarginLeft;
    private Space mInputMarginRight;

    private LinearLayout mChatInputContainer;
    private LinearLayout mMenuItemContainer;

    private FrameLayout mMenuContainer;

    private ViewStub mViewStub;

    private RelativeLayout mRecordVoiceRl;
    private LinearLayout mPreviewBtnLl;
    private ProgressButton mPreviewPlayBtn;
    private RecordControllerView mRecordControllerView;
    private Chronometer mChronometer;
    private TextView mRecordHintTv;
    private RecordVoiceButton mRecordVoiceBtn;

    private SelectView mSelectPhotoView;
    private SelectView mSelectVideoView;

    private ImageButton mSelectAlbumIb;
    private ImageButton mSelectVideo;

    private FrameLayout mCameraFl;
    private TextureView mTextureView;
    private ImageButton mCaptureBtn;
    private ImageButton mCloseBtn;
    private ImageButton mSwitchCameraBtn;
    private ImageButton mFullScreenBtn;
    private ImageButton mRecordVideoBtn;
    private EmojiView mEmojiRl;

    private OnMenuClickListener mListener;
    private OnCameraCallbackListener mCameraListener;
    private CameraControllerListener mCameraControllerListener;
    private OnSelectButtonListener mOnSelectButtonListener;
    private OnRecordVoiceListener mRecordVoiceListener;

    private ChatInputStyle mStyle;

    private boolean mShowBottomMenu = true;

    private InputMethodManager mImm;
    private Window mWindow;

    private int mWidth;
    private int mHeight;
    private int mSoftKeyboardHeight;
    public static int sMenuHeight = 831;

    private boolean mPendingShowMenu;

    private long mRecordTime;
    private boolean mPlaying = false;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private boolean mIsRecordVideoMode = false;

    private boolean mIsRecordingVideo = false;

    private boolean mFinishRecordingVideo = false;

    private String mVideoFilePath;

    private boolean mSetData;
    private FileInputStream mFIS;
    private FileDescriptor mFD;
    private boolean mIsEarPhoneOn;
    private CameraSupport mCameraSupport;
    private int mCameraId = -1;
    private boolean mIsBackCamera = true;
    private boolean mIsFullScreen = false;
    private Rect mRect = new Rect();

    private int mGalleryMode = OnMenuClickListener.ALL;

    private boolean useKeyboardHeight = true;

    private MenuManager mMenuManager;
    private Activity mActivity;

    private int mChangePaddingTop = 0;
    private int mChangePaddingBottom = 0;
    private int mChangePaddingLift = 0;
    private int mChangePaddingRight = 0;
    private OnWindowChangeListener mOnWindowChangeListener;

    public ChatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChatInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setOnSelectButtonListener(OnSelectButtonListener mOnSelectButtonListener) {
        this.mOnSelectButtonListener = mOnSelectButtonListener;
    }

    public void setGalleryMode(int galleryMode) {
        this.mGalleryMode = galleryMode;
        if (mSelectPhotoView == null || mSelectVideoView == null) return;
        if (galleryMode == OnMenuClickListener.ALL) {
            if (mSelectPhotoView.getVisibility() == VISIBLE) {
                mSelectPhotoView.initData(galleryMode);
            } else if (mSelectVideoView.getVisibility() == View.VISIBLE) {
                mSelectVideoView.initData(galleryMode);
            }
        } else if (galleryMode == OnMenuClickListener.SEPARATE) {
            mSelectPhotoView.initData(OnMenuClickListener.SEPARATE_PHOTO);
            mSelectVideoView.initData(OnMenuClickListener.SEPARATE_VIDEO);
        }
    }

    private void initField(Context context) {
        mActivity = JZUtils.scanForActivity(context);
        mImm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mWindow = mActivity.getWindow();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
    }

    private void findView() {
        mChatInputContainer = findViewById(R.id.aurora_ll_input_container);
        mChatInput = findViewById(R.id.aurora_et_chat_input);
        mChatInput.addTextChangedListener(this);
        mChatInput.setOnBackKeyClickListener(this);

        mInputMarginLeft = findViewById(R.id.aurora_input_margin_left);
        mInputMarginRight = findViewById(R.id.aurora_input_margin_right);

        mMenuItemContainer = findViewById(R.id.aurora_ll_menuitem_container);

        mViewStub = findViewById(R.id.aurora_fl_menu_container);

        mMenuManager = new MenuManager(this);
        mMenuManager.setMenuItemListener(this);

        // menu buttons
//        mSendCountTv = findViewById(R.id.aurora_menuitem_tv_send_count);
//
//        mRecordVoiceRl = findViewById(R.id.aurora_rl_recordvoice_container);
//        mPreviewBtnLl = findViewById(R.id.aurora_ll_recordvoice_btn_container);
//        mPreviewPlayBtn = findViewById(R.id.aurora_pb_recordvoice_play_audio);
//
//        mRecordControllerView = findViewById(R.id.aurora_rcv_recordvoice_controller);
//        mChronometer = findViewById(R.id.aurora_chronometer_recordvoice);
//        mRecordHintTv = findViewById(R.id.aurora_tv_recordvoice_hint);
//
//        findViewById(R.id.aurora_btn_recordvoice_send).setOnClickListener(this);
//        findViewById(R.id.aurora_btn_recordvoice_cancel).setOnClickListener(this);
//
//        mRecordVoiceBtn = findViewById(R.id.aurora_rvb_recordvoice_record);
//        mCameraFl = findViewById(R.id.aurora_fl_camera_container);
//        mTextureView = findViewById(R.id.aurora_txtv_camera_texture);
//        mCloseBtn = findViewById(R.id.aurora_ib_camera_close);
//        mFullScreenBtn = findViewById(R.id.aurora_ib_camera_full_screen);
//        mRecordVideoBtn = findViewById(R.id.aurora_ib_camera_record_video);
//        mCaptureBtn = findViewById(R.id.aurora_ib_camera_capture);
//        mSwitchCameraBtn = findViewById(R.id.aurora_ib_camera_switch);
//
//        mSelectPhotoView = findViewById(R.id.aurora_view_selectphoto);
//        mSelectAlbumIb = mSelectPhotoView.findViewById(R.id.aurora_imagebtn_select_album);
//        mSelectPhotoView.setOnFileSelectedListener(this);
//
//        mSelectAlbumIb.setOnClickListener(this);
//
//        mSelectVideoView = findViewById(R.id.aurora_view_select_video);
//        mSelectVideo = mSelectVideoView.findViewById(R.id.aurora_imagebtn_select_album);
//        mSelectVideoView.setOnFileSelectedListener(this);
//        mSelectVideo.setImageResource(R.drawable.aurora_button_video);
//        mSelectVideo.setOnClickListener(this);
//
//        mEmojiRl = findViewById(R.id.aurora_rl_emoji_container);
//
//        mMenuContainer.setVisibility(GONE);
//
//        mRecordVoiceBtn.setRecordController(mRecordControllerView);
//        mPreviewPlayBtn.setOnClickListener(this);
//
//        mCloseBtn.setOnClickListener(this);
//        mFullScreenBtn.setOnClickListener(this);
//        mRecordVideoBtn.setOnClickListener(this);
//        mCaptureBtn.setOnClickListener(this);
//        mSwitchCameraBtn.setOnClickListener(this);
//
//        mRecordControllerView.setWidth(mWidth);
//        mRecordControllerView.setOnControllerListener(this);
//        getViewTreeObserver().addOnPreDrawListener(this);
//
//        if (mMenuManager.isSeparate()) {
//            setGalleryMode(OnMenuClickListener.SEPARATE);
//        } else if (mMenuManager.isAll()) {
//            setGalleryMode(OnMenuClickListener.ALL);
//        }
    }

    @SuppressLint("NewApi")
    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        Resources resources = context.getResources();
        setBackgroundColor(resources.getColor(R.color.aurora_bg_input_container));
        inflate(context, R.layout.view_chat_input, this);

        initField(context);
        findView();

        mStyle = ChatInputStyle.parse(context, attrs);
        mChatInput.setMaxLines(mStyle.getInputMaxLines());
        mChatInput.setHint(mStyle.getInputHint());
        mChatInput.setText(mStyle.getInputText());
        mChatInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, mStyle.getInputTextSize());
        mChatInput.setTextColor(mStyle.getInputTextColor());
        mChatInput.setHintTextColor(mStyle.getInputHintColor());
        mChatInput.setBackgroundResource(mStyle.getInputEditTextBg());
        mChatInput.setPadding(mStyle.getInputPaddingLeft(), mStyle.getInputPaddingTop(), mStyle.getInputPaddingRight(), mStyle.getInputPaddingBottom());

        mInputMarginLeft.getLayoutParams().width = mStyle.getInputMarginLeft();
        mInputMarginRight.getLayoutParams().width = mStyle.getInputMarginRight();

        LayoutParams lp = (LayoutParams) mChatInputContainer.getLayoutParams();
        lp.setMargins(0, mStyle.getInputMarginTop(), 0, mStyle.getInputMarginBottom());
        mChatInputContainer.setLayoutParams(lp);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        mMediaPlayer.setOnErrorListener(this);

        SimpleCommonUtils.initEmoticonsEditText(mChatInput);
    }

    public void inflateViewStub() {
        if (mViewStub == null) return;
        mMenuContainer = (FrameLayout) mViewStub.inflate();
        mMenuManager.updateMenuContainer(mMenuContainer);
        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, sMenuHeight);
        mMenuContainer.setLayoutParams(params);

        mRecordVoiceRl = mMenuContainer.findViewById(R.id.aurora_rl_recordvoice_container);
        mPreviewBtnLl = mMenuContainer.findViewById(R.id.aurora_ll_recordvoice_btn_container);
        mPreviewPlayBtn = mMenuContainer.findViewById(R.id.aurora_pb_recordvoice_play_audio);
        mRecordControllerView = mMenuContainer.findViewById(R.id.aurora_rcv_recordvoice_controller);
        mChronometer = mMenuContainer.findViewById(R.id.aurora_chronometer_recordvoice);
        mRecordHintTv = mMenuContainer.findViewById(R.id.aurora_tv_recordvoice_hint);
        mRecordVoiceBtn = mMenuContainer.findViewById(R.id.aurora_rvb_recordvoice_record);
        mCameraFl = mMenuContainer.findViewById(R.id.aurora_fl_camera_container);
        mTextureView = mMenuContainer.findViewById(R.id.aurora_txtv_camera_texture);
        mCloseBtn = mMenuContainer.findViewById(R.id.aurora_ib_camera_close);
        mFullScreenBtn = mMenuContainer.findViewById(R.id.aurora_ib_camera_full_screen);
        mRecordVideoBtn = mMenuContainer.findViewById(R.id.aurora_ib_camera_record_video);
        mCaptureBtn = mMenuContainer.findViewById(R.id.aurora_ib_camera_capture);
        mSwitchCameraBtn = mMenuContainer.findViewById(R.id.aurora_ib_camera_switch);
        mEmojiRl = mMenuContainer.findViewById(R.id.aurora_rl_emoji_container);

        mSelectPhotoView = mMenuContainer.findViewById(R.id.aurora_view_selectphoto);
        mSelectAlbumIb = mSelectPhotoView.findViewById(R.id.aurora_imagebtn_select_album);

        mSelectVideoView = mMenuContainer.findViewById(R.id.aurora_view_select_video);
        mSelectVideo = mSelectVideoView.findViewById(R.id.aurora_imagebtn_select_album);

        mMenuContainer.findViewById(R.id.aurora_btn_recordvoice_send).setOnClickListener(this);
        mMenuContainer.findViewById(R.id.aurora_btn_recordvoice_cancel).setOnClickListener(this);

        mSelectPhotoView.setOnFileSelectedListener(this);
        mSelectAlbumIb.setOnClickListener(this);

        mSelectVideoView.setOnFileSelectedListener(this);
        mSelectVideo.setImageResource(R.drawable.aurora_button_video);
        mSelectVideo.setOnClickListener(this);

        mRecordVoiceBtn.setRecordController(mRecordControllerView);
        mPreviewPlayBtn.setOnClickListener(this);

        mCloseBtn.setOnClickListener(this);
        mFullScreenBtn.setOnClickListener(this);
        mRecordVideoBtn.setOnClickListener(this);
        mCaptureBtn.setOnClickListener(this);
        mSwitchCameraBtn.setOnClickListener(this);
        mRecordControllerView.setWidth(mWidth);
        mRecordControllerView.setOnControllerListener(this);
        getViewTreeObserver().addOnPreDrawListener(this);
        if (mRecordVoiceListener != null) {
            mRecordVoiceBtn.setRecordVoiceListener(mRecordVoiceListener);
        }

        if (mMenuManager.isSeparate()) {
            setGalleryMode(OnMenuClickListener.SEPARATE);
        } else if (mMenuManager.isAll()) {
            setGalleryMode(OnMenuClickListener.ALL);
        }

        mMenuManager.setButtonStyle(mStyle);
        mSelectAlbumIb.setVisibility(mStyle.getShowSelectAlbum() ? VISIBLE : INVISIBLE);
        mSelectVideo.setVisibility(mStyle.getShowSelectAlbum() ? VISIBLE : INVISIBLE);
        mEmojiRl.setAdapter(SimpleCommonUtils.getCommonAdapter(mActivity, this));

        mMenuContainer.setVisibility(View.GONE);
    }

    private void setCursor(Drawable drawable) {
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(mChatInput, drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMenuClickListener(OnMenuClickListener listener) {
        mListener = listener;
    }

    public void setCustomMenuClickListener(CustomMenuEventListener listener) {
        mMenuManager.setCustomMenuClickListener(listener);
    }

    public void setRecordVoiceListener(OnRecordVoiceListener listener) {
        if (this.mRecordVoiceBtn != null) {
            this.mRecordVoiceBtn.setRecordVoiceListener(listener);
        }
        this.mRecordVoiceListener = listener;
    }

    public void setOnCameraCallbackListener(OnCameraCallbackListener listener) {
        mCameraListener = listener;
    }

    public void setCameraControllerListener(CameraControllerListener listener) {
        mCameraControllerListener = listener;
    }

    private void triggerSelectView(SelectView mView, CharSequence s, int start, int before) {
        ImageButton sendButton = mMenuManager.getSendButton();
        if (mView != null && (mView.getSelectFiles() == null || mView.getSelectFiles().isEmpty())) {
            if (s.length() >= 1 && start == 0 && before == 0) { // Starting input
                triggerSendButtonAnimation(mView, sendButton, true, false);
            } else if (s.length() == 0 && before >= 1) { // Clear content
                triggerSendButtonAnimation(mView, sendButton, false, false);
            }
        }
    }

    private void triggerSelectView(CharSequence s, int start, int before) {
        ImageButton sendButton = mMenuManager.getSendButton();
        if (s.length() >= 1 && start == 0 && before == 0) { // Starting input
            triggerSendButtonAnimation(null, sendButton, true, false);
        } else if (s.length() == 0 && before >= 1) { // Clear content
            triggerSendButtonAnimation(null, sendButton, false, false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mInput = s;
        if (mSelectPhotoView == null && mSelectVideoView == null) {
            triggerSelectView(s, start, before);
        } else if (mSelectPhotoView != null)
            triggerSelectView(mSelectPhotoView, s, start, before);
        else
            triggerSelectView(mSelectVideoView, s, start, before);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    public EditText getInputView() {
        return mChatInput;
    }

    public RecordVoiceButton getRecordVoiceButton() {
        return mRecordVoiceBtn;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        if (view == mSelectAlbumIb && mOnSelectButtonListener != null) {
            int mode = mGalleryMode == OnMenuClickListener.SEPARATE ? OnMenuClickListener.SEPARATE_PHOTO : OnMenuClickListener.ALL;
            mOnSelectButtonListener.onSelectButtonClick(mode);
        } else if (view == mSelectVideo && mOnSelectButtonListener != null) {
            mOnSelectButtonListener.onSelectButtonClick(OnMenuClickListener.SEPARATE_VIDEO);
        } else if (view.getId() == R.id.aurora_pb_recordvoice_play_audio) {
            if (!mPlaying && mSetData) {
                mPreviewPlayBtn.startPlay();
                mMediaPlayer.start();
                mPlaying = true;
                mChronometer.setBase(convertStrTimeToLong(mChronometer.getText().toString()));
                mChronometer.start();
            } else if (!mPlaying) {
                playVoice();
            } else {
                mSetData = true;
                mMediaPlayer.pause();
                mChronometer.stop();
                mPlaying = false;
                mPreviewPlayBtn.stopPlay();
            }
        } else if (view.getId() == R.id.aurora_btn_recordvoice_cancel) {
            mPreviewBtnLl.setVisibility(GONE);
            mPreviewPlayBtn.setVisibility(GONE);
            mRecordControllerView.setVisibility(VISIBLE);
            mRecordVoiceBtn.setVisibility(VISIBLE);
            mRecordVoiceBtn.cancelRecord();
            mChronometer.setText("00:00");
            if (mRecordVoiceListener instanceof OnRecordVoiceUIListener) {
                ((OnRecordVoiceUIListener) mRecordVoiceListener).onPreviewCancel();
            }
        } else if (view.getId() == R.id.aurora_btn_recordvoice_send) {
            mPreviewBtnLl.setVisibility(GONE);
            mPreviewPlayBtn.setVisibility(GONE);
            dismissMenuLayout();
            mRecordVoiceBtn.finishRecord();
            mChronometer.setText("00:00");
            if (mRecordVoiceListener instanceof OnRecordVoiceUIListener) {
                ((OnRecordVoiceUIListener) mRecordVoiceListener).onPreviewSend();
            }
        } else if (view.getId() == R.id.aurora_ib_camera_full_screen) {
            if (!mIsFullScreen) {
                if (mCameraControllerListener != null) {
                    mCameraControllerListener.onFullScreenClick();
                }
                fullScreen();
            } else {
                if (mCameraControllerListener != null) {
                    mCameraControllerListener.onRecoverScreenClick();
                }
                recoverScreen();
            }
        } else if (view.getId() == R.id.aurora_ib_camera_record_video) {
            if (mCameraControllerListener != null) {
                mCameraControllerListener.onSwitchCameraModeClick(!mIsRecordVideoMode);
            }
            if (!mIsRecordVideoMode) {
                mIsRecordVideoMode = true;
                mCaptureBtn.setBackgroundResource(R.drawable.aurora_preview_record_video_start);
                mRecordVideoBtn.setBackgroundResource(R.drawable.aurora_preview_camera);
                fullScreen();
                mCloseBtn.setVisibility(VISIBLE);
            } else {
                mIsRecordVideoMode = false;
                mRecordVideoBtn.setBackgroundResource(R.drawable.aurora_preview_record_video);
                mCaptureBtn.setBackgroundResource(R.drawable.aurora_menuitem_send_pres);
                mFullScreenBtn.setBackgroundResource(R.drawable.aurora_preview_recover_screen);
                mFullScreenBtn.setVisibility(VISIBLE);
                mCloseBtn.setVisibility(GONE);
            }
        } else if (view.getId() == R.id.aurora_ib_camera_capture) {
            if (mIsRecordVideoMode) {
                if (!mIsRecordingVideo) { // start recording
                    mCameraSupport.startRecordingVideo();
                    postDelayed(mPostRunnable, 200);
                    mIsRecordingVideo = true;
                } else { // finish recording
                    mVideoFilePath = mCameraSupport.finishRecordingVideo();
                    mIsRecordingVideo = false;
                    mIsRecordVideoMode = false;
                    mFinishRecordingVideo = true;
                    mCaptureBtn.setBackgroundResource(R.drawable.aurora_menuitem_send_pres);
                    mRecordVideoBtn.setVisibility(GONE);
                    mSwitchCameraBtn.setBackgroundResource(R.drawable.aurora_preview_delete_video);
                    mSwitchCameraBtn.setVisibility(VISIBLE);
                    if (mVideoFilePath != null) {
                        playVideo();
                    }
                }
            } else if (mFinishRecordingVideo) {
                if (mListener != null && mVideoFilePath != null) {
                    File file = new File(mVideoFilePath);
                    VideoItem video = new VideoItem(mVideoFilePath, file.getName(), file.length() + "", System.currentTimeMillis() + "", mMediaPlayer.getDuration() / 1000);
                    List<FileItem> list = new ArrayList<>();
                    list.add(video);
                    mListener.onSendFiles(list);
                    mVideoFilePath = null;
                }
                mFinishRecordingVideo = false;
                mMediaPlayer.stop();
                mMediaPlayer.release();
                recoverScreen();
                dismissMenuLayout();
            } else {
                mCameraSupport.takePicture();
            }
        } else if (view.getId() == R.id.aurora_ib_camera_close) {
            try {
                if (mCameraControllerListener != null) {
                    mCameraControllerListener.onCloseCameraClick();
                }
                mMediaPlayer.stop();
                mMediaPlayer.release();
                if (mCameraSupport != null) {
                    mCameraSupport.cancelRecordingVideo();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            recoverScreen();
            dismissMenuLayout();
            mIsRecordVideoMode = false;
            mIsRecordingVideo = false;
            if (mFinishRecordingVideo) {
                mFinishRecordingVideo = false;
            }
        } else if (view.getId() == R.id.aurora_ib_camera_switch) {
            if (mFinishRecordingVideo) {
                Log.e("CameraNew", " mFinishRecordingVideo ==== " + mFinishRecordingVideo);
                mCameraSupport.cancelRecordingVideo();
                mSwitchCameraBtn.setBackgroundResource(R.drawable.aurora_preview_switch_camera);
                mRecordVideoBtn.setBackgroundResource(R.drawable.aurora_preview_camera);
                showRecordVideoBtn();

                mVideoFilePath = null;
                mFinishRecordingVideo = false;
                mIsRecordVideoMode = true;
                mCaptureBtn.setBackgroundResource(R.drawable.aurora_preview_record_video_start);
                mMediaPlayer.stop();
                mMediaPlayer.release();

                mCameraSupport.open(mCameraId, mWidth, mHeight, mIsBackCamera, mStyle.getCameraQuality());
            } else {
                if (Camera.getNumberOfCameras() < 2) {
                    Toast.makeText(getContext(), "Camera size 1 ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mIsBackCamera) {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    mIsBackCamera = false;
                    mCameraSupport.release();
                    mCameraSupport.open(mCameraId, mTextureView.getWidth(), mTextureView.getHeight(), mIsBackCamera, mStyle.getCameraQuality());
                } else {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    mIsBackCamera = true;
                    mCameraSupport.release();
                    mCameraSupport.open(mCameraId, mTextureView.getWidth(), mTextureView.getHeight(), mIsBackCamera, mStyle.getCameraQuality());

                }
//
//                for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
//                    Camera.CameraInfo info = new Camera.CameraInfo();
//                    Camera.getCameraInfo(i, info);
//                    if (mIsBackCamera && info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                        Log.e("CameraNew"," CAMERA_FACING_FRONT ==== " + Camera.getNumberOfCameras());
//                        mCameraId = i;
//                        mIsBackCamera = false;
//                        mCameraSupport.release();
//                        mCameraSupport.open(mCameraId, mTextureView.getWidth(), mTextureView.getHeight(), mIsBackCamera, mStyle.getCameraQuality());
//                        break;
//                    } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                        Log.e("CameraNew"," CAMERA_FACING_BACK ==== " + Camera.getNumberOfCameras());
//                        mCameraId = i;
//                        mIsBackCamera = true;
//                        mCameraSupport.release();
//                        mCameraSupport.open(mCameraId, mTextureView.getWidth(), mTextureView.getHeight(), mIsBackCamera, mStyle.getCameraQuality());
//                        break;
//                    }
//                }
            }
        }
        // bottom menu
        else if (view.getId() == R.id.aurora_menuitem_ib_send) {
            if (onSubmit()) mChatInput.setText("");
            ImageButton sendButton = mMenuManager.getSendButton();
            TextView sendCountTextView = mMenuManager.getSendCountTextView();
            if (mSelectPhotoView != null && mSelectPhotoView.getSelectFiles() != null && mSelectPhotoView.getSelectFiles().size() > 0) {
                mListener.onSendFiles(mSelectPhotoView.getSelectFiles());
                sendButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.aurora_menuitem_send));
                sendCountTextView.setVisibility(View.INVISIBLE);
                mSelectPhotoView.resetCheckState();
                dismissMenuLayout();
                mImm.hideSoftInputFromWindow(getWindowToken(), 0);
                mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            } else if (mSelectVideoView != null && mSelectVideoView.getSelectFiles() != null && mSelectVideoView.getSelectFiles().size() > 0) {
                mListener.onSendFiles(mSelectVideoView.getSelectFiles());
                sendButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.aurora_menuitem_send));
                sendCountTextView.setVisibility(View.INVISIBLE);
                mSelectVideoView.resetCheckState();
                dismissMenuLayout();
                mImm.hideSoftInputFromWindow(getWindowToken(), 0);
                mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        } else {
            if (mMenuContainer == null) {
                inflateViewStub();
            }
            mMenuManager.hideCustomMenu();
            mChatInput.clearFocus();
            if (view.getId() == R.id.aurora_ll_menuitem_voice_container) {
                if (mListener != null && mListener.switchToMicrophoneMode()) {
                    if (mRecordVoiceRl.getVisibility() == VISIBLE && mMenuContainer.getVisibility() == VISIBLE) {
                        dismissMenuLayout();
                    } else if (isKeyboardVisible()) {
                        mPendingShowMenu = true;
                        EmoticonsKeyboardUtils.closeSoftKeyboard(mChatInput);
                        showRecordVoiceLayout();
                    } else {
                        showMenuLayout();
                        showRecordVoiceLayout();
                    }
                }
            } else if (view.getId() == R.id.aurora_ll_menuitem_photo_container) {
                int mode = mGalleryMode == OnMenuClickListener.SEPARATE ? OnMenuClickListener.SEPARATE_PHOTO : OnMenuClickListener.ALL;
                if (mListener != null && mListener.switchToGalleryMode(mode)) {
                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (mSelectPhotoView.getVisibility() == VISIBLE && mMenuContainer.getVisibility() == VISIBLE) {
                        dismissMenuLayout();
                    } else if (isKeyboardVisible()) {
                        mPendingShowMenu = true;
                        EmoticonsKeyboardUtils.closeSoftKeyboard(mChatInput);
                        showSelectPhotoLayout();
                    } else {
                        showMenuLayout();
                        showSelectPhotoLayout();
                    }
                }
            } else if (view.getId() == R.id.aurora_ll_menuitem_video_container) {
                if (mListener != null && mListener.switchToGalleryMode(OnMenuClickListener.SEPARATE_VIDEO)) {
                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (mSelectVideoView.getVisibility() == VISIBLE && mMenuContainer.getVisibility() == VISIBLE) {
                        dismissMenuLayout();
                    } else if (isKeyboardVisible()) {
                        mPendingShowMenu = true;
                        EmoticonsKeyboardUtils.closeSoftKeyboard(mChatInput);
                        showSelectVideoLayout();
                    } else {
                        showMenuLayout();
                        showSelectVideoLayout();
                    }
                }
            } else if (view.getId() == R.id.aurora_ll_menuitem_camera_container) {
                if (mListener != null && mListener.switchToCameraMode()) {
                    if (mCameraFl.getVisibility() == VISIBLE && mMenuContainer.getVisibility() == VISIBLE) {
                        dismissMenuLayout();
                    } else if (isKeyboardVisible()) {
                        mPendingShowMenu = true;
                        EmoticonsKeyboardUtils.closeSoftKeyboard(mChatInput);
                        showCameraLayout();
                    } else {
                        showMenuLayout();
                        showCameraLayout();
                    }
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        if (mCameraSupport == null && mCameraFl.getVisibility() == VISIBLE) {
                            initCamera();
                        }
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.sdcard_not_exist_toast), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (view.getId() == R.id.aurora_ll_menuitem_emoji_container) {
                if (mListener != null && mListener.switchToEmojiMode()) {
                    if (mEmojiRl.getVisibility() == VISIBLE && mMenuContainer.getVisibility() == VISIBLE) {
                        dismissMenuLayout();
                    } else if (isKeyboardVisible()) {
                        mPendingShowMenu = true;
                        EmoticonsKeyboardUtils.closeSoftKeyboard(mChatInput);
                        showEmojiLayout();
                    } else {
                        showMenuLayout();
                        showEmojiLayout();
                    }
                }
            }
        }
    }

    // play audio
    private void playVoice() {
        try {
            if (mRecordVoiceBtn == null) return;
            mMediaPlayer.reset();
            mFIS = new FileInputStream(mRecordVoiceBtn.getRecordFile());
            mFD = mFIS.getFD();
            mMediaPlayer.setDataSource(mFD);
            if (mIsEarPhoneOn) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            Toast.makeText(getContext(), getContext().getString(R.string.file_not_found_toast), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (mFIS != null) {
                    mFIS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playVideo() {
        try {
            if (mTextureView == null) return;
            mCameraSupport.release();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mVideoFilePath);
            Surface surface = new Surface(mTextureView.getSurfaceTexture());
            mMediaPlayer.setSurface(surface);
            surface.release();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseVoice() {
        try {
            mMediaPlayer.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAudioPlayByEarPhone(int state) {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if (state == 0) {
            mIsEarPhoneOn = false;
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.STREAM_VOICE_CALL);
        } else {
            mIsEarPhoneOn = true;
            audioManager.setSpeakerphoneOn(false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);
        }
    }

    public void initCamera() {
        if (mTextureView == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraSupport = new CameraNew(getContext(), mTextureView);
        } else {
            mCameraSupport = new CameraOld(getContext(), mTextureView);
        }
        ViewGroup.LayoutParams params = mTextureView.getLayoutParams();
        params.height = mSoftKeyboardHeight == 0 ? sMenuHeight : mSoftKeyboardHeight;
        mTextureView.setLayoutParams(params);
        mCameraSupport.setCameraCallbackListener(mCameraListener);
        mCameraSupport.setCameraEventListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            mSwitchCameraBtn.setVisibility(View.GONE);
        }
//        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
//            Camera.CameraInfo info = new Camera.CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mIsBackCamera = true;
//                break;
//            }
//        }
        if (mTextureView.isAvailable()) {
            mCameraSupport.open(mCameraId, mWidth, sMenuHeight, mIsBackCamera, mStyle.getCameraQuality());
        } else {
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    /**
     * Full screen mode
     */
    private void fullScreen() {
        if (mFullScreenBtn == null) return;

        Window window = mActivity.getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mFullScreenBtn.setBackgroundResource(R.drawable.aurora_preview_recover_screen);
        mFullScreenBtn.setVisibility(VISIBLE);
        mChatInputContainer.setVisibility(GONE);
        Log.e("setVisibility", "setVisibility " + mMenuItemContainer.getVisibility());
        mMenuItemContainer.setVisibility(GONE);
        int height = mHeight;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = mWindow.getWindowManager().getDefaultDisplay();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            display.getRealMetrics(dm);
            height = dm.heightPixels;
        }
        MarginLayoutParams marginParams1 = new MarginLayoutParams(mCaptureBtn.getLayoutParams());
        marginParams1.setMargins(marginParams1.leftMargin, marginParams1.topMargin, marginParams1.rightMargin, dp2px(40));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(marginParams1);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        mCaptureBtn.setLayoutParams(params);

        MarginLayoutParams marginParams2 = new MarginLayoutParams(mRecordVideoBtn.getLayoutParams());
        marginParams2.setMargins(dp2px(20), marginParams2.topMargin, marginParams2.rightMargin, dp2px(48));
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(marginParams2);
        params2.gravity = Gravity.BOTTOM | Gravity.START;
        mRecordVideoBtn.setLayoutParams(params2);

        MarginLayoutParams marginParams3 = new MarginLayoutParams(mSwitchCameraBtn.getLayoutParams());
        marginParams3.setMargins(marginParams3.leftMargin, marginParams3.topMargin, dp2px(20), dp2px(48));
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(marginParams3);
        params3.gravity = Gravity.BOTTOM | Gravity.END;
        mSwitchCameraBtn.setLayoutParams(params3);
        if (mMenuContainer == null) return;
        mMenuContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTextureView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        mIsFullScreen = true;
        mChangePaddingLift = getPaddingLeft();
        mChangePaddingTop = getPaddingTop();
        mChangePaddingRight = getPaddingRight();
        mChangePaddingBottom = getPaddingBottom();
        setPadding(0, 0, 0, 0);
        //TODO
        if (mOnWindowChangeListener != null) {
            mOnWindowChangeListener.onWindowChange(true);
        }
    }

    public int dp2px(float value) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    /**
     * Recover screen
     */
    private void recoverScreen() {
        mActivity.runOnUiThread(this);
    }

    public void dismissMenuLayout() {
        if (mCameraSupport != null) {
            mCameraSupport.release();
            mCameraSupport = null;
        }
        if (mMenuManager == null) return;
        mMenuManager.hideCustomMenu();
        if (mMenuContainer == null) return;
        mMenuContainer.setVisibility(GONE);
    }

    public void invisibleMenuLayout() {
        if (mMenuContainer == null) return;
        mMenuContainer.setVisibility(INVISIBLE);
    }

    public void showMenuLayout() {
        if (mMenuContainer == null) return;
        EmoticonsKeyboardUtils.closeSoftKeyboard(mChatInput);
        mMenuContainer.setVisibility(VISIBLE);
    }

    public void showRecordVoiceLayout() {
        if (mSelectPhotoView == null) return;
        mSelectPhotoView.setVisibility(GONE);
        mSelectVideoView.setVisibility(View.GONE);
        mCameraFl.setVisibility(GONE);
        mEmojiRl.setVisibility(GONE);
        mRecordVoiceRl.setVisibility(VISIBLE);
        mRecordControllerView.setVisibility(VISIBLE);
        mRecordVoiceBtn.setVisibility(VISIBLE);
        mPreviewBtnLl.setVisibility(GONE);
        mPreviewPlayBtn.setVisibility(GONE);
    }

    public void dismissRecordVoiceLayout() {
        if (mRecordVoiceRl == null) return;
        mRecordVoiceRl.setVisibility(GONE);
    }

    public void showSelectPhotoLayout() {
        if (mRecordVoiceRl == null) return;
        mRecordVoiceRl.setVisibility(GONE);
        mCameraFl.setVisibility(GONE);
        mEmojiRl.setVisibility(GONE);
        mSelectVideoView.setVisibility(View.GONE);
        mSelectPhotoView.setVisibility(VISIBLE);
    }

    public void showSelectVideoLayout() {
        if (mRecordVoiceRl == null) return;
        mRecordVoiceRl.setVisibility(GONE);
        mCameraFl.setVisibility(GONE);
        mEmojiRl.setVisibility(GONE);
        mSelectVideoView.setVisibility(View.VISIBLE);
        mSelectPhotoView.setVisibility(GONE);
    }

    public void dismissPhotoLayout() {
        if (mSelectPhotoView == null) return;
        mSelectPhotoView.setVisibility(View.GONE);
    }

    public void showCameraLayout() {
        if (mRecordVoiceRl == null) return;
        mRecordVoiceRl.setVisibility(GONE);
        mSelectPhotoView.setVisibility(GONE);
        mSelectVideoView.setVisibility(View.GONE);
        mEmojiRl.setVisibility(GONE);
        mCameraFl.setVisibility(VISIBLE);
    }

    public void showRecordVideoBtn() {
        if (mRecordVideoBtn == null) return;
        if ("GONE".equals(mRecordVideoBtn.getTag())) {
            mRecordVideoBtn.setVisibility(GONE);
        } else {
            mRecordVideoBtn.setVisibility(VISIBLE);
        }
    }

    public void dismissCameraLayout() {
        if (mCameraSupport != null) {
            mCameraSupport.release();
            mCameraSupport = null;
        }
        if (mCameraFl == null || mTextureView == null) return;
        mCameraFl.setVisibility(GONE);
        mTextureView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, sMenuHeight));
    }

    public void showEmojiLayout() {
        if (mRecordVoiceRl == null) return;
        mRecordVoiceRl.setVisibility(GONE);
        mSelectPhotoView.setVisibility(GONE);
        mSelectVideoView.setVisibility(View.GONE);
        mCameraFl.setVisibility(GONE);
        mEmojiRl.setVisibility(VISIBLE);
    }

    public void hideDefaultMenuLayout() {
        if (mRecordVoiceRl == null) return;
        mRecordVoiceRl.setVisibility(GONE);
        mSelectPhotoView.setVisibility(GONE);
        mSelectVideoView.setVisibility(View.GONE);
        mCameraFl.setVisibility(GONE);
        mEmojiRl.setVisibility(GONE);
    }

    public void dismissEmojiLayout() {
        if (mEmojiRl != null) {
            mEmojiRl.setVisibility(GONE);
        }
    }

    public void setMenuContainerHeight(int height) {
        if (height <= 0) return;
        sMenuHeight = height;
        if (mMenuContainer == null) return;
        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        mMenuContainer.setLayoutParams(params);
    }

    private boolean onSubmit() {
        return mListener != null && mListener.onSendTextMessage(mInput);
    }

    public int getMenuState() {
        if (mMenuContainer == null) return GONE;
        return mMenuContainer.getVisibility();
    }

    @Override
    public void onFileSelected(SelectView view) {
        if (mSelectPhotoView == null || mSelectVideoView == null) return;
        if (view == mSelectPhotoView && mSelectVideoView.getSelectFiles() != null && !mSelectVideoView.getSelectFiles().isEmpty()) {
            mSelectVideoView.resetCheckState();
        } else if (view == mSelectVideoView && mSelectPhotoView.getSelectFiles() != null && !mSelectPhotoView.getSelectFiles().isEmpty()) {
            mSelectPhotoView.resetCheckState();
        }
        ImageButton sendButton = mMenuManager.getSendButton();
        TextView sendCountTextView = mMenuManager.getSendCountTextView();
        int size = view.getSelectFiles().size();
        if (mInput.length() == 0 && size == 1) {
            triggerSendButtonAnimation(view, sendButton, true, true);
        } else if (mInput.length() > 0 && sendCountTextView.getVisibility() != View.VISIBLE) {
            sendCountTextView.setVisibility(View.VISIBLE);
        }
        sendCountTextView.setText(String.valueOf(size));
    }

    @Override
    public void onFileDeselected(SelectView view) {
        ImageButton sendButton = mMenuManager.getSendButton();
        TextView sendCountTextView = mMenuManager.getSendCountTextView();
        int size = view.getSelectFiles().size();
        if (size > 0) {
            sendCountTextView.setText(String.valueOf(size));
        } else {
            sendCountTextView.setVisibility(View.INVISIBLE);
            if (mInput.length() == 0) {
                triggerSendButtonAnimation(view, sendButton, false, true);
            }
        }
    }

    private void triggerSendButtonAnimation(SelectView view, ImageButton sendBtn, boolean hasContent, boolean isSelectPhoto) {
        ImageButton sendButton = mMenuManager.getSendButton();
        TextView sendCountTextView = mMenuManager.getSendCountTextView();
        float[] shrinkValues = new float[]{0.6f};
        AnimatorSet shrinkAnimatorSet = new AnimatorSet();
        shrinkAnimatorSet.playTogether(ObjectAnimator.ofFloat(sendBtn, "scaleX", shrinkValues), ObjectAnimator.ofFloat(sendBtn, "scaleY", shrinkValues));
        shrinkAnimatorSet.setDuration(100);

        float[] restoreValues = new float[]{1.0f};
        AnimatorSet restoreAnimatorSet = new AnimatorSet();
        restoreAnimatorSet.playTogether(ObjectAnimator.ofFloat(sendBtn, "scaleX", restoreValues), ObjectAnimator.ofFloat(sendBtn, "scaleY", restoreValues));
        restoreAnimatorSet.setDuration(100);
        restoreAnimatorSet.addListener(new RestoreAnimator(this, sendCountTextView, view));

        shrinkAnimatorSet.addListener(new ShrinkAnimator(hasContent, isSelectPhoto, sendCountTextView, sendButton, mStyle, restoreAnimatorSet));
        shrinkAnimatorSet.start();
    }

    @Deprecated
    public void setCameraCaptureFile(String path, String fileName) {
    }

    @Override
    public void onStart() {
        if (mChronometer == null) return;
        mChronometer.setVisibility(VISIBLE);
        mRecordHintTv.setVisibility(INVISIBLE);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    @Override
    public void onMoving() {
        if (mChronometer == null) return;
        mChronometer.setVisibility(VISIBLE);
        mRecordHintTv.setVisibility(INVISIBLE);
    }

    @Override
    public void onMovedLeft() {
        if (mChronometer == null) return;
        mChronometer.setVisibility(INVISIBLE);
        mRecordHintTv.setVisibility(VISIBLE);
        mRecordHintTv.setText(getContext().getString(R.string.preview_play_audio_hint));
    }

    @Override
    public void onMovedRight() {
        if (mChronometer == null) return;
        mChronometer.setVisibility(INVISIBLE);
        mRecordHintTv.setVisibility(VISIBLE);
        mRecordHintTv.setText(getContext().getString(R.string.cancel_record_voice_hint));
    }

    @Override
    public void onLeftUpTapped() {
        if (mChronometer == null) return;
        mChronometer.stop();
        mRecordTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
        mPreviewPlayBtn.setMax(Math.round((float) mRecordTime / 1000));
        mChronometer.setVisibility(VISIBLE);
        mRecordHintTv.setVisibility(INVISIBLE);
        mPreviewBtnLl.setVisibility(VISIBLE);
        mPreviewPlayBtn.setVisibility(VISIBLE);
        mRecordControllerView.setVisibility(GONE);
        mRecordVoiceBtn.setVisibility(GONE);
    }

    @Override
    public void onRightUpTapped() {
        if (mChronometer == null) return;
        mChronometer.stop();
        mChronometer.setText("00:00");
        mChronometer.setVisibility(INVISIBLE);
        mRecordHintTv.setText(getContext().getString(R.string.record_voice_hint));
        mRecordHintTv.setVisibility(VISIBLE);
    }

    @Override
    public void onFinish() {
        if (mChronometer == null) return;
        mChronometer.stop();
        mChronometer.setText("00:00");
        mChronometer.setVisibility(GONE);
        mRecordHintTv.setVisibility(VISIBLE);
    }

    private long convertStrTimeToLong(String strTime) {
        String[] timeArray = strTime.split(":");
        long longTime = 0;
        if (timeArray.length == 2) { // If time format is MM:SS
            longTime = Integer.parseInt(timeArray[0]) * 60 * 1000 + Integer.parseInt(timeArray[1]) * 1000;
        }
        return SystemClock.elapsedRealtime() - longTime;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCameraSupport != null) {
            mCameraSupport.release();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        getViewTreeObserver().removeOnPreDrawListener(this);
        mMediaPlayer = null;
    }


    @Override
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == GONE && mCameraSupport != null) {
            mCameraSupport.release();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mHeight <= 0) {
            this.getRootView().getGlobalVisibleRect(mRect);
            mHeight = mRect.bottom;
        }
    }

    public boolean isKeyboardVisible() {
        if (mMenuContainer == null) return false;
        int visibility = mMenuContainer.getVisibility();
        return (getDistanceFromInputToBottom() > 300 && visibility == GONE) || (getDistanceFromInputToBottom() > (mMenuContainer.getHeight() + 300) && visibility == VISIBLE);
    }

    @Override
    public void onFinishTakePicture() {
        if (!mIsFullScreen) return;
        recoverScreen();
    }

    public boolean isFullScreen() {
        return this.mIsFullScreen;
    }

    public void setPendingShowMenu(boolean flag) {
        this.mPendingShowMenu = flag;
    }

    @Override
    public boolean onPreDraw() {
        if (mPendingShowMenu) {
            if (isKeyboardVisible() && mMenuContainer != null) {
                ViewGroup.LayoutParams params = mMenuContainer.getLayoutParams();
                int distance = getDistanceFromInputToBottom();
                if (distance < mHeight / 2 && distance > 300 && distance != params.height && useKeyboardHeight) {
                    params.height = distance;
                    mSoftKeyboardHeight = distance;
                    mMenuContainer.setLayoutParams(params);
                }
                return false;
            } else {
                showMenuLayout();
                mPendingShowMenu = false;
                return false;
            }
        } else {
            if (mMenuContainer != null && mMenuContainer.getVisibility() == VISIBLE && isKeyboardVisible()) {
                dismissMenuLayout();
                return false;
            }
        }
        return true;
    }

    public void setShowBottomMenu(Boolean showBottomMenu) {
        this.mShowBottomMenu = showBottomMenu;
        mMenuItemContainer.setVisibility(showBottomMenu ? View.VISIBLE : View.GONE);
    }

    public boolean isShowBottomMenu() {
        return mShowBottomMenu;
    }

    public int getDistanceFromInputToBottom() {
        if (isShowBottomMenu()) {
            mMenuItemContainer.getGlobalVisibleRect(mRect);
        } else {
            mChatInputContainer.getGlobalVisibleRect(mRect);
        }
        return mHeight - mRect.bottom;
    }


    @Override
    public void requestLayout() {
        super.requestLayout();
        post(measureAndLayout);
    }

    public void setUseKeyboardHeight(boolean b) {
        this.useKeyboardHeight = b;
    }

    public int getSoftKeyboardHeight() {
        return mSoftKeyboardHeight > 0 ? mSoftKeyboardHeight : sMenuHeight;
    }

    public FrameLayout getCameraContainer() {
        return mCameraFl;
    }

    public RelativeLayout getVoiceContainer() {
        return mRecordVoiceRl;
    }

    public FrameLayout getSelectPictureContainer() {
        return mSelectPhotoView;
    }

    public EmojiView getEmojiContainer() {
        return mEmojiRl;
    }

    public ChatInputStyle getStyle() {
        return this.mStyle;
    }

    public SelectView getSelectPhotoView() {
        return this.mSelectPhotoView;
    }

    public ImageButton getRecordVideoBtn() {
        return this.mRecordVideoBtn;
    }

    public MenuManager getMenuManager() {
        return this.mMenuManager;
    }

    public LinearLayout getChatInputContainer() {
        return this.mChatInputContainer;
    }

    public LinearLayout getMenuItemContainer() {
        return this.mMenuItemContainer;
    }

    public FrameLayout getMenuContainer() {
        return this.mMenuContainer;
    }

    public void setCameraQuality(float cameraQuality) {
        mStyle.setCameraQuality(cameraQuality);
    }

    @Override
    public void onBackKeyClick() {
        if (mMenuContainer != null && mMenuContainer.getVisibility() == VISIBLE) {
            dismissMenuLayout();
        } else if (isKeyboardVisible()) {
            EmoticonsKeyboardUtils.closeSoftKeyboard(mChatInput);
        }
    }

    public boolean onBackPressed() {
        if (isFullScreen()) {
            Log.e("onBackPressed", " isFullScreen() ->  " + isFullScreen());
            recoverScreen();
            return true;
        } else if (mMenuContainer != null && mMenuContainer.getVisibility() == VISIBLE) {
            Log.e("onBackPressed", " mMenuItemContainer visibility ->  " + mMenuContainer.getVisibility());
            dismissMenuLayout();
            return true;
        }
        return false;
    }

    @Override
    public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
        if (isDelBtn) {
            SimpleCommonUtils.delClick(mChatInput);
        } else {
            if (o == null || actionType == Constants.EMOTICON_CLICK_BIGIMAGE) return;
            String content = null;
            if (o instanceof EmojiBean) {
                content = ((EmojiBean) o).emoji;
            } else if (o instanceof EmoticonEntity) {
                content = ((EmoticonEntity) o).getContent();
            }
            if (TextUtils.isEmpty(content)) {
                return;
            }
            int index = mChatInput.getSelectionStart();
            Editable editable = mChatInput.getText();
            if (editable != null) {
                editable.insert(index, content);
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mChronometer == null) return;
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mPreviewPlayBtn.startPlay();
        mChronometer.start();
        mp.start();
        mPlaying = true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mChronometer == null) return;
        mp.stop();
        mSetData = false;
        mChronometer.stop();
        mPlaying = false;
        mPreviewPlayBtn.finishPlay();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mCameraSupport == null) {
            initCamera();
        } else {
            mCameraSupport.open(mCameraId, width, height, mIsBackCamera, mStyle.getCameraQuality());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        if (mTextureView != null && mTextureView.getVisibility() == VISIBLE && mCameraSupport != null) {
            mCameraSupport.open(mCameraId, width, height, mIsBackCamera, mStyle.getCameraQuality());
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (null != mCameraSupport) {
            mCameraSupport.release();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void run() {
        if (mCloseBtn == null || mFullScreenBtn == null) return;
        Window window = mActivity.getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(attrs);
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mIsFullScreen = false;
        mIsRecordingVideo = false;
        mIsRecordVideoMode = false;
        mCloseBtn.setVisibility(GONE);
        mFullScreenBtn.setBackgroundResource(R.drawable.aurora_preview_full_screen);
        mFullScreenBtn.setVisibility(VISIBLE);
        mChatInputContainer.setVisibility(VISIBLE);
        Log.e("setVisibility", "" + isShowBottomMenu());


        mMenuItemContainer.setVisibility(isShowBottomMenu() ? VISIBLE : GONE);
        Log.e("setVisibility", "" + mMenuItemContainer.getVisibility());
        int height = sMenuHeight;
        if (mSoftKeyboardHeight != 0 && useKeyboardHeight) height = mSoftKeyboardHeight;
        setMenuContainerHeight(height);
        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        mTextureView.setLayoutParams(params);
        mRecordVideoBtn.setBackgroundResource(R.drawable.aurora_preview_record_video);
        showRecordVideoBtn();
        mSwitchCameraBtn.setBackgroundResource(R.drawable.aurora_preview_switch_camera);
        mSwitchCameraBtn.setVisibility(VISIBLE);
        mCaptureBtn.setBackgroundResource(R.drawable.aurora_menuitem_send_pres);

        MarginLayoutParams marginParams1 = new MarginLayoutParams(mCaptureBtn.getLayoutParams());
        marginParams1.setMargins(marginParams1.leftMargin, marginParams1.topMargin, marginParams1.rightMargin, dp2px(12));
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(marginParams1);
        params1.gravity = Gravity.BOTTOM | Gravity.CENTER;
        mCaptureBtn.setLayoutParams(params1);

        MarginLayoutParams marginParams2 = new MarginLayoutParams(mRecordVideoBtn.getLayoutParams());
        marginParams2.setMargins(dp2px(20), marginParams2.topMargin, marginParams2.rightMargin, dp2px(20));
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(marginParams2);
        params2.gravity = Gravity.BOTTOM | Gravity.START;
        mRecordVideoBtn.setLayoutParams(params2);
        MarginLayoutParams marginParams3 = new MarginLayoutParams(mSwitchCameraBtn.getLayoutParams());
        marginParams3.setMargins(marginParams3.leftMargin, marginParams3.topMargin, dp2px(20), dp2px(20));
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(marginParams3);
        params3.gravity = Gravity.BOTTOM | Gravity.END;
        mSwitchCameraBtn.setLayoutParams(params3);
        setPadding(mChangePaddingLift, mChangePaddingTop, mChangePaddingRight, mChangePaddingBottom);
        if (mOnWindowChangeListener != null) {
            mOnWindowChangeListener.onWindowChange(false);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isFullScreen()) {
            recoverScreen();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setOnWindowChangeListener(OnWindowChangeListener mOnWindowChangeListener) {
        this.mOnWindowChangeListener = mOnWindowChangeListener;
    }

    private final Runnable mPostRunnable = new Runnable() {

        @Override
        public void run() {
            if (mCaptureBtn == null) return;
            mCaptureBtn.setBackgroundResource(R.drawable.aurora_preview_record_video_stop);
            mRecordVideoBtn.setVisibility(GONE);
            mSwitchCameraBtn.setVisibility(GONE);
            mCloseBtn.setVisibility(VISIBLE);
        }

    };

    private final Runnable measureAndLayout = new Runnable() {

        @Override
        public void run() {
            int width = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
            int height = MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY);
            measure(width, height);
            layout(getLeft(), getTop(), getRight(), getBottom());
        }

    };

    public interface OnWindowChangeListener {

        void onWindowChange(boolean full);

    }
}
