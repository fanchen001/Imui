package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.fanchen.R;
import im.sdk.debug.activity.TypeActivity;

public class ShowTransCommandActivity extends Activity {
    private TextView mTv_showText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String sender = intent.getStringExtra(TypeActivity.TRANS_COMMAND_SENDER);
        String target = intent.getStringExtra(TypeActivity.TRANS_COMMAND_TARGET);
        String type = intent.getStringExtra(TypeActivity.TRANS_COMMAND_TYPE);
        String cmd = intent.getStringExtra(TypeActivity.TRANS_COMMAND_CMD);
        mTv_showText.setText("sender: " + sender + "\ntarget: " + target +
        "\ntype: " + type + "\ncmd: " + cmd);
    }

    private void initView() {
        setContentView(R.layout.activity_show_trans_command);
        mTv_showText = (TextView) findViewById(R.id.tv_show_trans_command_notification);
    }
}
