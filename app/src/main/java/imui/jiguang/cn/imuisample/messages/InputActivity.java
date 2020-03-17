package imui.jiguang.cn.imuisample.messages;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.fanchen.R;

public class InputActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long l = System.currentTimeMillis();
        setContentView(R.layout.activity_chat_input);
        Log.e("InputActivity","耗时 -》 "+( System.currentTimeMillis() - l));
    }
}
