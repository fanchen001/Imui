package imui.jiguang.cn.imuisample.messages;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.fanchen.R;
import com.fanchen.chat.ChatInputView;
import com.fanchen.chat.listener.OnCameraCallbackListener;
import com.fanchen.chat.listener.OnMenuClickListener;
import com.fanchen.chat.listener.OnSelectButtonListener;
import com.fanchen.chat.listener.RecordVoiceListener;
import com.fanchen.chat.model.FileItem;
import com.fanchen.chat.model.VideoItem;
import com.fanchen.filepicker.FilePicker;
import com.fanchen.filepicker.model.EssFile;
import com.fanchen.filepicker.util.Const;
import com.fanchen.filepicker.util.FileSizeUtil;
import com.fanchen.location.MapNavigationActivity;
import com.fanchen.location.MapSendActivity;
import com.fanchen.location.bean.LocationBean;
import com.fanchen.location.utils.CommonUtils;
import com.fanchen.message.commons.ImageLoader;
import com.fanchen.message.commons.models.IMessage;
import com.fanchen.message.messages.MsgListAdapter;
import com.fanchen.message.messages.ptr.PtrHandler;
import com.fanchen.message.messages.ptr.PullToRefreshLayout;
import com.fanchen.message.messages.ViewHolderController;
import com.fanchen.video.Jzvd;
import com.fanchen.video.JzvdStd;
import com.fanchen.video.SimpleVideoActivity;
import com.fanchen.view.ChatView;
import com.fanchen.view.DefaultFeatureAdapter;

