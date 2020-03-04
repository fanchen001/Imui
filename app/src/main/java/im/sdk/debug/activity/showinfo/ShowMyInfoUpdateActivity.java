package im.sdk.debug.activity.showinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.fanchen.R;
import im.sdk.debug.activity.TypeActivity;

/**
 * Created by ${chenyn} on 2016/12/21.
 */

public class ShowMyInfoUpdateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_myinfo_update);

        initView();
    }

    private void initView() {
        TextView tv_showMyInfoUpdate = (TextView) findViewById(R.id.tv_showMyInfoUpdate);

        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(TypeActivity.INFO_UPDATE);
        tv_showMyInfoUpdate.setText("被修改信息的 userName = " + stringExtra);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
