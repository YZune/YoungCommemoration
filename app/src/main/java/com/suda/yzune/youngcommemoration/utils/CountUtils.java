package com.suda.yzune.youngcommemoration.utils;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yzune on 2018/2/6.
 */

public class CountUtils {
    public static int daysBetween(Context context, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayTime = sdf.format(new Date());// 获取当前的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(date));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(todayTime));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        Log.d("日期", Integer.parseInt(String.valueOf(between_days)) + "");
        return Integer.parseInt(String.valueOf(between_days));
    }

    public static int daysBetween(Context context, String date, int a) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayTime = sdf.format(new Date());// 获取当前的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(date));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(todayTime));
        long time2 = cal.getTimeInMillis();
        long between_days = (time1 - time2) / (1000 * 3600 * 24);
        Log.d("日期", Integer.parseInt(String.valueOf(between_days)) + "");
        return Integer.parseInt(String.valueOf(between_days));
    }
}
