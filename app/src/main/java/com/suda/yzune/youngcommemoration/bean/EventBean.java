package com.suda.yzune.youngcommemoration.bean;

import com.google.gson.Gson;

/**
 * Created by yzune on 2018/2/5.
 */

public class EventBean {
    private String date;
    private String context;
    private int type;
    private String picture_path;
    private boolean isFavourite;

    public EventBean(String date, String context, int type, String picture_path, boolean isFavourite) {
        this.date = date;
        this.context = context;
        this.type = type;
        this.picture_path = picture_path;
        this.isFavourite = isFavourite;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
