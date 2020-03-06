package imui.jiguang.cn.imuisample.models;


import com.fanchen.message.commons.models.IMessage;
import com.fanchen.message.commons.models.IUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class MyMessage implements IMessage {

    private long id;
    private String text;
    private String timeString;
    private int type;
    private IUser user;
    private String mediaFilePath;
    private long duration;
    private String progress;
    private Object tag;
    private MessageStatus mMsgStatus = MessageStatus.CREATED;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public static MyMessage from(Message message, DownloadCallback callback) {
        return from(message, false, callback);
    }

    public static MyMessage from(Message message, boolean isSend, DownloadCallback callback) {
        MyMessage msg = null;
        HashMap<String, String> extras = null;
        UserInfo userInfo = message.getFromUser();
        switch (message.getContentType()) {
            case file:
                FileContent fileContent = (FileContent) message.getContent();
                if("location".equals(fileContent.getStringExtra("type"))){
                    msg = new MyMessage(isSend ? MessageType.SEND_LOCATION.ordinal() : MessageType.RECEIVE_LOCATION.ordinal());
                    msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                    msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                    msg.setMessageStatus(isSend ? MessageStatus.SEND_SUCCEED :IMessage.MessageStatus.RECEIVE_SUCCEED);
                    extras = msg.getExtras();
                    extras.put("scale", String.valueOf(fileContent.getNumberExtra("scale")));
                    extras.put("city", fileContent.getStringExtra("city"));
                    extras.put("latitude",  String.valueOf(fileContent.getNumberExtra("latitude")));
                    extras.put("longitude",  String.valueOf(fileContent.getNumberExtra("longitude")));
                    extras.put("locationTitle", fileContent.getStringExtra("locationTitle"));
                    extras.put("locationDetails", fileContent.getStringExtra("locationDetails"));
                    extras.put("path", "");
                    if (callback != null) fileContent.downloadFile(message, callback);
                }else{
                    msg = new MyMessage(isSend ? IMessage.MessageType.SEND_FILE.ordinal() : IMessage.MessageType.RECEIVE_FILE.ordinal());
                    msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                    msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                    msg.setMessageStatus(isSend ? MessageStatus.SEND_SUCCEED :IMessage.MessageStatus.RECEIVE_SUCCEED);
                    extras = msg.getExtras();
                    extras.put("fileName", fileContent.getFileName());
                    extras.put("fileSize", message.getContent().getStringExtra("fileSize"));
                    if (callback != null) fileContent.downloadFile(message, callback);
                }
//                msg = new MyMessage(isSend ? IMessage.MessageType.SEND_FILE.ordinal() : IMessage.MessageType.RECEIVE_FILE.ordinal());
//                msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
//                msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
//                msg.setMessageStatus(isSend ? MessageStatus.SEND_SUCCEED :IMessage.MessageStatus.RECEIVE_SUCCEED);
//                extras = msg.getExtras();
//                extras.put("fileTitle", fileContent.getFileName());
//                extras.put("fileSize", message.getContent().getStringExtra("fileSize"));
//                if (callback != null) fileContent.downloadFile(message, callback);
                break;
            case image:
                ImageContent imageContent = (ImageContent) message.getContent();
                msg = new MyMessage(isSend ? IMessage.MessageType.SEND_IMAGE.ordinal() : IMessage.MessageType.RECEIVE_IMAGE.ordinal());
                msg.setMediaFilePath("");
                msg.setMessageStatus(isSend ? MessageStatus.SEND_GOING :IMessage.MessageStatus.RECEIVE_GOING);
                if (callback != null) imageContent.downloadOriginImage(message, callback);
                msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                break;
            case text:
                TextContent textContent = (TextContent) message.getContent();
                msg = new MyMessage(textContent.getText(), isSend ? IMessage.MessageType.SEND_TEXT.ordinal() : IMessage.MessageType.RECEIVE_TEXT.ordinal());
                msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                msg.setMessageStatus(isSend ? MessageStatus.SEND_SUCCEED :IMessage.MessageStatus.RECEIVE_SUCCEED);
                break;
            case location:
                LocationContent locationContent = (LocationContent) message.getContent();
                msg = new MyMessage(isSend ? IMessage.MessageType.SEND_LOCATION.ordinal() : IMessage.MessageType.RECEIVE_LOCATION.ordinal());
                msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                msg.setMessageStatus(isSend ? MessageStatus.SEND_GOING :MessageStatus.RECEIVE_GOING);
                extras = msg.getExtras();
                extras.put("locationTitle", locationContent.getStringExtra("locationTitle"));
                extras.put("locationDetails", locationContent.getAddress());
                extras.put("path", "");
                break;
            case video:
                VideoContent videoContent = (VideoContent) message.getContent();
                msg = new MyMessage(isSend ? IMessage.MessageType.SEND_VIDEO.ordinal() : IMessage.MessageType.RECEIVE_VIDEO.ordinal());
                msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                msg.setMessageStatus(isSend ? MessageStatus.SEND_GOING :IMessage.MessageStatus.RECEIVE_GOING);
                msg.setDuration(videoContent.getDuration());
                if (callback != null) videoContent.downloadThumbImage(message,callback);
                if (callback != null) videoContent.downloadVideoFile(message, callback);
                break;
            case voice:
                VoiceContent voiceContent = (VoiceContent) message.getContent();
                msg = new MyMessage(isSend ? IMessage.MessageType.SEND_VOICE.ordinal() : IMessage.MessageType.RECEIVE_VOICE.ordinal());
                msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                msg.setMessageStatus(isSend ? MessageStatus.SEND_GOING :IMessage.MessageStatus.RECEIVE_GOING);
                msg.setDuration(voiceContent.getDuration());
                if (callback != null) voiceContent.downloadVoiceFile(message, callback);
                break;
            case custom:
                CustomContent customContent = (CustomContent) message.getContent();
                String stringValue = customContent.getStringValue("type");
                switch (stringValue){
                    case "id":
                        msg = new MyMessage(isSend ? IMessage.MessageType.SEND_ID_CARD.ordinal() : IMessage.MessageType.RECEIVE_ID_CARD.ordinal());
                        msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                        msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                        msg.setMessageStatus(isSend ? MessageStatus.SEND_SUCCEED : MessageStatus.RECEIVE_SUCCEED);
                        break;
                    case "order":
                        msg = new MyMessage(isSend ? IMessage.MessageType.SEND_ORDER.ordinal() : MessageType.RECEIVE_ORDER.ordinal());
                        msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                        msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                        msg.setMessageStatus(isSend ? MessageStatus.SEND_SUCCEED :MessageStatus.RECEIVE_SUCCEED);
                        break;
                    case "goods":
                        msg = new MyMessage(isSend ? IMessage.MessageType.SEND_GOODS.ordinal() : MessageType.RECEIVE_GOODS.ordinal());
                        msg.setUserInfo(new DefaultUser(userInfo.getUserName(), userInfo.getDisplayName(), userInfo.getAvatarFile().getAbsolutePath()));
                        msg.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(message.getCreateTime())));
                        msg.setMessageStatus(isSend ? MessageStatus.SEND_SUCCEED :IMessage.MessageStatus.RECEIVE_SUCCEED);
                        break;
                }
                break;
                default:
                    msg = new MyMessage(isSend ? MessageType.SEND_RECALL.ordinal():MessageType.RECEIVE_RECALL.ordinal());
                    break;
        }
        if (msg != null) msg.setTag(message);
        if (callback != null) callback.setMyMessage(msg, isSend);
        return msg;
    }

    public MyMessage(int type) {
        this.text = "";
        this.type = type;
        this.id = UUID.randomUUID().getLeastSignificantBits();
    }

    public MyMessage(String text, int type) {
        this.text = text;
        this.type = type;
        this.id = UUID.randomUUID().getLeastSignificantBits();
    }

    @Override
    public String getMsgId() {
        return String.valueOf(id);
    }

    public long getId() {
        return this.id;
    }

    @Override
    public IUser getFromUser() {
        if (user == null) {
            return new DefaultUser("0", "user1", null);
        }
        return user;
    }

    public void setUserInfo(IUser user) {
        this.user = user;
    }

    public void setMediaFilePath(String path) {
        this.mediaFilePath = path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String getProgress() {
        return progress;
    }

    HashMap<String, String> objectObjectHashMap;

    @Override
    public HashMap<String, String> getExtras() {
        if (objectObjectHashMap == null) {
            objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("idCardTitle", "金木梳");
            objectObjectHashMap.put("idCardNumber", "账号:520520520");

            objectObjectHashMap.put("fileName", "iOS 沉浸式状态栏实现.apk");
            objectObjectHashMap.put("fileSize", "100.45Mb");

            objectObjectHashMap.put("locationTitle", "聂市镇");
            objectObjectHashMap.put("locationDetails", "岳阳市临湘市聂市镇105县道");
//            objectObjectHashMap.put("path", "https://dss0.baidu.com/73x1bjeh1BF3odCf/it/u=2678639494,2096646928&fm=85&s=F190CB3278D6FFEB52904FEF0300303F");

        }

        return objectObjectHashMap;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    @Override
    public String getTimeString() {
        return timeString;
    }

    @Override
    public long getTime() {
        return i;
    }
    public long i;
private boolean is;
    @Override
    public boolean showTime() {
        return is;
    }

    @Override
    public void setShowTime(boolean show) {
        this.is = show;
    }

    @Override
    public boolean haveRead() {
        return new Random().nextInt() % 2 == 1;
    }

    public void setType(int type) {
        if (type >= 0 && type <= 12) {
            throw new IllegalArgumentException("Message type should not take the value between 0 and 12");
        }
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }

    /**
     * Set Message status. After sending Message, change the status so that the progress bar will dismiss.
     */
    public void setMessageStatus(MessageStatus messageStatus) {
        this.mMsgStatus = messageStatus;
    }

    @Override
    public MessageStatus getMessageStatus() {
        return this.mMsgStatus;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getMediaFilePath() {
        return mediaFilePath;
    }
}