import cc.shinichi.library.ImagePreview;
import imui.jiguang.cn.imuisample.models.DefaultUser;
import imui.jiguang.cn.imuisample.models.MyMessage;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MessageListActivity extends Activity implements View.OnTouchListener,
        EasyPermissions.PermissionCallbacks, SensorEventListener {

    private final static String TAG = "MessageListActivity";
    private final int RC_RECORD_VOICE = 0x0001;
    private final int RC_CAMERA = 0x0002;
    private final int RC_PHOTO = 0x0003;

    private ChatView mChatView;
    private MsgListAdapter<MyMessage> mAdapter;
    private List<MyMessage> mData;

    private InputMethodManager mImm;
    private Window mWindow;
    private HeadsetDetectReceiver mReceiver;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    /**
     * Store all image messages' path, pass it to {@link BrowserImageActivity},
     * so that click image message can browser all images.
     */
    private ArrayList<String> mPathList = new ArrayList<>();
    private ArrayList<String> mMsgIdList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 500 && data != null) {
            ArrayList<EssFile> essFileList = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);
            for (EssFile file : essFileList) {

                MyMessage message = new MyMessage("", IMessage.MessageType.SEND_FILE.ordinal());
                message.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                message.setMessageStatus(IMessage.MessageStatus.SEND_GOING);
                HashMap<String, String> extras = message.getExtras();

                extras.put("fileTitle", file.getName());
                extras.put("fileSize", FileSizeUtil.getAutoFileOrFilesSize(file.getFile()));

                mAdapter.addToStart(message, true);
            }
        } else if (requestCode == 600 && data != null) {
            LocationBean bean = data.getParcelableExtra("Location");
            String thumbnailPath = data.getStringExtra("thumbnailPath");
            MyMessage message = new MyMessage("", IMessage.MessageType.SEND_LOCATION.ordinal());
            message.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
            message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
            message.setMessageStatus(IMessage.MessageStatus.SEND_GOING);
            HashMap<String, String> extras = message.getExtras();

            extras.put("locationTitle", bean.getName());
            extras.put("locationDetails", bean.getAddress());
            extras.put("path", thumbnailPath);


            mAdapter.addToStart(message, true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mWindow = getWindow();
        registerProximitySensorListener();
        mChatView = (ChatView) findViewById(R.id.chat_view);
        mChatView.customMenuBuild("6666", new DefaultFeatureAdapter(), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    FilePicker.from(MessageListActivity.this)
                            .chooseForMimeType()
                            .isSingle()
                            .setFileTypes("png", "doc", "apk", "mp3", "gif", "txt", "mp4", "zip")
                            .requestCode(500)
                            .start();
                } else if (position == 4) {
                    startActivityForResult(new Intent(MessageListActivity.this, MapSendActivity.class), 600);
                } else if (position == 5) {
                    MyMessage message2 = new MyMessage("", IMessage.MessageType.SEND_ID_CARD.ordinal());
                    message2.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                    message2.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message2.setMessageStatus(IMessage.MessageStatus.SEND_DRAFT);
                    mAdapter.addToStart(message2, true);
                } else if (position == 0) {
                    MyMessage message2 = new MyMessage("", IMessage.MessageType.SEND_ORDER.ordinal());
                    message2.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                    message2.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message2.setMessageStatus(IMessage.MessageStatus.SEND_FAILED);
                    mAdapter.addToStart(message2, true);
                } else if (position == 1) {
                    MyMessage message2 = new MyMessage("", IMessage.MessageType.SEND_GOODS.ordinal());
                    message2.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                    message2.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message2.setMessageStatus(IMessage.MessageStatus.SEND_SUCCEED);
                    mAdapter.addToStart(message2, true);
                }
            }
        });
        mData = getMessages();
        initMsgAdapter();
        mReceiver = new HeadsetDetectReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
        mChatView.setOnTouchListener(this);
        mChatView.setMenuClickListener(new OnMenuClickListener() {
            @Override
            public boolean onSendTextMessage(CharSequence input) {
                if (input.length() == 0) {
                    return false;
                }
                {


                    MyMessage message1 = new MyMessage(input.toString(), IMessage.MessageType.SEND_FILE.ordinal());
                    message1.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                    message1.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message1.setMessageStatus(IMessage.MessageStatus.SEND_FAILED);
                    mAdapter.addToStart(message1, true);

                    MyMessage message2 = new MyMessage(input.toString(), IMessage.MessageType.SEND_ID_CARD.ordinal());
                    message2.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                    message2.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message2.setMessageStatus(IMessage.MessageStatus.SEND_DRAFT);
                    mAdapter.addToStart(message2, true);
                }

                {
                    MyMessage message = new MyMessage(input.toString(), IMessage.MessageType.RECEIVE_FILE.ordinal());
                    message.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                    message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message.setMessageStatus(IMessage.MessageStatus.RECEIVE_SUCCEED);
                    mAdapter.addToStart(message, true);

                    MyMessage message1 = new MyMessage(input.toString(), IMessage.MessageType.RECEIVE_ID_CARD.ordinal());
                    message1.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                    message1.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message1.setMessageStatus(IMessage.MessageStatus.RECEIVE_FAILED);
                    mAdapter.addToStart(message1, true);

                    MyMessage message2 = new MyMessage(input.toString(), IMessage.MessageType.RECEIVE_LOCATION.ordinal());
                    message2.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                    message2.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message2.setMessageStatus(IMessage.MessageStatus.RECEIVE_GOING);
                    mAdapter.addToStart(message2, true);
                }

                return true;
            }

            @Override
            public void onSendFiles(List<FileItem> list) {
                if (list == null || list.isEmpty()) {
                    return;
                }

                MyMessage message;
                for (FileItem item : list) {
                    if (item.getType() == FileItem.Type.Image) {
                        message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE.ordinal());
                        mPathList.add(item.getFilePath());
                        mMsgIdList.add(message.getMsgId());
                    } else if (item.getType() == FileItem.Type.Video) {
                        message = new MyMessage(null, IMessage.MessageType.SEND_VIDEO.ordinal());
                        message.setDuration(((VideoItem) item).getDuration());

                    } else {
                        throw new RuntimeException("Invalid FileItem type. Must be Type.Image or Type.Video");
                    }

                    message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message.setMediaFilePath(item.getFilePath());
                    message.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));

                    final MyMessage fMsg = message;
                    MessageListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addToStart(fMsg, true);
                        }
                    });
                }
            }

            @Override
            public boolean switchToMicrophoneMode() {
                scrollToBottom();
                String[] perms = new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                if (!EasyPermissions.hasPermissions(MessageListActivity.this, perms)) {
                    EasyPermissions.requestPermissions(MessageListActivity.this,
                            getResources().getString(R.string.rationale_record_voice),
                            RC_RECORD_VOICE, perms);
                }
                return true;
            }

            @Override
            public boolean switchToGalleryMode(int mode) {
                scrollToBottom();
                String[] perms = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };

                if (!EasyPermissions.hasPermissions(MessageListActivity.this, perms)) {
                    EasyPermissions.requestPermissions(MessageListActivity.this,
                            getResources().getString(R.string.rationale_photo),
                            RC_PHOTO, perms);
                }
                // If you call updateData, select photo view will try to update data(Last update over 30 seconds.)
                mChatView.getChatInputView().getSelectPhotoView().updateData();
                return true;
            }

            @Override
            public boolean switchToCameraMode() {
                scrollToBottom();
                String[] perms = new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                };

                if (!EasyPermissions.hasPermissions(MessageListActivity.this, perms)) {
                    EasyPermissions.requestPermissions(MessageListActivity.this,
                            getResources().getString(R.string.rationale_camera),
                            RC_CAMERA, perms);
                    return false;
                } else {
                    File rootDir = getFilesDir();
                    String fileDir = rootDir.getAbsolutePath() + "/photo";
                    mChatView.setCameraCaptureFile(fileDir, new SimpleDateFormat("yyyy-MM-dd-hhmmss",
                            Locale.getDefault()).format(new Date()));
                }
                return true;
            }

            @Override
            public boolean switchToEmojiMode() {
                scrollToBottom();
                return true;
            }
        });

        mChatView.setRecordVoiceListener(new RecordVoiceListener() {
            @Override
            public void onStartRecord() {
                // set voice file path, after recording, audio file will save here
                String path = Environment.getExternalStorageDirectory().getPath() + "/voice";
                File destDir = new File(path);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                mChatView.setRecordVoiceFile(destDir.getPath(), DateFormat.format("yyyy-MM-dd-hhmmss",
                        Calendar.getInstance(Locale.CHINA)) + "");
            }

            @Override
            public void onFinishRecord(File voiceFile, int duration) {
                MyMessage message = new MyMessage(null, IMessage.MessageType.SEND_VOICE.ordinal());
                message.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                message.setMediaFilePath(voiceFile.getPath());
                message.setDuration(duration);
                message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                mAdapter.addToStart(message, true);
            }

            @Override
            public void onCancelRecord() {

            }

            /**
             * In preview record voice layout, fires when click cancel button
             * Add since chatinput v0.7.3
             */
            @Override
            public void onPreviewCancel() {

            }

            /**
             * In preview record voice layout, fires when click send button
             * Add since chatinput v0.7.3
             */
            @Override
            public void onPreviewSend() {

            }
        });

        mChatView.setOnCameraCallbackListener(new OnCameraCallbackListener() {
            @Override
            public void onTakePictureCompleted(String photoPath) {
                final MyMessage message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE.ordinal());
                message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                message.setMediaFilePath(photoPath);
                mPathList.add(photoPath);
                mMsgIdList.add(message.getMsgId());
                message.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
                MessageListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addToStart(message, true);
                    }
                });
            }

            @Override
            public void onStartVideoRecord() {

            }

            @Override
            public void onFinishVideoRecord(String videoPath) {

            }

            @Override
            public void onCancelVideoRecord() {

            }
        });

        mChatView.getChatInputView().getInputView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollToBottom();
                return false;
            }
        });

        mChatView.getChatInputView().setOnSelectButtonListener(new OnSelectButtonListener() {

            @Override
            public void onSelectButtonClick(int mode) {
                if (mode == OnMenuClickListener.ALL) {
                    FilePicker.from(MessageListActivity.this)
                            .chooseMedia().requestCode(200)
                            .start();
                } else if (mode == OnMenuClickListener.SEPARATE_PHOTO) {
                    FilePicker.from(MessageListActivity.this).chooseMedia()
                            .enabledCapture(true).onlyShowImages().requestCode(200)
                            .start();
                } else {
                    FilePicker.from(MessageListActivity.this)
                            .chooseMedia().onlyShowVideos().requestCode(200)
                            .start();
                }
            }

        });

