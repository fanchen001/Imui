package imui.jiguang.cn.imuisample.models;

import com.fanchen.message.commons.models.ISticky;
import com.fanchen.message.pinyin.PinyinUtil;

public class DefaultF implements ISticky {

    private String avatar;
    private String displayName;
    private String username;

    public DefaultF(String avatar, String displayName, String username) {
        this.avatar = avatar;
        this.displayName = displayName;
        this.username = username;
    }

    @Override
    public String getLetter() {
        return PinyinUtil.getLetter(getDisplayName());
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isSelect() {
        return false;
    }

    @Override
    public boolean isShow() {
        return false;
    }

    @Override
    public void setSelect(boolean select) {

    }

}
