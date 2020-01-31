package com.tallate.cgh.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tallate
 */
@Slf4j
public class TimeUtil {

    private static final Integer FIRST_MONTH = 1;
    private static final Integer LAST_MONTH = 12;

    private static final String[] H = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    /**
     * 将数字月份转换为字符串 1 -> Jan
     */
    public static String month2str(int month) {
        if (month < FIRST_MONTH || month > LAST_MONTH) {
            return "";
        }
        return H[month];
    }

    /**
     * 2017
     */
    public static String getCurYear() {
        Calendar calendar = new GregorianCalendar();
        return Integer.toString(calendar.get(Calendar.YEAR));
    }

    /**
     * 1
     */
    public static String getCurMonth() {
        Calendar calendar = new GregorianCalendar();
        // 注意：get获取的月份是基于0的，即从0开始数，因此最后结果需要+1
        return Integer.toString(calendar.get(Calendar.MONTH) + 1);
    }

    /**
     * 1
     */
    public static String getCurDay() {
        Calendar calendar = new GregorianCalendar();
        return Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 2017-01-01
     */
    public static String getCurDate() {
        Calendar calendar = new GregorianCalendar();
        // 注意：MONTH的获取是基于0的，即0代表1月
        // 注意：需要在月和日前补0以达到2位数字
        return calendar.get(Calendar.YEAR) + "-"
                + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-"
                + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 判断今年是否为闰年
     */
    public static boolean isCurYearLeap() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0);
    }

    /**
     * 获取日期中的月份
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * intervalSecond 返回两个Date之间相隔的秒数
     */
    public static int intervalSeconds(Date dateFrom, Date dateTo) {
        // 注意Date.getTime()返回的是毫秒数
        return (int) (dateTo.getTime() / 1000 - dateFrom.getTime() / 1000);
    }

    /**
     * intervalMinute 返回两个Date之间相隔的分钟数
     */
    public static double intervalMinutes(Date dateFrom, Date dateTo) {
        return intervalSeconds(dateFrom, dateTo) / 60.0;
    }

    /**
     * 求两个日期之间相差的天数
     */
    public static long intervalDays(Date d1, Date d2) {
        return (d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000);
    }


    /**
     * 默认格式化为hh:mm:ss.ms
     */
    public static String defaultFormat(long intervalMillSeconds) {
        long hours = intervalMillSeconds / 3600000;
        intervalMillSeconds %= 3600000;
        if (hours > 24) {
            log.info("计时时间超出1天，请检查代码逻辑");
        }
        long minutes = intervalMillSeconds / 60000;
        intervalMillSeconds %= 60000;
        return hours + ":" + minutes + ":" + intervalMillSeconds / 1000
                + "." + intervalMillSeconds % 1000;
    }

    public static Date parse(String dateStr) throws UtilException {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new UtilException("parse date string [" + dateStr + "] failed");
        }
    }

    /**
     * mid 返回两个Date的中值
     */
    public static Date mid(Date dateLb, Date dateRb) {
        return new Date(dateLb.getTime() / 2 + dateRb.getTime() / 2);
    }

    /**
     * secondsFromZero 返回当天0点到d的秒数
     */
    public static int secondsFromZero(Date d) {
        return (int) (d.getTime() / 1000 % 86400);
    }

    /**
     * 加n天
     */
    public static Date addNDays(Date d, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DATE, n);
        return calendar.getTime();
    }

    /**
     * 减n天
     */
    public static Date subtractNDays(Date d, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DATE, -n);
        return calendar.getTime();
    }

}
