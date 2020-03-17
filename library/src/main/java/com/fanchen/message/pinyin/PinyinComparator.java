package com.fanchen.message.pinyin;


import com.fanchen.message.commons.models.ISticky;

import java.util.Comparator;

public class PinyinComparator implements Comparator<ISticky> {

    public int compare(ISticky o1, ISticky o2) {
        if (o1.getLetter().equals("@") || o2.getLetter().equals("#")) {
            return -1;
        } else if (o1.getLetter().equals("#") || o2.getLetter().equals("@")) {
            return 1;
        } else {
            return o1.getLetter().compareTo(o2.getLetter());
        }
    }



}
