package im.sdk.debug.activity.showinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.fanchen.R;

public class ShowAnnouncementChangedActivity extends Activity {
    public static final String SHOW_ANNOUNCEMENT_CHANGED = "show_announcement_changed";
    private TextView mTvShowChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_announcement_change);
        mTvShowChange = (TextView) findViewById(R.id.tv_show_announcement_changed);
        String displayText = getIntent().getStringExtra(SHOW_ANNOUNCEMENT_CHANGED);
        mTvShowChange.append(displayText);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String displayText = intent.getStringExtra(SHOW_ANNOUNCEMENT_CHANGED);
        mTvShowChange.append(displayText);
    }
}
