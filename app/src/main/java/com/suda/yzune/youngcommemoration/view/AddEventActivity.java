package com.suda.yzune.youngcommemoration.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.suda.yzune.youngcommemoration.utils.GlideAppEngine;
import com.suda.yzune.youngcommemoration.utils.LunarSolar;
import com.suda.yzune.youngcommemoration.utils.SharedPreferencesUtils;
import com.suda.yzune.youngcommemoration.utils.ViewUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AddEventActivity extends AppCompatActivity {
    int mYear, mMonth, mDay;
    String mDate;
    ImageView iv_pic;
    EditText editText;
    ImageButton ib_save, ib_delete;
    RadioGroup radioGroup;
    Switch aSwitch;
    LinearLayout ll_date;
    TextView tv_date, tv_title;
    String pic_path;
    int position = -1;
    int choose_type = -1;
    final int DATE_DIALOG = 1;
    boolean is_fav;
    private static final int REQUEST_CODE_CHOOSE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initView();
        initData();
        initEvent();
    }

    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        editText = (EditText) findViewById(R.id.et_text);
        ib_save = (ImageButton) findViewById(R.id.ib_save);
        ib_delete = (ImageButton) findViewById(R.id.ib_delete);
        radioGroup = (RadioGroup) findViewById(R.id.rg_choose_type);
        aSwitch = (Switch) findViewById(R.id.switch_isFav);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        tv_date = (TextView) findViewById(R.id.tv_date);
    }

    public void initData() {
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("who").equals("add")) {
            Uri uri = Uri.parse(bundle.getString("pic"));
            Glide.with(this).load(uri).into(iv_pic);
            pic_path = uri.toString();
        } else {
            tv_title.setText("修改事件");
            Gson gson = new Gson();
            EventBean e = gson.fromJson(bundle.getString("event_json"), new TypeToken<EventBean>() {
            }.getType());
            Uri uri = Uri.parse(e.getPicture_path());
            Glide.with(this).load(uri).into(iv_pic);
            is_fav = e.isFavourite();
            pic_path = e.getPicture_path();
            position = bundle.getInt("event_locate");
            editText.setText(e.getContext());
            tv_date.setText(e.getDate());
            aSwitch.setChecked(e.isFavourite());
            choose_type = e.getType();
            ib_delete.setVisibility(View.VISIBLE);
            switch (choose_type) {
                case 0:
                    radioGroup.check(R.id.rb_commemoration);
                    break;
                case 1:
                    radioGroup.check(R.id.rb_birthday);
                    break;
                case 2:
                    radioGroup.check(R.id.rb_rest);
                    break;
                case 3:
                    radioGroup.check(R.id.rb_lunar_birthday);
                    break;
            }
        }
    }

    public void initEvent() {
        ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddEventActivity.this);
                builder.setTitle("警告");
                builder.setMessage("确定要删除吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<EventBean> eventBeanList;
                        Gson gson = new Gson();
                        String eventJson = SharedPreferencesUtils.getStringFromSP(AddEventActivity.this, "events", "");
                        eventBeanList = gson.fromJson(eventJson, new TypeToken<List<EventBean>>() {
                        }.getType());
                        eventBeanList.remove(position);
                        String newJson = gson.toJson(eventBeanList);
                        SharedPreferencesUtils.saveStringToSP(AddEventActivity.this, "events", newJson);
                        finish();
                    }
                });
                builder.setNegativeButton("手滑了", null);
                builder.show();
            }
        });

        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matisse.from(AddEventActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(1)
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideAppEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                is_fav = isChecked;
            }
        });

        ll_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_commemoration:
                        choose_type = 0;
                        break;
                    case R.id.rb_birthday:
                        choose_type = 1;
                        break;
                    case R.id.rb_rest:
                        choose_type = 2;
                        break;
                    case R.id.rb_lunar_birthday:
                        choose_type = 3;
                        break;
                }
            }
        });

        ib_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (choose_type == -1) {
                    flag = false;
                    Toasty.warning(AddEventActivity.this, "请选择事件类型哦~").show();
                }
                if (editText.getText().toString().equals("")) {
                    flag = false;
                    Toasty.warning(AddEventActivity.this, "别忘了写点东西呀~").show();
                }
                if (tv_date.getText().toString().charAt(0) == '请') {
                    flag = false;
                    Toasty.warning(AddEventActivity.this, "请选择日期哦~").show();
                }
                if (flag) {
                    List<EventBean> eventBeanList;
                    Gson gson = new Gson();
                    String eventJson = SharedPreferencesUtils.getStringFromSP(AddEventActivity.this, "events", "");
                    if (eventJson.equals("")) {
                        eventBeanList = new ArrayList<EventBean>();
                    } else {
                        eventBeanList = gson.fromJson(eventJson, new TypeToken<List<EventBean>>() {
                        }.getType());
                        if (is_fav && (!eventBeanList.isEmpty())) {
                            for (EventBean e : eventBeanList) {
                                e.setFavourite(false);
                            }
                        } else if (eventBeanList.isEmpty()) {
                            is_fav = true;
                        }
                    }
                    if (position < 0) {
                        eventBeanList.add(new EventBean(
                                tv_date.getText().toString(),
                                editText.getText().toString(),
                                choose_type,
                                pic_path,
                                is_fav
                        ));
                        Toasty.success(AddEventActivity.this, "添加成功~").show();
                    } else {
                        eventBeanList.get(position).setDate(tv_date.getText().toString());
                        eventBeanList.get(position).setContext(editText.getText().toString());
                        eventBeanList.get(position).setType(choose_type);
                        eventBeanList.get(position).setPicture_path(pic_path);
                        eventBeanList.get(position).setFavourite(is_fav);

                        DaoUtils.init(AddEventActivity.this);
//                        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "appwidget.db", null);
//                        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
//                        DaoSession daoSession = daoMaster.newSession();
//                        AppWidgetBeanDao widgetBeanDao = daoSession.getAppWidgetBeanDao();
                        List<AppWidgetBean> w1 = DaoUtils.getWidgetInstance().daoSession.queryBuilder(AppWidgetBean.class).where(AppWidgetBeanDao.Properties.Position.eq(position)).list();

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                        for (AppWidgetBean w : w1) {
                            w.setJson(gson.toJson(eventBeanList.get(position)));
                            DaoUtils.getWidgetInstance().daoSession.update(w);
                            //widgetBeanDao.update(w);
                            RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.events_widget);
                            switch (w.getStyle()) {
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
                            try {
                                initWidget(getApplicationContext(), eventBeanList.get(position), views, w.getId().intValue(), w.getStyle(), w.getText(), appWidgetManager);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        Toasty.success(AddEventActivity.this, "修改成功~").show();
                    }
                    String newJson = gson.toJson(eventBeanList);
                    SharedPreferencesUtils.saveStringToSP(AddEventActivity.this, "events", newJson);
                    finish();
                }
            }
        });
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
        appWidgetManager.updateAppWidget(id, views);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(Matisse.obtainResult(data).get(0).toString());
            Glide.with(this).load(uri).into(iv_pic);
            pic_path = uri.toString();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void displayDate() {
        mDate = new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append("").toString();
        tv_date.setText(mDate);
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            displayDate();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && SharedPreferencesUtils.getBooleanFromSP(AddEventActivity.this, "s_back", true)) {
            exitBy2Click();  //退出应用的操作
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && !SharedPreferencesUtils.getBooleanFromSP(AddEventActivity.this, "s_back", true)) {
            finish();
        }
        return false;
    }

    /**
     * 双击退出函数
     */

    private static Boolean isExit = false;

    private void exitBy2Click() {
        if (!isExit) {
            isExit = true; // 准备退出
            Toasty.warning(AddEventActivity.this, "再按一次退出编辑").show();
            Timer tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
        }
    }
}
