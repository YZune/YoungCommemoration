package com.suda.yzune.youngcommemoration.bean;

/**
 * Created by yzune on 2018/2/15.
 */

public class Solar {
    public int solarYear;
    public int solarMonth;
    public int solarDay;

    public Solar() {

    }

    public Solar(String date) {
        String[] list = date.split("-");
        solarYear = Integer.parseInt(list[0]);
        solarMonth = Integer.parseInt(list[1]);
        solarDay = Integer.parseInt(list[2]);
    }

    public int getSolarYear() {
        return solarYear;
    }

    public void setSolarYear(int solarYear) {
        this.solarYear = solarYear;
    }

    public int getSolarMonth() {
        return solarMonth;
    }

    public void setSolarMonth(int solarMonth) {
        this.solarMonth = solarMonth;
    }

    public int getSolarDay() {
        return solarDay;
    }

    public void setSolarDay(int solarDay) {
        this.solarDay = solarDay;
    }

    public String toString() {
        return solarYear + "-" + solarMonth + "-" + solarDay;
    }
}
