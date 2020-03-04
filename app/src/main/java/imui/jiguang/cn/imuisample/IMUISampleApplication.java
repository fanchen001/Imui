package imui.jiguang.cn.imuisample;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import com.fanchen.BuildConfig;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

//import com.squareup.leakcanary.LeakCanary;


public class IMUISampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this,true);

//        JMessageClient.register("fanchen1", "123321", new BasicCallback() {
//
//            @Override
//            public void gotResult(int i, String s) {
//                Log.e("JMessageClient","register -> " + s);
//            }
//
//        });

        JMessageClient.login("fanchen", "123321", new BasicCallback() {

            @Override
            public void gotResult(int i, String s) {
                Conversation conversation = Conversation.createSingleConversation("fanchen2");
                Message message = conversation.createSendTextMessage("我他妈形态爆炸");
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String s) {
                        if (responseCode == 0) {
                            //消息发送成功
                            Log.e("JMessageClient","消息发送成功 -> " + s);
                        } else {
                            //消息发送失败
                            Log.e("JMessageClient","消息发送失败 -> " + s);
                        }
                    }
                });
                JMessageClient.sendMessage(message);
            }

        });




        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }
}
