package im.sdk.debug.activity.notify;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import cn.jpush.im.android.api.JMessageClient;
import com.fanchen.R;

/**
 * 设置通知栏通知展示方式
 */
public class NotifyTypeActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = NotifyTypeActivity.class.getSimpleName();
    private int notificationFlag = JMessageClient.getNotificationFlag();

    private CheckBox cb_sound;
    private CheckBox cb_vibrate;
    private CheckBox cb_led;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_notify_type);
        CheckBox cb_enable = (CheckBox) findViewById(R.id.cb_notify_enable);
        cb_sound = (CheckBox) findViewById(R.id.cb_notify_sound);
        cb_vibrate = (CheckBox) findViewById(R.id.cb_notify_vibrate);
        cb_led = (CheckBox) findViewById(R.id.cb_notify_led);

        //初始化几个cb的选中状态，需要在下面setOnCheckedChangeListener之前进行。
        boolean isDisable = 0 != (notificationFlag & JMessageClient.FLAG_NOTIFY_DISABLE);
        cb_enable.setChecked(!isDisable);
        cb_sound.setEnabled(!isDisable);
        cb_vibrate.setEnabled(!isDisable);
        cb_led.setEnabled(!isDisable);

        boolean isSoundEnable = 0 != (notificationFlag & JMessageClient.FLAG_NOTIFY_WITH_SOUND);
        boolean isVibrateEnable = 0 != (notificationFlag & JMessageClient.FLAG_NOTIFY_WITH_VIBRATE);
        boolean isLedEnable = 0 != (notificationFlag & JMessageClient.FLAG_NOTIFY_WITH_LED);
        cb_sound.setChecked(isSoundEnable);
        cb_vibrate.setChecked(isVibrateEnable);
        cb_led.setChecked(isLedEnable);

        cb_enable.setOnCheckedChangeListener(this);
        cb_sound.setOnCheckedChangeListener(this);
        cb_vibrate.setOnCheckedChangeListener(this);
        cb_led.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_notify_enable:
                if (isChecked) {
                    notificationFlag ^= JMessageClient.FLAG_NOTIFY_DISABLE;
                } else {
                    notificationFlag |= JMessageClient.FLAG_NOTIFY_DISABLE;
                }
                cb_sound.setEnabled(isChecked);
                cb_vibrate.setEnabled(isChecked);
                cb_led.setEnabled(isChecked);
                JMessageClient.setNotificationFlag(notificationFlag);
                break;
            case R.id.cb_notify_sound:
                if (isChecked) {
                    notificationFlag |= JMessageClient.FLAG_NOTIFY_WITH_SOUND;
                } else {
                    notificationFlag ^= JMessageClient.FLAG_NOTIFY_WITH_SOUND;
                }
                JMessageClient.setNotificationFlag(notificationFlag);
                break;
            case R.id.cb_notify_vibrate:
                if (isChecked) {
                    notificationFlag |= JMessageClient.FLAG_NOTIFY_WITH_VIBRATE;
                } else {
                    notificationFlag ^= JMessageClient.FLAG_NOTIFY_WITH_VIBRATE;
                }
                JMessageClient.setNotificationFlag(notificationFlag);
                break;
            case R.id.cb_notify_led:
                if (isChecked) {
                    notificationFlag |= JMessageClient.FLAG_NOTIFY_WITH_LED;
                } else {
                    notificationFlag ^= JMessageClient.FLAG_NOTIFY_WITH_LED;
                }
                JMessageClient.setNotificationFlag(notificationFlag);
                break;
        }
    }
}
