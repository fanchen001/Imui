package im.sdk.debug.activity.showinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.fanchen.R;

public class ShowChatRoomNotificationActivity extends Activity {
    public static final String SHOW_CHAT_ROOM_NOTIFICATION = "show_chat_room_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chat_room_notification);
        ((TextView) findViewById(R.id.tv_show_notification)).setText(getIntent().getStringExtra(SHOW_CHAT_ROOM_NOTIFICATION));
    }
}
