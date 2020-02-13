package imui.jiguang.cn.imuisample.models;

import com.fanchen.message.commons.models.IMessage;
import com.fanchen.message.commons.models.IUser;

import java.util.HashMap;
import java.util.UUID;


public class MyMessage implements IMessage {

    private long id;
    private String text;
    private String timeString;
    private int type;
    private IUser user;
    private String mediaFilePath;
    private long duration;
    private String progress;
    private MessageStatus mMsgStatus = MessageStatus.CREATED;

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
        if(objectObjectHashMap == null)  {
            objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("idCardTitle","金木梳");
            objectObjectHashMap.put("idCardNumber","账号:520520520");

            objectObjectHashMap.put("fileTitle","iOS 沉浸式状态栏实现.apk");
            objectObjectHashMap.put("fileSize","100.45Mb");

            objectObjectHashMap.put("locationTitle","聂市镇");
            objectObjectHashMap.put("locationDetails","岳阳市临湘市聂市镇105县道");
            objectObjectHashMap.put("path","https://dss0.baidu.com/73x1bjeh1BF3odCf/it/u=2678639494,2096646928&fm=85&s=F190CB3278D6FFEB52904FEF0300303F");

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