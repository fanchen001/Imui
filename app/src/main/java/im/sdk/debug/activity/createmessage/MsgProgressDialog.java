package im.sdk.debug.activity.createmessage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import cn.jpush.im.android.api.model.Message;

/**
 * ProgressDialog with cancel
 * Created by jiguang on 2019/2/21.
 */

class MsgProgressDialog extends ProgressDialog {
    MsgProgressDialog(Context context, final Message message) {
        super(context);
        setTitle("提示:");
        setMessage("正在发送中。。。");
        setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                message.cancelSend();
            }
        });
    }

    static MsgProgressDialog show(Context context, Message message) {
        MsgProgressDialog msgProgressDialog = new MsgProgressDialog(context, message);
        msgProgressDialog.show();
        return msgProgressDialog;
    }


}
