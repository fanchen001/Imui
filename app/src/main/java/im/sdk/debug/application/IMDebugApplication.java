package im.sdk.debug.application;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.wanjian.sak.SAK;
import com.wanjian.sak.config.Config;

import cn.jpush.im.android.api.JMessageClient;
import im.sdk.debug.GlobalEventListener;

/**
 * Created by ${chenyn} on 16/3/22.
 *
 * @desc :
 */
public class IMDebugApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("IMDebugApplication", "init");
        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext(), true);
        //注册全局事件监听类
        JMessageClient.registerEventReceiver(new GlobalEventListener(getApplicationContext()));
        SAK.init(this,new Config.Build(this).build());
    }
}

