package imui.jiguang.cn.imuisample.messages;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fanchen.R;
import com.fanchen.base.BaseIActivity;
import com.fanchen.message.commons.models.ISticky;
import com.fanchen.message.sticky.DefaultStickyAdapter;
import com.fanchen.view.ContactsView;

import java.util.ArrayList;

import imui.jiguang.cn.imuisample.models.DefaultF;

public class AAA extends BaseIActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final ContactsView viewById = findViewById(R.id.contactsview);
        ArrayList<ISticky> iFriends1 = new ArrayList<>();

        final DefaultStickyAdapter defaultStickyAdapter = new DefaultStickyAdapter(viewById, iFriends1);

        viewById.setAdapter(defaultStickyAdapter);

        final ArrayList<ISticky> iFriends = new ArrayList<>();
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","张三","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","李四","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","王五","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","涨了","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","额了","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","你们","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","TMD","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","MIN","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","SDOJ","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","完结","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","我今晚","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","今晚","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","晚","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","我玩的","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","我就恶趣味","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","额外热无","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","问问","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","驱蚊器","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","we0 ","1"));
        iFriends.add(new DefaultF("https://avatars1.githubusercontent.com/u/13826873?s=40&v=4","wdw","1"));

        viewById.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewById.dismissLoading();
                defaultStickyAdapter.setDate(iFriends);
            }
        },3000);
    }

}
