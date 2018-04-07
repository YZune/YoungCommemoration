package com.suda.yzune.youngcommemoration.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yzune on 2018/2/18.
 */

@Entity
public class AppWidgetBean {

    //不能用int
    @Id
    private Long id;

    private int position;

    private String json;

    private int style;

    private String text;

    @Generated(hash = 1403304532)
    public AppWidgetBean(Long id, int position, String json, int style,
            String text) {
        this.id = id;
        this.position = position;
        this.json = json;
        this.style = style;
        this.text = text;
    }

    @Generated(hash = 1977486489)
    public AppWidgetBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getStyle() {
        return this.style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
