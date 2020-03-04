package imui.jiguang.cn.imuisample.models;

import com.fanchen.message.commons.models.IMessage;

import java.io.File;

import cn.jpush.im.android.api.callback.DownloadCompletionCallback;

public abstract class DownloadCallback extends DownloadCompletionCallback {
    private MyMessage myMessage;
    private boolean isSend = false;

    public void setMyMessage(MyMessage myMessage,boolean isSend) {
        this.myMessage = myMessage;
        this.isSend = isSend;
    }

    @Override
    public void onComplete(int i, String s, File file) {
        if (i == 0) {
            myMessage.setMessageStatus(isSend ? IMessage.MessageStatus.SEND_SUCCEED : IMessage.MessageStatus.RECEIVE_SUCCEED);
            onComplete(myMessage, file);
        } else {
            myMessage.setMessageStatus(isSend ? IMessage.MessageStatus.SEND_FAILED : IMessage.MessageStatus.RECEIVE_FAILED);
            onFailed(myMessage);
        }
    }

    public void onComplete(MyMessage message, File file){}

    public void onFailed(MyMessage message){}
}
