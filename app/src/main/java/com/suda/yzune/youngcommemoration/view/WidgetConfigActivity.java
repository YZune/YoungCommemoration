package com.suda.yzune.youngcommemoration.view;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.youngcommemoration.GlideApp;
import com.suda.yzune.youngcommemoration.R;
import com.suda.yzune.youngcommemoration.adapter.EventsAdapter;
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
import com.suda.yzune.youngcommemoration.utils.SharedPreferencesUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class WidgetConfigActivity extends AppCompatActivity {

    RecyclerView rv_events;
    RadioGroup rg_style;
    LinearLayoutManager layoutManager;
    EventsAdapter adapter;
    List<EventBean> eventBeanList;
    Gson gson = new Gson();
    int mAppWidgetId;
    int style = 0;
    TextView tv_text;
    EditText et_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);
        initView();
        try {
            initData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        tv_text = (TextView) findViewById(R.id.tv_text);
        et_text = (EditText) findViewById(R.id.et_text);
        rv_events = (RecyclerView) findViewById(R.id.rv_events);
        layoutManager = new LinearLayoutManager(this);
        rv_events.setLayoutManager(layoutManager);
        layoutManager.setAutoMeasureEnabled(true);
        rg_style = (RadioGroup) findViewById(R.id.rg_style);
        rg_style.setFocusable(true);
        rg_style.setFocusableInTouchMode(true);
        rg_style.requestFocus();
        tv_text.setVisibility(View.GONE);
        et_text.setVisibility(View.GONE);
    }

    public void initData() throws ParseException {
        rg_style.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_style_0:
                        style = 0;
                        tv_text.setVisibility(View.GONE);
                        et_text.setVisibility(View.GONE);
                        break;
                    case R.id.rb_style_1:
                        style = 1;
                        tv_text.setVisibility(View.GONE);
                        et_text.setVisibility(View.GONE);
                        break;
                    case R.id.rb_style_5:
                        style = 5;
                        tv_text.setVisibility(View.GONE);
                        et_text.setVisibility(View.GONE);
                        break;
                    case R.id.rb_style_6:
                        style = 6;
                        tv_text.setVisibility(View.GONE);
                        et_text.setVisibility(View.GONE);
                        break;
                    case R.id.rb_style_2:
                        style = 2;
                        tv_text.setVisibility(View.VISIBLE);
                        et_text.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_style_3:
                        style = 3;
                        tv_text.setVisibility(View.VISIBLE);
                        et_text.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_style_4:
                        style = 4;
                        tv_text.setVisibility(View.VISIBLE);
                        et_text.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        final String eventJson = SharedPreferencesUtils.getStringFromSP(WidgetConfigActivity.this, "events", "");
        if (eventJson.equals("")) {
            eventBeanList = new ArrayList<EventBean>();
        } else {
            eventBeanList = gson.fromJson(eventJson, new TypeToken<List<EventBean>>() {
            }.getType());
        }

        adapter = new EventsAdapter(R.layout.item_event, eventBeanList);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget);
                switch (style) {
                    case 0:
                        views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget);
                        break;
                    case 1:
                        views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget_1);
                        break;
                    case 2:
                        views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget_2);
                        break;
                    case 3:
                        views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget_3);
                        break;
                    case 4:
                        views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget_4);
                        break;
                    case 5:
                        views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget_5);
                        break;
                    case 6:
                        views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget_6);
                        break;
                }
                EventBean e = eventBeanList.get(position);
                try {
                    initWidget(e, views);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                appWidgetManager.updateAppWidget(mAppWidgetId, views);
                Log.d("小部件", "创建" + mAppWidgetId);
                DaoUtils.init(WidgetConfigActivity.this);
                if (et_text.getVisibility() == View.VISIBLE) {
                    DaoUtils.getWidgetInstance().daoSession.insert(new AppWidgetBean((long) mAppWidgetId, position, gson.toJson(eventBeanList.get(position)), style, et_text.getText().toString()));
                } else {
                    DaoUtils.getWidgetInstance().daoSession.insert(new AppWidgetBean((long) mAppWidgetId, position, gson.toJson(eventBeanList.get(position)), style, ""));
                }
                //SharedPreferencesUtils.saveStringToSP(WidgetConfigActivity.this, mAppWidgetId + "", gson.toJson(e));
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
        rv_events.setAdapter(adapter);
    }

    public void initWidget(EventBean e, RemoteViews views) throws ParseException {
        if (style == 0) {
            Glide.get(getApplicationContext()).clearMemory();
            Uri uri = Uri.parse(e.getPicture_path());
            //views.setImageViewUri(R.id.iv_widget, uri);
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(getApplicationContext(), R.id.iv_widget, views, mAppWidgetId);
            GlideApp.with(getApplicationContext()).asBitmap().override(Target.SIZE_ORIGINAL).load(uri).into(appWidgetTarget);
        }

        if (style == 5) {
            Uri uri = Uri.parse(e.getPicture_path());
            //views.setImageViewUri(R.id.iv_widget, uri);
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(getApplicationContext(), R.id.iv_widget, views, mAppWidgetId);
            AppWidgetTarget appWidgetTarget1 = new AppWidgetTarget(getApplicationContext(), R.id.iv_pic_bg, views, mAppWidgetId);
            GlideApp.with(getApplicationContext()).asBitmap().load(uri).into(appWidgetTarget);
            GlideApp.with(getApplicationContext()).asBitmap().load(uri).into(appWidgetTarget1);
        }

        if (style == 6) {
            Uri uri = Uri.parse(e.getPicture_path());
            //views.setImageViewUri(R.id.iv_widget, uri);
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(getApplicationContext(), R.id.iv_pic_bg, views, mAppWidgetId);
            GlideApp.with(getApplicationContext()).asBitmap().load(uri).apply(bitmapTransform(new BlurTransformation(25))).into(appWidgetTarget);
        }

        if (style == 0 || style == 1 || style == 5 || style == 6) {
            switch (e.getType()) {
                case 0:
                    views.setTextViewText(R.id.tv_days_widget, CountUtils.daysBetween(getApplicationContext(), e.getDate()) + "");
                    views.setTextViewText(R.id.tv_event_widget, e.getContext());
                    break;
                case 1:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String todayTime = sdf.format(new Date());
                    int years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(e.getDate().substring(0, 4));
                    int days = CountUtils.daysBetween(getApplicationContext(), todayTime.substring(0, 4) + e.getDate().substring(4), 0);
                    if (days < 0) {
                        years += 1;
                        days = CountUtils.daysBetween(getApplicationContext(), String.valueOf(Integer.parseInt(todayTime.substring(0, 4)) + 1) + e.getDate().substring(4), 0);
                    }
                    views.setTextViewText(R.id.tv_event_widget, e.getContext() + years + "岁生日\n还有");
                    views.setTextViewText(R.id.tv_days_widget, days + "");
                    break;
                case 2:
                    days = CountUtils.daysBetween(getApplicationContext(), e.getDate(), 0);
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
                    days = CountUtils.daysBetween(getApplicationContext(), LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                    if (days < 0) {
                        years += 1;
                        birth_lunar.setLunarYear(now_lunar.getLunarYear() + 1);
                        days = CountUtils.daysBetween(getApplicationContext(), LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                    }
                    views.setTextViewText(R.id.tv_event_widget, e.getContext() + years + "岁农历生日\n还有");
                    views.setTextViewText(R.id.tv_days_widget, days + "");
                    break;
            }
        }

        if (style == 2 || style == 3 || style == 4) {
            if (et_text.getVisibility() == View.GONE) {
                views.setViewVisibility(R.id.tv_text, View.GONE);
            } else {
                views.setTextViewText(R.id.tv_text, et_text.getText().toString());
            }
            switch (e.getType()) {
                case 0:
                    views.setTextViewText(R.id.tv_event_widget, "「" + e.getContext() + "」" + CountUtils.daysBetween(getApplicationContext(), e.getDate()) + "天");
                    break;
                case 1:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String todayTime = sdf.format(new Date());
                    int years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(e.getDate().substring(0, 4));
                    int days = CountUtils.daysBetween(getApplicationContext(), todayTime.substring(0, 4) + e.getDate().substring(4), 0);
                    if (days < 0) {
                        years += 1;
                        days = CountUtils.daysBetween(getApplicationContext(), String.valueOf(Integer.parseInt(todayTime.substring(0, 4)) + 1) + e.getDate().substring(4), 0);
                    }
                    views.setTextViewText(R.id.tv_event_widget, e.getContext() + years + "岁生日还有" + days + "天");
                    break;
                case 2:
                    days = CountUtils.daysBetween(getApplicationContext(), e.getDate(), 0);
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
                    days = CountUtils.daysBetween(getApplicationContext(), LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                    if (days < 0) {
                        years += 1;
                        birth_lunar.setLunarYear(now_lunar.getLunarYear() + 1);
                        days = CountUtils.daysBetween(getApplicationContext(), LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                    }
                    views.setTextViewText(R.id.tv_event_widget, e.getContext() + years + "岁农历生日还有" + days + "天");
                    break;
            }
        }
    }

}
