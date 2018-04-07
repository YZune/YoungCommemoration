package com.suda.yzune.youngcommemoration.view;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.youngcommemoration.GlideApp;
import com.suda.yzune.youngcommemoration.R;
import com.suda.yzune.youngcommemoration.bean.AppWidgetBean;
import com.suda.yzune.youngcommemoration.bean.AppWidgetBeanDao;
import com.suda.yzune.youngcommemoration.bean.DaoMaster;
import com.suda.yzune.youngcommemoration.bean.DaoSession;
import com.suda.yzune.youngcommemoration.bean.EventBean;
import com.suda.yzune.youngcommemoration.bean.Lunar;
import com.suda.yzune.youngcommemoration.bean.Solar;
import com.suda.yzune.youngcommemoration.utils.CountUtils;
import com.suda.yzune.youngcommemoration.utils.DaoUtils;
import com.suda.yzune.youngcommemoration.utils.LunarSolar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Implementation of App Widget functionality.
 */
public class EventsWidget extends AppWidgetProvider {

    RemoteViews mRemoteViews;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        DaoUtils.init(context);
        Gson gson = new Gson();
        List<AppWidgetBean> beanList = DaoUtils.getWidgetInstance().daoSession.loadAll(AppWidgetBean.class);
        for (AppWidgetBean a : beanList) {
            Log.d("小部件", a.getId().intValue() + "  " + a.getJson());
            switch (a.getStyle()) {
                case 0:
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.events_widget);
                    break;
                case 1:
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.events_widget_1);
                    break;
                case 2:
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.events_widget_2);
                    break;
                case 3:
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.events_widget_3);
                    break;
                case 4:
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.events_widget_4);
                    break;
                case 5:
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.events_widget_5);
                    break;
                case 6:
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.events_widget_6);
                    break;
            }
            try {
                initWidget(context, (EventBean) gson.fromJson(a.getJson(), new TypeToken<EventBean>() {
                }.getType()), mRemoteViews, a.getId().intValue(), a.getStyle(), a.getText(), appWidgetManager);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void initWidget(Context context, EventBean e, RemoteViews views, int id, int style, String text, AppWidgetManager appWidgetManager) throws ParseException {
        if (style == 0) {
            Uri uri = Uri.parse(e.getPicture_path());
            //views.setImageViewUri(R.id.iv_widget, uri);
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.iv_widget, views, id);
            GlideApp.with(context).asBitmap().load(uri).into(appWidgetTarget);
        }

        if (style == 5) {
            Uri uri = Uri.parse(e.getPicture_path());
            //views.setImageViewUri(R.id.iv_widget, uri);
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.iv_widget, views, id);
            AppWidgetTarget appWidgetTarget1 = new AppWidgetTarget(context, R.id.iv_pic_bg, views, id);
            GlideApp.with(context).asBitmap().load(uri).into(appWidgetTarget);
            GlideApp.with(context).asBitmap().load(uri).apply(bitmapTransform(new BlurTransformation(25))).into(appWidgetTarget1);
        }

        if (style == 6) {
            Uri uri = Uri.parse(e.getPicture_path());
            //views.setImageViewUri(R.id.iv_widget, uri);
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.iv_pic_bg, views, id);
            GlideApp.with(context).asBitmap().load(uri).apply(bitmapTransform(new BlurTransformation(25))).into(appWidgetTarget);
        }

        if (style == 0 || style == 1 || style == 5 || style == 6) {
            switch (e.getType()) {
                case 0:
                    views.setTextViewText(R.id.tv_days_widget, CountUtils.daysBetween(context, e.getDate()) + "");
                    views.setTextViewText(R.id.tv_event_widget, e.getContext());
                    break;
                case 1:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String todayTime = sdf.format(new Date());
                    int years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(e.getDate().substring(0, 4));
                    int days = CountUtils.daysBetween(context, todayTime.substring(0, 4) + e.getDate().substring(4), 0);
                    if (days < 0) {
                        years += 1;
                        days = CountUtils.daysBetween(context, String.valueOf(Integer.parseInt(todayTime.substring(0, 4)) + 1) + e.getDate().substring(4), 0);
                    }
                    views.setTextViewText(R.id.tv_event_widget, e.getContext() + years + "岁生日\n还有");
                    views.setTextViewText(R.id.tv_days_widget, days + "");
                    break;
                case 2:
                    days = CountUtils.daysBetween(context, e.getDate(), 0);
                    if (days < 0) {
                        views.setTextViewText(R.id.tv_event_widget, e.getContext() + "\n已过去");
                        views.setTextViewText(R.id.tv_days_widget, (-days) + "");
                    } else {
                        views.setTextViewText(R.id.tv_event_widget, e.getContext() + "\n还有");
                        views.setTextViewText(R.id.tv_days_widget, +days + "");
                    }
                    break;
                case 3:
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    todayTime = sdf.format(new Date());
                    Solar birth_solar = new Solar(e.getDate());

                    Lunar birth_lunar = LunarSolar.SolarToLunar(birth_solar);
                    Lunar now_lunar = LunarSolar.SolarToLunar(new Solar(todayTime));
                    birth_lunar.setIsleap(false);
                    birth_lunar.setLunarYear(now_lunar.getLunarYear());

                    years = now_lunar.getLunarYear() - LunarSolar.SolarToLunar(birth_solar).getLunarYear();
                    days = CountUtils.daysBetween(context, LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                    if (days < 0) {
                        years += 1;
                        birth_lunar.setLunarYear(now_lunar.getLunarYear() + 1);
                        days = CountUtils.daysBetween(context, LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                    }
                    views.setTextViewText(R.id.tv_event_widget, e.getContext() + years + "岁农历生日\n还有");
                    views.setTextViewText(R.id.tv_days_widget, days + "");
                    break;
            }
        }

        if (style == 2 || style == 3 || style == 4) {
            if (text.equals("")) {
                views.setViewVisibility(R.id.tv_text, View.GONE);
            } else {
                views.setTextViewText(R.id.tv_text, text);
            }
            switch (e.getType()) {
                case 0:
                    views.setTextViewText(R.id.tv_event_widget, "「" + e.getContext() + "」" + CountUtils.daysBetween(context, e.getDate()) + "天");
                    break;
                case 1:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String todayTime = sdf.format(new Date());
                    int years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(e.getDate().substring(0, 4));
                    int days = CountUtils.daysBetween(context, todayTime.substring(0, 4) + e.getDate().substring(4), 0);
                    if (days < 0) {
                        years += 1;
                        days = CountUtils.daysBetween(context, String.valueOf(Integer.parseInt(todayTime.substring(0, 4)) + 1) + e.getDate().substring(4), 0);
                    }
                    views.setTextViewText(R.id.tv_event_widget, e.getContext() + years + "岁生日还有" + days + "天");
                    break;
                case 2:
                    days = CountUtils.daysBetween(context, e.getDate(), 0);
                    if (days < 0) {
                        views.setTextViewText(R.id.tv_event_widget, e.getContext() + "已过去" + (-days) + "天");
                    } else {
                        views.setTextViewText(R.id.tv_event_widget, "离" + e.getContext() + "还有" + days + "天");
                    }
                    break;
                case 3:
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    todayTime = sdf.format(new Date());
                    Solar birth_solar = new Solar(e.getDate());

                    Lunar birth_lunar = LunarSolar.SolarToLunar(birth_solar);
                    Lunar now_lunar = LunarSolar.SolarToLunar(new Solar(todayTime));
                    birth_lunar.setIsleap(false);
                    birth_lunar.setLunarYear(now_lunar.getLunarYear());

                    years = now_lunar.getLunarYear() - LunarSolar.SolarToLunar(birth_solar).getLunarYear();
                    days = CountUtils.daysBetween(context, LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                    if (days < 0) {
                        years += 1;
                        birth_lunar.setLunarYear(now_lunar.getLunarYear() + 1);
                        days = CountUtils.daysBetween(context, LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                    }
                    views.setTextViewText(R.id.tv_event_widget, e.getContext() + years + "岁农历生日还有" + days + "天");
                    break;
            }
        }
        appWidgetManager.updateAppWidget(id, mRemoteViews);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        DaoUtils.init(context);
        for (int i : appWidgetIds) {
            DaoUtils.getWidgetInstance().daoSession.getAppWidgetBeanDao().deleteByKey((long) i);
        }
    }
}

