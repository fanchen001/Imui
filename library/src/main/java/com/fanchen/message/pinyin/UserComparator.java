package com.fanchen.message.pinyin;

import com.fanchen.message.commons.models.IUser;

import java.util.Comparator;

public class UserComparator implements Comparator<IUser> {

    public int compare(IUser o1, IUser o2) {
        String notename = o1.getDisplayName();
        String notename2 = o2.getDisplayName();
        //要转成拼音否则有中文有英文时候比较出来的结果不正确
        notename = HanyuPinyin.getInstance().getStringPinYin(notename.substring(0,1));
        notename2 = HanyuPinyin.getInstance().getStringPinYin(notename2.substring(0,1));
        return notename.compareTo(notename2);
    }

}
