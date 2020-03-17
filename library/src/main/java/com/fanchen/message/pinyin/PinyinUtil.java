package com.fanchen.message.pinyin;

import android.text.TextUtils;

import java.util.ArrayList;

public class PinyinUtil {

    public static String getLetter(String displayName) {
        String letter;
        if (displayName != null && !TextUtils.isEmpty(displayName.trim())) {
            ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(displayName);
            StringBuilder sb = new StringBuilder();
            if (tokens != null && tokens.size() > 0) {
                for (HanziToPinyin.Token token : tokens) {
                    if (token.type == HanziToPinyin.Token.PINYIN) {
                        sb.append(token.target);
                    } else {
                        sb.append(token.source);
                    }
                }
            }
            String sortString = sb.toString().substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                letter = sortString.toUpperCase();
            } else {
                letter = "#";
            }
        } else {
            letter = "#";
        }
        return letter;
    }
}
