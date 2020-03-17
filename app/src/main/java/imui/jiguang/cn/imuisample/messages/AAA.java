package imui.jiguang.cn.imuisample.messages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.fanchen.R;
import com.fanchen.base.BaseIActivity;
import com.fanchen.view.ContactsView;

public class AAA extends BaseIActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ContactsView viewById = findViewById(R.id.contactsview);
        viewById.setAdapter(new StickyListAdapter());
    }

}
