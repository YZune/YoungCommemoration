package com.suda.yzune.youngcommemoration.bean;

/**
 * Created by yzune on 2018/2/15.
 */

public class Lunar {
    public int lunarYear;
    public int lunarMonth;
    public int lunarDay;
    public boolean isleap;

    public int getLunarYear() {
        return lunarYear;
    }

    public void setLunarYear(int lunarYear) {
        this.lunarYear = lunarYear;
    }

    public int getLunarMonth() {
        return lunarMonth;
    }

    public void setLunarMonth(int lunarMonth) {
        this.lunarMonth = lunarMonth;
    }

    public int getLunarDay() {
        return lunarDay;
    }

    public void setLunarDay(int lunarDay) {
        this.lunarDay = lunarDay;
    }

    public boolean isIsleap() {
        return isleap;
    }

    public void setIsleap(boolean isleap) {
        this.isleap = isleap;
    }

    public String toString() {
        return lunarYear + "-" + lunarMonth + "-" + lunarDay;
    }
}