//        mChatView.getSelectAlbumBtn().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Toast.makeText(MessageListActivity.this, "OnClick select album button",
////                        Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void registerProximitySensorListener() {
        try {
            mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        try {
            if (audioManager.isBluetoothA2dpOn() || audioManager.isWiredHeadsetOn()) {
                return;
            }
            if (mAdapter.getMediaPlayer().isPlaying()) {
                float distance = event.values[0];
                if (distance >= mSensor.getMaximumRange()) {
                    mAdapter.setAudioPlayByEarPhone(0);
                    setScreenOn();
                } else {
                    mAdapter.setAudioPlayByEarPhone(2);
                    ViewHolderController.getInstance().replayVoice();
                    setScreenOff();
                }
            } else {
                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                    mWakeLock = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setScreenOn() {
        if (mWakeLock != null) {
            mWakeLock.setReferenceCounted(false);
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void setScreenOff() {
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
        }
        mWakeLock.acquire();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class HeadsetDetectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.hasExtra("state")) {
                    int state = intent.getIntExtra("state", 0);
                    mAdapter.setAudioPlayByEarPhone(state);
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private List<MyMessage> getMessages() {
        List<MyMessage> list = new ArrayList<>();
        Resources res = getResources();
        String[] messages = res.getStringArray(R.array.messages_array);
        for (int i = 0; i < messages.length; i++) {
            MyMessage message;
            if (i % 2 == 0) {
                message = new MyMessage(messages[i], IMessage.MessageType.RECEIVE_TEXT.ordinal());
                message.setUserInfo(new DefaultUser("0", "DeadPool", "R.drawable.deadpool"));
            } else {
                message = new MyMessage(messages[i], IMessage.MessageType.SEND_TEXT.ordinal());
                message.setUserInfo(new DefaultUser("1", "IronMan", "R.drawable.ironman"));
            }
            message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
            list.add(message);
        }
        return list;
    }

    private void initMsgAdapter() {
        final float density = getResources().getDisplayMetrics().density;
        final float MIN_WIDTH = 60 * density;
        final float MAX_WIDTH = 200 * density;
        final float MIN_HEIGHT = 60 * density;
        final float MAX_HEIGHT = 200 * density;
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                // You can use other image load libraries.
                if (string.contains("R.drawable")) {
                    Integer resId = getResources().getIdentifier(string.replace("R.drawable.", ""),
                            "drawable", getPackageName());

                    avatarImageView.setImageResource(resId);
                } else {
                    Glide.with(MessageListActivity.this)
                            .load(string).asBitmap()
                            .into(avatarImageView);
                }
            }

            /**
             * Load image message
             * @param imageView Image message's ImageView.
             * @param string A file path, or a uri or url.
             */
            @Override
            public void loadImage(final ImageView imageView, String string) {
                // You can use other image load libraries.
                Glide.with(getApplicationContext())
                        .load(string).asBitmap()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                int imageWidth = resource.getWidth();
                                int imageHeight = resource.getHeight();
                                Log.d(TAG, "Image width " + imageWidth + " height: " + imageHeight);

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
                            }
                        });
            }

            /**
             * Load video message
             * @param imageCover Video message's image cover
             * @param uri Local path or url.
             */
            @Override
            public void loadVideo(ImageView imageCover, String uri) {
                long interval = 5000 * 1000;
                Glide.with(MessageListActivity.this)
                        .load(uri).asBitmap()
                        .override(200, 400)
                        // Resize image view by change override size.
                        .into(imageCover);
            }
        };

        // Use default layout
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        mAdapter = new MsgListAdapter<>("0", holdersConfig, imageLoader);
        // If you want to customise your layout, try to create custom ViewHolder:
        // holdersConfig.setSenderTxtMsg(CustomViewHolder.class, layoutRes);
        // holdersConfig.setReceiverTxtMsg(CustomViewHolder.class, layoutRes);
        // CustomViewHolder must extends ViewHolders defined in MsgListAdapter.
        // Current ViewHolders are TxtViewHolder, VoiceViewHolder.

        mAdapter.setOnMsgClickListener(new MsgListAdapter.OnMsgClickListener<MyMessage>() {
            @Override
            public void onMessageClick(MyMessage message) {
                // do something
                if (message.getType() == IMessage.MessageType.RECEIVE_VIDEO.ordinal() || message.getType() == IMessage.MessageType.SEND_VIDEO.ordinal()) {
                    if (!TextUtils.isEmpty(message.getMediaFilePath())) {
                        Intent intent = new Intent(MessageListActivity.this, SimpleVideoActivity.class);
                        intent.putExtra("path", message.getMediaFilePath());
                        intent.putExtra("name", new File(message.getMediaFilePath()).getName());
                        startActivity(intent);
                    }
                } else if (message.getType() == IMessage.MessageType.RECEIVE_IMAGE.ordinal()
                        || message.getType() == IMessage.MessageType.SEND_IMAGE.ordinal()) {
                    ImagePreview instance = ImagePreview.getInstance();
                    instance.setContext(MessageListActivity.this);
                    instance.setEnableClickClose(true);
                    instance.setEnableDragClose(true);
                    instance.setIndex(mMsgIdList.indexOf(message.getMsgId())).setImageList(mPathList).start();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.message_click_hint),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMessageChildClick(View v, MyMessage message) {
                if (v.getId() == R.id.aurora_tv_msgitem_see) {
                    startActivity(new Intent(MessageListActivity.this, MapNavigationActivity.class));
                } else if (v.getId() == R.id.aurora_tv_msgitem_to) {
                    CommonUtils.showMapChoiceDialog(MessageListActivity.this, 0, 0, "");
                }
            }
        });

        mAdapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<MyMessage>() {
            @Override
            public void onMessageLongClick(View view, MyMessage message) {
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.message_long_click_hint),
                        Toast.LENGTH_SHORT).show();
                // do something
            }
        });

        mAdapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<MyMessage>() {
            @Override
            public void onAvatarClick(MyMessage message) {
                DefaultUser userInfo = (DefaultUser) message.getFromUser();
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.avatar_click_hint),
                        Toast.LENGTH_SHORT).show();
                // do something
            }
        });

        mAdapter.setMsgStatusViewClickListener(new MsgListAdapter.OnMsgStatusViewClickListener<MyMessage>() {
            @Override
            public void onStatusViewClick(MyMessage message) {
                // message status view click, resend or download here
            }
        });

        MyMessage message = new MyMessage("Hello World", IMessage.MessageType.RECEIVE_TEXT.ordinal());
        message.setUserInfo(new DefaultUser("0", "Deadpool", "R.drawable.deadpool"));
        mAdapter.addToStart(message, true);
        MyMessage voiceMessage = new MyMessage("", IMessage.MessageType.RECEIVE_VOICE.ordinal());
        voiceMessage.setUserInfo(new DefaultUser("0", "Deadpool", "R.drawable.deadpool"));
        voiceMessage.setMediaFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/voice/2018-02-28-105103.m4a");
        voiceMessage.setDuration(4);

        mAdapter.addToStart(voiceMessage, true);

        MyMessage sendVoiceMsg = new MyMessage("", IMessage.MessageType.SEND_VOICE.ordinal());
        sendVoiceMsg.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));
        sendVoiceMsg.setMediaFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/voice/2018-02-28-105103.m4a");
        sendVoiceMsg.setDuration(4);
        sendVoiceMsg.setMessageStatus(IMessage.MessageStatus.SEND_SUCCEED);
        mAdapter.addToStart(sendVoiceMsg, true);
        MyMessage eventMsg = new MyMessage("haha", IMessage.MessageType.EVENT.ordinal());
        mAdapter.addToStart(eventMsg, true);

        MyMessage receiveVideo = new MyMessage("", IMessage.MessageType.RECEIVE_VIDEO.ordinal());
        receiveVideo.setMediaFilePath(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Hangouts/video-20170407_135638.3gp");
        receiveVideo.setDuration(4);
        receiveVideo.setUserInfo(new DefaultUser("0", "Deadpool", "R.drawable.deadpool"));
        mAdapter.addToStart(receiveVideo, true);
        mAdapter.addToEndChronologically(mData);
        PullToRefreshLayout layout = mChatView.getPtrLayout();
        layout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PullToRefreshLayout layout) {
                Log.i("MessageListActivity", "Loading next page");
                loadNextPage();
            }
        });
        // Deprecated, should use onRefreshBegin to load next page
        mAdapter.setOnLoadMoreListener(new MsgListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalCount) {
//                Log.i("MessageListActivity", "Loading next page");
//                loadNextPage();
            }
        });

        mChatView.setAdapter(mAdapter);
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<MyMessage> list = new ArrayList<>();
                Resources res = getResources();
                String[] messages = res.getStringArray(R.array.conversation);
                for (int i = 0; i < messages.length; i++) {
                    MyMessage message;
                    if (i % 2 == 0) {
                        message = new MyMessage(messages[i], IMessage.MessageType.RECEIVE_TEXT.ordinal());
                        message.setUserInfo(new DefaultUser("0", "DeadPool", "R.drawable.deadpool"));
                    } else {
                        message = new MyMessage(messages[i], IMessage.MessageType.SEND_TEXT.ordinal());
                        message.setUserInfo(new DefaultUser("1", "IronMan", "R.drawable.ironman"));
                    }
                    message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    list.add(message);
                }
//                Collections.reverse(list);
                // MessageList 0.7.2 add this method, add messages chronologically.
                mAdapter.addToEndChronologically(list);
                mChatView.getPtrLayout().refreshComplete();
            }
        }, 1500);
    }

    private void scrollToBottom() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatView.getMessageListView().smoothScrollToPosition(0);
            }
        }, 200);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ChatInputView chatInputView = mChatView.getChatInputView();
                if (chatInputView.getMenuState() == View.VISIBLE) {
                    chatInputView.dismissMenuLayout();
                }
                try {
                    View v = getCurrentFocus();
                    if (mImm != null && v != null) {
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        view.clearFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MotionEvent.ACTION_UP:
                view.performClick();
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mSensorManager.unregisterListener(this);
    }
}
