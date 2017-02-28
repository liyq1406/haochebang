package com.haoche51.checker.util;

import android.text.TextUtils;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UnixTimeUtil {
  public static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
  public static final String YEAR_MONTH_PATTERN = "yyyy年MM月";
  public static final String YEAR_MONTH_HOUR_PATTERN = "yyyy-MM-dd HH";
  private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm";
  public static final String DEFAULT_PATTERN_2 = "yyyy-MM-dd HH:mm:ss";
  public static final String YEAR_MONTH_PATTERN_NEW = "yyyyMM";
  private static final String EVALTIME_PATTERN = "MM月dd日";
  private static final String NOTICE_PATTERN = "MM-dd HH:mm";
  private static final String HOURS_MINUTES = "HH:mm";
  public static final String YEAR_DOT_MONTH = "yyyy.MM.dd HH:mm";
  /**
   * 过户时间显示格式
   */
  private static final String TRANSFER_PATTERN = "MM-dd  HH:mm";
  private static String errMsg = "";

  public static String getErrMsg() {
    return errMsg;
  }

  public static String format(int unixTime, String pattern) {
    if (pattern == null) pattern = DEFAULT_PATTERN;
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
      return dateFormat.format(new Date(unixTime * 1000L));
    } catch (IllegalArgumentException e) {
      errMsg = "catch IllegalArgumentException: " + e.getMessage();
    }
    return null;
  }

  public static String format(long unixTime, String pattern) {
    if (pattern == null) pattern = DEFAULT_PATTERN;
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
      return dateFormat.format(new Date(unixTime * 1000L));
    } catch (IllegalArgumentException e) {
      errMsg = "catch IllegalArgumentException: " + e.getMessage();
    }
    return null;
  }

  public static String format(int unixTime) {
    return format(unixTime, DEFAULT_PATTERN);
  }

  public static String formatYearMonthDay(int unixTime) {
    return format(unixTime, YEAR_MONTH_DAY_PATTERN);
  }

  public static String formatEvalTime(int unixTime) {
    return format(unixTime, EVALTIME_PATTERN);
  }

  public static String formatNoticeTime(int unixTime) {
    return format(unixTime, NOTICE_PATTERN);
  }

  public static String formatHoursMinutes(int unixTime) {
    return format(unixTime, HOURS_MINUTES);
  }

  public static String formatYearDotMounth(int unixTime) {
    return format(unixTime, YEAR_DOT_MONTH);
  }

  /**
   * 格式化过户时间
   */
  public static String formatTransferTime(int unixTime) {
    return format(unixTime, TRANSFER_PATTERN);
  }

  public static String relativeStyle(int unixTime) {
    int curTime = (int) (System.currentTimeMillis() / 1000);
    int sub = curTime - unixTime;
    if (sub < 0) {
      return format(unixTime);
    }
    if (sub < 60) {
      return sub + "秒前";
    }
    sub = sub / 60;
    if (sub < 60) {
      return sub + "分钟前";
    }
    sub = sub / 60;
    if (sub < 24) {
      return sub + "小时前";
    }
    return format(unixTime);
  }

  public static String reverseStyle(int unixTime) {
    int curTime = (int) (System.currentTimeMillis() / 1000);
    int sub = unixTime - curTime;
    if (sub < 0) {
      sub = Math.abs(sub);
      if (sub < 60) {
        return sub + "秒前";
      }
      sub = sub / 60;
      if (sub < 60) {
        return sub + "分钟前";
      }
      sub = sub / 60;
      if (sub < 24) {
        return sub + "小时前";
      }
      sub = sub / 24;
      return sub + "天前";
    }
    if (sub < 60) {
      return sub + "秒后";
    }
    sub = sub / 60;
    if (sub < 60) {
      return sub + "分钟后";
    }
    sub = sub / 60;
    if (sub < 24) {
      return sub + "小时后";
    }
    sub = sub / 24;
    return sub + "天后";
  }

  public static int getUnixTime(String time, String pattern) {
    if(TextUtils.isEmpty(time)){
      return 0;
    }
    if (pattern == null) pattern = DEFAULT_PATTERN;
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
      Date date = dateFormat.parse(time);
      return (int) (date.getTime() / 1000);
    } catch (IllegalArgumentException e) {
      errMsg = "catch IllegalArgumentException: " + e.getMessage();
    } catch (ParseException e) {
      errMsg = "catch ParseException: " + e.getMessage();
    }
    return 0;
  }


  public static int getUnixTime(String time) {
    return getUnixTime(time, DEFAULT_PATTERN);
  }

  public static int getModifyUnixTime(String time) {
    return getUnixTime(time, YEAR_MONTH_DAY_PATTERN);
  }

  /**
   * 把 年-月-日 存入时间戳
   *
   * @param year  1970年以后
   * @param month 自然月，1就是一月，2就是二月
   *              Notice: 这个与java默认不同
   * @param day   1就是第一天
   * @return 时间戳
   */
  public static int getUnixTime(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, day);
    return (int) (calendar.getTimeInMillis() / 1000);
  }

  public static int getUnixTime(int year, int month) {
    return getUnixTime(year, month, 1);
  }

  public static int getUnixTime(Spinner yearSpn, Spinner monthSpn) {
    int year = Integer.valueOf(yearSpn.getSelectedItem().toString());

    int month = Integer.valueOf(monthSpn.getSelectedItem().toString());

    return getUnixTime(year, month);
  }

  /**
   * 从时间戳中读出年份
   *
   * @param unixTime 时间戳
   * @return year 年份
   */
  public static int getYear(int unixTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(unixTime * 1000L);
    return calendar.get(Calendar.YEAR);
  }

  /**
   * 从时间戳中读出月份
   *
   * @param unixTime 时间戳
   * @return month 自然月，1就是一月，2就是二月
   */
  public static int getMonth(int unixTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(unixTime * 1000L);
    return calendar.get(Calendar.MONTH) + 1;
  }

  /**
   * 从时间戳中读出天
   *
   * @param unixTime 时间戳
   * @return day of month 1就是第一天
   */
  public static int getDay(int unixTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(unixTime * 1000L);
    return calendar.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 判断时间是否为当天
   *
   * @param unixTime
   * @return
   */
  public static boolean isToday(int unixTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    HCLogUtil.e("com.haoche51.checker", "unixTime is " + unixTime);
    return calendar.get(Calendar.YEAR) == getYear(unixTime) && calendar.get(Calendar.MONTH) + 1 == getMonth(unixTime) &&
            calendar.get(Calendar.DAY_OF_MONTH) == getDay(unixTime);
  }

  /**
   * 返回格式化的播放时间mm:ss
   *
   * @param time
   * @return
   */
  public static String getFormatTime(int time) {
    DecimalFormat df = new DecimalFormat("00");
    time = time / 1000;
    String durationStr;
    if (time / 60 > 0) {
      durationStr = df.format(time / 60) + ":";
    } else {
      durationStr = "00:";
    }
    if (time % 60 > 0) {
      durationStr = durationStr + df.format(time % 60);
    } else {
      durationStr = durationStr + "00";
    }
    return durationStr;
  }


  /**
   * 将时间戳转换成日期格式字符串
   * @param millSeconds 时间戳
   * @param format 转换格式，默认为：yyyy-MM-dd HH:mm:ss
   * @return
   */
  public static String timeStampToDate(long millSeconds,String format) {
    if(millSeconds == 0){
      return "0";
    }
    StringBuilder sb=new StringBuilder();
    long hour=millSeconds/(1000*60*60);//小时
    long minute=millSeconds%(1000*60*60)/(60*1000);//分钟
    long second=millSeconds%(1000*60*60)%(60*1000)/1000;//秒
    sb.append(hour).append("时").append(minute).append("分").append(second).append("秒");
    return sb.toString();
  }

  /**
   * 获取当前时间
   *
   * @return
   */
  public static String getCurrTime() {
    Date currDate = new Date(System.currentTimeMillis());
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    return formatter.format(currDate);
  }

  /**
   * 获取当前年份
   * 格式yyyy
   * @return
   */
  public static int getCurrYear() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    String date = sdf.format(new Date());
    HCLogUtil.e("=====================getCurrYear","当前年份："+date);
    int year=2017;
    try{
      year=Integer.parseInt(date);
    }catch (Exception e){
      e.printStackTrace();
    }
    return year;
  }
}
