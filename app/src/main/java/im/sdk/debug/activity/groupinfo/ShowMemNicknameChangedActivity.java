package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.fanchen.R;

public class ShowMemNicknameChangedActivity extends Activity {

    public static final String SHOW_MEM_NICKNAME_CHANGED = "show_mem_nickname_changed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_show_mem_nickname);
        String displayText = getIntent().getStringExtra(SHOW_MEM_NICKNAME_CHANGED);
        ((TextView) findViewById(R.id.tv_show_mem_nickname_changed)).setText(displayText);
    }
}
