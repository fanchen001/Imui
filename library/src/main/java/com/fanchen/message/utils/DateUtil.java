package com.fanchen.message.utils;

import com.fanchen.message.commons.models.IMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DateUtil {
    /**
     * 返回指定pattern样的日期时间字符串。
     *
     * @param dt
     * @param pattern
     * @return 如果时间转换成功则返回结果，否则返回空字符串""
     * @author 即时通讯网([url = http : / / www.52im.net]http : / / www.52im.net[ / url])
     */
    public static String getTimeString(Date dt, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);//"yyyy-MM-dd HH:mm:ss"
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(dt);
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 仿照微信中的消息时间显示逻辑，将时间戳（单位：毫秒）转换为友好的显示格式.
     *
     * <p>
     * <p>
     * 1）7天之内的日期显示逻辑是：今天、昨天(-1d)、前天(-2d)、星期？（只显示总计7天之内的星期数，即<=-4d）；<br>
     * <p>
     * 2）7天之外（即>7天）的逻辑：直接显示完整日期时间。
     *
     * @param srcDate         要处理的源日期时间对象
     * @param mustIncludeTime true表示输出的格式里一定会包含“时间:分钟”，否则不包含（参考微信，不包含时分的情况，用于首页“消息”中显示时）
     * @return 输出格式形如：“10:30”、“昨天 12:04”、“前天 20:51”、“星期二”、“2019/2/21 12:09”等形式
     * @author 即时通讯网([url = http : / / www.52im.net]http : / / www.52im.net[ / url])
     * @since 4.5
     */

    public static String getTimeStringAutoShort2(Date srcDate, boolean mustIncludeTime) {
        String ret = "";
        try {
            GregorianCalendar gcCurrent = new GregorianCalendar();
            gcCurrent.setTime(new Date());
            int currentYear = gcCurrent.get(GregorianCalendar.YEAR);
            int currentMonth = gcCurrent.get(GregorianCalendar.MONTH) + 1;
            int currentDay = gcCurrent.get(GregorianCalendar.DAY_OF_MONTH);
            GregorianCalendar gcSrc = new GregorianCalendar();
            gcSrc.setTime(srcDate);
            int srcYear = gcSrc.get(GregorianCalendar.YEAR);
            int srcMonth = gcSrc.get(GregorianCalendar.MONTH) + 1;
            int srcDay = gcSrc.get(GregorianCalendar.DAY_OF_MONTH);
            String timeExtraStr = (mustIncludeTime ? " " + getTimeString(srcDate, "HH:mm") : "");
            if (currentYear == srcYear) {// 当年
                long currentTimestamp = gcCurrent.getTimeInMillis();
                long srcTimestamp = gcSrc.getTimeInMillis();
                long delta = (currentTimestamp - srcTimestamp);
                if (currentMonth == srcMonth && currentDay == srcDay) {
                    if (delta < 60 * 1000)
                        ret = "刚刚";
                    else
                        ret = getTimeString(srcDate, "HH:mm");
                } else {
                    GregorianCalendar yesterdayDate = new GregorianCalendar();
                    yesterdayDate.add(GregorianCalendar.DAY_OF_MONTH, -1);
                    GregorianCalendar beforeYesterdayDate = new GregorianCalendar();
                    beforeYesterdayDate.add(GregorianCalendar.DAY_OF_MONTH, -2);
                    if (srcMonth == (yesterdayDate.get(GregorianCalendar.MONTH) + 1) && srcDay == yesterdayDate.get(GregorianCalendar.DAY_OF_MONTH)) {
                        ret = "昨天" + timeExtraStr;// -1d
                    } else if (srcMonth == (beforeYesterdayDate.get(GregorianCalendar.MONTH) + 1)
                            && srcDay == beforeYesterdayDate.get(GregorianCalendar.DAY_OF_MONTH)) {
                        ret = "前天" + timeExtraStr;// -2d
                    } else {
                        long deltaHour = (delta / (3600 * 1000));
                        if (deltaHour < 7 * 24) {
                            String[] weekday = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
                            String weedayDesc = weekday[gcSrc.get(GregorianCalendar.DAY_OF_WEEK) - 1];
                            ret = weedayDesc + timeExtraStr;
                        } else
                            ret = getTimeString(srcDate, "yyyy/M/d") + timeExtraStr;
                    }
                }
            } else
                ret = getTimeString(srcDate, "yyyy/M/d") + timeExtraStr;
        } catch (Exception e) {
            System.err.println("【DEBUG-getTimeStringAutoShort】计算出错：" + e.getMessage() + " 【NO】");
        }
        return ret;
    }

    public static void updateShowTime(List<? extends IMessage> list,int timeSpace) {
        if (list == null || list.isEmpty()) {
            return;
        }
        long time = list.get(0).getTime();
        for (int i = 0; i < list.size(); i++) {
            IMessage message = list.get(i);
            if (i == 0) {
                message.setShowTime(true);
            } else if (Math.abs(message.getTime() - time) > timeSpace * 1000) {
                time = message.getTime();
                message.setShowTime(true);
            } else {
                message.setShowTime(false);
            }
        }
    }
}
